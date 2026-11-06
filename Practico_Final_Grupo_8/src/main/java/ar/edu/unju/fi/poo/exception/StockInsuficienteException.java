package ar.edu.unju.fi.poo.exception;

public class StockInsuficienteException extends NegocioException {

	private static final long serialVersionUID = 1L;

	public StockInsuficienteException(String mensaje) {
		super(mensaje);
	}
}
