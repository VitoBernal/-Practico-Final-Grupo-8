package ar.edu.unju.fi.poo.service;

import java.util.List;

import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.TipoCliente;

public interface ClienteService {

	Cliente guardarCliente(Cliente cliente) throws ClienteException;

	Cliente buscarClientePorNumeroIdentificacion(String numeroIdentificacion) throws ClienteException;

	void validarClienteDuplicado(String numeroIdentificacion, String email) throws ClienteException;

	List<Cliente> buscarClientesPorNombreORazonSocial(String nombreRazonSocial) throws ClienteException;

	List<Cliente> obtenerTodosLosClientes() throws ClienteException;

	List<Cliente> obtenerClientesPorTipo(TipoCliente tipoCliente) throws ClienteException;
}
