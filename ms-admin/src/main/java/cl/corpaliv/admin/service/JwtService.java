package cl.corpaliv.admin.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import cl.corpaliv.admin.model.Usuario;

// Crea (firma) los tokens JWT. Los demás microservicios solo los validan con la misma clave secreta.
// EXPLICAR: un JWT es un texto con 3 partes (cabecera.datos.firma). Los "datos" (claims) dicen quién es el usuario:
//   sub = correo, rol = GESTOR o ADMIN, nombre. La firma (HS256) permite saber que nadie modificó el token.
@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long horasDeVida;

    public JwtService(JwtEncoder jwtEncoder, @Value("${jwt.expiracion-horas:8}") long horasDeVida) {
        this.jwtEncoder = jwtEncoder;
        this.horasDeVida = horasDeVida;
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuario.getCorreo())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(Duration.ofHours(horasDeVida)))
                .claim("rol", usuario.getRol().getNombre())
                .claim("nombre", usuario.getNombre())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    // Cuántos segundos dura un token (se informa a la app en la respuesta del login)
    public long segundosDeVida() {
        return Duration.ofHours(horasDeVida).toSeconds();
    }
}
