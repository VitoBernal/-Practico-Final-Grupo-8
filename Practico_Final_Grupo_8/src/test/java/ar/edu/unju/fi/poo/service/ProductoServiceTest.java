package ar.edu.unju.fi.poo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.poo.exception.ProductoException;
import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.service.imp.ProductoServiceImp;

@SpringBootTest
@Transactional
class ProductoServiceTest {

	@Autowired
	ProductoServiceImp productoService;

	private Producto p1;
	private Producto p2;
	private Producto p3;
	private Producto p4;
	private Producto p5;

	@BeforeEach
	void setUp() {
		Categoria deporte = new Categoria("Deporte");

		p1 = new Producto("Smartphone Galaxy A55", "Teléfono Android 5G", "Samsung", new Categoria("Tecnología"), 12,
				450000.0);
		p2 = new Producto("Galaxy Buds", "Auriculares inalámbricos", "Samsung", new Categoria("Tecnología"), 5,
				120000.0);
		p3 = new Producto("Heladera No Frost", "Heladera familiar", "LG", new Categoria("Hogar"), 5, 1100000.0);
		p4 = new Producto("Pelota de Fútbol", "Pelota profesional", "Adidas", deporte, 20, 35000.0);

		productoService.guardar(p1);
		productoService.guardar(p2);
		productoService.guardar(p3);
		productoService.guardar(p4);
		productoService.traerTodos();
	}

	@Test
	@DisplayName("Guarda los productos y la categorías a la que pertenecen.")
	void guardarTest() {
		//Categoria repetida guarda el producto
		p5 = new Producto("Microondas 28L", "Microondas digital", "Philips", new Categoria("Hogar"), 5, 195000.0);
		productoService.guardar(p5);
		assertNotNull(p5.getId());
		
		//La categoria repetida no se guarda dos veces
		assertEquals(p1.getCategoria().getId(), p2.getCategoria().getId());

		// Se guardan todos los productos creados en setUp
		assertEquals(5, productoService.traerTodos().size());
	}

	@Test
	@DisplayName("Buscar por Id")
	void buscarPorIdTest() {
		Producto producto = productoService.buscarPorId(p1.getId());

		assertNotNull(producto);
		assertEquals("Smartphone Galaxy A55", producto.getNombre());
	}

	@Test
	@DisplayName("Buscar por ID null")
	void buscarPorIdNull() {
		// Lanza la exception cuando no se ingresa el Id
		assertThrows(ProductoException.class, () -> productoService.buscarPorId(null));
	}

	@Test
	@DisplayName("Buscar por ID inexistente")
	void buscarPorIdInexistente() {
		// Lanza la exception cuando se ingresa un id que no existe
		assertThrows(ProductoException.class, () -> productoService.buscarPorId(99));
	}

	@Test
	@DisplayName("Productos que pertenecen a una cierta Categoría")
	void buscarPorCategoriaTest() {
		String categoriaBuscada = "Tecnología";

		assertNotNull(productoService.buscarPorCategoria(categoriaBuscada));
		assertEquals(2, productoService.buscarPorCategoria(categoriaBuscada).size());
	}

	@Test
	@DisplayName("Buscar por Marca")
	void testBuscarPorMarca() {
		List<Producto> samsung = productoService.buscarPorMarca("Samsung");
		assertEquals(2, samsung.size());
		assertTrue(samsung.stream().allMatch(p -> p.getMarca().equals("Samsung")));
	}

	@Test
	@DisplayName("Buscar por Rango de Precios")
	void buscarPorRangoDePrecioTest() {
		List<Producto> productos = productoService.buscarPorRangoDePrecio(100000, 500000);

		assertFalse(productos.isEmpty());

		assertTrue(productos.stream().allMatch(p -> p.getPrecioBase() >= 100000 && p.getPrecioBase() <= 500000));

	}

	@Test
	@DisplayName("Buscar con Stock Critico")
	void buscarConStockCriticoTest() {
		List<Producto> productos = productoService.buscarConStockCritico();

		assertFalse(productos.isEmpty());

		assertTrue(productos.stream().allMatch(Producto::tieneStockCritico));
	}

	@Test
	@DisplayName("Buscar por debajo de umbral")
	void buscarPorDebajoDeUmbralTest() {
		List<Producto> productos = productoService.buscarPorDebajoDeUmbral(10);

		assertFalse(productos.isEmpty());

		assertTrue(productos.stream().allMatch(p -> p.getStock() < 10));
	}

	@Test
	@DisplayName("Alerta de Stock")
	void obtenerAlertaDeStock() {
		List<String> alertas = productoService.obtenerAlertasDeStock();

		assertFalse(alertas.isEmpty());
	}
}
