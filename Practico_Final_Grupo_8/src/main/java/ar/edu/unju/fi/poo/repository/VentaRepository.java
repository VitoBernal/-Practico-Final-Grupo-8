package ar.edu.unju.fi.poo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.unju.fi.poo.model.Cupon;
import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.model.VentaContado;
import ar.edu.unju.fi.poo.model.VentaFinanciada;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

	@Query("SELECT c FROM Cupon c WHERE c.codigo = :codigo")
	Optional<Cupon> buscarCuponPorCodigo(@Param("codigo") String codigo);

	List<Venta> findByClienteIdOrderByFechaDesc(Integer clienteId);

	List<Venta> findByFechaBetweenOrderByFechaDesc(LocalDate desde, LocalDate hasta);

	@Query("SELECT v FROM VentaContado v ORDER BY v.fecha DESC")
	List<VentaContado> buscarVentasContado();

	@Query("SELECT v FROM VentaFinanciada v ORDER BY v.fecha DESC")
	List<VentaFinanciada> buscarVentasFinanciadas();

	@Query("SELECT v FROM VentaFinanciada v WHERE v.cliente.id = :clienteId")
	List<VentaFinanciada> buscarVentasFinanciadasPorCliente(@Param("clienteId") Integer clienteId);
}
