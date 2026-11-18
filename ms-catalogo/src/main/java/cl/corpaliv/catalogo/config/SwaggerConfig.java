package cl.corpaliv.catalogo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Datos que aparecen arriba en la página de Swagger (http://localhost:9094/doc/swagger-ui.html)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-catalogo | CORPALIV")
                        .version("1.0")
                        .description("Productos, servicios y categorías del catálogo de CORPALIV / Alba Lab"));
    }
}
