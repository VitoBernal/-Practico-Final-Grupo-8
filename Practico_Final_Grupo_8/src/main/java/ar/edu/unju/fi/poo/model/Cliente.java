package ar.edu.unju.fi.poo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "numero_identificacion", nullable = false, unique = true, length = 20)
	private String numeroIdentificacion;

	@Column(length = 60)
	private String nombre;

	@Column(length = 60)
	private String apellido;

	@Column(name = "razon_social", length = 120)
	private String razonSocial;

	@Column(length = 30)
	private String telefono;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(length = 150)
	private String direccion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoCliente tipo;

	@Column(nullable = false)
	private boolean habilitado = true;

	public Cliente() {
	}

	public static Cliente particular(String dni, String nombre, String apellido, String telefono, String email,
			String direccion) {
		Cliente cliente = new Cliente();
		cliente.setNumeroIdentificacion(dni);
		cliente.setNombre(nombre);
		cliente.setApellido(apellido);
		cliente.setTelefono(telefono);
		cliente.setEmail(email);
		cliente.setDireccion(direccion);
		cliente.setTipoCliente(TipoCliente.PARTICULAR);
		return cliente;
	}

	public static Cliente empresa(String cuit, String razonSocial, String telefono, String email, String direccion) {
		Cliente cliente = new Cliente();
		cliente.setNumeroIdentificacion(cuit);
		cliente.setRazonSocial(razonSocial);
		cliente.setTelefono(telefono);
		cliente.setEmail(email);
		cliente.setDireccion(direccion);
		cliente.setTipoCliente(TipoCliente.EMPRESA);
		return cliente;
	}

    public String getNombreCompleto() {
        if (tipo == TipoCliente.PARTICULAR) {
            return nombre + " " + apellido;
        } else if (tipo == TipoCliente.EMPRESA) {
            return razonSocial;
        }
        return "";
    }
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumeroIdentificacion() {
		return numeroIdentificacion;
	}

	public void setNumeroIdentificacion(String numeroIdentificacion) {
		this.numeroIdentificacion = numeroIdentificacion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public TipoCliente getTipoCliente() {
		return tipo;
	}

	public void setTipoCliente(TipoCliente tipo) {
		this.tipo = tipo;
	}

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}

	@Override
	public String toString() {
		return "Cliente [id=" + id + ", numeroIdentificacion=" + numeroIdentificacion + ", nombre="
				+ getNombreCompleto() + ", tipo=" + tipo + "]";
	}
}
