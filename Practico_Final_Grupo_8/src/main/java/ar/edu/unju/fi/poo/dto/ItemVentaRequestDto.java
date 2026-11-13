package ar.edu.unju.fi.poo.dto;

import jakarta.validation.constraints.NotNull;

public class ItemVentaRequestDto {

	@NotNull(message = "El id del producto es obligatorio")
	private Integer productoId;

	@NotNull(message = "La cantidad es obligatoria")
	private Integer cantidad;

	public ItemVentaRequestDto() {
	}

	public ItemVentaRequestDto(Integer productoId, Integer cantidad) {
		this.productoId = productoId;
		this.cantidad = cantidad;
	}

	public Integer getProductoId() {
		return productoId;
	}

	public void setProductoId(Integer productoId) {
		this.productoId = productoId;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
}
