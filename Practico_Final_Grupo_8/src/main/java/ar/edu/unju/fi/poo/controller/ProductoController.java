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

import ar.edu.unju.fi.poo.dto.ErrorResponseDto;
import ar.edu.unju.fi.poo.dto.ProductoRequestDto;
import ar.edu.unju.fi.poo.dto.ProductoResponseDto;
import ar.edu.unju.fi.poo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Alta, modificacion y consultas del catalogo de productos")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	// =================Devolver TODOS los productos
	@Operation(summary = "Listar productos", description = "Devuelve todos los productos del catalogo")
	@ApiResponse(responseCode = "200", description = "Listado de productos")
	@GetMapping
	public ResponseEntity<List<ProductoResponseDto>> traerTodos() {
		return ResponseEntity.ok(productoService.traerTodos());
	}

	// =================Buscar producto por ID
	@Operation(summary = "Buscar producto por id", description = "Devuelve los datos de un producto")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Producto encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el producto", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/{id}")
	public ResponseEntity<ProductoResponseDto> buscarPorId(
			@Parameter(description = "Id del producto", example = "1") @PathVariable Integer id) {
		return ResponseEntity.ok(productoService.buscarPorId(id));
	}

	// ==================Buscar products por CATEGORIA
	@Operation(summary = "Filtrar productos por categoria", description = "Devuelve los productos de esa categoria "
			+ "ordenados por nombre, sin importar mayusculas. Si no hay ninguno devuelve una lista vacia.")
	@ApiResponse(responseCode = "200", description = "Productos de la categoria")
	@GetMapping("/categoria/{categoria}")
	public ResponseEntity<List<ProductoResponseDto>> buscarPorCategoria(
			@Parameter(description = "Nombre de la categoria", example = "Herramientas") @PathVariable String categoria) {
		return ResponseEntity.ok(productoService.buscarPorCategoria(categoria));
	}

	// =================Buscar productos por RANGO DE PRECIO
	@Operation(summary = "Filtrar productos por rango de precios", description = "Devuelve los productos con precio "
			+ "entre min y max, incluidos, del mas barato al mas caro")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Productos en ese rango"),
			@ApiResponse(responseCode = "400", description = "Falta min o max, alguno es 0 o negativo, o min es "
					+ "mayor que max", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/precio")
	public ResponseEntity<List<ProductoResponseDto>> buscarPorRangoDePrecio(
			@Parameter(description = "Precio minimo", example = "1000") @RequestParam double min,
			@Parameter(description = "Precio maximo", example = "60000") @RequestParam double max) {
		return ResponseEntity.ok(productoService.buscarPorRangoDePrecio(min, max));
	}

	// =================CREAR un producto
	@Operation(summary = "Crear producto", description = "Registra un producto. Si la categoria no existe se crea "
			+ "y si ya existe se reutiliza. El stock no puede ser negativo y el precio tiene que ser mayor a 0.")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Producto creado"),
			@ApiResponse(responseCode = "400", description = "Falta el nombre, la categoria, el stock o el precio, o el "
					+ "stock es negativo", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "422", description = "El precio es 0 o negativo", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping
	public ResponseEntity<ProductoResponseDto> crearProducto(@Valid @RequestBody ProductoRequestDto productoDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardar(productoDto));
	}

	// =================Buscar prodcutos por MARCA
	@Operation(summary = "Filtrar productos por marca", description = "Devuelve los productos de esa marca ordenados "
			+ "por nombre, sin importar mayusculas. Si no hay ninguno devuelve una lista vacia.")
	@ApiResponse(responseCode = "200", description = "Productos de la marca")
	@GetMapping("/marca/{marca}")
	public ResponseEntity<List<ProductoResponseDto>> buscarPorMarca(
			@Parameter(description = "Marca", example = "Bosch") @PathVariable String marca) {
		return ResponseEntity.ok(productoService.buscarPorMarca(marca));
	}

	// =================Buscar productos con STOCK CRITICO
	@Operation(summary = "Productos con stock critico", description = "Devuelve los productos cuyo stock es menor "
			+ "o igual a su stock critico, que es 5")
	@ApiResponse(responseCode = "200", description = "Productos con stock critico")
	@GetMapping("/stock-critico")
	public ResponseEntity<List<ProductoResponseDto>> buscarConStockCritico() {
		return ResponseEntity.ok(productoService.buscarConStockCritico());
	}

	// =================Obtener ALERTAS de STOCK
	@Operation(summary = "Alertas de stock", description = "Devuelve un mensaje de alerta por cada producto con "
			+ "stock critico")
	@ApiResponse(responseCode = "200", description = "Mensajes de alerta")
	@GetMapping("/alertas-stock")
	public ResponseEntity<List<String>> obtenerAlertasDeStock() {
		return ResponseEntity.ok(productoService.obtenerAlertasDeStock());
	}

	// =================Buscar productos por debajo de un UMBRAL
	@Operation(summary = "Productos por debajo de un umbral", description = "Devuelve los productos con stock "
			+ "menor al umbral, del que tiene menos al que tiene mas")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Productos por debajo del umbral"),
			@ApiResponse(responseCode = "400", description = "Falta el umbral o es negativo", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/stock-bajo")
	public ResponseEntity<List<ProductoResponseDto>> buscarPorDebajoDeUmbral(
			@Parameter(description = "Cantidad de stock", example = "10") @RequestParam int umbral) {
		return ResponseEntity.ok(productoService.buscarPorDebajoDeUmbral(umbral));
	}

	// =================ACTUALIZAR PRODUCTO
	@Operation(summary = "Modificar producto", description = "Modifica todos los datos del producto, por ejemplo "
			+ "su stock o su precio, con las mismas reglas que al crearlo")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Producto modificado"),
			@ApiResponse(responseCode = "400", description = "Falta el nombre, la categoria, el stock o el precio, o el "
					+ "stock es negativo", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "No existe el producto", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "422", description = "El precio es 0 o negativo", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PutMapping("/{id}")
	public ResponseEntity<ProductoResponseDto> actualizar(
			@Parameter(description = "Id del producto", example = "1") @PathVariable Integer id,
			@Valid @RequestBody ProductoRequestDto productoDto) {
		ProductoResponseDto actualizado = productoService.actualizar(id, productoDto);

		return ResponseEntity.ok(actualizado);
	}
}
