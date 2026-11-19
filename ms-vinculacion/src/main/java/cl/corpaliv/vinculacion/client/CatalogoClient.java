package cl.corpaliv.vinculacion.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import cl.corpaliv.vinculacion.exception.ServiceUnavailableException;

// COMUNICACIÓN ENTRE MICROSERVICIOS: ms-vinculacion le pregunta a ms-catalogo si un producto existe.
// Llama a GET http://localhost:9094/productos/{id}/exists (público). Si ms-catalogo no responde, lanza 503.
@Component
public class CatalogoClient {

    private final RestClient restClient;

    public CatalogoClient(@Value("${servicios.catalogo.url}") String urlBase) {
        this.restClient = ClienteHttp.crear(urlBase);
    }

    public boolean existeProducto(Long id) {
        try {
            Boolean existe = restClient.get()
                    .uri("/productos/{id}/exists", id)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(existe);
        } catch (RestClientException e) {
            throw new ServiceUnavailableException(
                    "No se pudo validar el producto porque el servicio de catálogo no está disponible");
        }
    }
}
