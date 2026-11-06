package ar.edu.unju.fi.poo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ventas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "modalidad")
public abstract class Venta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "codigo_comprobante", nullable = false, unique = true, length = 30)
	private String codigoComprobante;

	@Column(nullable = false)
	private LocalDate fecha;

	@ManyToOne(optional = false)
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;

	@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DetalleVenta> detalles = new ArrayList<>();

	@Column(nullable = false)
	private double subtotal;

	@Column(nullable = false)
	private double total;

	public Venta() {
	}

	public Venta(Cliente cliente, String codigoComprobante, LocalDate fecha) {
		this.cliente = cliente;
		this.codigoComprobante = codigoComprobante;
		this.fecha = fecha;
	}

	public void agregarDetalle(Producto producto, int cantidad) {
		detalles.add(new DetalleVenta(this, producto, cantidad));
	}

	public double calcularSubtotal() {
		double suma = 0;
		for (DetalleVenta detalle : detalles) {
			suma = suma + detalle.getSubtotal();
		}
		return redondear(suma);
	}

	public abstract double calcularTotal();

	protected double redondear(double valor) {
		return Math.round(valor * 100.0) / 100.0;
	}

	public Integer getId() {
		return id;
	}

	public String getCodigoComprobante() {
		return codigoComprobante;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public List<DetalleVenta> getDetalles() {
		return detalles;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "Venta [id=" + id + ", codigoComprobante=" + codigoComprobante + ", fecha=" + fecha + ", subtotal="
				+ subtotal + ", total=" + total + "]";
	}
}
