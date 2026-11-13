package ar.edu.unju.fi.poo.dto;

import java.util.ArrayList;
import java.util.List;

public class EstadoCuentaResponseDto {

	private Integer clienteId;
	private String clienteNombreCompleto;
	private int cuotasPendientes;
	private int cuotasVencidas;
	private double totalAdeudado;
	private List<CuotaResponseDto> cuotas = new ArrayList<>();

	public EstadoCuentaResponseDto() {
	}

	public Integer getClienteId() {
		return clienteId;
	}

	public void setClienteId(Integer clienteId) {
		this.clienteId = clienteId;
	}

	public String getClienteNombreCompleto() {
		return clienteNombreCompleto;
	}

	public void setClienteNombreCompleto(String clienteNombreCompleto) {
		this.clienteNombreCompleto = clienteNombreCompleto;
	}

	public int getCuotasPendientes() {
		return cuotasPendientes;
	}

	public void setCuotasPendientes(int cuotasPendientes) {
		this.cuotasPendientes = cuotasPendientes;
	}

	public int getCuotasVencidas() {
		return cuotasVencidas;
	}

	public void setCuotasVencidas(int cuotasVencidas) {
		this.cuotasVencidas = cuotasVencidas;
	}

	public double getTotalAdeudado() {
		return totalAdeudado;
	}

	public void setTotalAdeudado(double totalAdeudado) {
		this.totalAdeudado = totalAdeudado;
	}

	public List<CuotaResponseDto> getCuotas() {
		return cuotas;
	}

	public void setCuotas(List<CuotaResponseDto> cuotas) {
		this.cuotas = cuotas;
	}
}
