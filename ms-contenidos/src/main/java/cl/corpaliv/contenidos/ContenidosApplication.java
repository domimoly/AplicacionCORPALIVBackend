package cl.corpaliv.contenidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada del microservicio. @SpringBootApplication enciende Spring: busca los controladores,
// servicios y repositorios de este paquete (cl.corpaliv.contenidos) y levanta el servidor en el puerto 9091.
@SpringBootApplication
public class ContenidosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContenidosApplication.class, args);
    }
}
