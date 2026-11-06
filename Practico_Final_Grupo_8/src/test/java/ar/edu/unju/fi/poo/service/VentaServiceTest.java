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

import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.exception.CuponInvalidoException;
import ar.edu.unju.fi.poo.exception.ProductoException;
import ar.edu.unju.fi.poo.exception.StockInsuficienteException;
import ar.edu.unju.fi.poo.exception.VentaException;
import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.Cuota;
import ar.edu.unju.fi.poo.model.EstadoCuota;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.model.VentaContado;
import ar.edu.unju.fi.poo.model.VentaFinanciada;

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

	Cliente ana;
	Cliente norte;
	Producto taladro;
	Producto amoladora;
	Producto pintura;
	Producto rodillo;

	@BeforeEach
	void setUp() {
		ana = clienteService.guardarCliente(
				Cliente.particular("30111222", "Ana", "Gomez", "3884111111", "ana.gomez@mail.com", "Belgrano 100"));
		norte = clienteService.guardarCliente(Cliente.empresa("30712345678", "Constructora Norte SA", "3884222222",
				"contacto@norte.com", "Ruta 34 km 5"));
		Categoria herramientas = new Categoria("Herramientas");
		Categoria pinturas = new Categoria("Pintura");
		taladro = productoService.guardar(
				new Producto("Taladro percutor", "Taladro 750W", "Bosch", herramientas, 10, 50000));
		amoladora = productoService.guardar(
				new Producto("Amoladora", "Amoladora angular", "Makita", herramientas, 5, 80000));
		pintura = productoService.guardar(
				new Producto("Pintura latex 20L", "Latex interior", "Alba", pinturas, 3, 30000));
		rodillo = productoService.guardar(new Producto("Rodillo", "Rodillo de lana", "Alba", pinturas, 20, 5000));
	}

	private int stockDe(Producto producto) {
		return productoService.buscarPorId(producto.getId()).getStock();
	}

	@Test
	@DisplayName("Venta de contado sin cupon")
	void ventaContadoSinCupon() {
		VentaContado venta = ventaService.registrarVentaContado(ana.getId(),
				Map.of(taladro.getId(), 2, rodillo.getId(), 1), null);

		assertNotNull(venta.getId());
		assertTrue(venta.getCodigoComprobante().startsWith("CON-"));
		assertEquals(2, venta.getDetalles().size());
		assertEquals(105000, venta.getSubtotal(), 0.01);
		assertEquals(0, venta.getMontoDescuento(), 0.01);
		assertEquals(105000, venta.getTotal(), 0.01);
		assertNull(venta.getCupon());
		assertEquals(8, stockDe(taladro));
		assertEquals(19, stockDe(rodillo));
	}

	@Test
	@DisplayName("Venta de contado con cupon valido")
	void ventaContadoConCupon() {
		VentaContado venta = ventaService.registrarVentaContado(ana.getId(), Map.of(amoladora.getId(), 1), "DESC10");

		assertEquals(80000, venta.getSubtotal(), 0.01);
		assertEquals(8000, venta.getMontoDescuento(), 0.01);
		assertEquals(72000, venta.getTotal(), 0.01);
		assertTrue(venta.getCupon().isUsado());
	}

	@Test
	@DisplayName("Cupon vencido")
	void cuponVencido() {
		assertThrows(CuponInvalidoException.class,
				() -> ventaService.registrarVentaContado(ana.getId(), Map.of(amoladora.getId(), 1), "VENC20"));
		assertEquals(5, stockDe(amoladora));
	}

	@Test
	@DisplayName("Cupon que no existe")
	void cuponInexistente() {
		assertThrows(CuponInvalidoException.class,
				() -> ventaService.registrarVentaContado(ana.getId(), Map.of(rodillo.getId(), 1), "NOEXISTE"));
	}

	@Test
	@DisplayName("Un cupon no se puede usar dos veces")
	void cuponYaUsado() {
		ventaService.registrarVentaContado(ana.getId(), Map.of(rodillo.getId(), 1), "DESC10");
		assertThrows(CuponInvalidoException.class,
				() -> ventaService.registrarVentaContado(norte.getId(), Map.of(rodillo.getId(), 1), "DESC10"));
	}

	@Test
	@DisplayName("Sin stock suficiente no se hace la venta")
	void stockInsuficiente() {
		assertThrows(StockInsuficienteException.class, () -> ventaService.registrarVentaContado(ana.getId(),
				Map.of(taladro.getId(), 1, pintura.getId(), 4), null));
		assertEquals(10, stockDe(taladro));
		assertEquals(3, stockDe(pintura));
	}

	@Test
	@DisplayName("Cliente que no existe")
	void clienteInexistente() {
		assertThrows(ClienteException.class,
				() -> ventaService.registrarVentaContado(-1, Map.of(rodillo.getId(), 1), null));
	}

	@Test
	@DisplayName("Producto que no existe")
	void productoInexistente() {
		assertThrows(ProductoException.class,
				() -> ventaService.registrarVentaContado(ana.getId(), Map.of(-1, 1), null));
	}

	@Test
	@DisplayName("Venta sin productos")
	void ventaSinProductos() {
		assertThrows(VentaException.class, () -> ventaService.registrarVentaContado(ana.getId(), Map.of(), null));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, -1, -10 })
	@DisplayName("Cantidad menor o igual a 0")
	void cantidadInvalida(int cantidad) {
		assertThrows(VentaException.class,
				() -> ventaService.registrarVentaContado(ana.getId(), Map.of(rodillo.getId(), cantidad), null));
	}

	@Test
	@DisplayName("La venta deja el producto en stock critico")
	void ventaDejaStockCritico() {
		assertFalse(productoService.buscarPorId(taladro.getId()).tieneStockCritico());

		ventaService.registrarVentaContado(ana.getId(), Map.of(taladro.getId(), 5), null);

		Producto actualizado = productoService.buscarPorId(taladro.getId());
		assertTrue(actualizado.tieneStockCritico());
		assertTrue(actualizado.generarAlertaStock().contains("Taladro percutor tiene 5 unidades"));
	}

	@Test
	@DisplayName("Venta financiada con recargo y 6 cuotas")
	void ventaFinanciada() {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(norte.getId(),
				Map.of(amoladora.getId(), 1, pintura.getId(), 2), 20);

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
		assertEquals(4, stockDe(amoladora));
		assertEquals(1, stockDe(pintura));
	}

	@Test
	@DisplayName("La suma de las cuotas da el total")
	void cuotasSumanElTotal() {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(ana.getId(), Map.of(rodillo.getId(), 1), 0);

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
				() -> ventaService.registrarVentaFinanciada(ana.getId(), Map.of(rodillo.getId(), 1), -5));
	}

	@Test
	@DisplayName("Venta financiada sin stock suficiente")
	void financiadaSinStock() {
		assertThrows(StockInsuficienteException.class,
				() -> ventaService.registrarVentaFinanciada(norte.getId(), Map.of(amoladora.getId(), 6), 20));
		assertEquals(5, stockDe(amoladora));
	}

	@Test
	@DisplayName("Cada venta tiene un codigo distinto")
	void codigoComprobanteDistinto() {
		Venta primera = ventaService.registrarVentaContado(ana.getId(), Map.of(rodillo.getId(), 1), null);
		Venta segunda = ventaService.registrarVentaContado(ana.getId(), Map.of(rodillo.getId(), 1), null);
		assertNotEquals(primera.getCodigoComprobante(), segunda.getCodigoComprobante());
	}

	@Test
	@DisplayName("Historial de ventas de un cliente")
	void buscarPorCliente() {
		ventaService.registrarVentaContado(ana.getId(), Map.of(rodillo.getId(), 1), null);
		ventaService.registrarVentaFinanciada(ana.getId(), Map.of(taladro.getId(), 1), 20);

		List<Venta> ventas = ventaService.buscarPorCliente(ana.getId());
		assertEquals(2, ventas.size());
		assertEquals(ana.getId(), ventas.get(0).getCliente().getId());
		assertTrue(ventaService.buscarPorCliente(norte.getId()).isEmpty());
	}

	@Test
	@DisplayName("Historial de un cliente que no existe")
	void buscarPorClienteInexistente() {
		assertThrows(ClienteException.class, () -> ventaService.buscarPorCliente(-1));
	}

	@Test
	@DisplayName("Cuotas pendientes de un cliente")
	void buscarCuotasPendientes() {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(norte.getId(), Map.of(taladro.getId(), 1), 20);
		assertEquals(6, ventaService.buscarCuotasPendientes(norte.getId()).size());

		ventaService.pagarCuota(venta.getId(), 1);
		ventaService.pagarCuota(venta.getId(), 2);

		List<Cuota> pendientes = ventaService.buscarCuotasPendientes(norte.getId());
		assertEquals(4, pendientes.size());
		assertEquals(3, pendientes.get(0).getNumero());
		assertTrue(ventaService.buscarCuotasPendientes(ana.getId()).isEmpty());
	}

	@Test
	@DisplayName("Cuotas pendientes de un cliente que no existe")
	void buscarCuotasPendientesClienteInexistente() {
		assertThrows(ClienteException.class, () -> ventaService.buscarCuotasPendientes(-1));
	}

	@Test
	@DisplayName("Ventas entre dos fechas")
	void buscarPorRangoDeFechas() {
		ventaService.registrarVentaContado(ana.getId(), Map.of(rodillo.getId(), 1), null);
		ventaService.registrarVentaFinanciada(norte.getId(), Map.of(taladro.getId(), 1), 20);
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
		ventaService.registrarVentaContado(ana.getId(), Map.of(rodillo.getId(), 1), null);
		ventaService.registrarVentaContado(norte.getId(), Map.of(rodillo.getId(), 2), null);
		ventaService.registrarVentaFinanciada(norte.getId(), Map.of(taladro.getId(), 1), 20);

		List<VentaContado> contado = ventaService.buscarVentasContado();
		List<VentaFinanciada> financiadas = ventaService.buscarVentasFinanciadas();
		assertEquals(2, contado.size());
		assertEquals(1, financiadas.size());
		assertEquals(6, financiadas.get(0).getCuotas().size());
	}

	@Test
	@DisplayName("Pagar una cuota")
	void pagarCuota() {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(ana.getId(), Map.of(rodillo.getId(), 1), 20);
		Cuota cuota = ventaService.pagarCuota(venta.getId(), 1);

		assertEquals(1, cuota.getNumero());
		assertEquals(EstadoCuota.PAGADA, cuota.getEstado());
		assertEquals(EstadoCuota.ADEUDADA, venta.getCuotas().get(1).getEstado());
	}

	@Test
	@DisplayName("Pagar dos veces la misma cuota")
	void pagarCuotaDosVeces() {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(ana.getId(), Map.of(rodillo.getId(), 1), 20);
		ventaService.pagarCuota(venta.getId(), 1);
		assertThrows(VentaException.class, () -> ventaService.pagarCuota(venta.getId(), 1));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 7, -1 })
	@DisplayName("Numero de cuota que no existe")
	void pagarCuotaInexistente(int numero) {
		VentaFinanciada venta = ventaService.registrarVentaFinanciada(ana.getId(), Map.of(rodillo.getId(), 1), 20);
		assertThrows(VentaException.class, () -> ventaService.pagarCuota(venta.getId(), numero));
	}

	@Test
	@DisplayName("Pagar cuota de una venta de contado")
	void pagarCuotaVentaContado() {
		VentaContado venta = ventaService.registrarVentaContado(ana.getId(), Map.of(rodillo.getId(), 1), null);
		assertThrows(VentaException.class, () -> ventaService.pagarCuota(venta.getId(), 1));
	}

	@Test
	@DisplayName("Pagar cuota de una venta que no existe")
	void pagarCuotaVentaInexistente() {
		assertThrows(VentaException.class, () -> ventaService.pagarCuota(-1, 1));
	}
}
