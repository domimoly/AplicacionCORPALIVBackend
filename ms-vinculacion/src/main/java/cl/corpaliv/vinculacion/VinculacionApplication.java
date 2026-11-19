package cl.corpaliv.vinculacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada del microservicio. @SpringBootApplication enciende Spring: busca los controladores,
// servicios y repositorios de este paquete (cl.corpaliv.vinculacion) y levanta el servidor en el puerto 9092.
@SpringBootApplication
public class VinculacionApplication {

    public static void main(String[] args) {
        SpringApplication.run(VinculacionApplication.class, args);
    }
}
