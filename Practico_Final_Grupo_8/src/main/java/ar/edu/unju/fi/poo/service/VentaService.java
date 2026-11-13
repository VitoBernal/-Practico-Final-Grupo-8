package ar.edu.unju.fi.poo.service;

import java.time.LocalDate;
import java.util.List;

import ar.edu.unju.fi.poo.dto.CuotaEstadoRequestDto;
import ar.edu.unju.fi.poo.dto.CuotaResponseDto;
import ar.edu.unju.fi.poo.dto.EstadoCuentaResponseDto;
import ar.edu.unju.fi.poo.dto.VentaRequestDto;
import ar.edu.unju.fi.poo.dto.VentaResponseDto;

public interface VentaService {

	// segun la modalidad registra una venta de contado o una financiada
	VentaResponseDto registrarVenta(VentaRequestDto ventaDto);

	VentaResponseDto buscarPorId(Integer id);

	List<VentaResponseDto> buscarTodas();

	List<VentaResponseDto> buscarPorCliente(Integer clienteId);

	EstadoCuentaResponseDto obtenerEstadoDeCuenta(Integer clienteId);

	List<VentaResponseDto> buscarPorRangoDeFechas(LocalDate desde, LocalDate hasta);

	List<VentaResponseDto> buscarPorModalidad(String modalidad);

	// por ahora solo se puede pasar una cuota a PAGADA
	CuotaResponseDto cambiarEstadoCuota(Integer ventaId, int numeroCuota, CuotaEstadoRequestDto estadoDto);
}
