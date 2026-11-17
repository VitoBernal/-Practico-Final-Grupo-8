package ar.edu.unju.fi.poo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class ClienteEstadoRequestDto {

    @Schema(description = "true para habilitar al cliente, false para inhabilitarlo", example = "true")
    @NotNull(message = "El campo habilitado es obligatorio")
    private Boolean habilitado;

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }
}
