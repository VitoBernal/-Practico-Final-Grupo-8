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
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.poo.dto.*;
import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.exception.CuponInvalidoException;
import ar.edu.unju.fi.poo.exception.ProductoException;
import ar.edu.unju.fi.poo.exception.StockInsuficienteException;
import ar.edu.unju.fi.poo.exception.VentaException;
import ar.edu.unju.fi.poo.model.*;

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
				Cliente.particular("30111222", "Ana", "Gomez", "3884111111", "ana.gomez@mail.com", "Belgrano 100")).getId();
		norteId = clienteService.guardarCliente(Cliente.empresa("30712345678", "Constructora Norte SA", "3884222222",
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

	@Test
	@DisplayName("Venta de contado sin cupon")
	void ventaContadoSinCupon() {
		VentaContado venta = ventaService.registrarVentaContado(anaId,
				Map.of(taladroId, 2, rodilloId, 1), null);

		assertNotNull(venta.getId());
		assertTrue(venta.getCodigoComprobante().startsWith("CON-"));
		assertEquals(2, venta.getDetalles().size());
		assertEquals(105000, venta.getSubtotal(), 0.01);
		assertEquals(0, venta.getMontoDescuento(), 0.01);
		assertEquals(105000, venta.getTotal(), 0.01);
		assertNull(venta.getCupon());
		assertEquals(8, stockDe(taladroId));
		assertEquals(19, stockDe(rodilloId));
	}

	@Test
	@DisplayName("Venta de contado con cupon valido")
	void ventaContadoConCupon() {
		VentaContado venta = ventaService.registrarVentaContado(anaId, Map.of(amoladoraId, 1), "DESC10");

		assertEquals(80000, venta.getSubtotal(), 0.01);
		assertEquals(8000, venta.getMontoDescuento(), 0.01);
		assertEquals(72000, venta.getTotal(), 0.01);
		assertTrue(venta.getCupon().isUsado());
	}

	@Test
	@DisplayName("Cupon vencido")
	void cuponVencido() {
		assertThrows(CuponInvalidoException.class,
				() -> ventaService.registrarVentaContado(anaId, Map.of(amoladoraId, 1), "VENC20"));
		assertEquals(5, stockDe(amoladoraId));
	}

	@Test
	@DisplayName("Cupon que no existe")
	void cuponInexistente() {
		assertThrows(CuponInvalidoException.class,
				() -> ventaService.registrarVentaContado(anaId, Map.of(rodilloId, 1), "NOEXISTE"));
	}

	@Test
	@DisplayName("Un cupon no se puede usar dos veces")
	void cuponYaUsado() {
		ventaService.registrarVentaContado(anaId, Map.of(rodilloId, 1), "DESC10");
		assertThrows(CuponInvalidoException.class,
				() -> ventaService.registrarVentaContado(norteId, Map.of(rodilloId, 1), "DESC10"));
	}

	@Test
	@DisplayName("Sin stock suficiente no se hace la venta")
	void stockInsuficiente() {
		assertThrows(StockInsuficienteException.class, () -> ventaService.registrarVentaContado(anaId,
				Map.of(taladroId, 1, pinturaId, 4), null));
		assertEquals(10, stockDe(taladroId));
		assertEquals(3, stockDe(pinturaId));
	}

	@Test
	@DisplayName("Cliente que no existe")
	void clienteInexistente() {
		assertThrows(ClienteException.class,
				() -> ventaService.registrarVentaContado(-1, Map.of(rodilloId, 1), null));
	}

	@Test
	@DisplayName("Producto que no existe")
	void productoInexistente() {
		assertThrows(ProductoException.class,
				() -> ventaService.registrarVentaContado(anaId, Map.of(-1, 1), null));
	}

	@Test
	@DisplayName("Venta sin productos")
	void ventaSinProductos() {
		assertThrows(VentaException.class, () -> ventaService.registrarVentaContado(anaId, Map.of(), null));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, -1, -10 })
	@DisplayName("Cantidad menor o igual a 0")
	void cantidadInvalida(int cantidad) {
		assertThrows(VentaException.class,
				() -> ventaService.registrarVentaContado(anaId, Map.of(rodilloId, cantidad), null));
	}

	@Test
	@DisplayName("La venta deja el producto en stock critico")
	void ventaDejaStockCritico() {
		assertFalse(productoService.buscarConStockCritico().stream().anyMatch(p -> p.getId().equals(taladroId)));

		ventaService.registrarVentaContado(anaId, Map.of(taladroId, 5), null);

		assertTrue(productoService.buscarConStockCritico().stream().anyMatch(p -> p.getId().equals(taladroId)));
		assertTrue(productoService.obtenerAlertasDeStock().stream()
				.anyMatch(alerta -> alerta.contains("Taladro percutor tiene 5 unidades")));
	}

	@Test
	@DisplayName("Venta financiada con recargo y 6 cuotas")
	void ventaFinanciada() {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(norteId,
				Map.of(amoladoraId, 1, pinturaId, 2), 20);

		assertTrue(venta.getCodigoComprobante().startsWith("FIN-"));
		assertEquals(140000, venta.getSubtotal(), 0.01);
		assertEquals(28000, venta.getMontoRecargo(), 0.01);
		assertEquals(168000, venta.getTotal(), 0.01);
		assertEquals(6, venta.getCuotas().size());
		for (Cuota cuota : venta.getCuotas()) {
			assertEquals(28000, cuota.getImporte(), 0.01);
			assertEquals(EstadoCuota.ADEUDADA, cuota.getEstado());
			assertEquals(venta.getFecha().plusMonths(cuota.getNumero()), cuota.getFechaVencimiento());
		}
		assertEquals(4, stockDe(amoladoraId));
		assertEquals(1, stockDe(pinturaId));
	}

	@Test
	@DisplayName("La suma de las cuotas da el total")
	void cuotasSumanElTotal() {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(anaId, Map.of(rodilloId, 1), 0);

		double suma = 0;
		for (Cuota cuota : venta.getCuotas()) {
			suma = suma + cuota.getImporte();
		}
		assertEquals(5000, suma, 0.01);
		assertEquals(833.33, venta.getCuotas().get(0).getImporte(), 0.01);
		assertEquals(833.35, venta.getCuotas().get(5).getImporte(), 0.01);
	}

	@Test
	@DisplayName("Recargo negativo")
	void recargoNegativo() {
		assertThrows(VentaException.class,
				() -> ventaService.registrarVentaFinanciada(anaId, Map.of(rodilloId, 1), -5));
	}

	@Test
	@DisplayName("Venta financiada sin stock suficiente")
	void financiadaSinStock() {
		assertThrows(StockInsuficienteException.class,
				() -> ventaService.registrarVentaFinanciada(norteId, Map.of(amoladoraId, 6), 20));
		assertEquals(5, stockDe(amoladoraId));
	}

	@Test
	@DisplayName("Cada venta tiene un codigo distinto")
	void codigoComprobanteDistinto() {
		Venta primera = ventaService.registrarVentaContado(anaId, Map.of(rodilloId, 1), null);
		Venta segunda = ventaService.registrarVentaContado(anaId, Map.of(rodilloId, 1), null);
		assertNotEquals(primera.getCodigoComprobante(), segunda.getCodigoComprobante());
	}

	@Test
	@DisplayName("Historial de ventas de un cliente")
	void buscarPorCliente() {
		ventaService.registrarVentaContado(anaId, Map.of(rodilloId, 1), null);
		ventaService.registrarVentaFinanciada(anaId, Map.of(taladroId, 1), 20);

		List<Venta> ventas = ventaService.buscarPorCliente(anaId);
		assertEquals(2, ventas.size());
		assertEquals(anaId, ventas.get(0).getCliente().getId());
		assertTrue(ventaService.buscarPorCliente(norteId).isEmpty());
	}

	@Test
	@DisplayName("Historial de un cliente que no existe")
	void buscarPorClienteInexistente() {
		assertThrows(ClienteException.class, () -> ventaService.buscarPorCliente(-1));
	}

	@Test
	@DisplayName("Cuotas pendientes de un cliente")
	void buscarCuotasPendientes() {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(norteId, Map.of(taladroId, 1), 20);
		assertEquals(6, ventaService.buscarCuotasPendientes(norteId).size());

		ventaService.pagarCuota(venta.getId(), 1);
		ventaService.pagarCuota(venta.getId(), 2);

		List<Cuota> pendientes = ventaService.buscarCuotasPendientes(norteId);
		assertEquals(4, pendientes.size());
		assertEquals(3, pendientes.get(0).getNumero());
		assertTrue(ventaService.buscarCuotasPendientes(anaId).isEmpty());
	}

	@Test
	@DisplayName("Cuotas pendientes de un cliente que no existe")
	void buscarCuotasPendientesClienteInexistente() {
		assertThrows(ClienteException.class, () -> ventaService.buscarCuotasPendientes(-1));
	}

	@Test
	@DisplayName("Ventas entre dos fechas")
	void buscarPorRangoDeFechas() {
		ventaService.registrarVentaContado(anaId, Map.of(rodilloId, 1), null);
		ventaService.registrarVentaFinanciada(norteId, Map.of(taladroId, 1), 20);
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
		ventaService.registrarVentaContado(anaId, Map.of(rodilloId, 1), null);
		ventaService.registrarVentaContado(norteId, Map.of(rodilloId, 2), null);
		ventaService.registrarVentaFinanciada(norteId, Map.of(taladroId, 1), 20);

		List<VentaContado> contado = ventaService.buscarVentasContado();
		List<VentaFinanciada> financiadas = ventaService.buscarVentasFinanciadas();
		assertEquals(2, contado.size());
		assertEquals(1, financiadas.size());
		assertEquals(6, financiadas.get(0).getCuotas().size());
	}

	@Test
	@DisplayName("Pagar una cuota")
	void pagarCuota() {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(anaId, Map.of(rodilloId, 1), 20);
		Cuota cuota = ventaService.pagarCuota(venta.getId(), 1);

		assertEquals(1, cuota.getNumero());
		assertEquals(EstadoCuota.PAGADA, cuota.getEstado());
		assertEquals(EstadoCuota.ADEUDADA, venta.getCuotas().get(1).getEstado());
	}

	@Test
	@DisplayName("Pagar dos veces la misma cuota")
	void pagarCuotaDosVeces() {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(anaId, Map.of(rodilloId, 1), 20);
		ventaService.pagarCuota(venta.getId(), 1);
		assertThrows(VentaException.class, () -> ventaService.pagarCuota(venta.getId(), 1));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 7, -1 })
	@DisplayName("Numero de cuota que no existe")
	void pagarCuotaInexistente(int numero) {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(anaId, Map.of(rodilloId, 1), 20);
		assertThrows(VentaException.class, () -> ventaService.pagarCuota(venta.getId(), numero));
	}

	@Test
	@DisplayName("Pagar cuota de una venta de contado")
	void pagarCuotaVentaContado() {
		VentaContado venta = ventaService.registrarVentaContado(anaId, Map.of(rodilloId, 1), null);
		assertThrows(VentaException.class, () -> ventaService.pagarCuota(venta.getId(), 1));
	}

	@Test
	@DisplayName("Pagar cuota de una venta que no existe")
	void pagarCuotaVentaInexistente() {
		assertThrows(VentaException.class, () -> ventaService.pagarCuota(-1, 1));
	}
}
