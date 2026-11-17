package ar.edu.unju.fi.poo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ar.edu.unju.fi.poo.exception.ConflictoException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

@Entity
@DiscriminatorValue("FINANCIADA")
public class VentaFinanciada extends Venta {

	private static final int CANTIDAD_CUOTAS = 6;

	@Column(name = "porcentaje_recargo")
	private Double porcentajeRecargo = 0.0;

	@Column(name = "monto_recargo")
	private Double montoRecargo = 0.0;

	@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("numero")
	private List<Cuota> cuotas = new ArrayList<>();

	public VentaFinanciada() {
	}

	public VentaFinanciada(Cliente cliente, String codigoComprobante, LocalDate fecha, double porcentajeRecargo) {
		super(cliente, codigoComprobante, fecha);
		this.porcentajeRecargo = porcentajeRecargo;
	}

	@Override
	public double calcularTotal() {
		double subtotal = calcularSubtotal();
		montoRecargo = redondear(subtotal * porcentajeRecargo / 100);
		return redondear(subtotal + montoRecargo);
	}

	public void generarCuotas() {
		cuotas.clear();
		double importe = redondear(getTotal() / CANTIDAD_CUOTAS);
		// la ultima cuota se ajusta para que la suma de exacto el total
		double importeUltima = redondear(getTotal() - importe * (CANTIDAD_CUOTAS - 1));
		for (int i = 1; i <= CANTIDAD_CUOTAS; i++) {
			double monto = importe;
			if (i == CANTIDAD_CUOTAS) {
				monto = importeUltima;
			}
			cuotas.add(new Cuota(this, i, monto, getFecha().plusMonths(i)));
		}
	}

	// la proxima cuota es la primera que no esta pagada, asi no se saltea ninguna
	public Cuota buscarProximaCuota() {
		for (Cuota cuota : cuotas) {
			if (cuota.getEstado() != EstadoCuota.PAGADA) {
				return cuota;
			}
		}
		return null;
	}

	public Cuota pagarProximaCuota(LocalDate fechaPago) {
		Cuota cuota = buscarProximaCuota();
		if (cuota == null) {
			throw new ConflictoException("La venta " + getCodigoComprobante() + " ya esta saldada");
		}
		cuota.pagar(fechaPago);
		if (buscarProximaCuota() == null) {
			saldar();
		}
		return cuota;
	}

	public int contarCuotasPendientes() {
		int pendientes = 0;
		for (Cuota cuota : cuotas) {
			if (cuota.getEstado() != EstadoCuota.PAGADA) {
				pendientes++;
			}
		}
		return pendientes;
	}

	public double calcularSaldoPendiente() {
		double saldo = 0;
		for (Cuota cuota : cuotas) {
			if (cuota.getEstado() != EstadoCuota.PAGADA) {
				saldo = saldo + cuota.getImporte();
			}
		}
		return redondear(saldo);
	}

	public Double getPorcentajeRecargo() {
		return porcentajeRecargo;
	}

	public Double getMontoRecargo() {
		return montoRecargo;
	}

	public List<Cuota> getCuotas() {
		return cuotas;
	}
}
