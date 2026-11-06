package ar.edu.unju.fi.poo.service;

import java.util.List;

import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.TipoCliente;

public interface ClienteService {
	
	Cliente guardarCliente(Cliente cliente) throws ClienteException;
    
    Cliente buscarClientePorNumeroIdentificacion(String numeroIdentificacion) throws ClienteException;

    // valida que no exista otro cliente con el mismo DNI o CUIT y email,si existe lanza ClienteException
    void validarClienteDuplicado(String numeroIdentificacion, String email) throws ClienteException;

    //Busca por nombre, apellido o razón social,contiene el texto, sin importar mayusculas
    List<Cliente> buscarClientesPorNombreORazonSocial(String NombreRazonSocial) throws ClienteException;

    List<Cliente> obtenerTodosLosClientes() throws ClienteException;

    List<Cliente> obtenerClientesPorTipo(TipoCliente tipoCliente) throws ClienteException;
}
