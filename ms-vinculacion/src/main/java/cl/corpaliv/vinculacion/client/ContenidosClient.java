package cl.corpaliv.vinculacion.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import cl.corpaliv.vinculacion.exception.ServiceUnavailableException;

// COMUNICACIÓN ENTRE MICROSERVICIOS: ms-vinculacion le pregunta a ms-contenidos si un programa existe.
// EXPLICAR: llama a GET http://localhost:9091/programas/{id}/exists, que responde true o false (es un endpoint público).
// Si ms-contenidos está apagado o no responde, se lanza ServiceUnavailableException (503) en vez de un error confuso.
@Component
public class ContenidosClient {

    private final RestClient restClient;

    public ContenidosClient(@Value("${servicios.contenidos.url}") String urlBase) {
        this.restClient = ClienteHttp.crear(urlBase);
    }

    public boolean existePrograma(Long id) {
        try {
            Boolean existe = restClient.get()
                    .uri("/programas/{id}/exists", id)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(existe);
        } catch (RestClientException e) {
            throw new ServiceUnavailableException(
                    "No se pudo validar el programa porque el servicio de contenidos no está disponible");
        }
    }
}
