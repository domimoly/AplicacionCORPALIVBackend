package cl.corpaliv.contenidos.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

// SEGURIDAD: define qué endpoints son públicos y cuáles necesitan un token JWT.
// EXPLICAR: el visitante de la app NO inicia sesión, así que los GET de contenido son públicos.
// Crear, modificar y eliminar (POST, PUT, DELETE) y las listas '/admin' (que incluyen borradores) exigen un token,
// que solo obtiene un gestor al iniciar sesión en ms-admin (POST /auth/login).
@Configuration
public class SecurityConfig {

    // Misma clave que usa ms-admin para firmar los tokens (viene de application.properties)
    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // IMPORTANTE: Spring revisa las reglas EN ORDEN y usa la primera que coincide.
            // Por eso '/admin' va ANTES de 'cualquier GET'.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/doc/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/programas/admin", "/historias/admin",
                        "/indicadores/admin").authenticated()
                .requestMatchers(HttpMethod.GET, "/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {})
            );

        return http.build();
    }

    // Valida la firma del token con la clave secreta (HMAC). Si la firma no coincide, el token se rechaza (401)
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );

        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
