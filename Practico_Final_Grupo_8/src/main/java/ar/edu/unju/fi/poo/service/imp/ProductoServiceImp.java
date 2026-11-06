package ar.edu.unju.fi.poo.service.imp;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.poo.exception.ProductoException;
import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.service.ProductoService;

@Service
@Transactional
public class ProductoServiceImp implements ProductoService {

	@Autowired
	private ProductoRepository productoRepository;

	// ============Guardar Producto
	/*
	 * Guarda un objeto de tipo Producto. Se pasa como parámetro su categoría, entre
	 * otros atributos, si la categoría no existe la crea. Si la categoría existe no
	 * la duplica.
	 */
	@Override
	@Transactional
	public Producto guardar(Producto producto) {
		String nombreCategoria = producto.getCategoria().getNombre().trim();
		// si la categoría ya existe se reutiliza; si no, se crea una nueva (orElseGet
		// recibe un lambda)
		// y se guarda en cascada junto con el producto
		Categoria categoria = productoRepository.buscarCategoriaPorNombre(nombreCategoria)
				.orElseGet(() -> new Categoria(nombreCategoria));
		producto.setCategoria(categoria);

		return productoRepository.save(producto);
	}

	// ============Buscar por Id
	/*
	 * Busca un Producto a través de su ID. Lanza una EXCEPTION si NO se pasa como
	 * parámetro ningún ID y si se pasa como parámetro un ID que no existe.
	 */
	@Override
	@Transactional(readOnly = true)
	public Producto buscarPorId(Integer id) {

		if (id == null) {
			throw new ProductoException("Debe ingresar el id");
		}

		return productoRepository.findById(id)
				.orElseThrow(() -> new ProductoException("No existe un producto con id: " + id));
	}

	// ============Buscar por Categoria
	/*
	 * Busca y retorna Productos que pertenecen a una Categoría determinada.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Producto> buscarPorCategoria(String categoria) {

		return productoRepository.findByCategoriaNombreOrderByNombreAsc(categoria);
	}

	// ============Buscar por Marca
	/*
	 * Busca y retorna Productos de una determinada MARCA.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Producto> buscarPorMarca(String marca) {

		return productoRepository.findByMarcaOrderByNombreAsc(marca);
	}

	// ============Buscar por Rango de Precio
	/*
	 * Busca y retorna una lista con los Productos, cuyo PrecioBase se encuentre
	 * dentro de un intervalo (precioMin y precioMax).
	 */
	@Override
	@Transactional
	public List<Producto> buscarPorRangoDePrecio(double min, double max) {

		return productoRepository.findByPrecioBaseBetween(min, max);
	}

	// ============Buscar con Stock Critico
	/*
	 * Busca y retorna una lista cuyo Stock se encuentre en estado crítico, stock=5.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Producto> buscarConStockCritico() {

		return productoRepository.findAll().stream().filter(Producto::tieneStockCritico)
				.sorted(Comparator.comparing(Producto::getStock)).toList();
	}

	// ============Buscar por Debajo de Umbral
	/*
	 * Busca Productos cuya cantidad de Stock se encuentre por debajo de una
	 * cantidad dada(umbral). Si se pasa como parámetro un número menor a cero se
	 * lanza una Exception.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Producto> buscarPorDebajoDeUmbral(int umbral) {
		if (umbral < 0) {
			throw new ProductoException("El umbral debe ser mayor a cero");
		}

		return productoRepository.findByStockLessThanOrderByStockAsc(umbral);
	}

	// ============Obtener Mensajes de Alerta de Stock
	/*
	 * Muestra los Productos y la cantidad de stock que tiene cada Producto.
	 */
	@Override
	@Transactional
	public List<String> obtenerAlertasDeStock() {

		return productoRepository.findAll().stream().filter(Producto::tieneStockCritico)
				.map(Producto::generarAlertaStock).toList();

	}

	// =============Retornar todos los Productos registrados.
	@Override
	@Transactional(readOnly = true)
	public List<Producto> traerTodos() {
		return productoRepository.findAll();
	}

}
