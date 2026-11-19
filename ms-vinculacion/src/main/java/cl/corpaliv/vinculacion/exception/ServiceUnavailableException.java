package cl.corpaliv.vinculacion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Se lanza cuando otro microservicio (ms-contenidos o ms-catalogo) no responde. Responde 503 (Service Unavailable)
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
