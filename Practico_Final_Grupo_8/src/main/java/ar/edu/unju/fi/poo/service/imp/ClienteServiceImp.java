package ar.edu.unju.fi.poo.service.imp;

import java.util.List;
import org.springframework.stereotype.Service;

import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.TipoCliente;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.service.ClienteService;
import jakarta.transaction.Transactional;

@Service
public class ClienteServiceImp implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImp(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public Cliente guardarCliente(Cliente cliente) throws ClienteException {
        // Validaciones generales de campos obligatorios
        if (cliente == null) {
            throw new ClienteException("El cliente no puede ser nulo");
        }

        if (cliente.getTipoCliente() == null) {
            throw new ClienteException("Debe indicar el tipo de cliente");
        }

        if (cliente.getNumeroIdentificacion() == null || cliente.getNumeroIdentificacion().isBlank()) {
            throw new ClienteException("El numero de identificacion es obligatorio");
        }

        if (cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new ClienteException("El email es obligatorio");
        }

        // limpieza de espacios en blanco
        String numeroIdentificacion = cliente.getNumeroIdentificacion().trim();
        String email = cliente.getEmail().trim();
        cliente.setNumeroIdentificacion(numeroIdentificacion);
        cliente.setEmail(email);

        if (cliente.getTelefono() != null) {
            cliente.setTelefono(cliente.getTelefono().trim());
        }
        if (cliente.getDireccion() != null) {
            cliente.setDireccion(cliente.getDireccion().trim());
        }

        // control de duplicados por identificacion o Email
        validarClienteDuplicado(numeroIdentificacion, email);

        // reglas de negocio segun tipo de cliente
        if (cliente.getTipoCliente() == TipoCliente.PARTICULAR) {
            if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
                throw new ClienteException("El nombre es obligatorio para clientes particulares");
            }

            if (cliente.getApellido() == null || cliente.getApellido().isBlank()) {
                throw new ClienteException("El apellido es obligatorio para clientes particulares");
            }

            cliente.setNombre(cliente.getNombre().trim());
            cliente.setApellido(cliente.getApellido().trim());
            // clientes particulares no llevan razon social
            cliente.setRazonSocial(null);
        }

        if (cliente.getTipoCliente() == TipoCliente.EMPRESA) {
            if (cliente.getRazonSocial() == null || cliente.getRazonSocial().isBlank()) {
                throw new ClienteException("La razon social es obligatoria para empresas");
            }

            cliente.setRazonSocial(cliente.getRazonSocial().trim());
            // clientes empresa no llevan nombre ni apellido individuales
            cliente.setNombre(null);
            cliente.setApellido(null);
        }

        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente buscarClientePorNumeroIdentificacion(String numeroIdentificacion) throws ClienteException {
        if (numeroIdentificacion == null || numeroIdentificacion.isBlank()) {
            throw new ClienteException("Debe indicar un numero de identificacion");
        }

        String dniCuit = numeroIdentificacion.trim();
        Cliente cliente = clienteRepository.findByNumeroIdentificacion(dniCuit);
        if (cliente == null) {
            throw new ClienteException("No existe un cliente con el numero de identificacion: " + dniCuit);
        }
        return cliente;
    }

    @Override
    public void validarClienteDuplicado(String numeroIdentificacion, String email) throws ClienteException {
        // Verifica si el numero de identificacion ya esta registrado
        if (numeroIdentificacion != null && !numeroIdentificacion.isBlank()) {
            if (clienteRepository.findByNumeroIdentificacion(numeroIdentificacion) != null) {
                throw new ClienteException(
                        "Ya existe un cliente con el numero de identificacion: " + numeroIdentificacion);
            }
        }

        // verifica si el email ya esta registrado
        if (email != null && !email.isBlank()) {
            if (clienteRepository.findByEmail(email) != null) {
                throw new ClienteException("Ya existe un cliente con el email: " + email);
            }
        }
    }

    @Override
    public List<Cliente> buscarClientesPorNombreORazonSocial(String nombreRazonSocial) throws ClienteException {
        if (nombreRazonSocial == null || nombreRazonSocial.isBlank()) {
            return List.of();
        }
        String valor = nombreRazonSocial.trim();
        //busca coincidencias parciales en nombre, apellido o razon social
        return clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrRazonSocialContainingIgnoreCase(valor, valor, valor);
    }

    @Override
    public List<Cliente> obtenerTodosLosClientes() throws ClienteException {
        return clienteRepository.findAll();
    }

    @Override
    public List<Cliente> obtenerClientesPorTipo(TipoCliente tipoCliente) throws ClienteException {
        if (tipoCliente == null) {
            throw new ClienteException("Debe indicar el tipo de cliente");
        }
        return clienteRepository.findByTipo(tipoCliente);
    }
}
