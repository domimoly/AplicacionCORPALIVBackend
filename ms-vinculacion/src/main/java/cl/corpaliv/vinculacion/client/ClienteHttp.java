package cl.corpaliv.vinculacion.client;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

// Ayuda para crear el RestClient (la herramienta de Spring para llamar a OTROS microservicios por HTTP).
// Se le pone un tiempo máximo de espera de 3 segundos, para que una solicitud no quede colgada si el otro servicio no responde.
final class ClienteHttp {

    private ClienteHttp() {
    }

    static RestClient crear(String urlBase) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(httpClient);
        fabrica.setReadTimeout(Duration.ofSeconds(3));
        return RestClient.builder()
                .baseUrl(urlBase)
                .requestFactory(fabrica)
                .build();
    }
}
