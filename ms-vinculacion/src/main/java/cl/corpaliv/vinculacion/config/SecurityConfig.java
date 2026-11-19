package cl.corpaliv.vinculacion.config;

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

// SEGURIDAD: define qué endpoints son públicos y cuáles necesitan un token JWT de un gestor.
// EXPLICAR: el visitante de la app NO inicia sesión, así que puede (1) enviar una solicitud, (2) consultar su estado con el
// código de seguimiento que recibió y (3) ver los tipos de colaboración. Todo lo demás (ver datos de las personas, cambiar
// estados, eliminar) es solo para gestores con token, porque las solicitudes contienen datos de contacto.
@Configuration
public class SecurityConfig {

    // Misma clave que usa ms-admin para firmar los tokens (viene de application.properties)
    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // IMPORTANTE: Spring revisa las reglas EN ORDEN y usa la primera que coincide
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/doc/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/solicitudes").permitAll()
                .requestMatchers(HttpMethod.GET, "/solicitudes/seguimiento/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/tipos-colaboracion").permitAll()
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
