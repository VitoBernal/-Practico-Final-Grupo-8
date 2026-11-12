package ar.edu.unju.fi.poo.service.imp;

import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unju.fi.poo.dto.ClienteRequestDto;
import ar.edu.unju.fi.poo.dto.ClienteResponseDto;
import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.mapper.ClienteMapper;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.TipoCliente;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.service.ClienteService;
import jakarta.transaction.Transactional;

@Service
public class ClienteServiceImp implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteServiceImp(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Override
    @Transactional
    public ClienteResponseDto guardarCliente(ClienteRequestDto requestDto) throws ClienteException {
        if (requestDto == null) {
            throw new ClienteException("El cliente no puede ser nulo");
        }

        if (requestDto.getTipo() == null) {
            throw new ClienteException("Debe indicar el tipo de cliente");
        }

        if (requestDto.getNumeroIdentificacion() == null || requestDto.getNumeroIdentificacion().isBlank()) {
            throw new ClienteException("El numero de identificacion es obligatorio");
        }

        if (requestDto.getEmail() == null || requestDto.getEmail().isBlank()) {
            throw new ClienteException("El email es obligatorio");
        }

        // Limpieza de espacios en blanco
        String numeroIdentificacion = requestDto.getNumeroIdentificacion().trim();
        String email = requestDto.getEmail().trim();
        requestDto.setNumeroIdentificacion(numeroIdentificacion);
        requestDto.setEmail(email);

        if (requestDto.getTelefono() != null) {
            requestDto.setTelefono(requestDto.getTelefono().trim());
        }
        if (requestDto.getDireccion() != null) {
            requestDto.setDireccion(requestDto.getDireccion().trim());
        }

        // Control de duplicados
        validarClienteDuplicado(numeroIdentificacion, email);

        // Reglas de negocio segun tipo de cliente
        if (requestDto.getTipo() == TipoCliente.PARTICULAR) {
            if (requestDto.getNombre() == null || requestDto.getNombre().isBlank()) {
                throw new ClienteException("El nombre es obligatorio para clientes particulares");
            }
            if (requestDto.getApellido() == null || requestDto.getApellido().isBlank()) {
                throw new ClienteException("El apellido es obligatorio para clientes particulares");
            }
            requestDto.setNombre(requestDto.getNombre().trim());
            requestDto.setApellido(requestDto.getApellido().trim());
            requestDto.setRazonSocial(null);
        }

        if (requestDto.getTipo() == TipoCliente.EMPRESA) {
            if (requestDto.getRazonSocial() == null || requestDto.getRazonSocial().isBlank()) {
                throw new ClienteException("La razon social es obligatoria para empresas");
            }
            requestDto.setRazonSocial(requestDto.getRazonSocial().trim());
            requestDto.setNombre(null);
            requestDto.setApellido(null);
        }

        Cliente cliente = clienteMapper.toEntity(requestDto);
        @SuppressWarnings("null")
		Cliente guardado = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(guardado);
    }

    @Override
    public ClienteResponseDto buscarClientePorNumeroIdentificacion(String numeroIdentificacion)
            throws ClienteException {
        if (numeroIdentificacion == null || numeroIdentificacion.isBlank()) {
            throw new ClienteException("Debe indicar un numero de identificacion");
        }
        String dniCuit = numeroIdentificacion.trim();
        Cliente cliente = clienteRepository.findByNumeroIdentificacion(dniCuit);
        if (cliente == null) {
            throw new ClienteException("No existe un cliente con el numero de identificacion: " + dniCuit);
        }
        return clienteMapper.toResponseDto(cliente);
    }

    @Override
    public ClienteResponseDto buscarClientePorId(Integer id) throws ClienteException {
        if (id == null) {
            throw new ClienteException("Debe indicar un id");
        }
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("No existe un cliente con el id: " + id));
        return clienteMapper.toResponseDto(cliente);
    }

    @Override
    public void validarClienteDuplicado(String numeroIdentificacion, String email) throws ClienteException {
        if (numeroIdentificacion != null && !numeroIdentificacion.isBlank()) {
            if (clienteRepository.findByNumeroIdentificacion(numeroIdentificacion) != null) {
                throw new ClienteException(
                        "Ya existe un cliente con el numero de identificacion: " + numeroIdentificacion);
            }
        }
        if (email != null && !email.isBlank()) {
            if (clienteRepository.findByEmail(email) != null) {
                throw new ClienteException("Ya existe un cliente con el email: " + email);
            }
        }
    }

    @Override
    public List<ClienteResponseDto> buscarClientesPorNombreORazonSocial(String nombreRazonSocial)
            throws ClienteException {
        if (nombreRazonSocial == null || nombreRazonSocial.isBlank()) {
            return List.of();
        }
        String valor = nombreRazonSocial.trim();
        return clienteRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrRazonSocialContainingIgnoreCase(
                        valor, valor, valor)
                .stream()
                .map(clienteMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ClienteResponseDto> obtenerTodosLosClientes() throws ClienteException {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ClienteResponseDto> obtenerClientesPorTipo(TipoCliente tipoCliente) throws ClienteException {
        if (tipoCliente == null) {
            throw new ClienteException("Debe indicar el tipo de cliente");
        }
        return clienteRepository.findByTipo(tipoCliente)
                .stream()
                .map(clienteMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void eliminarCliente(Integer id) throws ClienteException {
        // Borrado logico, busca la entidad real y pone habilitado=false
        @SuppressWarnings("null")
		Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("No existe un cliente con el id: " + id));

        if (!cliente.isHabilitado()) {
            throw new ClienteException("El cliente con id " + id + " ya se encuentra deshabilitado");
        }

        cliente.setHabilitado(false);
        clienteRepository.save(cliente);
    }
}
