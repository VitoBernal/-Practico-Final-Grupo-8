package ar.edu.unju.fi.poo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ErrorResponseDto {

	private String fecha;
	private int estado;
	private String error;
	private String mensaje;
	private String ruta;
	private List<String> errores = new ArrayList<>();

	public ErrorResponseDto() {
	}

	public ErrorResponseDto(int estado, String error, String mensaje, String ruta) {
		this.fecha = LocalDateTime.now().withNano(0).toString();
		this.estado = estado;
		this.error = error;
		this.mensaje = mensaje;
		this.ruta = ruta;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getRuta() {
		return ruta;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

	public List<String> getErrores() {
		return errores;
	}

	public void setErrores(List<String> errores) {
		this.errores = errores;
	}
}
