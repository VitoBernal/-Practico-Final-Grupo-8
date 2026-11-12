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

import ar.edu.unju.fi.poo.dto.ProductoRequestDto;
import ar.edu.unju.fi.poo.dto.ProductoResponseDto;
import ar.edu.unju.fi.poo.exception.ImporteInvalidoException;
import ar.edu.unju.fi.poo.exception.NoEncontradoException;
import ar.edu.unju.fi.poo.exception.ProductoException;
import ar.edu.unju.fi.poo.service.imp.ProductoServiceImp;

@SpringBootTest
@Transactional
class ProductoServiceTest {

    @Autowired
    private ProductoServiceImp productoService;

    private ProductoRequestDto p1;
    private ProductoRequestDto p2;
    private ProductoRequestDto p3;
    private ProductoRequestDto p4;

    @BeforeEach
    void setUp() {
        p1 = new ProductoRequestDto("Smartphone Galaxy A55", "Teléfono Android 5G",
                "Samsung", "Tecnología", 12, 450000.0);
        p2 = new ProductoRequestDto("Galaxy Buds", "Auriculares inalámbricos",
                "Samsung", "Tecnología", 5, 120000.0);
        p3 = new ProductoRequestDto("Heladera No Frost", "Heladera familiar",
                "LG", "Hogar", 5, 1100000.0);
        p4 = new ProductoRequestDto("Pelota de Fútbol", "Pelota profesional",
                "Adidas", "Deporte", 20, 35000.0);
    }

    // ==================== PRUEBAS EXITOSAS ====================

    @Test
    @DisplayName("Guardar un producto correctamente")
    void guardarTest() {
        ProductoResponseDto guardado = productoService.guardar(p1);

        assertNotNull(guardado);
        assertNotNull(guardado.getId());
        assertEquals("Smartphone Galaxy A55", guardado.getNombre());
        assertEquals("Samsung", guardado.getMarca());
        assertEquals("Tecnología", guardado.getCategoria());
    }

    @Test
    @DisplayName("Buscar un producto por ID existente")
    void buscarPorIdTest() {
        ProductoResponseDto guardado = productoService.guardar(p1);

        ProductoResponseDto encontrado = productoService.buscarPorId(guardado.getId());

        assertNotNull(encontrado);
        assertEquals(guardado.getId(), encontrado.getId());
        assertEquals("Smartphone Galaxy A55", encontrado.getNombre());
    }

    @Test
    @DisplayName("Buscar productos por categoría")
    void buscarPorCategoriaTest() {
        productoService.guardar(p1);
        productoService.guardar(p2);

        List<ProductoResponseDto> productos =
                productoService.buscarPorCategoria("Tecnología");

        assertEquals(2, productos.size());
        assertTrue(productos.stream()
                .allMatch(p -> p.getCategoria().equalsIgnoreCase("Tecnología")));
    }

    @Test
    @DisplayName("Buscar productos por marca")
    void buscarPorMarcaTest() {
        productoService.guardar(p1);
        productoService.guardar(p2);

        List<ProductoResponseDto> productos =
                productoService.buscarPorMarca("Samsung");

        assertEquals(2, productos.size());
        assertTrue(productos.stream()
                .allMatch(p -> p.getMarca().equalsIgnoreCase("Samsung")));
    }

    @Test
    @DisplayName("Buscar productos por rango de precios")
    void buscarPorRangoDePrecioTest() {
        productoService.guardar(p1);
        productoService.guardar(p2);
        productoService.guardar(p4);

        List<ProductoResponseDto> productos =
                productoService.buscarPorRangoDePrecio(100000, 500000);

        assertFalse(productos.isEmpty());
        assertTrue(productos.stream()
                .allMatch(p -> p.getPrecioBase() >= 100000
                        && p.getPrecioBase() <= 500000));
    }
    
    @Test
    @DisplayName("Actualizar producto correctamente")
    void actualizarProductoTest() {
    	ProductoResponseDto guardado = productoService.guardar(p3);
    	
    	p3.setNombre("Smartphone Moto G85");
    	p3.setStock(8);
    	p3.setPrecioBase(750000.0);
    	
    	ProductoResponseDto actualizado = productoService.actualizar(guardado.getId(), p3);
    	
    	assertEquals(guardado.getId(), actualizado.getId());
    	assertEquals("Smartphone Moto G85", actualizado.getNombre());
    	assertEquals(8, actualizado.getStock());
    	assertEquals(750000.0, actualizado.getPrecioBase());
    }
    
    @Test
    @DisplayName("Reutilizar categoria al guardar")
    void reutilizarCategoriaAlGuardarTest() {
    	// Categoria: "Tecnología"
        productoService.guardar(p1);
        productoService.guardar(p2);

        p2.setCategoria("teCNOLOGia");

        ProductoResponseDto resultado = productoService.guardar(p2);

        assertEquals("Tecnología", resultado.getCategoria());
        assertEquals(p1.getCategoria(), resultado.getCategoria());
    }
    
    @Test
	@DisplayName("Buscar con Stock Critico")
	void buscarConStockCriticoTest() {
    	productoService.guardar(p1);
        productoService.guardar(p2);
        productoService.guardar(p3);
        productoService.guardar(p4);
    	
		List<ProductoResponseDto> productos = productoService.buscarConStockCritico();

		assertFalse(productos.isEmpty());
		assertEquals(2, productos.size());
		assertTrue(productos.get(0).getStock()<=5);
	}

	@Test
	@DisplayName("Buscar por debajo de umbral")
	void buscarPorDebajoDeUmbralTest() {
		productoService.guardar(p1);
        productoService.guardar(p2);
        productoService.guardar(p3);
        productoService.guardar(p4);
        
		List<ProductoResponseDto> productos = productoService.buscarPorDebajoDeUmbral(15);

		assertFalse(productos.isEmpty());
		assertEquals(3, productos.size());
	}

	@Test
	@DisplayName("Alerta de Stock")
	void obtenerAlertaDeStock() {
		productoService.guardar(p1);
        productoService.guardar(p2);
        productoService.guardar(p3);
        productoService.guardar(p4);
        
		List<String> alertas = productoService.obtenerAlertasDeStock();

		assertFalse(alertas.isEmpty());
		assertEquals(2, alertas.size());
	}
	
	@Test
	@DisplayName("Traer todos los productos")
	void traerTodosTest() {
		productoService.guardar(p1);
		productoService.guardar(p2);
		productoService.guardar(p3);
		
		List<ProductoResponseDto> productos = productoService.traerTodos();
		
		assertNotNull(productos);
		assertEquals(3, productos.size());
	}

    // ==================== EXCEPCIONES AL GUARDAR ====================

    @Test
    @DisplayName("Guardar: nombre nulo")
    void guardarNombreNuloTest() {
        p1.setNombre(null);

        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.guardar(p1));

        assertEquals("Ingrese el nombre del producto.", ex.getMessage());
    }

    @Test
    @DisplayName("Guardar: nombre vacío o en blanco")
    void guardarNombreVacioTest() {
        p1.setNombre("   ");

        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.guardar(p1));

        assertEquals("Ingrese el nombre del producto.", ex.getMessage());
    }

    @Test
    @DisplayName("Guardar: categoría nula")
    void guardarCategoriaNulaTest() {
        p1.setCategoria(null);

        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.guardar(p1));

        assertEquals("Ingrese el nombre de la categoria del producto.", ex.getMessage());
    }

    @Test
    @DisplayName("Guardar: categoría vacía o en blanco")
    void guardarCategoriaVaciaTest() {
        p1.setCategoria(" ");

        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.guardar(p1));

        assertEquals("Ingrese el nombre de la categoria del producto.", ex.getMessage());
    }

    @Test
    @DisplayName("Guardar: stock nulo")
    void guardarStockNuloTest() {
        p1.setStock(null);

        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.guardar(p1));

        assertEquals("Ingrese stock del producto", ex.getMessage());
    }

    @Test
    @DisplayName("Guardar: stock igual a cero")
    void guardarStockCeroTest() {
        p1.setStock(0);
        
        ProductoResponseDto guardado = productoService.guardar(p1);

        assertNotNull(guardado);
        assertNotNull(guardado.getId());
        assertEquals("Smartphone Galaxy A55", guardado.getNombre());
        assertEquals("Samsung", guardado.getMarca());
        assertEquals("Tecnología", guardado.getCategoria());
    } 

    @Test
    @DisplayName("Guardar: stock negativo")
    void guardarStockNegativoTest() {
        p1.setStock(-3);

        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.guardar(p1));

        assertEquals("El stock no puede ser negativo.", ex.getMessage());
    }

    @Test
    @DisplayName("Guardar: precio nulo")
    void guardarPrecioNuloTest() {
        p1.setPrecioBase(null);

        ImporteInvalidoException ex = assertThrows(ImporteInvalidoException.class,
                () -> productoService.guardar(p1));

        assertEquals("Ingrese el precio del producto.", ex.getMessage());
    }

    @Test
    @DisplayName("Guardar: precio igual a cero")
    void guardarPrecioCeroTest() {
        p1.setPrecioBase(0.0);

        ImporteInvalidoException ex = assertThrows(ImporteInvalidoException.class,
                () -> productoService.guardar(p1));

        assertEquals("El precio del producto debe ser mayor a cero.", ex.getMessage());
    }

    @Test
    @DisplayName("Guardar: precio negativo")
    void guardarPrecioNegativoTest() {
        p1.setPrecioBase(-100.0);

        ImporteInvalidoException ex = assertThrows(ImporteInvalidoException.class,
                () -> productoService.guardar(p1));

        assertEquals("El precio del producto debe ser mayor a cero.", ex.getMessage());
    }

    // ==================== EXCEPCIONES AL ACTUALIZAR ====================

    @Test
    @DisplayName("Actualizar: ID nulo")
    void actualizarIdNuloTest() {
        NoEncontradoException ex = assertThrows(NoEncontradoException.class,
                () -> productoService.actualizar(null, p1));

        assertEquals("Ingrese el ID.", ex.getMessage());
    }

    @Test
    @DisplayName("Actualizar: ID inexistente")
    void actualizarIdInexistenteTest() {
        NoEncontradoException ex = assertThrows(NoEncontradoException.class,
                () -> productoService.actualizar(999999, p1));

        assertEquals("No existe el producto con id: 999999", ex.getMessage());
    }


    // ==================== EXCEPCIONES EN BÚSQUEDAS ====================

    @Test
    @DisplayName("Buscar por ID: ID nulo")
    void buscarPorIdNuloTest() {
        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.buscarPorId(null));

        assertEquals("Debe ingresar el id", ex.getMessage());
    }

    @Test
    @DisplayName("Buscar por ID: ID inexistente")
    void buscarPorIdInexistenteTest() {
        NoEncontradoException ex = assertThrows(NoEncontradoException.class,
                () -> productoService.buscarPorId(999999));

        assertEquals("No existe el producto con id: 999999", ex.getMessage());
    }

    @Test
    @DisplayName("Buscar por categoría: categoría nula")
    void buscarPorCategoriaNulaTest() {
        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.buscarPorCategoria(null));

        assertEquals("Ingrese categoría.", ex.getMessage());
    }

    @Test
    @DisplayName("Buscar por categoría: categoría vacía")
    void buscarPorCategoriaVaciaTest() {
        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.buscarPorCategoria("   "));

        assertEquals("Ingrese categoría.", ex.getMessage());
    }

    @Test
    @DisplayName("Buscar por categoría: no hay productos")
    void buscarPorCategoriaSinResultadosTest() {
    	
    	List<ProductoResponseDto> productos = productoService.buscarPorCategoria("Categoría inexistente");
    	
    	assertEquals(0, productos.size());

    }

    @Test
    @DisplayName("Buscar por marca: marca nula")
    void buscarPorMarcaNulaTest() {
        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.buscarPorMarca(null));

        assertEquals("Debe ingresar una marca.", ex.getMessage());
    }

    @Test
    @DisplayName("Buscar por marca: marca vacía")
    void buscarPorMarcaVaciaTest() {
        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.buscarPorMarca(" "));

        assertEquals("Debe ingresar una marca.", ex.getMessage());
    }

    @Test
    @DisplayName("Buscar por marca: no hay productos")
    void buscarPorMarcaSinResultadosTest() {
       List<ProductoResponseDto> productos = productoService.buscarPorMarca("Marca inexistente");
       
       assertEquals(0, productos.size());
    }

    @Test
    @DisplayName("Buscar por rango: precio mínimo igual a cero")
    void buscarPorRangoPrecioMinimoCeroTest() {
        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.buscarPorRangoDePrecio(0, 1000));

        assertEquals("Los valores deben ser mayores a cero.", ex.getMessage());
    }

    @Test
    @DisplayName("Buscar por rango: precio máximo negativo")
    void buscarPorRangoPrecioMaximoNegativoTest() {
        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.buscarPorRangoDePrecio(100, -1));

        assertEquals("Los valores deben ser mayores a cero.", ex.getMessage());
    }

    @Test
    @DisplayName("Buscar por rango: mínimo superior al máximo")
    void buscarPorRangoMinimoMayorMaximoTest() {
        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.buscarPorRangoDePrecio(2000, 1000));

        assertEquals("El valor minimo supera al valor maximo", ex.getMessage());
    }

    @Test
    @DisplayName("Buscar por debajo de umbral: valor negativo")
    void buscarPorDebajoDeUmbralNegativoTest() {
        ProductoException ex = assertThrows(ProductoException.class,
                () -> productoService.buscarPorDebajoDeUmbral(-1));

        assertEquals("El umbral debe ser mayor a cero.", ex.getMessage());
    }
}