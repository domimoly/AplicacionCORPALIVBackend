package cl.corpaliv.vinculacion.model;

import java.util.List;

// ESTADOS de una solicitud. Son los mismos códigos de la Propuesta Técnica y de la app (EstadoSolicitud.kt).
// EXPLICAR: una solicitud nace RECIBIDA y el gestor la va cambiando. CONCRETADO y DESCARTADO son estados FINALES:
// cuando una solicitud llega a ellos, ya no se puede modificar.
public final class EstadoSolicitud {

    public static final String RECIBIDA = "RECIBIDA";
    public static final String EN_REVISION = "EN_REVISION";
    public static final String CONTACTADO = "CONTACTADO";
    public static final String CONCRETADO = "CONCRETADO";
    public static final String DESCARTADO = "DESCARTADO";

    public static final List<String> TODOS = List.of(RECIBIDA, EN_REVISION, CONTACTADO, CONCRETADO, DESCARTADO);

    private EstadoSolicitud() {
    }

    public static boolean esValido(String estado) {
        return TODOS.contains(estado);
    }

    public static boolean esFinal(String estado) {
        return CONCRETADO.equals(estado) || DESCARTADO.equals(estado);
    }
}
