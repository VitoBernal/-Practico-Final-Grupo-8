package ar.edu.unju.fi.poo.service;

import java.util.List;

import ar.edu.unju.fi.poo.dto.ProductoRequestDto;
import ar.edu.unju.fi.poo.dto.ProductoResponseDto;

public interface ProductoService {
	
	ProductoResponseDto guardar (ProductoRequestDto productoDto);
	
	ProductoResponseDto actualizar(Integer id, ProductoRequestDto productoDto);

	ProductoResponseDto buscarPorId(Integer id);

	List<ProductoResponseDto> buscarPorCategoria(String categoria);

	List<ProductoResponseDto> buscarPorMarca(String marca);

	List<ProductoResponseDto> buscarPorRangoDePrecio(double min, double max);

	List<ProductoResponseDto> buscarConStockCritico();

	List<ProductoResponseDto> buscarPorDebajoDeUmbral(int umbral);

	List<String> obtenerAlertasDeStock();
	
	List<ProductoResponseDto> traerTodos();
}
