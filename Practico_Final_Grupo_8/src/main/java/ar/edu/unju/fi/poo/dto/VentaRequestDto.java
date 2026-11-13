package ar.edu.unju.fi.poo.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class VentaRequestDto {

	@NotNull(message = "El cliente es obligatorio")
	private Integer clienteId;

	@NotBlank(message = "La modalidad es obligatoria")
	private String modalidad;

	@NotEmpty(message = "La venta tiene que tener al menos un producto")
	@Valid
	private List<ItemVentaRequestDto> items = new ArrayList<>();

	// solo para contado
	private String cuponCodigo;

	// solo para financiada
	private Double porcentajeRecargo;

	public VentaRequestDto() {
	}

	public Integer getClienteId() {
		return clienteId;
	}

	public void setClienteId(Integer clienteId) {
		this.clienteId = clienteId;
	}

	public String getModalidad() {
		return modalidad;
	}

	public void setModalidad(String modalidad) {
		this.modalidad = modalidad;
	}

	public List<ItemVentaRequestDto> getItems() {
		return items;
	}

	public void setItems(List<ItemVentaRequestDto> items) {
		this.items = items;
	}

	public String getCuponCodigo() {
		return cuponCodigo;
	}

	public void setCuponCodigo(String cuponCodigo) {
		this.cuponCodigo = cuponCodigo;
	}

	public Double getPorcentajeRecargo() {
		return porcentajeRecargo;
	}

	public void setPorcentajeRecargo(Double porcentajeRecargo) {
		this.porcentajeRecargo = porcentajeRecargo;
	}
}
