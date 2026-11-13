package ar.edu.unju.fi.poo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.dto.ProductoRequestDto;
import ar.edu.unju.fi.poo.dto.ProductoResponseDto;
import ar.edu.unju.fi.poo.service.ProductoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	// =================Devolver TODOS los productos
	@GetMapping
	public ResponseEntity<List<ProductoResponseDto>> traerTodos() {
		return ResponseEntity.ok(productoService.traerTodos());
	}

	// =================Buscar producto por ID
	@GetMapping("/{id}")
	public ResponseEntity<ProductoResponseDto> buscarPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(productoService.buscarPorId(id));
	}

	// ==================Buscar products por CATEGORIA
	@GetMapping("/categoria/{categoria}")
	public ResponseEntity<List<ProductoResponseDto>> buscarPorCategoria(@PathVariable String categoria) {
		System.out.println("Categoría recibida: " + categoria);
		return ResponseEntity.ok(productoService.buscarPorCategoria(categoria));
	}

	// =================Buscar productos por RANGO DE PRECIO
	@GetMapping("/precio")
	public ResponseEntity<List<ProductoResponseDto>> buscarPorRangoDePrecio(@RequestParam double min,
			@RequestParam double max) {
		return ResponseEntity.ok(productoService.buscarPorRangoDePrecio(min, max));
	}

	// =================CREAR un producto
	@PostMapping
	public ResponseEntity<ProductoResponseDto> crearProducto(@Valid @RequestBody ProductoRequestDto productoDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardar(productoDto));
	}

	// =================Buscar prodcutos por MARCA
	@GetMapping("/marca/{marca}")
	public ResponseEntity<List<ProductoResponseDto>> buscarPorMarca(@PathVariable String marca) {
		return ResponseEntity.ok(productoService.buscarPorMarca(marca));
	}

	// =================Buscar productos con STOCK CRITICO
	@GetMapping("/stock-critico")
	public ResponseEntity<List<ProductoResponseDto>> buscarConStockCritico() {
		return ResponseEntity.ok(productoService.buscarConStockCritico());
	}

	// =================Obtener ALERTAS de STOCK
	@GetMapping("/alertas-stock")
	public ResponseEntity<List<String>> obtenerAlertasDeStock() {
		return ResponseEntity.ok(productoService.obtenerAlertasDeStock());
	}

	// =================Buscar productos por debajo de un UMBRAL
	@GetMapping("/stock-bajo")
	public ResponseEntity<List<ProductoResponseDto>> buscarPorDebajoDeUmbral(@RequestParam int umbral) {
		return ResponseEntity.ok(productoService.buscarPorDebajoDeUmbral(umbral));
	}

	// =================ACTUALIZAR PRODUCTO
	@PutMapping("/{id}")
	public ResponseEntity<ProductoResponseDto> actualizar(@PathVariable Integer id,
			@RequestBody ProductoRequestDto produtcoDto) {
		ProductoResponseDto actualizado = productoService.actualizar(id, produtcoDto);

		return ResponseEntity.ok(actualizado);
	}
}
