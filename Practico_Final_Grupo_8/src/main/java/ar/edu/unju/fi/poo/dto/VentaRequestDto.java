package ar.edu.unju.fi.poo.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class VentaRequestDto {

	@Schema(description = "Id del cliente que compra", example = "1")
	@NotNull(message = "El cliente es obligatorio")
	private Integer clienteId;

	@Schema(description = "Modalidad de pago", example = "CONTADO", allowableValues = { "CONTADO", "FINANCIADA" })
	@NotBlank(message = "La modalidad es obligatoria")
	private String modalidad;

	@Schema(description = "Productos de la venta, cada uno con su id y la cantidad")
	@NotEmpty(message = "La venta tiene que tener al menos un producto")
	@Valid
	private List<ItemVentaRequestDto> items = new ArrayList<>();

	@Schema(description = "Codigo del cupon de descuento, opcional y solo para CONTADO", example = "DESC10")
	private String cuponCodigo;

	@Schema(description = "Porcentaje de recargo entre 0 y 100, obligatorio solo para FINANCIADA", example = "20")
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
