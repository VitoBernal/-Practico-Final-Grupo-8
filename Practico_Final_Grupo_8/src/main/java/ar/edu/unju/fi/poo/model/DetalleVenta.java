package ar.edu.unju.fi.poo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "venta_id")
	private Venta venta;

	@ManyToOne(optional = false)
	@JoinColumn(name = "producto_id")
	private Producto producto;

	@Column(nullable = false)
	private int cantidad;

	@Column(name = "precio_base_aplicado", nullable = false)
	private double precioBaseAplicado;

	@Column(nullable = false)
	private double subtotal;

	public DetalleVenta() {
	}

	public DetalleVenta(Venta venta, Producto producto, int cantidad) {
		this.venta = venta;
		this.producto = producto;
		this.cantidad = cantidad;
		this.precioBaseAplicado = producto.getPrecioBase();
		this.subtotal = calcularSubtotal();
	}

	public double calcularSubtotal() {
		return Math.round(precioBaseAplicado * cantidad * 100.0) / 100.0;
	}

	public Integer getId() {
		return id;
	}

	public Venta getVenta() {
		return venta;
	}

	public Producto getProducto() {
		return producto;
	}

	public int getCantidad() {
		return cantidad;
	}

	public double getPrecioBaseAplicado() {
		return precioBaseAplicado;
	}

	public double getSubtotal() {
		return subtotal;
	}

	@Override
	public String toString() {
		return "DetalleVenta [producto=" + producto.getNombre() + ", cantidad=" + cantidad + ", precioBaseAplicado="
				+ precioBaseAplicado + ", subtotal=" + subtotal + "]";
	}
}
