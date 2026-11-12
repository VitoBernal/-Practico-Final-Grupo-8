package ar.edu.unju.fi.poo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//Lo que envía el cliente para crear o actualizar:
public class ProductoRequestDto {
	
	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;
	private String descripcion;
	private String marca;
	@NotBlank(message = "El nombre de la categoria es obligatorio")
	private String categoria;
	@NotNull(message = "El stock es obligatorio")
	@Positive
	private Integer stock;
	@NotNull(message = "El precio es obligatorio")
	@Positive
	private Double precioBase;

	public ProductoRequestDto() {
		super();
	}

	public ProductoRequestDto(@NotBlank(message = "El nombre es obligatorio") String nombre, String descripcion,
			String marca, @NotBlank(message = "El nombre de la categoria es obligatorio") String categoria,
			@NotBlank(message = "El stock es obligatorio") Integer stock,
			@NotBlank(message = "El precio es obligatorio") Double precioBase) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.marca = marca;
		this.categoria = categoria;
		this.stock = stock;
		this.precioBase = precioBase;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Double getPrecioBase() {
		return precioBase;
	}

	public void setPrecioBase(Double precioBase) {
		this.precioBase = precioBase;
	}
	
}

