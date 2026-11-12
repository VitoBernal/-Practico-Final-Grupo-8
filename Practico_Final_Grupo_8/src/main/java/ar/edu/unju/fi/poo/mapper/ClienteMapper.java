package ar.edu.unju.fi.poo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ar.edu.unju.fi.poo.dto.ClienteRequestDto;
import ar.edu.unju.fi.poo.dto.ClienteResponseDto;
import ar.edu.unju.fi.poo.model.Cliente;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    // Cliente (entidad) → ClienteResponseDto (lo que devuelve la API)
    // "tipoCliente" en la entidad se mapea a "tipo" en el DTO
    // "nombreCompleto" lo calcula el método getNombreCompleto() de la entidad
    @Mapping(source = "tipoCliente", target = "tipo")
    @Mapping(source = "nombreCompleto", target = "nombreCompleto")
    ClienteResponseDto toResponseDto(Cliente cliente);

    // ClienteRequestDto (lo que llega en el body) → Cliente (entidad)
    @Mapping(source = "tipo", target = "tipoCliente")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ventas", ignore = true)
    @Mapping(target = "habilitado", ignore = true)
    Cliente toEntity(ClienteRequestDto requestDto);
}
