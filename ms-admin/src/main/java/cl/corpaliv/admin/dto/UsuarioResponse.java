package cl.corpaliv.admin.dto;

import cl.corpaliv.admin.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lo que la API devuelve de un usuario. OJO: no incluye la clave ni su hash
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String correo;
    private String rol;
    private boolean activo;

    // Convierte la entidad Usuario (tabla) en este DTO
    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(),
                usuario.getRol().getNombre(), usuario.isActivo());
    }
}
