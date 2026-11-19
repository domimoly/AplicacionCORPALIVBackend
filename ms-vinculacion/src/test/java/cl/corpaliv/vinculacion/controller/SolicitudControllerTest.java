package cl.corpaliv.vinculacion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cl.corpaliv.vinculacion.dto.CambioEstadoRequest;
import cl.corpaliv.vinculacion.dto.SolicitudRequest;
import cl.corpaliv.vinculacion.exception.BadRequestException;
import cl.corpaliv.vinculacion.exception.GlobalExceptionHandler;
import cl.corpaliv.vinculacion.exception.ResourceNotFoundException;
import cl.corpaliv.vinculacion.exception.ServiceUnavailableException;
import cl.corpaliv.vinculacion.model.EstadoSolicitud;
import cl.corpaliv.vinculacion.model.SolicitudColaboracion;
import cl.corpaliv.vinculacion.model.TipoColaboracion;
import cl.corpaliv.vinculacion.service.SolicitudService;

// PRUEBAS DEL CONTROLADOR con MockMvc: simulan peticiones HTTP (GET, POST, PATCH...) sin levantar el servidor.
// El servicio es un mock. Se incluye GlobalExceptionHandler para comprobar los códigos de error (400, 404, 503).
@ExtendWith(MockitoExtension.class)
class SolicitudControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SolicitudService solicitudService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SolicitudController(solicitudService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // Las fechas quedan en null a propósito para que la prueba no dependa de cómo se escriben las fechas en JSON
    private SolicitudColaboracion crearSolicitud(String estado) {
        SolicitudColaboracion solicitud = new SolicitudColaboracion();
        solicitud.setId(10L);
        solicitud.setCodigoSeguimiento("COL-ABCD2345");
        solicitud.setNombre("Donante Ejemplo");
        solicitud.setMedioContacto("donante@ejemplo.cl");
        solicitud.setTipo(new TipoColaboracion(1L, "DONACION", "Donación", "Aporte"));
        solicitud.setEstado(estado);
        solicitud.setObservacion("");
        return solicitud;
    }

    private static final String JSON_VALIDO =
            "{\"nombre\":\"Donante Ejemplo\",\"medioContacto\":\"donante@ejemplo.cl\",\"tipo\":\"DONACION\",\"aceptaPrivacidad\":true}";

    @Test
    void crear_conDatosValidos_responde201ConElCodigoDeSeguimiento() throws Exception {
        when(solicitudService.crear(any(SolicitudRequest.class))).thenReturn(crearSolicitud(EstadoSolicitud.RECIBIDA));

        mockMvc.perform(post("/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoSeguimiento").value("COL-ABCD2345"))
                .andExpect(jsonPath("$.estado").value("RECIBIDA"))
                // La respuesta pública NO incluye datos personales
                .andExpect(jsonPath("$.nombre").doesNotExist())
                .andExpect(jsonPath("$.medioContacto").doesNotExist());
    }

    @Test
    void crear_sinAceptarPrivacidad_responde400YNoLlamaAlServicio() throws Exception {
        String json = "{\"nombre\":\"Donante Ejemplo\",\"medioContacto\":\"donante@ejemplo.cl\",\"tipo\":\"DONACION\",\"aceptaPrivacidad\":false}";

        mockMvc.perform(post("/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(solicitudService, never()).crear(any());
    }

    @Test
    void crear_sinNombre_responde400() throws Exception {
        String json = "{\"nombre\":\"\",\"medioContacto\":\"donante@ejemplo.cl\",\"tipo\":\"DONACION\",\"aceptaPrivacidad\":true}";

        mockMvc.perform(post("/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(solicitudService, never()).crear(any());
    }

    @Test
    void crear_conProgramaInexistente_responde400ConMensaje() throws Exception {
        when(solicitudService.crear(any(SolicitudRequest.class)))
                .thenThrow(new BadRequestException("El programa indicado no existe"));

        mockMvc.perform(post("/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El programa indicado no existe"));
    }

    @Test
    void crear_cuandoOtroServicioNoResponde_responde503() throws Exception {
        when(solicitudService.crear(any(SolicitudRequest.class)))
                .thenThrow(new ServiceUnavailableException("El servicio de contenidos no está disponible"));

        mockMvc.perform(post("/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void seguimiento_conCodigoExistente_responde200SinDatosPersonales() throws Exception {
        when(solicitudService.obtenerPorCodigo("COL-ABCD2345")).thenReturn(crearSolicitud(EstadoSolicitud.CONTACTADO));

        mockMvc.perform(get("/solicitudes/seguimiento/COL-ABCD2345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONTACTADO"))
                .andExpect(jsonPath("$.tipo").value("DONACION"))
                .andExpect(jsonPath("$.nombre").doesNotExist())
                .andExpect(jsonPath("$.medioContacto").doesNotExist());
    }

    @Test
    void seguimiento_conCodigoInexistente_responde404() throws Exception {
        when(solicitudService.obtenerPorCodigo("COL-NADA0000"))
                .thenThrow(new ResourceNotFoundException("No encontramos una solicitud con ese código"));

        mockMvc.perform(get("/solicitudes/seguimiento/COL-NADA0000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No encontramos una solicitud con ese código"));
    }

    @Test
    void listar_responde200ConLosDatosCompletosParaElGestor() throws Exception {
        when(solicitudService.listar(null)).thenReturn(List.of(crearSolicitud(EstadoSolicitud.RECIBIDA)));

        mockMvc.perform(get("/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].nombre").value("Donante Ejemplo"))
                .andExpect(jsonPath("$[0].medioContacto").value("donante@ejemplo.cl"));
    }

    @Test
    void listar_conFiltroDeEstado_pasaElEstadoAlServicio() throws Exception {
        when(solicitudService.listar("EN_REVISION")).thenReturn(List.of(crearSolicitud(EstadoSolicitud.EN_REVISION)));

        mockMvc.perform(get("/solicitudes").param("estado", "EN_REVISION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("EN_REVISION"));
    }

    @Test
    void cambiarEstado_usaElCorreoDelGestorDelToken() throws Exception {
        when(solicitudService.cambiarEstado(eq(10L), any(CambioEstadoRequest.class), eq("gestor@ejemplo.cl")))
                .thenReturn(crearSolicitud(EstadoSolicitud.EN_REVISION));

        // .principal(...) simula al usuario autenticado; getName() es el correo que vendría en el token
        mockMvc.perform(patch("/solicitudes/10/estado")
                        .principal(() -> "gestor@ejemplo.cl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"EN_REVISION\",\"observacion\":\"Estamos revisando\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_REVISION"));
    }

    @Test
    void cambiarEstado_sinEstado_responde400YNoLlamaAlServicio() throws Exception {
        mockMvc.perform(patch("/solicitudes/10/estado")
                        .principal(() -> "gestor@ejemplo.cl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(solicitudService, never()).cambiarEstado(any(), any(), any());
    }

    @Test
    void cambiarEstado_deUnaSolicitudCerrada_responde400() throws Exception {
        when(solicitudService.cambiarEstado(eq(10L), any(CambioEstadoRequest.class), eq("gestor@ejemplo.cl")))
                .thenThrow(new BadRequestException("La solicitud ya está cerrada y no se puede modificar"));

        mockMvc.perform(patch("/solicitudes/10/estado")
                        .principal(() -> "gestor@ejemplo.cl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"EN_REVISION\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La solicitud ya está cerrada y no se puede modificar"));
    }

    @Test
    void eliminar_responde204() throws Exception {
        mockMvc.perform(delete("/solicitudes/10"))
                .andExpect(status().isNoContent());

        verify(solicitudService).eliminar(10L);
    }
}
