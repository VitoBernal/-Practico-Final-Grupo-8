package ar.edu.unju.fi.poo.model;

public enum TipoCliente {

	PARTICULAR("Particular", "DNI"),
	EMPRESA("Empresa", "CUIT");

	private String descripcion;
	private String tipoIdentificacion;

	TipoCliente(String descripcion, String tipoIdentificacion) {
		this.descripcion = descripcion;
		this.tipoIdentificacion = tipoIdentificacion;
	}

	public boolean esIdentificacionValida(String numero) {
		if (numero == null) {
			return false;
		}
		if (tipoIdentificacion.equals("DNI")) {
			return numero.matches("\\d{7,8}");
		}
		return numero.matches("\\d{11}");
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}
}
