package ar.edu.unju.fi.poo.dto;

public class CuotaResponseDto {

	private Integer ventaId;
	private String ventaCodigoComprobante;
	private int numero;
	private double importe;
	private String fechaVencimiento;
	private String estado;

	public CuotaResponseDto() {
	}

	public Integer getVentaId() {
		return ventaId;
	}

	public void setVentaId(Integer ventaId) {
		this.ventaId = ventaId;
	}

	public String getVentaCodigoComprobante() {
		return ventaCodigoComprobante;
	}

	public void setVentaCodigoComprobante(String ventaCodigoComprobante) {
		this.ventaCodigoComprobante = ventaCodigoComprobante;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public String getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(String fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
}
