package ar.edu.unju.fi.poo.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import ar.edu.unju.fi.poo.dto.ProductoResponseDto;
import ar.edu.unju.fi.poo.model.Producto;

@Mapper (componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductoMapper {
	
	@Mapping(target = "categoria", source = "categoria.nombre")
	ProductoResponseDto productoADto(Producto producto);
	
	List<ProductoResponseDto> productosADto(List<Producto> productos); 

}