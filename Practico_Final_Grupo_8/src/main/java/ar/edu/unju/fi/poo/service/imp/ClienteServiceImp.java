package ar.edu.unju.fi.poo.service.imp;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import ar.edu.unju.fi.poo.dto.ClienteRequestDto;
import ar.edu.unju.fi.poo.dto.ClienteResponseDto;
import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.exception.ConflictoException;
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
    public ClienteResponseDto guardarCliente(ClienteRequestDto clienteDto) throws ClienteException {
        validarYPrepararCliente(clienteDto, null);

        Cliente cliente = clienteMapper.toEntity(clienteDto);
        @SuppressWarnings("null")
        Cliente guardado = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(guardado);
    }

    @Override
    @Transactional
    public ClienteResponseDto actualizarCliente(Integer id, ClienteRequestDto clienteDto) throws ClienteException {
        if (id == null) {
            throw new ClienteException("Debe indicar un id");
        }

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("No existe un cliente con el id: " + id));
        validarYPrepararCliente(clienteDto, id);
        clienteMapper.actualizarDesdeRequestDto(clienteDto, cliente);
        return clienteMapper.toResponseDto(clienteRepository.save(cliente));
    }

    // valida y prepara el cliente antes de guardarlo o actualizarlo
    private void validarYPrepararCliente(ClienteRequestDto clienteDto, Integer idActual) {
        if (clienteDto == null) {
            throw new ClienteException("El cliente no puede ser nulo");
        }

        if (clienteDto.getTipo() == null) {
            throw new ClienteException("Debe indicar el tipo de cliente");
        }

        if (clienteDto.getNumeroIdentificacion() == null || clienteDto.getNumeroIdentificacion().isBlank()) {
            throw new ClienteException("El numero de identificacion es obligatorio");
        }

        if (clienteDto.getEmail() == null || clienteDto.getEmail().isBlank()) {
            throw new ClienteException("El email es obligatorio");
        }

        // limpieza de espacios en blanco
        String numeroIdentificacion = clienteDto.getNumeroIdentificacion().trim();
        String email = clienteDto.getEmail().trim();
        clienteDto.setNumeroIdentificacion(numeroIdentificacion);
        clienteDto.setEmail(email);

        if (clienteDto.getTelefono() != null) {
            clienteDto.setTelefono(clienteDto.getTelefono().trim());
        }
        if (clienteDto.getDireccion() != null) {
            clienteDto.setDireccion(clienteDto.getDireccion().trim());
        }

        // Control de duplicados
        validarClienteDuplicado(numeroIdentificacion, email, idActual);

        // Reglas de negocio segun tipo de cliente
        if (clienteDto.getTipo() == TipoCliente.PARTICULAR) {
            if (clienteDto.getNombre() == null || clienteDto.getNombre().isBlank()) {
                throw new ClienteException("El nombre es obligatorio para clientes particulares");
            }
            if (clienteDto.getApellido() == null || clienteDto.getApellido().isBlank()) {
                throw new ClienteException("El apellido es obligatorio para clientes particulares");
            }
            clienteDto.setNombre(clienteDto.getNombre().trim());
            clienteDto.setApellido(clienteDto.getApellido().trim());
            clienteDto.setRazonSocial(null);
        }

        if (clienteDto.getTipo() == TipoCliente.EMPRESA) {
            if (clienteDto.getRazonSocial() == null || clienteDto.getRazonSocial().isBlank()) {
                throw new ClienteException("La razon social es obligatoria para empresas");
            }
            clienteDto.setRazonSocial(clienteDto.getRazonSocial().trim());
            clienteDto.setNombre(null);
            clienteDto.setApellido(null);
        }

    }

    @Override
    @Transactional
    public ClienteResponseDto actualizarEstadoCliente(Integer id, Boolean habilitado) throws ClienteException {
        if (id == null) {
            throw new ClienteException("Debe indicar un id");
        }
        if (habilitado == null) {
            throw new ClienteException("Debe indicar si el cliente esta habilitado");
        }

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("No existe un cliente con el id: " + id));
        cliente.setHabilitado(habilitado);
        return clienteMapper.toResponseDto(clienteRepository.save(cliente));
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
        validarClienteDuplicado(numeroIdentificacion, email, null);
    }

    private void validarClienteDuplicado(String numeroIdentificacion, String email, Integer idActual) {
        if (numeroIdentificacion != null && !numeroIdentificacion.isBlank()) {
            Cliente clienteConIdentificacion = clienteRepository.findByNumeroIdentificacion(numeroIdentificacion);
            if (clienteConIdentificacion != null && !Objects.equals(clienteConIdentificacion.getId(), idActual)) {
                throw new ConflictoException(
                        "Ya existe un cliente con el numero de identificacion: " + numeroIdentificacion);
            }
        }
        if (email != null && !email.isBlank()) {
            Cliente clienteConEmail = clienteRepository.findByEmail(email);
            if (clienteConEmail != null && !Objects.equals(clienteConEmail.getId(), idActual)) {
                throw new ConflictoException("Ya existe un cliente con el email: " + email);
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
        if (id == null) {
            throw new ClienteException("Debe indicar un id");
        }

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
