package cl.corpaliv.vinculacion.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

// Crea el CÓDIGO DE SEGUIMIENTO de una solicitud, por ejemplo 'COL-7KQ4M2XA'.
// EXPLICAR: es aleatorio (SecureRandom) y sin letras confusas (sin 0, O, 1, I) para que sea fácil de leer y copiar.
// Así nadie puede adivinar el código de otra persona ni recorrer solicitudes probando números (1, 2, 3...).
@Component
public class GeneradorCodigo {

    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LARGO = 8;

    private final SecureRandom random = new SecureRandom();

    public String generar() {
        StringBuilder codigo = new StringBuilder("COL-");
        for (int i = 0; i < LARGO; i++) {
            codigo.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return codigo.toString();
    }
}
