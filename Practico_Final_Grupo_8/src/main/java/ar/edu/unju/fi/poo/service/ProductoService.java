package ar.edu.unju.fi.poo.service;

import java.util.List;

import ar.edu.unju.fi.poo.model.Producto;

public interface ProductoService {

	Producto guardar(Producto producto);

	Producto buscarPorId(Integer id);

	List<Producto> buscarPorCategoria(String categoria);

	List<Producto> buscarPorMarca(String marca);

	List<Producto> buscarPorRangoDePrecio(double min, double max);

	List<Producto> buscarConStockCritico();

	List<Producto> buscarPorDebajoDeUmbral(int umbral);

	List<String> obtenerAlertasDeStock();
}
