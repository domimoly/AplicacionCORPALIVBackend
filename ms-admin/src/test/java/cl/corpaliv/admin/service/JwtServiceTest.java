package cl.corpaliv.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import cl.corpaliv.admin.model.Rol;
import cl.corpaliv.admin.model.Usuario;

// Prueba que el token que FIRMA ms-admin lo puede LEER un decodificador con la misma clave
// (es lo que hacen ms-contenidos y ms-catalogo). No usa mocks: usa las clases reales.
class JwtServiceTest {

    private static final String SECRET = "corpaliv_clave_secreta_jwt_proyecto_2026";

    private JwtService jwtService;
    private SecretKey clave;

    @BeforeEach
    void setUp() {
        clave = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        jwtService = new JwtService(new NimbusJwtEncoder(new ImmutableSecret<>(clave)), 8);
    }

    private Usuario crearUsuario() {
        Rol rol = new Rol(1L, "GESTOR", "Gestor");
        return new Usuario(1L, "Gestor de vinculación (demo)", "gestor@ejemplo.cl", "hash", true, rol);
    }

    @Test
    void generarToken_contieneCorreoRolYNombre_yExpira() {
        String token = jwtService.generarToken(crearUsuario());

        Jwt jwt = NimbusJwtDecoder.withSecretKey(clave).build().decode(token);

        assertEquals("gestor@ejemplo.cl", jwt.getSubject());
        assertEquals("GESTOR", jwt.getClaimAsString("rol"));
        assertEquals("Gestor de vinculación (demo)", jwt.getClaimAsString("nombre"));
        assertNotNull(jwt.getExpiresAt());
        assertTrue(jwt.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void segundosDeVida_son8HorasEnSegundos() {
        assertEquals(8 * 3600, jwtService.segundosDeVida());
    }

    @Test
    void tokenFirmadoConOtraClave_noSePuedeValidar() {
        String token = jwtService.generarToken(crearUsuario());
        SecretKey otraClave = new SecretKeySpec(
                "otra_clave_distinta_de_32_caracteres_o_mas_xx".getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        org.junit.jupiter.api.Assertions.assertThrows(org.springframework.security.oauth2.jwt.JwtException.class,
                () -> NimbusJwtDecoder.withSecretKey(otraClave).build().decode(token));
    }
}
