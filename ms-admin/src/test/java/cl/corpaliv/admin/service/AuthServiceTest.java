package cl.corpaliv.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.corpaliv.admin.dto.LoginRequest;
import cl.corpaliv.admin.dto.LoginResponse;
import cl.corpaliv.admin.exception.UnauthorizedException;
import cl.corpaliv.admin.model.Rol;
import cl.corpaliv.admin.model.Usuario;
import cl.corpaliv.admin.repository.UsuarioRepository;

// Pruebas del login. El repositorio y el JwtService son mocks (falsos); el PasswordEncoder es el BCrypt real,
// así se comprueba de verdad que la clave escrita coincide con el hash.
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(usuarioRepository, passwordEncoder, jwtService);
    }

    private Usuario crearUsuario(boolean activo) {
        Rol rol = new Rol(1L, "GESTOR", "Gestor");
        return new Usuario(1L, "Gestor demo", "gestor@ejemplo.cl", passwordEncoder.encode("Gestor2026"), activo, rol);
    }

    @Test
    void login_conDatosCorrectos_devuelveToken() {
        Usuario usuario = crearUsuario(true);
        when(usuarioRepository.findByCorreo("gestor@ejemplo.cl")).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken(usuario)).thenReturn("token-de-prueba");
        when(jwtService.segundosDeVida()).thenReturn(28800L);

        // Se escribe con mayúsculas y espacios a propósito: el login normaliza el correo
        LoginResponse respuesta = authService.login(new LoginRequest("  Gestor@Ejemplo.cl ", "Gestor2026"));

        assertEquals("token-de-prueba", respuesta.getToken());
        assertEquals("Bearer", respuesta.getTipo());
        assertEquals("GESTOR", respuesta.getRol());
        assertEquals("gestor@ejemplo.cl", respuesta.getCorreo());
    }

    @Test
    void login_conClaveIncorrecta_lanzaUnauthorizedYNoGeneraToken() {
        when(usuarioRepository.findByCorreo("gestor@ejemplo.cl")).thenReturn(Optional.of(crearUsuario(true)));

        UnauthorizedException error = assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("gestor@ejemplo.cl", "claveMala")));

        assertEquals("Correo o clave incorrectos", error.getMessage());
        verify(jwtService, never()).generarToken(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void login_conCorreoInexistente_lanzaUnauthorizedConElMismoMensaje() {
        when(usuarioRepository.findByCorreo("nadie@ejemplo.cl")).thenReturn(Optional.empty());

        UnauthorizedException error = assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("nadie@ejemplo.cl", "Gestor2026")));

        assertEquals("Correo o clave incorrectos", error.getMessage());
    }

    @Test
    void login_conUsuarioDesactivado_lanzaUnauthorized() {
        when(usuarioRepository.findByCorreo("gestor@ejemplo.cl")).thenReturn(Optional.of(crearUsuario(false)));

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("gestor@ejemplo.cl", "Gestor2026")));

        verify(jwtService, never()).generarToken(org.mockito.ArgumentMatchers.any());
    }
}
