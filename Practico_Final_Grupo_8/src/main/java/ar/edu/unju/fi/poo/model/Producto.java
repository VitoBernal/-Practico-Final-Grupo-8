package ar.edu.unju.fi.poo.model;

import ar.edu.unju.fi.poo.exception.StockInsuficienteException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(length = 255)
	private String descripcion;

	@Column(length = 60)
	private String marca;

	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;

	@Column(nullable = false)
	private int stock;

	@Column(name = "stock_critico", nullable = false)
	private int stockCritico = 5;

	@Column(name = "precio_base", nullable = false)
	private double precioBase;

	public Producto() {
	}

	public Producto(String nombre, String descripcion, String marca, Categoria categoria, int stock,
			double precioBase) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.marca = marca;
		this.categoria = categoria;
		this.stock = stock;
		this.precioBase = precioBase;
	}

	public boolean hayStock(int cantidad) {
		return stock >= cantidad;
	}

	public void descontarStock(int cantidad) {
		if (!hayStock(cantidad)) {
			throw new StockInsuficienteException("No hay stock suficiente de " + nombre);
		}
		stock = stock - cantidad;
	}

	public boolean tieneStockCritico() {
		return stock <= stockCritico;
	}

	public String generarAlertaStock() {
		return "ALERTA DE STOCK: " + nombre + " tiene " + stock + " unidades, hay que reponer";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getStockCritico() {
		return stockCritico;
	}

	public void setStockCritico(int stockCritico) {
		this.stockCritico = stockCritico;
	}

	public double getPrecioBase() {
		return precioBase;
	}

	public void setPrecioBase(double precioBase) {
		this.precioBase = precioBase;
	}

	@Override
	public String toString() {
		return "Producto [id=" + id + ", nombre=" + nombre + ", marca=" + marca + ", stock=" + stock
				+ ", precioBase=" + precioBase + "]";
	}
}
