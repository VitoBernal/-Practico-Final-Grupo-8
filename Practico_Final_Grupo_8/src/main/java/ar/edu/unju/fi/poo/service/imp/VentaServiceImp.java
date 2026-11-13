package ar.edu.unju.fi.poo.service.imp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.poo.dto.CuotaEstadoRequestDto;
import ar.edu.unju.fi.poo.dto.CuotaResponseDto;
import ar.edu.unju.fi.poo.dto.EstadoCuentaResponseDto;
import ar.edu.unju.fi.poo.dto.ItemVentaRequestDto;
import ar.edu.unju.fi.poo.dto.VentaRequestDto;
import ar.edu.unju.fi.poo.dto.VentaResponseDto;
import ar.edu.unju.fi.poo.dto.mapper.VentaMapper;
import ar.edu.unju.fi.poo.exception.ClienteInhabilitadoException;
import ar.edu.unju.fi.poo.exception.CuponInvalidoException;
import ar.edu.unju.fi.poo.exception.ImporteInvalidoException;
import ar.edu.unju.fi.poo.exception.NoEncontradoException;
import ar.edu.unju.fi.poo.exception.StockInsuficienteException;
import ar.edu.unju.fi.poo.exception.VentaException;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.Cuota;
import ar.edu.unju.fi.poo.model.Cupon;
import ar.edu.unju.fi.poo.model.EstadoCuota;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.model.VentaContado;
import ar.edu.unju.fi.poo.model.VentaFinanciada;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.repository.VentaRepository;
import ar.edu.unju.fi.poo.service.VentaService;

@Service
@Transactional
public class VentaServiceImp implements VentaService {

	private static final String CONTADO = "CONTADO";
	private static final String FINANCIADA = "FINANCIADA";

	@Autowired
	private VentaRepository ventaRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private VentaMapper ventaMapper;

	@Override
	public VentaResponseDto registrarVenta(VentaRequestDto ventaDto) {
		String modalidad = validarModalidad(ventaDto);
		Map<Integer, Integer> productos = armarPedido(ventaDto.getItems());

		if (modalidad.equals(CONTADO)) {
			return convertirADto(
					registrarVentaContado(ventaDto.getClienteId(), productos, ventaDto.getCuponCodigo()));
		}
		return convertirADto(
				registrarVentaFinanciada(ventaDto.getClienteId(), productos, ventaDto.getPorcentajeRecargo()));
	}

	@Override
	@Transactional(readOnly = true)
	public VentaResponseDto buscarPorId(Integer id) {
		return convertirADto(buscarVenta(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<VentaResponseDto> buscarTodas() {
		return convertirLista(ventaRepository.findAllByOrderByFechaDescIdDesc());
	}

	@Override
	@Transactional(readOnly = true)
	public List<VentaResponseDto> buscarPorCliente(Integer clienteId) {
		List<Venta> ventas = new ArrayList<>(buscarCliente(clienteId).getVentas());
		ventas.sort(Comparator.comparing(Venta::getFecha).thenComparing(Venta::getId).reversed());
		return convertirLista(ventas);
	}

	@Override
	@Transactional(readOnly = true)
	public EstadoCuentaResponseDto obtenerEstadoDeCuenta(Integer clienteId) {
		Cliente cliente = buscarCliente(clienteId);
		List<Cuota> pendientes = buscarCuotasPendientes(cliente);

		EstadoCuentaResponseDto estadoCuenta = new EstadoCuentaResponseDto();
		estadoCuenta.setClienteId(cliente.getId());
		estadoCuenta.setClienteNombreCompleto(cliente.getNombreCompleto());
		estadoCuenta.setCuotas(ventaMapper.cuotasADto(pendientes));
		estadoCuenta.setCuotasPendientes(pendientes.size());
		estadoCuenta.setCuotasVencidas(contarVencidas(pendientes));
		estadoCuenta.setTotalAdeudado(sumarImportes(pendientes));
		return estadoCuenta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<VentaResponseDto> buscarPorRangoDeFechas(LocalDate desde, LocalDate hasta) {
		if (desde == null || hasta == null || desde.isAfter(hasta)) {
			throw new VentaException("El rango de fechas no es valido");
		}
		return convertirLista(ventaRepository.findByFechaBetweenOrderByFechaDesc(desde, hasta));
	}

	@Override
	@Transactional(readOnly = true)
	public List<VentaResponseDto> buscarPorModalidad(String modalidad) {
		if (convertirModalidad(modalidad).equals(CONTADO)) {
			return convertirLista(ventaRepository.buscarVentasContado());
		}
		return convertirLista(ventaRepository.buscarVentasFinanciadas());
	}

	@Override
	public CuotaResponseDto cambiarEstadoCuota(Integer ventaId, int numeroCuota, CuotaEstadoRequestDto estadoDto) {
		validarEstadoPagada(estadoDto);
		VentaFinanciada venta = buscarVentaFinanciada(ventaId);
		Cuota cuota = buscarCuota(venta, numeroCuota);

		cuota.pagar();
		ventaRepository.save(venta);
		return ventaMapper.cuotaADto(cuota);
	}

	private VentaContado registrarVentaContado(Integer clienteId, Map<Integer, Integer> productos, String cuponCodigo) {
		Cliente cliente = buscarClienteHabilitado(clienteId);
		Map<Producto, Integer> pedido = verificarStock(productos);
		Cupon cupon = buscarCupon(cuponCodigo);

		VentaContado venta = new VentaContado(cliente, generarCodigo("CON"), LocalDate.now());
		agregarProductos(venta, pedido);
		if (cupon != null) {
			venta.setCupon(cupon);
			cupon.setUsado(true);
		}
		venta.setSubtotal(venta.calcularSubtotal());
		venta.setTotal(venta.calcularTotal());
		ventaRepository.save(venta);
		cliente.agregarVenta(venta);

		avisarStockCritico(pedido);
		return venta;
	}

	private VentaFinanciada registrarVentaFinanciada(Integer clienteId, Map<Integer, Integer> productos,
			double porcentajeRecargo) {
		if (porcentajeRecargo < 0 || porcentajeRecargo > 100) {
			throw new ImporteInvalidoException("El porcentaje de recargo tiene que estar entre 0 y 100");
		}
		Cliente cliente = buscarClienteHabilitado(clienteId);
		Map<Producto, Integer> pedido = verificarStock(productos);

		VentaFinanciada venta = new VentaFinanciada(cliente, generarCodigo("FIN"), LocalDate.now(), porcentajeRecargo);
		agregarProductos(venta, pedido);
		venta.setSubtotal(venta.calcularSubtotal());
		venta.setTotal(venta.calcularTotal());
		venta.generarCuotas();
		ventaRepository.save(venta);
		cliente.agregarVenta(venta);

		avisarStockCritico(pedido);
		return venta;
	}

	// el cupon es solo para contado y el recargo solo para financiada
	private String validarModalidad(VentaRequestDto ventaDto) {
		if (ventaDto == null) {
			throw new VentaException("Debe indicar los datos de la venta");
		}
		String modalidad = convertirModalidad(ventaDto.getModalidad());
		boolean tieneCupon = ventaDto.getCuponCodigo() != null && !ventaDto.getCuponCodigo().isBlank();

		if (modalidad.equals(CONTADO) && ventaDto.getPorcentajeRecargo() != null) {
			throw new VentaException("La venta de contado no lleva recargo");
		}
		if (modalidad.equals(FINANCIADA) && tieneCupon) {
			throw new VentaException("El cupon de descuento solo se puede usar en ventas de contado");
		}
		if (modalidad.equals(FINANCIADA) && ventaDto.getPorcentajeRecargo() == null) {
			throw new VentaException("Debe indicar el porcentaje de recargo de la venta financiada");
		}
		return modalidad;
	}

	private String convertirModalidad(String modalidad) {
		if (modalidad == null || modalidad.isBlank()) {
			throw new VentaException("Debe indicar la modalidad de la venta");
		}
		String valor = modalidad.trim().toUpperCase();
		if (!valor.equals(CONTADO) && !valor.equals(FINANCIADA)) {
			throw new VentaException("La modalidad tiene que ser CONTADO o FINANCIADA");
		}
		return valor;
	}

	private Venta buscarVenta(Integer id) {
		if (id == null) {
			throw new VentaException("Debe indicar la venta");
		}
		return ventaRepository.findById(id).orElseThrow(() -> new NoEncontradoException("No existe la venta con id " + id));
	}

	private VentaFinanciada buscarVentaFinanciada(Integer ventaId) {
		Venta venta = buscarVenta(ventaId);
		if (!(venta instanceof VentaFinanciada)) {
			throw new VentaException("La venta " + venta.getCodigoComprobante() + " es de contado, no tiene cuotas");
		}
		return (VentaFinanciada) venta;
	}

	private Cliente buscarCliente(Integer clienteId) {
		if (clienteId == null) {
			throw new VentaException("Debe indicar el cliente");
		}
		return clienteRepository.findById(clienteId)
				.orElseThrow(() -> new NoEncontradoException("No existe el cliente con id " + clienteId));
	}

	private Cliente buscarClienteHabilitado(Integer clienteId) {
		Cliente cliente = buscarCliente(clienteId);
		if (!cliente.isHabilitado()) {
			throw new ClienteInhabilitadoException(
					"El cliente " + cliente.getNombreCompleto() + " esta inhabilitado y no puede comprar");
		}
		return cliente;
	}

	private Map<Integer, Integer> armarPedido(List<ItemVentaRequestDto> items) {
		Map<Integer, Integer> productos = new LinkedHashMap<>();
		if (items == null) {
			return productos;
		}
		for (ItemVentaRequestDto item : items) {
			validarItem(item);
			// si el mismo producto viene dos veces se suman las cantidades
			productos.merge(item.getProductoId(), item.getCantidad(), Integer::sum);
		}
		return productos;
	}

	private void validarItem(ItemVentaRequestDto item) {
		if (item == null || item.getProductoId() == null) {
			throw new VentaException("Falta el id de un producto");
		}
		if (item.getCantidad() == null || item.getCantidad() <= 0) {
			throw new VentaException("La cantidad tiene que ser mayor a 0");
		}
	}

	// se revisa todo el pedido antes de tocar el stock
	private Map<Producto, Integer> verificarStock(Map<Integer, Integer> productos) {
		if (productos.isEmpty()) {
			throw new VentaException("La venta tiene que tener al menos un producto");
		}
		Map<Producto, Integer> pedido = new LinkedHashMap<>();
		for (Integer id : productos.keySet()) {
			pedido.put(buscarProductoConStock(id, productos.get(id)), productos.get(id));
		}
		return pedido;
	}

	private Producto buscarProductoConStock(Integer id, int cantidad) {
		Producto producto = productoRepository.findById(id)
				.orElseThrow(() -> new NoEncontradoException("No existe el producto con id " + id));
		if (!producto.hayStock(cantidad)) {
			throw new StockInsuficienteException("No hay stock suficiente de " + producto.getNombre());
		}
		return producto;
	}

	private void agregarProductos(Venta venta, Map<Producto, Integer> pedido) {
		for (Producto producto : pedido.keySet()) {
			int cantidad = pedido.get(producto);
			venta.agregarDetalle(producto, cantidad);
			producto.descontarStock(cantidad);
			productoRepository.save(producto);
		}
	}

	private Cupon buscarCupon(String codigo) {
		if (codigo == null || codigo.isBlank()) {
			return null;
		}
		Cupon cupon = ventaRepository.buscarCuponPorCodigo(codigo)
				.orElseThrow(() -> new CuponInvalidoException("No existe el cupon " + codigo));
		if (!cupon.esValido(LocalDate.now())) {
			throw new CuponInvalidoException("El cupon " + codigo + " esta vencido o ya se uso");
		}
		return cupon;
	}

	private void avisarStockCritico(Map<Producto, Integer> pedido) {
		for (Producto producto : pedido.keySet()) {
			if (producto.tieneStockCritico()) {
				System.out.println(producto.generarAlertaStock());
			}
		}
	}

	private String generarCodigo(String prefijo) {
		return prefijo + "-" + (ventaRepository.count() + 1);
	}

	// por ahora el unico cambio permitido es pasar la cuota a PAGADA
	private void validarEstadoPagada(CuotaEstadoRequestDto estadoDto) {
		if (estadoDto == null || estadoDto.getEstado() == null
				|| !estadoDto.getEstado().trim().equalsIgnoreCase(EstadoCuota.PAGADA.name())) {
			throw new VentaException("Solo se puede cambiar el estado de una cuota a PAGADA");
		}
	}

	private Cuota buscarCuota(VentaFinanciada venta, int numeroCuota) {
		for (Cuota cuota : venta.getCuotas()) {
			if (cuota.getNumero() == numeroCuota) {
				return cuota;
			}
		}
		throw new NoEncontradoException(
				"La venta " + venta.getCodigoComprobante() + " no tiene la cuota numero " + numeroCuota);
	}

	// cuotas adeudadas de todas las ventas financiadas del cliente, primero la que vence antes
	private List<Cuota> buscarCuotasPendientes(Cliente cliente) {
		List<Cuota> pendientes = new ArrayList<>();
		for (Venta venta : cliente.getVentas()) {
			if (venta instanceof VentaFinanciada) {
				pendientes.addAll(((VentaFinanciada) venta).getCuotas().stream()
						.filter(cuota -> cuota.getEstado() == EstadoCuota.ADEUDADA).toList());
			}
		}
		pendientes.sort(Comparator.comparing(Cuota::getFechaVencimiento));
		return pendientes;
	}

	private int contarVencidas(List<Cuota> cuotas) {
		return (int) cuotas.stream().filter(cuota -> cuota.getFechaVencimiento().isBefore(LocalDate.now())).count();
	}

	private double sumarImportes(List<Cuota> cuotas) {
		double total = 0;
		for (Cuota cuota : cuotas) {
			total = total + cuota.getImporte();
		}
		return Math.round(total * 100.0) / 100.0;
	}

	private VentaResponseDto convertirADto(Venta venta) {
		if (venta instanceof VentaContado) {
			return ventaMapper.ventaContadoADto((VentaContado) venta);
		}
		return ventaMapper.ventaFinanciadaADto((VentaFinanciada) venta);
	}

	private List<VentaResponseDto> convertirLista(List<? extends Venta> ventas) {
		return ventas.stream().map(this::convertirADto).toList();
	}
}
