package ar.edu.unju.fi.poo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@DiscriminatorValue("CONTADO")
public class VentaContado extends Venta {

	@OneToOne
	@JoinColumn(name = "cupon_id", unique = true)
	private Cupon cupon;

	@Column(name = "monto_descuento")
	private Double montoDescuento = 0.0;

	public VentaContado() {
	}

	public VentaContado(Cliente cliente, String codigoComprobante, LocalDate fecha) {
		super(cliente, codigoComprobante, fecha);
	}

	@Override
	public double calcularTotal() {
		double subtotal = calcularSubtotal();
		montoDescuento = 0.0;
		if (cupon != null) {
			montoDescuento = redondear(subtotal * cupon.getPorcentajeDescuento() / 100);
		}
		return redondear(subtotal - montoDescuento);
	}

	public Cupon getCupon() {
		return cupon;
	}

	public void setCupon(Cupon cupon) {
		this.cupon = cupon;
	}

	public Double getMontoDescuento() {
		return montoDescuento;
	}
}
