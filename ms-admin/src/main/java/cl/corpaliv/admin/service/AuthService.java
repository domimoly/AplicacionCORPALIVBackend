package cl.corpaliv.admin.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.corpaliv.admin.dto.LoginRequest;
import cl.corpaliv.admin.dto.LoginResponse;
import cl.corpaliv.admin.exception.UnauthorizedException;
import cl.corpaliv.admin.model.Usuario;
import cl.corpaliv.admin.repository.UsuarioRepository;

// LOGIN: comprueba el correo y la clave y, si son correctos, entrega un token JWT.
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        String correo = request.getCorreo().trim().toLowerCase();

        // Mismo mensaje para "correo no existe", "clave incorrecta" y "usuario desactivado":
        // así no se le dice a un atacante cuál de las cosas falló
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UnauthorizedException("Correo o clave incorrectos"));

        // EXPLICAR: matches() aplica BCrypt a la clave escrita y la compara con el hash guardado
        if (!usuario.isActivo() || !passwordEncoder.matches(request.getClave(), usuario.getClaveHash())) {
            throw new UnauthorizedException("Correo o clave incorrectos");
        }

        String token = jwtService.generarToken(usuario);
        return new LoginResponse(token, "Bearer", jwtService.segundosDeVida(),
                usuario.getNombre(), usuario.getCorreo(), usuario.getRol().getNombre());
    }
}
