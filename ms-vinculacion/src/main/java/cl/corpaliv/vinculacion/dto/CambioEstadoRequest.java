package cl.corpaliv.vinculacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lo que el gestor envía en PATCH /solicitudes/{id}/estado
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstadoRequest {

    // RECIBIDA, EN_REVISION, CONTACTADO, CONCRETADO o DESCARTADO
    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    // Mensaje para la persona (opcional)
    @Size(max = 200, message = "La observación no puede superar los 200 caracteres")
    private String observacion;
}
