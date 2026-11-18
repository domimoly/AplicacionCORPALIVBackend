package cl.corpaliv.contenidos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.corpaliv.contenidos.exception.ResourceNotFoundException;
import cl.corpaliv.contenidos.model.Programa;
import cl.corpaliv.contenidos.repository.ProgramaRepository;

// PRUEBAS UNITARIAS del servicio. EXPLICAR: el repositorio se reemplaza por un MOCK (un doble de prueba):
// no usa la base de datos real, y con when(...).thenReturn(...) le decimos qué debe responder.
// Así se prueba SOLO la lógica del servicio, rápido y sin necesitar MySQL.
@ExtendWith(MockitoExtension.class)
class ProgramaServiceTest {

    @Mock
    private ProgramaRepository programaRepository;

    private ProgramaService programaService;

    // Se ejecuta antes de cada prueba: crea el servicio con el repositorio falso
    @BeforeEach
    void setUp() {
        programaService = new ProgramaService(programaRepository);
    }

    // Método auxiliar para no repetir el armado de un programa en cada prueba
    private Programa crearPrograma(Long id, String estado) {
        Programa programa = new Programa();
        programa.setId(id);
        programa.setNombre("Taller Sociolaboral");
        programa.setResumen("Formación para jóvenes y adultos");
        programa.setEstadoPublicacion(estado);
        return programa;
    }

    @Test
    void listarPublicados_pideSoloLosPublicados() {
        when(programaRepository.findByEstadoPublicacion("PUBLICADO"))
                .thenReturn(List.of(crearPrograma(1L, "PUBLICADO")));

        List<Programa> resultado = programaService.listarPublicados();

        assertEquals(1, resultado.size());
        verify(programaRepository).findByEstadoPublicacion("PUBLICADO");
    }

    @Test
    void obtenerPublicado_conBorrador_lanzaNoEncontrado() {
        when(programaRepository.findById(1L)).thenReturn(Optional.of(crearPrograma(1L, "BORRADOR")));

        assertThrows(ResourceNotFoundException.class, () -> programaService.obtenerPublicado(1L));
    }

    @Test
    void obtenerPublicado_conProgramaPublicado_loEntrega() {
        when(programaRepository.findById(1L)).thenReturn(Optional.of(crearPrograma(1L, "PUBLICADO")));

        Programa resultado = programaService.obtenerPublicado(1L);

        assertEquals("Taller Sociolaboral", resultado.getNombre());
    }

    @Test
    void obtenerPorId_inexistente_lanzaNoEncontrado() {
        when(programaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> programaService.obtenerPorId(99L));
    }

    @Test
    void guardar_asignaFechasYIgnoraElId() {
        // thenAnswer devuelve el mismo objeto que recibe save(), como haría la base de datos
        when(programaRepository.save(any(Programa.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        Programa nuevo = crearPrograma(55L, "BORRADOR");

        Programa guardado = programaService.guardar(nuevo);

        assertNull(guardado.getId());
        assertNotNull(guardado.getFechaCreacion());
        assertNotNull(guardado.getFechaActualizacion());
    }

    @Test
    void actualizar_cambiaLosCamposYLaFechaDeActualizacion() {
        Programa existente = crearPrograma(1L, "BORRADOR");
        when(programaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(programaRepository.save(any(Programa.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        Programa datos = crearPrograma(null, "PUBLICADO");
        datos.setNombre("Nombre nuevo");

        Programa actualizado = programaService.actualizar(1L, datos);

        assertEquals("Nombre nuevo", actualizado.getNombre());
        assertEquals("PUBLICADO", actualizado.getEstadoPublicacion());
        assertNotNull(actualizado.getFechaActualizacion());
    }

    @Test
    void eliminar_inexistente_lanzaNoEncontradoYNoBorra() {
        when(programaRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> programaService.eliminar(99L));
        verify(programaRepository, never()).deleteById(any());
    }

    @Test
    void eliminar_existente_borraElPrograma() {
        when(programaRepository.existsById(1L)).thenReturn(true);

        programaService.eliminar(1L);

        verify(programaRepository).deleteById(1L);
    }
}
