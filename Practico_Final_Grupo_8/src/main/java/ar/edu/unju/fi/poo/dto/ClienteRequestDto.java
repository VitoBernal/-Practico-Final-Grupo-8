package ar.edu.unju.fi.poo.dto;

import ar.edu.unju.fi.poo.model.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// lo que recibe del cliente
public class ClienteRequestDto {

    @NotBlank(message = "El numero de identificacion es obligatorio")
    private String numeroIdentificacion;
    private String nombre;
    private String apellido;
    private String razonSocial;
    private String telefono;
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    private String email;
    private String direccion;
    @NotNull(message = "El tipo de cliente es obligatorio")
    private TipoCliente tipo;

    public static ClienteRequestDto particular(String dni, String nombre, String apellido,
            String telefono, String email, String direccion) {
        ClienteRequestDto dto = new ClienteRequestDto();
        dto.setNumeroIdentificacion(dni);
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setTelefono(telefono);
        dto.setEmail(email);
        dto.setDireccion(direccion);
        dto.setTipo(TipoCliente.PARTICULAR);
        return dto;
    }

    public static ClienteRequestDto empresa(String cuit, String razonSocial,
            String telefono, String email, String direccion) {
        ClienteRequestDto dto = new ClienteRequestDto();
        dto.setNumeroIdentificacion(cuit);
        dto.setRazonSocial(razonSocial);
        dto.setTelefono(telefono);
        dto.setEmail(email);
        dto.setDireccion(direccion);
        dto.setTipo(TipoCliente.EMPRESA);
        return dto;
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

    public TipoCliente getTipo() {
        return tipo;
    }

    public void setTipo(TipoCliente tipo) {
        this.tipo = tipo;
    }
}
