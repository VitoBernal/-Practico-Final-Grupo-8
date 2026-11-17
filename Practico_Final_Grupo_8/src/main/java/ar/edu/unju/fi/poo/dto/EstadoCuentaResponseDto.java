package ar.edu.unju.fi.poo.dto;
 
import java.util.ArrayList;
import java.util.List;
 
public class EstadoCuentaResponseDto {
 
	private ClienteResponseDto cliente;
	private List<VentaResponseDto> ventas = new ArrayList<>();
	private int cuotasPendientes;
	private int cuotasVencidas;
	private double totalAdeudado;
	private List<CuotaResponseDto> cuotas = new ArrayList<>();
 
	public EstadoCuentaResponseDto() {
	}
 
	public ClienteResponseDto getCliente() {
		return cliente;
	}
 
	public void setCliente(ClienteResponseDto cliente) {
		this.cliente = cliente;
	}
 
	public List<VentaResponseDto> getVentas() {
		return ventas;
	}
 
	public void setVentas(List<VentaResponseDto> ventas) {
		this.ventas = ventas;
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

