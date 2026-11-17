package ar.edu.unju.fi.poo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.dto.ErrorResponseDto;
import ar.edu.unju.fi.poo.dto.EstadoCuentaResponseDto;
import ar.edu.unju.fi.poo.dto.PagoCuotaResponseDto;
import ar.edu.unju.fi.poo.dto.VentaRequestDto;
import ar.edu.unju.fi.poo.dto.VentaResponseDto;
import ar.edu.unju.fi.poo.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas", description = "Registro de ventas de contado o financiadas, consultas, estado de cuenta y cobro de cuotas")
public class VentaController {

	private static final String VENTA_CONTADO = """
			{
			  "clienteId": 1,
			  "modalidad": "CONTADO",
			  "cuponCodigo": "DESC10",
			  "items": [
			    { "productoId": 1, "cantidad": 2 },
			    { "productoId": 2, "cantidad": 1 }
			  ]
			}""";

	private static final String VENTA_FINANCIADA = """
			{
			  "clienteId": 1,
			  "modalidad": "FINANCIADA",
			  "porcentajeRecargo": 20,
			  "items": [
			    { "productoId": 1, "cantidad": 1 },
			    { "productoId": 3, "cantidad": 2 }
			  ]
			}""";

	@Autowired
	private VentaService ventaService;

	@Operation(summary = "Registrar venta", description = "Registra una venta de contado o financiada. En items se manda la "
			+ "lista de productos, cada uno con su productoId y su cantidad. Si un producto viene repetido se suman las "
			+ "cantidades. Antes de vender se revisa el stock de todo el pedido y despues se descuenta. "
			+ "CONTADO: puede llevar cuponCodigo para un descuento sobre el total, no lleva recargo y la venta queda SALDADA. "
			+ "FINANCIADA: lleva porcentajeRecargo entre 0 y 100, no admite cupon, se generan 6 cuotas mensuales "
			+ "ADEUDADAS y la venta queda PENDIENTE.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
					@ExampleObject(name = "Contado con cupon", value = VENTA_CONTADO),
					@ExampleObject(name = "Financiada con recargo", value = VENTA_FINANCIADA) })))
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Venta registrada"),
			@ApiResponse(responseCode = "400", description = "Datos invalidos: falta el cliente, la modalidad o los "
					+ "productos, una cantidad es 0, la modalidad no existe, hay cupon en una financiada o recargo en "
					+ "una de contado", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "No existe el cliente o alguno de los productos",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "422", description = "No cumple una regla de negocio: stock insuficiente, "
					+ "cupon vencido, usado o inexistente, cliente inhabilitado o recargo fuera de 0 a 100",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping
	public ResponseEntity<VentaResponseDto> registrarVenta(@Valid @RequestBody VentaRequestDto ventaDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrarVenta(ventaDto));
	}

	@Operation(summary = "Listar ventas", description = "Devuelve todas las ventas, de la mas reciente a la mas vieja")
	@ApiResponse(responseCode = "200", description = "Listado de ventas")
	@GetMapping
	public ResponseEntity<List<VentaResponseDto>> listarVentas() {
		return ResponseEntity.ok(ventaService.buscarTodas());
	}

	@Operation(summary = "Buscar venta por id", description = "Devuelve la venta con sus detalles y, si es "
			+ "financiada, sus 6 cuotas con el estado de cada una")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Venta encontrada"),
			@ApiResponse(responseCode = "404", description = "No existe la venta",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/{id}")
	public ResponseEntity<VentaResponseDto> buscarPorId(
			@Parameter(description = "Id de la venta", example = "1") @PathVariable Integer id) {
		return ResponseEntity.ok(ventaService.buscarPorId(id));
	}

	@Operation(summary = "Historial de ventas de un cliente", description = "Devuelve las ventas del cliente, "
			+ "de la mas reciente a la mas vieja")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Historial del cliente"),
			@ApiResponse(responseCode = "404", description = "No existe el cliente",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/cliente/{clienteId}")
	public ResponseEntity<List<VentaResponseDto>> buscarPorCliente(
			@Parameter(description = "Id del cliente", example = "1") @PathVariable Integer clienteId) {
		return ResponseEntity.ok(ventaService.buscarPorCliente(clienteId));
	}

	@Operation(summary = "Estado de cuenta de un cliente", description = "Devuelve los datos del cliente, su "
			+ "historial de ventas y las cuotas sin pagar (ADEUDADA o VENCIDA) de sus ventas financiadas, de la que "
			+ "vence primero a la ultima, con el total adeudado. En cuotasVencidas se cuentan las cuotas VENCIDA y "
			+ "las adeudadas cuya fecha de vencimiento ya paso.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Estado de cuenta del cliente"),
			@ApiResponse(responseCode = "404", description = "No existe el cliente",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/cliente/{clienteId}/estado-cuenta")
	public ResponseEntity<EstadoCuentaResponseDto> obtenerEstadoDeCuenta(
			@Parameter(description = "Id del cliente", example = "1") @PathVariable Integer clienteId) {
		return ResponseEntity.ok(ventaService.obtenerEstadoDeCuenta(clienteId));
	}

	@Operation(summary = "Ventas entre dos fechas", description = "Devuelve las ventas hechas entre las dos fechas, "
			+ "incluidas. Las fechas van con formato aaaa-mm-dd.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Ventas del periodo"),
			@ApiResponse(responseCode = "400", description = "Falta una fecha, tiene mal formato o desde es "
					+ "posterior a hasta", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/fechas")
	public ResponseEntity<List<VentaResponseDto>> buscarPorRangoDeFechas(
			@Parameter(description = "Fecha desde", example = "2026-11-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@Parameter(description = "Fecha hasta", example = "2026-11-30") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
		return ResponseEntity.ok(ventaService.buscarPorRangoDeFechas(desde, hasta));
	}

	@Operation(summary = "Filtrar ventas por modalidad", description = "Devuelve solo las ventas de contado o solo "
			+ "las financiadas")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Ventas de esa modalidad"),
			@ApiResponse(responseCode = "400", description = "La modalidad no es CONTADO ni FINANCIADA",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/modalidad/{modalidad}")
	public ResponseEntity<List<VentaResponseDto>> buscarPorModalidad(
			@Parameter(description = "CONTADO o FINANCIADA", example = "FINANCIADA") @PathVariable String modalidad) {
		return ResponseEntity.ok(ventaService.buscarPorModalidad(modalidad));
	}

	@Operation(summary = "Cobrar cuota", description = "Cobra la proxima cuota sin pagar de una venta financiada. "
			+ "No se elige el numero de cuota: siempre se cobra la primera pendiente, asi no se saltea ninguna ni se "
			+ "cobra una cuota futura si hay anteriores sin pagar. La cuota se puede pagar hasta el dia de su "
			+ "vencimiento inclusive. Si ya vencio pasa a VENCIDA, no se cobra y tampoco se pueden cobrar las "
			+ "siguientes. Cuando se paga la ultima cuota la venta pasa a SALDADA.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Cuota cobrada: devuelve la cuota pagada, el "
			+ "estado de la venta, cuantas cuotas faltan y el saldo pendiente"),
			@ApiResponse(responseCode = "400", description = "La venta es de contado y no tiene cuotas",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "No existe la venta",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "La venta ya esta saldada, no quedan cuotas para cobrar",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "422", description = "La cuota que toca pagar esta vencida: queda VENCIDA y "
					+ "no se cobra", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping("/{ventaId}/pagos")
	public ResponseEntity<PagoCuotaResponseDto> pagarCuota(
			@Parameter(description = "Id de la venta financiada", example = "3") @PathVariable Integer ventaId) {
		return ResponseEntity.ok(ventaService.pagarCuota(ventaId, LocalDate.now()));
	}
}
