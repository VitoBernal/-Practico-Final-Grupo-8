package ar.edu.unju.fi.poo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ar.edu.unju.fi.poo.dto.ClienteEstadoRequestDto;
import ar.edu.unju.fi.poo.dto.ClienteRequestDto;
import ar.edu.unju.fi.poo.dto.ClienteResponseDto;
import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.model.TipoCliente;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDto> crearCliente(@Valid @RequestBody ClienteRequestDto cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.guardarCliente(cliente));
    }

    @PutMapping("/{id}")
    public ClienteResponseDto actualizarCliente(@PathVariable Integer id,@Valid @RequestBody ClienteRequestDto cliente) {
        return clienteService.actualizarCliente(id, cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Integer id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ClienteResponseDto cambiarEstadoCliente(@PathVariable Integer id,
            @Valid @RequestBody ClienteEstadoRequestDto cliente) {
        return clienteService.actualizarEstadoCliente(id, cliente.getHabilitado());
    }

    @GetMapping
    public List<ClienteResponseDto> listarClientes() {
        return clienteService.obtenerTodosLosClientes();
    }

    @GetMapping("/{id}")
    public ClienteResponseDto BuscarPorID(@PathVariable Integer id) {
        return clienteService.buscarClientePorId(id);
    }

    @GetMapping("/identificacion/{numero}") // DNI o CUIT
    public ClienteResponseDto BuscarPorNumeroIdentificacion(@PathVariable String numero) {
        return clienteService.buscarClientePorNumeroIdentificacion(numero);
    }

    @GetMapping("/nombre/{texto}") // (nombre, apellido o razon social)
    public List<ClienteResponseDto> buscarPorNombre(@PathVariable("texto") String texto) {
        return clienteService.buscarClientesPorNombreORazonSocial(texto);
    }

    @GetMapping("/tipo/{tipo}")
    public List<ClienteResponseDto> clientesPorTipo(@PathVariable("tipo") String tipo) {
        TipoCliente tipoCliente;

        try {
            tipoCliente = TipoCliente.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ClienteException("El tipo debe ser PARTICULAR o EMPRESA");
        }

        return clienteService.obtenerClientesPorTipo(tipoCliente);
    }
}
