package ar.edu.unju.fi.poo.exception;

//errores de clientes: datos invalidos, identificacion o email repetidos, cliente inexistente
public class ClienteException extends NegocioException {
	private static final long serialVersionUID = 1L;

	public ClienteException(String mensaje) {
		super(mensaje);
	}
}