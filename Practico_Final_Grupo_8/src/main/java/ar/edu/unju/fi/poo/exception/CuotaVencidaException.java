package ar.edu.unju.fi.poo.exception;

public class CuotaVencidaException extends NegocioException{
	
	private static final long serialVersionUID = 1L;
	
	public CuotaVencidaException(String mensaje) {
		super(mensaje);
	}
}
