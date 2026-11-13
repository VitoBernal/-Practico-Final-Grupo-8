package ar.edu.unju.fi.poo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.poo.dto.*;
import ar.edu.unju.fi.poo.exception.ClienteInhabilitadoException;
import ar.edu.unju.fi.poo.exception.ConflictoException;
import ar.edu.unju.fi.poo.exception.CuponInvalidoException;
import ar.edu.unju.fi.poo.exception.ImporteInvalidoException;
import ar.edu.unju.fi.poo.exception.NoEncontradoException;
import ar.edu.unju.fi.poo.exception.StockInsuficienteException;
import ar.edu.unju.fi.poo.exception.VentaException;

@SpringBootTest
@Transactional
@DisplayName("Pruebas de VentaService")
class VentaServiceTest {

	@Autowired
	VentaService ventaService;

	@Autowired
	ClienteService clienteService;

	@Autowired
	ProductoService productoService;

	Integer anaId;
	Integer norteId;
	Integer taladroId;
	Integer amoladoraId;
	Integer pinturaId;
	Integer rodilloId;

	@BeforeEach
	void setUp() {
		crearClientes();
		crearProductos();
	}

	private void crearClientes() {
		anaId = clienteService.guardarCliente(
				ClienteRequestDto.particular("30111222", "Ana", "Gomez", "3884111111", "ana.gomez@mail.com", "Belgrano 100"))
				.getId();
		norteId = clienteService.guardarCliente(ClienteRequestDto.empresa("30712345678", "Constructora Norte SA", "3884222222",
				"contacto@norte.com", "Ruta 34 km 5")).getId();
	}

	private int stockDe(Integer productoId) {
		return productoService.buscarPorId(productoId).getStock();
	}

	private void crearProductos() {
		taladroId = productoService.guardar(
				nuevoProducto("Taladro percutor", "Taladro 750W", "Bosch", "Herramientas", 10, 50000)).getId();
		amoladoraId = productoService.guardar(
				nuevoProducto("Amoladora", "Amoladora angular", "Makita", "Herramientas", 5, 80000)).getId();
		pinturaId = productoService.guardar(
				nuevoProducto("Pintura latex 20L", "Latex interior", "Alba", "Pintura", 3, 30000)).getId();
		rodilloId = productoService.guardar(
				nuevoProducto("Rodillo", "Rodillo de lana", "Alba", "Pintura", 20, 5000)).getId();
	}

	private ProductoRequestDto nuevoProducto(String nombre, String descripcion, String marca, String categoria,
			int stock, double precio) {
		ProductoRequestDto producto = new ProductoRequestDto();
		producto.setNombre(nombre);
		producto.setDescripcion(descripcion);
		producto.setMarca(marca);
		producto.setCategoria(categoria);
		producto.setStock(stock);
		producto.setPrecioBase(precio);
		return producto;
	}

	private ItemVentaRequestDto item(Integer productoId, int cantidad) {
		return new ItemVentaRequestDto(productoId, cantidad);
	}

	private VentaRequestDto contado(Integer clienteId, String cupon, ItemVentaRequestDto... items) {
		VentaRequestDto venta = new VentaRequestDto();
		venta.setClienteId(clienteId);
		venta.setModalidad("CONTADO");
		venta.setCuponCodigo(cupon);
		venta.setItems(List.of(items));
		return venta;
	}

	private VentaRequestDto financiada(Integer clienteId, double recargo, ItemVentaRequestDto... items) {
		VentaRequestDto venta = new VentaRequestDto();
		venta.setClienteId(clienteId);
		venta.setModalidad("FINANCIADA");
		venta.setPorcentajeRecargo(recargo);
		venta.setItems(List.of(items));
		return venta;
	}

	private CuotaEstadoRequestDto pagada() {
		return new CuotaEstadoRequestDto("PAGADA");
	}

	@Test
	@DisplayName("Venta de contado sin cupon")
	void ventaContadoSinCupon() {
		VentaResponseDto venta = ventaService
				.registrarVenta(contado(anaId, null, item(taladroId, 2), item(rodilloId, 1)));

		assertNotNull(venta.getId());
		assertTrue(venta.getCodigoComprobante().startsWith("CON-"));
		assertEquals("CONTADO", venta.getModalidad());
		assertEquals(LocalDate.now().toString(), venta.getFecha());
		assertEquals(anaId, venta.getClienteId());
		assertEquals("Ana Gomez", venta.getClienteNombreCompleto());
		assertEquals("30111222", venta.getClienteNumeroIdentificacion());
		assertEquals(2, venta.getDetalles().size());
		assertEquals(105000, venta.getSubtotal(), 0.01);
		assertEquals(0, venta.getMontoDescuento(), 0.01);
		assertEquals(105000, venta.getTotal(), 0.01);
		assertNull(venta.getCuponCodigo());
		assertNull(venta.getCuotas());
		assertEquals(8, stockDe(taladroId));
		assertEquals(19, stockDe(rodilloId));
	}

	@Test
	@DisplayName("El detalle muestra el producto, la cantidad y el precio aplicado")
	void detalleDeLaVenta() {
		VentaResponseDto venta = ventaService.registrarVenta(contado(anaId, null, item(taladroId, 2)));

		DetalleVentaResponseDto detalle = venta.getDetalles().get(0);
		assertEquals(taladroId, detalle.getProductoId());
		assertEquals("Taladro percutor", detalle.getProductoNombre());
		assertEquals(2, detalle.getCantidad());
		assertEquals(50000, detalle.getPrecioBaseAplicado(), 0.01);
		assertEquals(100000, detalle.getSubtotal(), 0.01);
	}

	@Test
	@DisplayName("Venta de contado con cupon valido")
	void ventaContadoConCupon() {
		VentaResponseDto venta = ventaService.registrarVenta(contado(anaId, "DESC10", item(amoladoraId, 1)));

		assertEquals(80000, venta.getSubtotal(), 0.01);
		assertEquals(8000, venta.getMontoDescuento(), 0.01);
		assertEquals(72000, venta.getTotal(), 0.01);
		assertEquals("DESC10", venta.getCuponCodigo());
	}

	@Test
	@DisplayName("Cupon vencido")
	void cuponVencido() {
		assertThrows(CuponInvalidoException.class,
				() -> ventaService.registrarVenta(contado(anaId, "VENC20", item(amoladoraId, 1))));
		assertEquals(5, stockDe(amoladoraId));
	}

	@Test
	@DisplayName("Cupon que no existe")
	void cuponInexistente() {
		assertThrows(CuponInvalidoException.class,
				() -> ventaService.registrarVenta(contado(anaId, "NOEXISTE", item(rodilloId, 1))));
	}

	@Test
	@DisplayName("Un cupon no se puede usar dos veces")
	void cuponYaUsado() {
		ventaService.registrarVenta(contado(anaId, "DESC10", item(rodilloId, 1)));
		assertThrows(CuponInvalidoException.class,
				() -> ventaService.registrarVenta(contado(norteId, "DESC10", item(rodilloId, 1))));
	}

	@Test
	@DisplayName("Sin stock suficiente no se hace la venta")
	void stockInsuficiente() {
		assertThrows(StockInsuficienteException.class,
				() -> ventaService.registrarVenta(contado(anaId, null, item(taladroId, 1), item(pinturaId, 4))));
		assertEquals(10, stockDe(taladroId));
		assertEquals(3, stockDe(pinturaId));
	}

	@Test
	@DisplayName("Cliente que no existe")
	void clienteInexistente() {
		assertThrows(NoEncontradoException.class,
				() -> ventaService.registrarVenta(contado(-1, null, item(rodilloId, 1))));
	}

	@Test
	@DisplayName("Un cliente inhabilitado no puede comprar")
	void clienteInhabilitado() {
		clienteService.eliminarCliente(anaId);

		assertThrows(ClienteInhabilitadoException.class,
				() -> ventaService.registrarVenta(contado(anaId, null, item(rodilloId, 1))));
		assertEquals(20, stockDe(rodilloId));
	}

	@Test
	@DisplayName("Producto que no existe")
	void productoInexistente() {
		assertThrows(NoEncontradoException.class,
				() -> ventaService.registrarVenta(contado(anaId, null, item(-1, 1))));
	}

	@Test
	@DisplayName("Venta sin productos")
	void ventaSinProductos() {
		assertThrows(VentaException.class, () -> ventaService.registrarVenta(contado(anaId, null)));
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "CUOTAS", "efectivo" })
	@DisplayName("Venta sin modalidad o con una modalidad que no existe")
	void ventaConModalidadInvalida(String modalidad) {
		VentaRequestDto venta = contado(anaId, null, item(rodilloId, 1));
		venta.setModalidad(modalidad);
		assertThrows(VentaException.class, () -> ventaService.registrarVenta(venta));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, -1, -10 })
	@DisplayName("Cantidad menor o igual a 0")
	void cantidadInvalida(int cantidad) {
		assertThrows(VentaException.class,
				() -> ventaService.registrarVenta(contado(anaId, null, item(rodilloId, cantidad))));
	}

	@Test
	@DisplayName("Si un producto viene dos veces se suman las cantidades")
	void productoRepetido() {
		VentaResponseDto venta = ventaService
				.registrarVenta(contado(anaId, null, item(rodilloId, 1), item(rodilloId, 2)));

		assertEquals(1, venta.getDetalles().size());
		assertEquals(3, venta.getDetalles().get(0).getCantidad());
		assertEquals(17, stockDe(rodilloId));
	}

	@Test
	@DisplayName("La venta deja el producto en stock critico")
	void ventaDejaStockCritico() {
		assertFalse(productoService.buscarConStockCritico().stream().anyMatch(p -> p.getId().equals(taladroId)));

		ventaService.registrarVenta(contado(anaId, null, item(taladroId, 5)));

		assertTrue(productoService.buscarConStockCritico().stream().anyMatch(p -> p.getId().equals(taladroId)));
		assertTrue(productoService.obtenerAlertasDeStock().stream()
				.anyMatch(alerta -> alerta.contains("Taladro percutor tiene 5 unidades")));
	}

	@Test
	@DisplayName("Venta financiada con recargo y 6 cuotas")
	void ventaFinanciada() {
		VentaResponseDto venta = ventaService
				.registrarVenta(financiada(norteId, 20, item(amoladoraId, 1), item(pinturaId, 2)));

		assertTrue(venta.getCodigoComprobante().startsWith("FIN-"));
		assertEquals("FINANCIADA", venta.getModalidad());
		assertEquals("Constructora Norte SA", venta.getClienteNombreCompleto());
		assertEquals(140000, venta.getSubtotal(), 0.01);
		assertEquals(20, venta.getPorcentajeRecargo(), 0.01);
		assertEquals(28000, venta.getMontoRecargo(), 0.01);
		assertEquals(168000, venta.getTotal(), 0.01);
		assertNull(venta.getMontoDescuento());
		assertEquals(6, venta.getCuotas().size());
		LocalDate fecha = LocalDate.parse(venta.getFecha());
		for (CuotaResponseDto cuota : venta.getCuotas()) {
			assertEquals(28000, cuota.getImporte(), 0.01);
			assertEquals("ADEUDADA", cuota.getEstado());
			assertEquals(fecha.plusMonths(cuota.getNumero()).toString(), cuota.getFechaVencimiento());
		}
		assertEquals(4, stockDe(amoladoraId));
		assertEquals(1, stockDe(pinturaId));
	}

	@Test
	@DisplayName("La suma de las cuotas da el total")
	void cuotasSumanElTotal() {
		VentaResponseDto venta = ventaService.registrarVenta(financiada(anaId, 0, item(rodilloId, 1)));

		double suma = 0;
		for (CuotaResponseDto cuota : venta.getCuotas()) {
			suma = suma + cuota.getImporte();
		}
		assertEquals(5000, suma, 0.01);
		assertEquals(833.33, venta.getCuotas().get(0).getImporte(), 0.01);
		assertEquals(833.35, venta.getCuotas().get(5).getImporte(), 0.01);
	}

	@ParameterizedTest
	@ValueSource(doubles = { -5, 101 })
	@DisplayName("Recargo negativo o mayor a 100")
	void recargoInvalido(double recargo) {
		assertThrows(ImporteInvalidoException.class,
				() -> ventaService.registrarVenta(financiada(anaId, recargo, item(rodilloId, 1))));
	}

	@Test
	@DisplayName("Venta financiada sin recargo")
	void financiadaSinRecargo() {
		VentaRequestDto venta = financiada(anaId, 20, item(rodilloId, 1));
		venta.setPorcentajeRecargo(null);
		assertThrows(VentaException.class, () -> ventaService.registrarVenta(venta));
	}

	@Test
	@DisplayName("El cupon solo vale para contado y el recargo solo para financiada")
	void cuponYRecargoEnModalidadEquivocada() {
		VentaRequestDto financiadaConCupon = financiada(anaId, 20, item(rodilloId, 1));
		financiadaConCupon.setCuponCodigo("DESC10");
		VentaRequestDto contadoConRecargo = contado(anaId, null, item(rodilloId, 1));
		contadoConRecargo.setPorcentajeRecargo(20.0);

		assertThrows(VentaException.class, () -> ventaService.registrarVenta(financiadaConCupon));
		assertThrows(VentaException.class, () -> ventaService.registrarVenta(contadoConRecargo));
		assertEquals(20, stockDe(rodilloId));
	}

	@Test
	@DisplayName("Venta financiada sin stock suficiente")
	void financiadaSinStock() {
		assertThrows(StockInsuficienteException.class,
				() -> ventaService.registrarVenta(financiada(norteId, 20, item(amoladoraId, 6))));
		assertEquals(5, stockDe(amoladoraId));
	}

	@Test
	@DisplayName("Cada venta tiene un codigo distinto")
	void codigoComprobanteDistinto() {
		VentaResponseDto primera = ventaService.registrarVenta(contado(anaId, null, item(rodilloId, 1)));
		VentaResponseDto segunda = ventaService.registrarVenta(contado(anaId, null, item(rodilloId, 1)));
		assertNotEquals(primera.getCodigoComprobante(), segunda.getCodigoComprobante());
	}

	@Test
	@DisplayName("Buscar una venta por id")
	void buscarPorId() {
		VentaResponseDto venta = ventaService.registrarVenta(financiada(norteId, 20, item(taladroId, 1)));

		VentaResponseDto encontrada = ventaService.buscarPorId(venta.getId());
		assertEquals(venta.getCodigoComprobante(), encontrada.getCodigoComprobante());
		assertEquals(1, encontrada.getDetalles().size());
		assertEquals(6, encontrada.getCuotas().size());
	}

	@Test
	@DisplayName("Buscar una venta que no existe")
	void buscarPorIdInexistente() {
		assertThrows(NoEncontradoException.class, () -> ventaService.buscarPorId(-1));
	}

	@Test
	@DisplayName("Listar todas las ventas")
	void buscarTodas() {
		ventaService.registrarVenta(contado(anaId, null, item(rodilloId, 1)));
		ventaService.registrarVenta(financiada(norteId, 20, item(taladroId, 1)));

		assertEquals(2, ventaService.buscarTodas().size());
	}

	@Test
	@DisplayName("Historial de ventas de un cliente")
	void buscarPorCliente() {
		ventaService.registrarVenta(contado(anaId, null, item(rodilloId, 1)));
		ventaService.registrarVenta(financiada(anaId, 20, item(taladroId, 1)));

		List<VentaResponseDto> ventas = ventaService.buscarPorCliente(anaId);
		assertEquals(2, ventas.size());
		assertEquals(anaId, ventas.get(0).getClienteId());
		assertTrue(ventaService.buscarPorCliente(norteId).isEmpty());
	}

	@Test
	@DisplayName("Historial de un cliente que no existe")
	void buscarPorClienteInexistente() {
		assertThrows(NoEncontradoException.class, () -> ventaService.buscarPorCliente(-1));
	}

	@Test
	@DisplayName("Estado de cuenta: cuotas pendientes y total adeudado")
	void estadoDeCuenta() {
		VentaResponseDto venta = ventaService.registrarVenta(financiada(norteId, 20, item(taladroId, 1)));
		EstadoCuentaResponseDto estado = ventaService.obtenerEstadoDeCuenta(norteId);
		assertEquals(6, estado.getCuotasPendientes());
		assertEquals(60000, estado.getTotalAdeudado(), 0.01);
		assertEquals("Constructora Norte SA", estado.getClienteNombreCompleto());

		ventaService.cambiarEstadoCuota(venta.getId(), 1, pagada());
		ventaService.cambiarEstadoCuota(venta.getId(), 2, pagada());

		estado = ventaService.obtenerEstadoDeCuenta(norteId);
		assertEquals(4, estado.getCuotasPendientes());
		assertEquals(0, estado.getCuotasVencidas());
		assertEquals(40000, estado.getTotalAdeudado(), 0.01);
		assertEquals(3, estado.getCuotas().get(0).getNumero());
		assertEquals(venta.getId(), estado.getCuotas().get(0).getVentaId());
		assertEquals(venta.getCodigoComprobante(), estado.getCuotas().get(0).getVentaCodigoComprobante());

		EstadoCuentaResponseDto estadoAna = ventaService.obtenerEstadoDeCuenta(anaId);
		assertTrue(estadoAna.getCuotas().isEmpty());
		assertEquals(0, estadoAna.getTotalAdeudado(), 0.01);
	}

	@Test
	@DisplayName("Estado de cuenta de un cliente que no existe")
	void estadoDeCuentaClienteInexistente() {
		assertThrows(NoEncontradoException.class, () -> ventaService.obtenerEstadoDeCuenta(-1));
	}

	@Test
	@DisplayName("Ventas entre dos fechas")
	void buscarPorRangoDeFechas() {
		ventaService.registrarVenta(contado(anaId, null, item(rodilloId, 1)));
		ventaService.registrarVenta(financiada(norteId, 20, item(taladroId, 1)));
		LocalDate hoy = LocalDate.now();

		assertEquals(2, ventaService.buscarPorRangoDeFechas(hoy.minusDays(7), hoy).size());
		assertTrue(ventaService.buscarPorRangoDeFechas(hoy.minusDays(30), hoy.minusDays(1)).isEmpty());
	}

	@Test
	@DisplayName("Rango de fechas al reves")
	void buscarPorRangoDeFechasInvalido() {
		LocalDate hoy = LocalDate.now();
		assertThrows(VentaException.class, () -> ventaService.buscarPorRangoDeFechas(hoy, hoy.minusDays(1)));
	}

	@Test
	@DisplayName("Filtrar ventas de contado y financiadas")
	void filtrarPorModalidad() {
		ventaService.registrarVenta(contado(anaId, null, item(rodilloId, 1)));
		ventaService.registrarVenta(contado(norteId, null, item(rodilloId, 2)));
		ventaService.registrarVenta(financiada(norteId, 20, item(taladroId, 1)));

		List<VentaResponseDto> contado = ventaService.buscarPorModalidad("CONTADO");
		List<VentaResponseDto> financiadas = ventaService.buscarPorModalidad("financiada");
		assertEquals(2, contado.size());
		assertEquals(1, financiadas.size());
		assertEquals(6, financiadas.get(0).getCuotas().size());
	}

	@Test
	@DisplayName("Filtrar por una modalidad que no existe")
	void filtrarPorModalidadInvalida() {
		assertThrows(VentaException.class, () -> ventaService.buscarPorModalidad("CUOTAS"));
	}

	@Test
	@DisplayName("Pagar una cuota")
	void pagarCuota() {
		VentaResponseDto venta = ventaService.registrarVenta(financiada(anaId, 20, item(rodilloId, 1)));
		CuotaResponseDto cuota = ventaService.cambiarEstadoCuota(venta.getId(), 1, pagada());

		assertEquals(1, cuota.getNumero());
		assertEquals("PAGADA", cuota.getEstado());
		assertEquals(venta.getId(), cuota.getVentaId());
		assertEquals(5, ventaService.obtenerEstadoDeCuenta(anaId).getCuotasPendientes());
	}

	@Test
	@DisplayName("Una cuota solo se puede pasar a PAGADA")
	void cambiarCuotaAOtroEstado() {
		VentaResponseDto venta = ventaService.registrarVenta(financiada(anaId, 20, item(rodilloId, 1)));
		assertThrows(VentaException.class,
				() -> ventaService.cambiarEstadoCuota(venta.getId(), 1, new CuotaEstadoRequestDto("ADEUDADA")));
	}

	@Test
	@DisplayName("Pagar dos veces la misma cuota")
	void pagarCuotaDosVeces() {
		VentaResponseDto venta = ventaService.registrarVenta(financiada(anaId, 20, item(rodilloId, 1)));
		ventaService.cambiarEstadoCuota(venta.getId(), 1, pagada());
		assertThrows(ConflictoException.class, () -> ventaService.cambiarEstadoCuota(venta.getId(), 1, pagada()));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 7, -1 })
	@DisplayName("Numero de cuota que no existe")
	void pagarCuotaInexistente(int numero) {
		VentaResponseDto venta = ventaService.registrarVenta(financiada(anaId, 20, item(rodilloId, 1)));
		assertThrows(NoEncontradoException.class,
				() -> ventaService.cambiarEstadoCuota(venta.getId(), numero, pagada()));
	}

	@Test
	@DisplayName("Pagar cuota de una venta de contado")
	void pagarCuotaVentaContado() {
		VentaResponseDto venta = ventaService.registrarVenta(contado(anaId, null, item(rodilloId, 1)));
		assertThrows(VentaException.class, () -> ventaService.cambiarEstadoCuota(venta.getId(), 1, pagada()));
	}

	@Test
	@DisplayName("Pagar cuota de una venta que no existe")
	void pagarCuotaVentaInexistente() {
		assertThrows(NoEncontradoException.class, () -> ventaService.cambiarEstadoCuota(-1, 1, pagada()));
	}
}
