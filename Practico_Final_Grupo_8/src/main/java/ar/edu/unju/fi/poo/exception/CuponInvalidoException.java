package ar.edu.unju.fi.poo.exception;

public class CuponInvalidoException extends NegocioException {

	private static final long serialVersionUID = 1L;

	public CuponInvalidoException(String mensaje) {
		super(mensaje);
	}
}
