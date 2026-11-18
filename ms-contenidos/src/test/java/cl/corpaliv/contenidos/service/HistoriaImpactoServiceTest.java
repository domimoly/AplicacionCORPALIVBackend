package cl.corpaliv.contenidos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.corpaliv.contenidos.exception.BadRequestException;
import cl.corpaliv.contenidos.model.HistoriaImpacto;
import cl.corpaliv.contenidos.repository.HistoriaImpactoRepository;

// Prueba la REGLA DE NEGOCIO más importante de este microservicio:
// una historia solo se puede publicar si está autorizada Y anonimizada.
@ExtendWith(MockitoExtension.class)
class HistoriaImpactoServiceTest {

    @Mock
    private HistoriaImpactoRepository historiaRepository;

    private HistoriaImpactoService historiaService;

    @BeforeEach
    void setUp() {
        historiaService = new HistoriaImpactoService(historiaRepository);
    }

    private HistoriaImpacto crearHistoria(boolean autorizada, boolean anonimizada, String estado) {
        HistoriaImpacto historia = new HistoriaImpacto();
        historia.setTitulo("Un primer día de trabajo");
        historia.setResumen("Resumen");
        historia.setContenido("Contenido de ejemplo");
        historia.setAutorizada(autorizada);
        historia.setAnonimizada(anonimizada);
        historia.setEstadoPublicacion(estado);
        return historia;
    }

    @Test
    void guardar_publicadaSinAutorizacion_lanzaBadRequestYNoGuarda() {
        HistoriaImpacto historia = crearHistoria(false, true, "PUBLICADO");

        BadRequestException error = assertThrows(BadRequestException.class, () -> historiaService.guardar(historia));

        assertEquals("Una historia solo se puede publicar si está autorizada y anonimizada", error.getMessage());
        verify(historiaRepository, never()).save(any());
    }

    @Test
    void guardar_publicadaSinAnonimizar_lanzaBadRequest() {
        HistoriaImpacto historia = crearHistoria(true, false, "PUBLICADO");

        assertThrows(BadRequestException.class, () -> historiaService.guardar(historia));
    }

    @Test
    void guardar_publicadaYAutorizadaYAnonimizada_poneFechaDePublicacion() {
        when(historiaRepository.save(any(HistoriaImpacto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        HistoriaImpacto guardada = historiaService.guardar(crearHistoria(true, true, "PUBLICADO"));

        assertNotNull(guardada.getFechaPublicacion());
    }

    @Test
    void guardar_comoBorradorSinAutorizacion_esValido_yNoTieneFechaDePublicacion() {
        when(historiaRepository.save(any(HistoriaImpacto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        HistoriaImpacto guardada = historiaService.guardar(crearHistoria(false, false, "BORRADOR"));

        assertNull(guardada.getFechaPublicacion());
    }
}
