package ar.edu.unju.fi.poo.service.imp;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.poo.dto.ProductoRequestDto;
import ar.edu.unju.fi.poo.dto.ProductoResponseDto;
import ar.edu.unju.fi.poo.dto.mapper.ProductoMapper;
import ar.edu.unju.fi.poo.exception.ImporteInvalidoException;
import ar.edu.unju.fi.poo.exception.NoEncontradoException;
import ar.edu.unju.fi.poo.exception.ProductoException;
import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.service.ProductoService;

@Service
@Transactional
public class ProductoServiceImp implements ProductoService {

	private final ProductoRepository productoRepository;

	private final ProductoMapper productoMapper;

	public ProductoServiceImp(ProductoRepository productoRepository, ProductoMapper productoMapper) {
		this.productoRepository = productoRepository;
		this.productoMapper = productoMapper;
	}

	// ============Guardar Producto
	/*
	 * Guarda un objeto de tipo Producto. Se pasa como parámetro su categoría, entre
	 * otros atributos, si la categoría no existe la crea. Si la categoría existe no
	 * la duplica.
	 */
	@Override
	@Transactional
	public ProductoResponseDto guardar(ProductoRequestDto dto) throws ProductoException {

		exceptions(dto);

		Producto producto = new Producto();

		copiarDatos(dto, producto);

		Producto productoGuardado = productoRepository.save(producto);

		return productoMapper.productoADto(productoGuardado);
	}

	// ============Actualizar
	/*
	 * 
	 */
	public ProductoResponseDto actualizar(Integer id, ProductoRequestDto dto) throws ProductoException {

		if (id == null) {
			throw new NoEncontradoException("Ingrese el ID.");
		}

		Producto productoActualizado = productoRepository.findById(id)
				.orElseThrow(() -> new NoEncontradoException("No existe el producto con id: " + id));

		exceptions(dto);

		copiarDatos(dto, productoActualizado);

		Producto productoGuardado = productoRepository.save(productoActualizado);

		return productoMapper.productoADto(productoGuardado);
	}

	// ============Buscar por Id
	/*
	 * Busca un Producto a través de su ID. Lanza una EXCEPTION si NO se pasa como
	 * parámetro ningún ID y si se pasa como parámetro un ID que no existe.
	 */
	@Override
	@Transactional(readOnly = true)
	public ProductoResponseDto buscarPorId(Integer id) {

		if (id == null) {
			throw new ProductoException("Debe ingresar el id");
		}

		Producto productoBuscado = productoRepository.findById(id)
				.orElseThrow(() -> new NoEncontradoException("No existe el producto con id: " + id));

		return productoMapper.productoADto(productoBuscado);
	}

	// ============Buscar por Categoria
	/*
	 * Busca y retorna Productos que pertenecen a una Categoría determinada.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProductoResponseDto> buscarPorCategoria(String categoria) {
		if (categoria == null || categoria.isBlank()) {
			throw new ProductoException("Ingrese categoría.");
		}

		List<Producto> productos = productoRepository.findByCategoriaNombreIgnoreCaseOrderByNombreAsc(categoria.trim());

		if (productos.isEmpty()) {
			throw new ProductoException("No se encontraron productos pertenecientes a la categoria " + categoria);
		}

		return productos.stream().map(productoMapper::productoADto).toList();
	}

	// ============Buscar por Marca
	/*
	 * Busca y retorna Productos de una determinada MARCA.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProductoResponseDto> buscarPorMarca(String marca) {
		if (marca == null || marca.isBlank()) {
			throw new ProductoException("Debe ingresar una marca.");
		}

		List<Producto> productos = productoRepository.findByMarcaIgnoreCaseOrderByNombreAsc(marca.trim());

		if (productos.isEmpty()) {
			throw new ProductoException("No se encontraron productos de marca " + marca);
		}
		return productos.stream().map(productoMapper::productoADto).toList();
	}

	// ============Buscar por Rango de Precio
	/*
	 * Busca y retorna una lista con los Productos, cuyo PrecioBase se encuentre
	 * dentro de un intervalo (precioMin y precioMax).
	 */
	@Override
	@Transactional
	public List<ProductoResponseDto> buscarPorRangoDePrecio(double min, double max) {

		if (min <= 0 || max <= 0) {
			throw new ProductoException("Los valores deben ser mayores a cero.");
		}

		if (min > max) {
			throw new ProductoException("El valor minimo supera al valor maximo");
		}
		return productoRepository.findByPrecioBaseBetweenOrderByPrecioBaseAsc(min, max).stream()
				.map(productoMapper::productoADto).toList();
	}

	// ============Buscar con Stock Critico
	/*
	 * Busca y retorna una lista cuyo Stock se encuentre en estado crítico,
	 * stock<=5.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProductoResponseDto> buscarConStockCritico() {

		return productoRepository.buscarConStockCritico().stream().map(productoMapper::productoADto).toList();
	}

	// ============Buscar por Debajo de Umbral
	/*
	 * Busca Productos cuya cantidad de Stock se encuentre por debajo de una
	 * cantidad dada(umbral). Si se pasa como parámetro un número menor a cero se
	 * lanza una Exception.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProductoResponseDto> buscarPorDebajoDeUmbral(int umbral) {
		if (umbral < 0) {
			throw new ProductoException("El umbral debe ser mayor a cero.");
		}

		return productoRepository.findByStockLessThanOrderByStockAsc(umbral).stream().map(productoMapper::productoADto)
				.toList();
	}

	// ============Obtener Mensajes de Alerta de Stock
	/*
	 * Muestra los Productos y la cantidad de stock que tiene cada Producto.
	 */
	@Override
	@Transactional
	public List<String> obtenerAlertasDeStock() {

		return productoRepository.findAll()
				.stream()
				.filter(Producto::tieneStockCritico)
				.map(Producto::generarAlertaStock)
				.toList();

	}

	// =============Retornar todos los Productos registrados.
	@Override
	@Transactional(readOnly = true)
	public List<ProductoResponseDto> traerTodos() {
		return productoRepository.findAll().stream().map(productoMapper::productoADto).toList();
	}

	// Copia datos del dto a producto
	private void copiarDatos(ProductoRequestDto dto, Producto producto) {

		producto.setNombre(dto.getNombre());
		producto.setDescripcion(dto.getDescripcion());
		producto.setMarca(dto.getMarca());
		producto.setStock(dto.getStock());
		producto.setPrecioBase(dto.getPrecioBase());

		String nombreCategoria = dto.getCategoria().trim();
		
		// si la categoría ya existe se reutiliza; si no, se crea una nueva (orElseGet
		// recibe un lambda)
		// y se guarda en cascada junto con el producto

		Categoria categoria = productoRepository.buscarCategoriaPorNombre(nombreCategoria)
				.orElseGet(() -> new Categoria(nombreCategoria));

		producto.setCategoria(categoria);
		
	}

	// Exceptions
	private void exceptions(ProductoRequestDto dto) {
		if (dto.getNombre() == null || dto.getNombre().isBlank()) {
			throw new ProductoException("Ingrese el nombre del producto.");
		}

		if (dto.getCategoria() == null || dto.getCategoria().isBlank()) {
			throw new ProductoException("Ingrese el nombre de la categoria del producto.");
		}

		if (dto.getStock() == null) {
			throw new ProductoException("Ingrese stock del producto");
		}

		if (dto.getStock() <= 0) {
			throw new ProductoException("El stock debe ser mayor a cero.");
		}

		if (dto.getPrecioBase() == null) {
			throw new ImporteInvalidoException("Ingrese el precio del producto.");
		}

		if (dto.getPrecioBase() <= 0) {
			throw new ImporteInvalidoException("El precio del producto debe ser mayor a cero.");
		}
	}
}
