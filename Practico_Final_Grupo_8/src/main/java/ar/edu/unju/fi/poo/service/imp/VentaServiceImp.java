package ar.edu.unju.fi.poo.service.imp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.exception.CuponInvalidoException;
import ar.edu.unju.fi.poo.exception.ProductoException;
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

	@Autowired
	private VentaRepository ventaRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ProductoRepository productoRepository;

	@Override
	public VentaContado registrarVentaContado(Integer clienteId, Map<Integer, Integer> productos, String codigoCupon) {
		Cliente cliente = buscarCliente(clienteId);
		Map<Producto, Integer> pedido = verificarStock(productos);
		Cupon cupon = buscarCupon(codigoCupon);

		VentaContado venta = new VentaContado(cliente, generarCodigo("CON"), LocalDate.now());
		agregarProductos(venta, pedido);
		if (cupon != null) {
			venta.setCupon(cupon);
			cupon.setUsado(true);
		}
		venta.setSubtotal(venta.calcularSubtotal());
		venta.setTotal(venta.calcularTotal());
		ventaRepository.save(venta);

		avisarStockCritico(pedido);
		return venta;
	}

	@Override
	public VentaFinanciada registrarVentaFinanciada(Integer clienteId, Map<Integer, Integer> productos,
			double porcentajeRecargo) {
		if (porcentajeRecargo < 0) {
			throw new VentaException("El recargo no puede ser negativo");
		}
		Cliente cliente = buscarCliente(clienteId);
		Map<Producto, Integer> pedido = verificarStock(productos);

		VentaFinanciada venta = new VentaFinanciada(cliente, generarCodigo("FIN"), LocalDate.now(), porcentajeRecargo);
		agregarProductos(venta, pedido);
		venta.setSubtotal(venta.calcularSubtotal());
		venta.setTotal(venta.calcularTotal());
		venta.generarCuotas();
		ventaRepository.save(venta);

		avisarStockCritico(pedido);
		return venta;
	}

	@Override
	public List<Venta> buscarPorCliente(Integer clienteId) {
		buscarCliente(clienteId);
		return ventaRepository.findByClienteIdOrderByFechaDesc(clienteId);
	}

	@Override
	public List<Cuota> buscarCuotasPendientes(Integer clienteId) {
		buscarCliente(clienteId);
		List<Cuota> pendientes = new ArrayList<>();
		for (VentaFinanciada venta : ventaRepository.buscarVentasFinanciadasPorCliente(clienteId)) {
			for (Cuota cuota : venta.getCuotas()) {
				if (cuota.getEstado() == EstadoCuota.ADEUDADA) {
					pendientes.add(cuota);
				}
			}
		}
		pendientes.sort(Comparator.comparing(Cuota::getFechaVencimiento));
		return pendientes;
	}

	@Override
	public List<Venta> buscarPorRangoDeFechas(LocalDate desde, LocalDate hasta) {
		if (desde == null || hasta == null || desde.isAfter(hasta)) {
			throw new VentaException("El rango de fechas no es valido");
		}
		return ventaRepository.findByFechaBetweenOrderByFechaDesc(desde, hasta);
	}

	@Override
	public List<VentaContado> buscarVentasContado() {
		return ventaRepository.buscarVentasContado();
	}

	@Override
	public List<VentaFinanciada> buscarVentasFinanciadas() {
		return ventaRepository.buscarVentasFinanciadas();
	}

	@Override
	public Cuota pagarCuota(Integer ventaId, int numeroCuota) {
		Venta venta = ventaRepository.findById(ventaId)
				.orElseThrow(() -> new VentaException("No existe la venta con id " + ventaId));
		if (!(venta instanceof VentaFinanciada)) {
			throw new VentaException("La venta " + venta.getCodigoComprobante() + " es de contado, no tiene cuotas");
		}
		VentaFinanciada financiada = (VentaFinanciada) venta;
		for (Cuota cuota : financiada.getCuotas()) {
			if (cuota.getNumero() == numeroCuota) {
				cuota.pagar();
				ventaRepository.save(financiada);
				return cuota;
			}
		}
		throw new VentaException("La venta no tiene la cuota numero " + numeroCuota);
	}

	private Cliente buscarCliente(Integer clienteId) {
		return clienteRepository.findById(clienteId)
				.orElseThrow(() -> new ClienteException("No existe el cliente con id " + clienteId));
	}

	// se revisa todo el pedido antes de tocar el stock
	private Map<Producto, Integer> verificarStock(Map<Integer, Integer> productos) {
		if (productos == null || productos.isEmpty()) {
			throw new VentaException("La venta tiene que tener al menos un producto");
		}
		Map<Producto, Integer> pedido = new HashMap<>();
		for (Integer id : productos.keySet()) {
			Integer cantidad = productos.get(id);
			if (cantidad == null || cantidad <= 0) {
				throw new VentaException("La cantidad tiene que ser mayor a 0");
			}
			Producto producto = productoRepository.findById(id)
					.orElseThrow(() -> new ProductoException("No existe el producto con id " + id));
			if (!producto.hayStock(cantidad)) {
				throw new StockInsuficienteException("No hay stock suficiente de " + producto.getNombre());
			}
			pedido.put(producto, cantidad);
		}
		return pedido;
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
}
