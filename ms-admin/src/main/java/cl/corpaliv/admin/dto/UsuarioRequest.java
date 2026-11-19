package cl.corpaliv.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Datos para crear o modificar un usuario.
// La clave es obligatoria al crear (se revisa en UsuarioService); al modificar es opcional: si no viene, se mantiene la actual.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
    private String correo;

    @Size(min = 8, max = 60, message = "La clave debe tener entre 8 y 60 caracteres")
    private String clave;

    @NotNull(message = "El rol es obligatorio")
    private Long rolId;

    // Por defecto un usuario nuevo está activo
    private boolean activo = true;
}
