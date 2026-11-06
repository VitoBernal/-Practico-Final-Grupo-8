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
	List<Producto> findByCategoriaNombreOrderByNombreAsc(String nombreCategoria);

	/*
	 * Busca una Categoría dado su nombre
	 */
	@Query("""
			SELECT p.categoria
			FROM Producto p
			WHERE UPPER(p.categoria.nombre) = UPPER(:nombre)
			""")
	Optional<Categoria> buscarCategoriaPorNombre(@Param("nombre") String nombre);

	/*
	 * Filtra Productos según su marca.
	 */
	List<Producto> findByMarcaOrderByNombreAsc(String marca);

	/*
	 * Filta Productos que se encuentren en un rango de precio. El método debe
	 * recibir el precio Mínimo y el precio Máximo.
	 */
	List<Producto> findByPrecioBaseBetween(Double precioMin, Double precioMax);

	/*
	 * Muestra los productos cuyo Stock esté por debajo de un umbral.
	 */
	List<Producto> findByStockLessThanOrderByStockAsc(int umbral);

}
