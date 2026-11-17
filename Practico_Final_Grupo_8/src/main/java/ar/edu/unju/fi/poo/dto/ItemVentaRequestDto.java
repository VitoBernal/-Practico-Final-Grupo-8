package ar.edu.unju.fi.poo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class ItemVentaRequestDto {

	@Schema(description = "Id del producto", example = "1")
	@NotNull(message = "El id del producto es obligatorio")
	private Integer productoId;

	@Schema(description = "Cantidad que se compra, mayor a 0", example = "2")
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
