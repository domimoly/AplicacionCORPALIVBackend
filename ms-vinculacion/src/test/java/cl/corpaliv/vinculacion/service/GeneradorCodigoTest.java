package cl.corpaliv.vinculacion.service;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

// El código de seguimiento tiene el formato 'COL-' + 8 caracteres y no es siempre el mismo
class GeneradorCodigoTest {

    private final GeneradorCodigo generador = new GeneradorCodigo();

    @Test
    void generar_tieneElFormatoEsperado() {
        String codigo = generador.generar();

        assertTrue(codigo.matches("COL-[A-HJ-NP-Z2-9]{8}"), "Código con formato inesperado: " + codigo);
    }

    @Test
    void generar_dosVecesDaCodigosDistintos() {
        assertNotEquals(generador.generar(), generador.generar());
    }
}
