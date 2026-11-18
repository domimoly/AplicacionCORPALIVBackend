package cl.corpaliv.catalogo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Se lanza cuando los datos enviados no cumplen una regla de negocio. Responde 400 (Bad Request)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
