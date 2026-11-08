package ar.edu.unju.fi.poo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import ar.edu.unju.fi.poo.dto.ErrorResponseDto;
import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.exception.ConflictoException;
import ar.edu.unju.fi.poo.exception.NegocioException;
import ar.edu.unju.fi.poo.exception.NoEncontradoException;
import ar.edu.unju.fi.poo.exception.ProductoException;
import ar.edu.unju.fi.poo.exception.VentaException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

	@ExceptionHandler(NoEncontradoException.class)
	public ResponseEntity<ErrorResponseDto> manejarNoEncontrado(NoEncontradoException e, HttpServletRequest request) {
		return armarRespuesta(HttpStatus.NOT_FOUND, e.getMessage(), request);
	}

	@ExceptionHandler(ConflictoException.class)
	public ResponseEntity<ErrorResponseDto> manejarConflicto(ConflictoException e, HttpServletRequest request) {
		return armarRespuesta(HttpStatus.CONFLICT, e.getMessage(), request);
	}

	// datos que llegan mal: faltan, tienen mal formato o un valor que no
	// corresponde
	@ExceptionHandler({ ClienteException.class, ProductoException.class, VentaException.class })
	public ResponseEntity<ErrorResponseDto> manejarDatoInvalido(NegocioException e, HttpServletRequest request) {
		return armarRespuesta(HttpStatus.BAD_REQUEST, e.getMessage(), request);
	}

	// reglas de negocio: stock, cupon, cliente inhabilitado e importes
	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<ErrorResponseDto> manejarNegocio(NegocioException e, HttpServletRequest request) {
		return armarRespuesta(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDto> manejarDatosInvalidos(MethodArgumentNotValidException e,
			HttpServletRequest request) {
		ErrorResponseDto error = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(), "Hay datos invalidos en la solicitud",
				request.getRequestURI());
		for (FieldError campo : e.getBindingResult().getFieldErrors()) {
			error.getErrores().add(campo.getField() + ": " + campo.getDefaultMessage());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponseDto> manejarJsonInvalido(HttpMessageNotReadableException e,
			HttpServletRequest request) {
		return armarRespuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no tiene un formato valido", request);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponseDto> manejarTipoInvalido(MethodArgumentTypeMismatchException e,
			HttpServletRequest request) {
		return armarRespuesta(HttpStatus.BAD_REQUEST,
				"El valor '" + e.getValue() + "' no es valido para '" + e.getName() + "'", request);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponseDto> manejarParametroFaltante(MissingServletRequestParameterException e,
			HttpServletRequest request) {
		return armarRespuesta(HttpStatus.BAD_REQUEST, "Falta el parametro '" + e.getParameterName() + "'", request);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponseDto> manejarRutaInexistente(NoResourceFoundException e,
			HttpServletRequest request) {
		return armarRespuesta(HttpStatus.NOT_FOUND, "No existe la ruta " + request.getRequestURI(), request);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponseDto> manejarMetodoNoPermitido(HttpRequestMethodNotSupportedException e,
			HttpServletRequest request) {
		return armarRespuesta(HttpStatus.METHOD_NOT_ALLOWED,
				"El metodo " + e.getMethod() + " no esta permitido en esta ruta", request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDto> manejarErrorInesperado(Exception e, HttpServletRequest request) {
		return armarRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado", request);
	}

	private ResponseEntity<ErrorResponseDto> armarRespuesta(HttpStatus estado, String mensaje,
			HttpServletRequest request) {
		ErrorResponseDto error = new ErrorResponseDto(estado.value(), estado.getReasonPhrase(), mensaje,
				request.getRequestURI());
		return ResponseEntity.status(estado).body(error);
	}
}
