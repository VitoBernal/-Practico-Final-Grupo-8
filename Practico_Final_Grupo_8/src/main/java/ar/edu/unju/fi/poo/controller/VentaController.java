package ar.edu.unju.fi.poo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.dto.CuotaEstadoRequestDto;
import ar.edu.unju.fi.poo.dto.CuotaResponseDto;
import ar.edu.unju.fi.poo.dto.EstadoCuentaResponseDto;
import ar.edu.unju.fi.poo.dto.VentaRequestDto;
import ar.edu.unju.fi.poo.dto.VentaResponseDto;
import ar.edu.unju.fi.poo.service.VentaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

	@Autowired
	private VentaService ventaService;

	@PostMapping
	public ResponseEntity<VentaResponseDto> registrarVenta(@Valid @RequestBody VentaRequestDto ventaDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrarVenta(ventaDto));
	}

	@GetMapping
	public ResponseEntity<List<VentaResponseDto>> listarVentas() {
		return ResponseEntity.ok(ventaService.buscarTodas());
	}

	@GetMapping("/{id}")
	public ResponseEntity<VentaResponseDto> buscarPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(ventaService.buscarPorId(id));
	}

	@GetMapping("/cliente/{clienteId}")
	public ResponseEntity<List<VentaResponseDto>> buscarPorCliente(@PathVariable Integer clienteId) {
		return ResponseEntity.ok(ventaService.buscarPorCliente(clienteId));
	}

	@GetMapping("/cliente/{clienteId}/estado-cuenta")
	public ResponseEntity<EstadoCuentaResponseDto> obtenerEstadoDeCuenta(@PathVariable Integer clienteId) {
		return ResponseEntity.ok(ventaService.obtenerEstadoDeCuenta(clienteId));
	}

	@GetMapping("/fechas")
	public ResponseEntity<List<VentaResponseDto>> buscarPorRangoDeFechas(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
		return ResponseEntity.ok(ventaService.buscarPorRangoDeFechas(desde, hasta));
	}

	@GetMapping("/modalidad/{modalidad}")
	public ResponseEntity<List<VentaResponseDto>> buscarPorModalidad(@PathVariable String modalidad) {
		return ResponseEntity.ok(ventaService.buscarPorModalidad(modalidad));
	}

	@PatchMapping("/{ventaId}/cuotas/{numero}")
	public ResponseEntity<CuotaResponseDto> cambiarEstadoCuota(@PathVariable Integer ventaId, @PathVariable int numero,
			@Valid @RequestBody CuotaEstadoRequestDto estadoDto) {
		return ResponseEntity.ok(ventaService.cambiarEstadoCuota(ventaId, numero, estadoDto));
	}
}
