package ar.edu.unju.fi.poo.model;

import java.time.LocalDate;

import ar.edu.unju.fi.poo.exception.ConflictoException;
import ar.edu.unju.fi.poo.exception.CuotaVencidaException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cuotas")
public class Cuota {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "venta_id")
	private VentaFinanciada venta;

	@Column(nullable = false)
	private int numero;

	@Column(nullable = false)
	private double importe;

	@Column(name = "fecha_vencimiento", nullable = false)
	private LocalDate fechaVencimiento;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoCuota estado = EstadoCuota.ADEUDADA;

	public Cuota() {
	}

	public Cuota(VentaFinanciada venta, int numero, double importe, LocalDate fechaVencimiento) {
		this.venta = venta;
		this.numero = numero;
		this.importe = importe;
		this.fechaVencimiento = fechaVencimiento;
	}

	// se paga hasta el dia del vencimiento, si ya paso queda vencida y no se puede pagar
	public void pagar(LocalDate fechaPago) {
		if (estado == EstadoCuota.PAGADA) {
			throw new ConflictoException("La cuota " + numero + " ya esta pagada");
		}
		if (estaVencida(fechaPago)) {
			estado = EstadoCuota.VENCIDA;
			throw new CuotaVencidaException(
					"La cuota " + numero + " vencio el " + fechaVencimiento + " y no se puede pagar");
		}
		estado = EstadoCuota.PAGADA;
	}

	public boolean estaVencida(LocalDate fecha) {
		if (estado == EstadoCuota.VENCIDA) {
			return true;
		}
		return estado == EstadoCuota.ADEUDADA && fecha.isAfter(fechaVencimiento);
	}

	public Integer getId() {
		return id;
	}

	public VentaFinanciada getVenta() {
		return venta;
	}

	public int getNumero() {
		return numero;
	}

	public double getImporte() {
		return importe;
	}

	public LocalDate getFechaVencimiento() {
		return fechaVencimiento;
	}

	public EstadoCuota getEstado() {
		return estado;
	}

	@Override
	public String toString() {
		return "Cuota [numero=" + numero + ", importe=" + importe + ", fechaVencimiento=" + fechaVencimiento
				+ ", estado=" + estado + "]";
	}
}
