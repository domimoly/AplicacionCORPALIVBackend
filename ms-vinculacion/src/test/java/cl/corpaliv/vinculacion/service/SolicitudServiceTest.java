package cl.corpaliv.vinculacion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.corpaliv.vinculacion.client.CatalogoClient;
import cl.corpaliv.vinculacion.client.ContenidosClient;
import cl.corpaliv.vinculacion.dto.CambioEstadoRequest;
import cl.corpaliv.vinculacion.dto.SolicitudRequest;
import cl.corpaliv.vinculacion.exception.BadRequestException;
import cl.corpaliv.vinculacion.exception.ResourceNotFoundException;
import cl.corpaliv.vinculacion.model.EstadoSolicitud;
import cl.corpaliv.vinculacion.model.HistorialSolicitud;
import cl.corpaliv.vinculacion.model.SolicitudColaboracion;
import cl.corpaliv.vinculacion.model.TipoColaboracion;
import cl.corpaliv.vinculacion.repository.HistorialSolicitudRepository;
import cl.corpaliv.vinculacion.repository.SolicitudColaboracionRepository;
import cl.corpaliv.vinculacion.repository.TipoColaboracionRepository;

// PRUEBAS de las reglas de negocio de las solicitudes. Los repositorios y los clientes de los otros microservicios
// son mocks (falsos): así no hace falta base de datos ni que ms-contenidos / ms-catalogo estén encendidos.
@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudColaboracionRepository solicitudRepository;

    @Mock
    private TipoColaboracionRepository tipoRepository;

    @Mock
    private HistorialSolicitudRepository historialRepository;

    @Mock
    private ContenidosClient contenidosClient;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private GeneradorCodigo generadorCodigo;

    private SolicitudService solicitudService;

    private final TipoColaboracion tipoDonacion = new TipoColaboracion(1L, "DONACION", "Donación", "Aporte");

    @BeforeEach
    void setUp() {
        solicitudService = new SolicitudService(solicitudRepository, tipoRepository, historialRepository,
                contenidosClient, catalogoClient, generadorCodigo);
    }

    private SolicitudRequest crearRequest() {
        SolicitudRequest request = new SolicitudRequest();
        request.setNombre("Donante Ejemplo");
        request.setMedioContacto("donante@ejemplo.cl");
        request.setTipo("donacion"); // en minúsculas a propósito: el servicio lo normaliza
        request.setMonto(20000);
        request.setAceptaPrivacidad(true);
        return request;
    }

    private SolicitudColaboracion crearSolicitud(String estado) {
        SolicitudColaboracion solicitud = new SolicitudColaboracion();
        solicitud.setId(10L);
        solicitud.setCodigoSeguimiento("COL-ABCD2345");
        solicitud.setNombre("Donante Ejemplo");
        solicitud.setMedioContacto("donante@ejemplo.cl");
        solicitud.setTipo(tipoDonacion);
        solicitud.setEstado(estado);
        solicitud.setObservacion("");
        return solicitud;
    }

    // ---------- crear ----------

    @Test
    void crear_conDatosValidos_quedaRecibidaConCodigoYRegistraHistorial() {
        when(tipoRepository.findByCodigo("DONACION")).thenReturn(Optional.of(tipoDonacion));
        when(generadorCodigo.generar()).thenReturn("COL-ABCD2345");
        when(solicitudRepository.existsByCodigoSeguimiento("COL-ABCD2345")).thenReturn(false);
        when(solicitudRepository.save(any(SolicitudColaboracion.class))).thenAnswer(invocacion -> {
            SolicitudColaboracion s = invocacion.getArgument(0);
            s.setId(10L); // simula el id que asigna la base de datos
            return s;
        });

        SolicitudColaboracion guardada = solicitudService.crear(crearRequest());

        assertEquals(EstadoSolicitud.RECIBIDA, guardada.getEstado());
        assertEquals("COL-ABCD2345", guardada.getCodigoSeguimiento());
        assertEquals("DONACION", guardada.getTipo().getCodigo());
        assertNotNull(guardada.getFechaCreacion());

        // Se registra el primer movimiento del historial: sin estado anterior, hecho por el 'sistema'
        ArgumentCaptor<HistorialSolicitud> captor = ArgumentCaptor.forClass(HistorialSolicitud.class);
        verify(historialRepository).save(captor.capture());
        assertEquals(10L, captor.getValue().getSolicitudId());
        assertEquals(EstadoSolicitud.RECIBIDA, captor.getValue().getEstadoNuevo());
        assertEquals("sistema", captor.getValue().getGestor());
    }

    @Test
    void crear_conCodigoRepetido_generaOtroCodigo() {
        when(tipoRepository.findByCodigo("DONACION")).thenReturn(Optional.of(tipoDonacion));
        when(generadorCodigo.generar()).thenReturn("COL-REPETIDO", "COL-NUEVO2345");
        when(solicitudRepository.existsByCodigoSeguimiento("COL-REPETIDO")).thenReturn(true);
        when(solicitudRepository.existsByCodigoSeguimiento("COL-NUEVO2345")).thenReturn(false);
        when(solicitudRepository.save(any(SolicitudColaboracion.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        SolicitudColaboracion guardada = solicitudService.crear(crearRequest());

        assertEquals("COL-NUEVO2345", guardada.getCodigoSeguimiento());
    }

    @Test
    void crear_conTipoInexistente_lanzaBadRequestYNoGuarda() {
        when(tipoRepository.findByCodigo("DONACION")).thenReturn(Optional.empty());

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> solicitudService.crear(crearRequest()));

        assertEquals("El tipo de colaboración indicado no existe", error.getMessage());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void crear_conProgramaInexistente_lanzaBadRequestYNoGuarda() {
        when(tipoRepository.findByCodigo("DONACION")).thenReturn(Optional.of(tipoDonacion));
        when(contenidosClient.existePrograma(99L)).thenReturn(false);
        SolicitudRequest request = crearRequest();
        request.setProgramaId(99L);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> solicitudService.crear(request));

        assertEquals("El programa indicado no existe", error.getMessage());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void crear_conProductoInexistente_lanzaBadRequestYNoGuarda() {
        when(tipoRepository.findByCodigo("DONACION")).thenReturn(Optional.of(tipoDonacion));
        when(catalogoClient.existeProducto(77L)).thenReturn(false);
        SolicitudRequest request = crearRequest();
        request.setProductoId(77L);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> solicitudService.crear(request));

        assertEquals("El producto indicado no existe", error.getMessage());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void crear_conProgramaYProductoExistentes_guardaLosIds() {
        when(tipoRepository.findByCodigo("DONACION")).thenReturn(Optional.of(tipoDonacion));
        when(contenidosClient.existePrograma(2L)).thenReturn(true);
        when(catalogoClient.existeProducto(5L)).thenReturn(true);
        when(generadorCodigo.generar()).thenReturn("COL-ABCD2345");
        when(solicitudRepository.save(any(SolicitudColaboracion.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        SolicitudRequest request = crearRequest();
        request.setProgramaId(2L);
        request.setProductoId(5L);

        SolicitudColaboracion guardada = solicitudService.crear(request);

        assertEquals(2L, guardada.getProgramaId());
        assertEquals(5L, guardada.getProductoId());
    }

    // ---------- seguimiento ----------

    @Test
    void obtenerPorCodigo_normalizaElCodigo() {
        when(solicitudRepository.findByCodigoSeguimiento("COL-ABCD2345"))
                .thenReturn(Optional.of(crearSolicitud(EstadoSolicitud.RECIBIDA)));

        SolicitudColaboracion encontrada = solicitudService.obtenerPorCodigo("  col-abcd2345 ");

        assertEquals("COL-ABCD2345", encontrada.getCodigoSeguimiento());
    }

    @Test
    void obtenerPorCodigo_inexistente_lanzaNotFound() {
        when(solicitudRepository.findByCodigoSeguimiento("COL-NADA0000")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> solicitudService.obtenerPorCodigo("COL-NADA0000"));
    }

    // ---------- listar ----------

    @Test
    void listar_sinEstado_devuelveTodas() {
        when(solicitudRepository.findAllByOrderByFechaCreacionDesc())
                .thenReturn(List.of(crearSolicitud(EstadoSolicitud.RECIBIDA)));

        assertEquals(1, solicitudService.listar(null).size());
        verify(solicitudRepository, never()).findByEstadoOrderByFechaCreacionDesc(any());
    }

    @Test
    void listar_conEstadoValido_filtraPorEse() {
        when(solicitudRepository.findByEstadoOrderByFechaCreacionDesc("EN_REVISION"))
                .thenReturn(List.of(crearSolicitud(EstadoSolicitud.EN_REVISION)));

        assertEquals(1, solicitudService.listar("en_revision").size());
    }

    @Test
    void listar_conEstadoInvalido_lanzaBadRequest() {
        assertThrows(BadRequestException.class, () -> solicitudService.listar("INVENTADO"));
    }

    // ---------- cambiar estado ----------

    @Test
    void cambiarEstado_guardaElNuevoEstadoYRegistraAlGestor() {
        SolicitudColaboracion solicitud = crearSolicitud(EstadoSolicitud.RECIBIDA);
        when(solicitudRepository.findById(10L)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(any(SolicitudColaboracion.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        SolicitudColaboracion actualizada = solicitudService.cambiarEstado(10L,
                new CambioEstadoRequest("en_revision", "Estamos revisando tu solicitud."), "gestor@ejemplo.cl");

        assertEquals(EstadoSolicitud.EN_REVISION, actualizada.getEstado());
        assertEquals("Estamos revisando tu solicitud.", actualizada.getObservacion());

        ArgumentCaptor<HistorialSolicitud> captor = ArgumentCaptor.forClass(HistorialSolicitud.class);
        verify(historialRepository).save(captor.capture());
        assertEquals(EstadoSolicitud.RECIBIDA, captor.getValue().getEstadoAnterior());
        assertEquals(EstadoSolicitud.EN_REVISION, captor.getValue().getEstadoNuevo());
        assertEquals("gestor@ejemplo.cl", captor.getValue().getGestor());
    }

    @Test
    void cambiarEstado_conEstadoInvalido_lanzaBadRequestYNoGuarda() {
        when(solicitudRepository.findById(10L)).thenReturn(Optional.of(crearSolicitud(EstadoSolicitud.RECIBIDA)));

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> solicitudService.cambiarEstado(10L, new CambioEstadoRequest("INVENTADO", ""), "gestor@ejemplo.cl"));

        assertEquals("El estado indicado no es válido", error.getMessage());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void cambiarEstado_desdeUnEstadoFinal_lanzaBadRequestYNoGuarda() {
        when(solicitudRepository.findById(10L)).thenReturn(Optional.of(crearSolicitud(EstadoSolicitud.CONCRETADO)));

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> solicitudService.cambiarEstado(10L,
                        new CambioEstadoRequest(EstadoSolicitud.EN_REVISION, ""), "gestor@ejemplo.cl"));

        assertEquals("La solicitud ya está cerrada y no se puede modificar", error.getMessage());
        verify(solicitudRepository, never()).save(any());
        verify(historialRepository, never()).save(any());
    }

    @Test
    void cambiarEstado_solicitudInexistente_lanzaNotFound() {
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> solicitudService.cambiarEstado(99L,
                        new CambioEstadoRequest(EstadoSolicitud.EN_REVISION, ""), "gestor@ejemplo.cl"));
    }

    // ---------- historial y eliminar ----------

    @Test
    void obtenerHistorial_devuelveLosCambiosDeLaSolicitud() {
        when(solicitudRepository.findById(10L)).thenReturn(Optional.of(crearSolicitud(EstadoSolicitud.RECIBIDA)));
        when(historialRepository.findBySolicitudIdOrderByFechaAsc(10L))
                .thenReturn(List.of(new HistorialSolicitud(), new HistorialSolicitud()));

        assertEquals(2, solicitudService.obtenerHistorial(10L).size());
    }

    @Test
    void eliminar_borraElHistorialYLaSolicitud() {
        when(solicitudRepository.existsById(10L)).thenReturn(true);

        solicitudService.eliminar(10L);

        verify(historialRepository).deleteBySolicitudId(10L);
        verify(solicitudRepository).deleteById(10L);
    }

    @Test
    void eliminar_inexistente_lanzaNotFoundYNoBorra() {
        when(solicitudRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> solicitudService.eliminar(99L));

        verify(historialRepository, never()).deleteBySolicitudId(any());
        verify(solicitudRepository, never()).deleteById(any());
    }
}
