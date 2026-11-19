package cl.corpaliv.vinculacion.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

// Pruebas simples de las reglas de los estados (sin mocks ni Spring)
class EstadoSolicitudTest {

    @Test
    void hayCincoEstadosValidos() {
        assertEquals(5, EstadoSolicitud.TODOS.size());
        assertTrue(EstadoSolicitud.esValido("EN_REVISION"));
        assertFalse(EstadoSolicitud.esValido("INVENTADO"));
    }

    @Test
    void soloConcretadoYDescartadoSonFinales() {
        assertTrue(EstadoSolicitud.esFinal(EstadoSolicitud.CONCRETADO));
        assertTrue(EstadoSolicitud.esFinal(EstadoSolicitud.DESCARTADO));
        assertFalse(EstadoSolicitud.esFinal(EstadoSolicitud.RECIBIDA));
        assertFalse(EstadoSolicitud.esFinal(EstadoSolicitud.EN_REVISION));
        assertFalse(EstadoSolicitud.esFinal(EstadoSolicitud.CONTACTADO));
    }
}
