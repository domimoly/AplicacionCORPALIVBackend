package cl.corpaliv.catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada del microservicio. @SpringBootApplication enciende Spring: busca los controladores,
// servicios y repositorios de este paquete (cl.corpaliv.catalogo) y levanta el servidor en el puerto 9094.
@SpringBootApplication
public class CatalogoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogoApplication.class, args);
    }
}
