package ar.edu.unju.fi.poo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
	/*
	 * Filtra Productos según el nombre de la categoría a la que pertenecen.
	 */
	List<Producto> findByCategoriaNombreIgnoreCaseOrderByNombreAsc(String nombreCategoria);

	/*
	 * Busca una Categoría dado su nombre
	 */
	@Query("""
	        SELECT DISTINCT p.categoria
	        FROM Producto p
	        WHERE FUNCTION('LOWER', p.categoria.nombre) =
	              FUNCTION('LOWER', :nombre)
	        """)
	Optional<Categoria> buscarCategoriaPorNombre(@Param("nombre") String nombre);

	/*
	 * Filtra Productos según su marca.
	 */
	List<Producto> findByMarcaIgnoreCaseOrderByNombreAsc(String marca);

	/*
	 * Filta Productos que se encuentren en un rango de precio. El método debe
	 * recibir el precio Mínimo y el precio Máximo.
	 */
	List<Producto> findByPrecioBaseBetweenOrderByPrecioBaseAsc(Double precioMin, Double precioMax);

	/*
	 * 
	 */
	@Query("""
			SELECT p
			FROM Producto p
			WHERE p.stock <= p.stockCritico
			""")
	List<Producto> buscarConStockCritico();

	/*
	 * Muestra los productos cuyo Stock esté por debajo de un umbral.
	 */
	List<Producto> findByStockLessThanOrderByStockAsc(int umbral);

}
