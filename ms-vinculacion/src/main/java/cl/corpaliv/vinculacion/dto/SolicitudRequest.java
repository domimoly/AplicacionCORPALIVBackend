package cl.corpaliv.vinculacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lo que la app envía en POST /solicitudes. Los campos obligatorios son los de la Propuesta Técnica:
// nombre, medio de contacto, tipo de colaboración y aceptar la política de privacidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El medio de contacto es obligatorio")
    @Size(max = 120, message = "El medio de contacto no puede superar los 120 caracteres")
    private String medioContacto;

    // Código del tipo: DONACION, ALIANZA_RSE, VOLUNTARIADO, COMPRA, CAPACITACION u OTRO
    @NotBlank(message = "El tipo de colaboración es obligatorio")
    @Size(max = 30, message = "El tipo no puede superar los 30 caracteres")
    private String tipo;

    @Size(max = 500, message = "El mensaje no puede superar los 500 caracteres")
    private String mensaje;

    @PositiveOrZero(message = "El monto no puede ser negativo")
    private Integer monto;

    private LocalDate fechaContacto;

    private Long programaId;

    private Long productoId;

    private boolean prioritario;

    // @AssertTrue = el valor DEBE ser true; si no, la petición se rechaza con 400
    @AssertTrue(message = "Debes aceptar la política de privacidad")
    private boolean aceptaPrivacidad;
}
