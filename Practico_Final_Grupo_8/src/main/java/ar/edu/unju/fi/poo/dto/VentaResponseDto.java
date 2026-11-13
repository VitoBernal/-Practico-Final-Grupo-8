package ar.edu.unju.fi.poo.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

// los datos que no corresponden a la modalidad (null) no se muestran
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VentaResponseDto {

	private Integer id;
	private String codigoComprobante;
	private String fecha;
	private String modalidad;
	private Integer clienteId;
	private String clienteNombreCompleto;
	private String clienteNumeroIdentificacion;
	private List<DetalleVentaResponseDto> detalles = new ArrayList<>();
	private double subtotal;
	private String cuponCodigo;
	private Double montoDescuento;
	private Double porcentajeRecargo;
	private Double montoRecargo;
	private double total;
	private List<CuotaResponseDto> cuotas;

	public VentaResponseDto() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigoComprobante() {
		return codigoComprobante;
	}

	public void setCodigoComprobante(String codigoComprobante) {
		this.codigoComprobante = codigoComprobante;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getModalidad() {
		return modalidad;
	}

	public void setModalidad(String modalidad) {
		this.modalidad = modalidad;
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

	public String getClienteNumeroIdentificacion() {
		return clienteNumeroIdentificacion;
	}

	public void setClienteNumeroIdentificacion(String clienteNumeroIdentificacion) {
		this.clienteNumeroIdentificacion = clienteNumeroIdentificacion;
	}

	public List<DetalleVentaResponseDto> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<DetalleVentaResponseDto> detalles) {
		this.detalles = detalles;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	public String getCuponCodigo() {
		return cuponCodigo;
	}

	public void setCuponCodigo(String cuponCodigo) {
		this.cuponCodigo = cuponCodigo;
	}

	public Double getMontoDescuento() {
		return montoDescuento;
	}

	public void setMontoDescuento(Double montoDescuento) {
		this.montoDescuento = montoDescuento;
	}

	public Double getPorcentajeRecargo() {
		return porcentajeRecargo;
	}

	public void setPorcentajeRecargo(Double porcentajeRecargo) {
		this.porcentajeRecargo = porcentajeRecargo;
	}

	public Double getMontoRecargo() {
		return montoRecargo;
	}

	public void setMontoRecargo(Double montoRecargo) {
		this.montoRecargo = montoRecargo;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public List<CuotaResponseDto> getCuotas() {
		return cuotas;
	}

	public void setCuotas(List<CuotaResponseDto> cuotas) {
		this.cuotas = cuotas;
	}
}
