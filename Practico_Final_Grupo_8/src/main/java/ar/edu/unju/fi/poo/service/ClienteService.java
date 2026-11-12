package ar.edu.unju.fi.poo.service;

import java.util.List;

import ar.edu.unju.fi.poo.dto.ClienteRequestDto;
import ar.edu.unju.fi.poo.dto.ClienteResponseDto;
import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.model.TipoCliente;

public interface ClienteService {

    // recibe RequestDto, entrada del usuario y devuelve ResponseDto
    ClienteResponseDto guardarCliente(ClienteRequestDto clienteRequestDto) throws ClienteException;

    ClienteResponseDto buscarClientePorNumeroIdentificacion(String numeroIdentificacion) throws ClienteException;

    ClienteResponseDto buscarClientePorId(Integer id) throws ClienteException;

    // valida que no exista otro cliente con el mismo DNI o CUIT y email
    void validarClienteDuplicado(String numeroIdentificacion, String email) throws ClienteException;

    // busca por nombre, apellido o razon social, sin importar mayusculas/minusculas
    List<ClienteResponseDto> buscarClientesPorNombreORazonSocial(String nombreRazonSocial) throws ClienteException;

    List<ClienteResponseDto> obtenerTodosLosClientes() throws ClienteException;

    List<ClienteResponseDto> obtenerClientesPorTipo(TipoCliente tipoCliente) throws ClienteException;

    // borrado logico, pone habilitado=false en vez de eliminar el registro
    void eliminarCliente(Integer id) throws ClienteException;
}
