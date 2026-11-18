package cl.corpaliv.contenidos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Datos que aparecen arriba en la página de Swagger (http://localhost:9091/doc/swagger-ui.html)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-contenidos | CORPALIV")
                        .version("1.0")
                        .description("Programas, historias e indicadores de impacto, productos y categorías de CORPALIV / Alba Lab"));
    }
}
