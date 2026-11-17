package ar.edu.unju.fi.poo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import ar.edu.unju.fi.poo.dto.ErrorResponseDto;
import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.model.TipoCliente;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Alta, modificacion, baja logica y consultas de clientes particulares y empresas")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "Crear cliente", description = "Registra un cliente. Si es PARTICULAR lleva nombre y "
            + "apellido, si es EMPRESA lleva razon social. El DNI/CUIT y el email son obligatorios y no se pueden "
            + "repetir. El cliente queda habilitado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado"),
            @ApiResponse(responseCode = "400", description = "Faltan datos obligatorios o el email no tiene un formato valido",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un cliente con ese DNI/CUIT o con ese email",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
    @PostMapping
    public ResponseEntity<ClienteResponseDto> crearCliente(@Valid @RequestBody ClienteRequestDto cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.guardarCliente(cliente));
    }

    @Operation(summary = "Modificar cliente", description = "Modifica todos los datos del cliente con las mismas "
            + "reglas que al crearlo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente modificado"),
            @ApiResponse(responseCode = "400", description = "Faltan datos obligatorios o el email no tiene un formato valido",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No existe el cliente",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "El DNI/CUIT o el email ya lo tiene otro cliente",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
    @PutMapping("/{id}")
    public ClienteResponseDto actualizarCliente(
            @Parameter(description = "Id del cliente", example = "1") @PathVariable Integer id,
            @Valid @RequestBody ClienteRequestDto cliente) {
        return clienteService.actualizarCliente(id, cliente);
    }

    @Operation(summary = "Dar de baja un cliente", description = "Baja logica: el cliente no se borra porque puede "
            + "tener ventas, queda inhabilitado y ya no puede comprar")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente inhabilitado"),
            @ApiResponse(responseCode = "400", description = "El cliente ya estaba inhabilitado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No existe el cliente",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(
            @Parameter(description = "Id del cliente", example = "1") @PathVariable Integer id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Habilitar o inhabilitar cliente", description = "Cambia el estado del cliente con "
            + "habilitado en true o en false. Un cliente inhabilitado no puede comprar.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado del cliente actualizado"),
            @ApiResponse(responseCode = "400", description = "Falta el campo habilitado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No existe el cliente",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
    @PatchMapping("/{id}")
    public ClienteResponseDto cambiarEstadoCliente(
            @Parameter(description = "Id del cliente", example = "1") @PathVariable Integer id,
            @Valid @RequestBody ClienteEstadoRequestDto cliente) {
        return clienteService.actualizarEstadoCliente(id, cliente.getHabilitado());
    }

    @Operation(summary = "Listar clientes", description = "Devuelve todos los clientes, habilitados e inhabilitados")
    @ApiResponse(responseCode = "200", description = "Listado de clientes")
    @GetMapping
    public List<ClienteResponseDto> listarClientes() {
        return clienteService.obtenerTodosLosClientes();
    }

    @Operation(summary = "Buscar cliente por id", description = "Devuelve los datos de un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe el cliente",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
    @GetMapping("/{id}")
    public ClienteResponseDto BuscarPorID(
            @Parameter(description = "Id del cliente", example = "1") @PathVariable Integer id) {
        return clienteService.buscarClientePorId(id);
    }

    @Operation(summary = "Buscar cliente por DNI o CUIT", description = "Busca el cliente con ese numero de "
            + "identificacion exacto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un cliente con ese DNI o CUIT",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
    @GetMapping("/identificacion/{numero}") // DNI o CUIT
    public ClienteResponseDto BuscarPorNumeroIdentificacion(
            @Parameter(description = "DNI o CUIT, sin puntos ni guiones", example = "30111222") @PathVariable String numero) {
        return clienteService.buscarClientePorNumeroIdentificacion(numero);
    }

    @Operation(summary = "Buscar clientes por nombre o razon social", description = "Devuelve los clientes cuyo "
            + "nombre, apellido o razon social contiene el texto, sin importar mayusculas. Si no hay ninguno "
            + "devuelve una lista vacia.")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados")
    @GetMapping("/nombre/{texto}") // (nombre, apellido o razon social)
    public List<ClienteResponseDto> buscarPorNombre(
            @Parameter(description = "Parte del nombre, apellido o razon social", example = "gomez") @PathVariable("texto") String texto) {
        return clienteService.buscarClientesPorNombreORazonSocial(texto);
    }

    @Operation(summary = "Listar clientes por tipo", description = "Devuelve solo los clientes particulares o solo "
            + "las empresas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes de ese tipo"),
            @ApiResponse(responseCode = "400", description = "El tipo no es PARTICULAR ni EMPRESA",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
    @GetMapping("/tipo/{tipo}")
    public List<ClienteResponseDto> clientesPorTipo(
            @Parameter(description = "PARTICULAR o EMPRESA", example = "PARTICULAR") @PathVariable("tipo") String tipo) {
        TipoCliente tipoCliente;

        try {
            tipoCliente = TipoCliente.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ClienteException("El tipo debe ser PARTICULAR o EMPRESA");
        }

        return clienteService.obtenerClientesPorTipo(tipoCliente);
    }
}
