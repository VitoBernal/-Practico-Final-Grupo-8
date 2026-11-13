package ar.edu.unju.fi.poo.dto;

import jakarta.validation.constraints.NotNull;

public class ClienteEstadoRequestDto {

    @NotNull(message = "El campo habilitado es obligatorio")
    private Boolean habilitado;

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }
}
