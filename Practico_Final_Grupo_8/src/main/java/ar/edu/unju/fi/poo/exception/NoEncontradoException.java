package ar.edu.unju.fi.poo.exception;

public class NoEncontradoException extends NegocioException {

	private static final long serialVersionUID = 1L;

	public NoEncontradoException(String mensaje) {
		super(mensaje);
	}
}
