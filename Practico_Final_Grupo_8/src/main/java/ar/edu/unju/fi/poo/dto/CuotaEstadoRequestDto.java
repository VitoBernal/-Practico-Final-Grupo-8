package ar.edu.unju.fi.poo.dto;

import jakarta.validation.constraints.NotBlank;

public class CuotaEstadoRequestDto {

	@NotBlank(message = "El estado es obligatorio")
	private String estado;

	public CuotaEstadoRequestDto() {
	}

	public CuotaEstadoRequestDto(String estado) {
		this.estado = estado;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
}
