package ar.edu.unju.fi.poo.dto;

public class PagoCuotaResponseDto {
	
	private Integer ventaId;
	private String ventaCodigoComprobante;
	private CuotaResponseDto cuotaPagada;
	private String estadoVenta;
	private int cuotasPendientes;
	private double saldoPendiente;
	
	public PagoCuotaResponseDto() {
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

	public CuotaResponseDto getCuotaPagada() {
		return cuotaPagada;
	}

	public void setCuotaPagada(CuotaResponseDto cuotaPagada) {
		this.cuotaPagada = cuotaPagada;
	}

	public String getEstadoVenta() {
		return estadoVenta;
	}

	public void setEstadoVenta(String estadoVenta) {
		this.estadoVenta = estadoVenta;
	}

	public int getCuotasPendientes() {
		return cuotasPendientes;
	}

	public void setCuotasPendientes(int cuotasPendientes) {
		this.cuotasPendientes = cuotasPendientes;
	}

	public double getSaldoPendiente() {
		return saldoPendiente;
	}

	public void setSaldoPendiente(double saldoPendiente) {
		this.saldoPendiente = saldoPendiente;
	}
}
