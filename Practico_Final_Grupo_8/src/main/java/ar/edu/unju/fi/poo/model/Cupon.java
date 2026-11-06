package ar.edu.unju.fi.poo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cupones")
public class Cupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true, length = 30)
	private String codigo;

	@Column(name = "porcentaje_descuento", nullable = false)
	private double porcentajeDescuento;

	@Column(name = "fecha_vencimiento", nullable = false)
	private LocalDate fechaVencimiento;

	@Column(nullable = false)
	private boolean usado;

	public Cupon() {
	}

	public Cupon(String codigo, double porcentajeDescuento, LocalDate fechaVencimiento) {
		this.codigo = codigo;
		this.porcentajeDescuento = porcentajeDescuento;
		this.fechaVencimiento = fechaVencimiento;
	}

	public boolean esValido(LocalDate fecha) {
		return !usado && !fecha.isAfter(fechaVencimiento);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public double getPorcentajeDescuento() {
		return porcentajeDescuento;
	}

	public void setPorcentajeDescuento(double porcentajeDescuento) {
		this.porcentajeDescuento = porcentajeDescuento;
	}

	public LocalDate getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(LocalDate fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public boolean isUsado() {
		return usado;
	}

	public void setUsado(boolean usado) {
		this.usado = usado;
	}

	@Override
	public String toString() {
		return "Cupon [codigo=" + codigo + ", porcentajeDescuento=" + porcentajeDescuento + ", fechaVencimiento="
				+ fechaVencimiento + ", usado=" + usado + "]";
	}
}
