package ar.edu.unju.fi.poo.dto;

public class DetalleVentaResponseDto {

	private Integer productoId;
	private String productoNombre;
	private int cantidad;
	private double precioBaseAplicado;
	private double subtotal;

	public DetalleVentaResponseDto() {
	}

	public Integer getProductoId() {
		return productoId;
	}

	public void setProductoId(Integer productoId) {
		this.productoId = productoId;
	}

	public String getProductoNombre() {
		return productoNombre;
	}

	public void setProductoNombre(String productoNombre) {
		this.productoNombre = productoNombre;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getPrecioBaseAplicado() {
		return precioBaseAplicado;
	}

	public void setPrecioBaseAplicado(double precioBaseAplicado) {
		this.precioBaseAplicado = precioBaseAplicado;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
}
