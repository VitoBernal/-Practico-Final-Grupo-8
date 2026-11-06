package ar.edu.unju.fi.poo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import ar.edu.unju.fi.poo.model.Cuota;
import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.model.VentaContado;
import ar.edu.unju.fi.poo.model.VentaFinanciada;

public interface VentaService {

	// en el map de productos va el id del producto y la cantidad
	VentaContado registrarVentaContado(Integer clienteId, Map<Integer, Integer> productos, String codigoCupon);

	VentaFinanciada registrarVentaFinanciada(Integer clienteId, Map<Integer, Integer> productos,
			double porcentajeRecargo);

	List<Venta> buscarPorCliente(Integer clienteId);

	List<Cuota> buscarCuotasPendientes(Integer clienteId);

	List<Venta> buscarPorRangoDeFechas(LocalDate desde, LocalDate hasta);

	List<VentaContado> buscarVentasContado();

	List<VentaFinanciada> buscarVentasFinanciadas();

	Cuota pagarCuota(Integer ventaId, int numeroCuota);
}
