package cl.corpaliv.vinculacion.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Datos que aparecen arriba en la página de Swagger (http://localhost:9092/doc/swagger-ui.html).
// También agrega el botón "Authorize": ahí se pega el token JWT (sin escribir 'Bearer') para probar los endpoints protegidos.
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-vinculacion | CORPALIV")
                        .version("1.0")
                        .description("Solicitudes de colaboración, estados e historial de CORPALIV"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
