package cl.corpaliv.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Respuesta del login. La app guarda el 'token' y lo envía en cada petición protegida:
//   Authorization: Bearer <token>
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo;          // siempre "Bearer"
    private long expiraEnSegundos; // cuánto dura el token
    private String nombre;
    private String correo;
    private String rol;
}
