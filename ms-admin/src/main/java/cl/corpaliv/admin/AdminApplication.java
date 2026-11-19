package cl.corpaliv.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada del microservicio. @SpringBootApplication enciende Spring: busca los controladores,
// servicios y repositorios de este paquete (cl.corpaliv.admin) y levanta el servidor en el puerto 9093.
@SpringBootApplication
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
