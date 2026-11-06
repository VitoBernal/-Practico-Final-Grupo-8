package ar.edu.unju.fi.poo.exception;

public class ClienteException extends NegocioException {

	private static final long serialVersionUID = 1L;

	public ClienteException(String mensaje) {
		super(mensaje);
	}
}
