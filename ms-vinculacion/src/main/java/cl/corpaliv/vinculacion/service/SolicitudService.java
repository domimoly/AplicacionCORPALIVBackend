package cl.corpaliv.vinculacion.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

// SERVICIO: aquí están las REGLAS DE NEGOCIO de las solicitudes (el controlador solo recibe y responde).
// Reglas principales:
//  - Una solicitud nueva siempre parte en RECIBIDA y recibe un código de seguimiento único.
//  - El tipo debe existir; si se indica un programa o un producto, deben existir en ms-contenidos / ms-catalogo.
//  - Solo se aceptan los 5 estados válidos y una solicitud CERRADA (CONCRETADO o DESCARTADO) no se puede modificar.
//  - Cada cambio de estado queda registrado en el historial con el correo del gestor.
@Service
public class SolicitudService {

    private static final String SISTEMA = "sistema";

    private final SolicitudColaboracionRepository solicitudRepository;
    private final TipoColaboracionRepository tipoRepository;
    private final HistorialSolicitudRepository historialRepository;
    private final ContenidosClient contenidosClient;
    private final CatalogoClient catalogoClient;
    private final GeneradorCodigo generadorCodigo;

    public SolicitudService(SolicitudColaboracionRepository solicitudRepository,
                            TipoColaboracionRepository tipoRepository,
                            HistorialSolicitudRepository historialRepository,
                            ContenidosClient contenidosClient,
                            CatalogoClient catalogoClient,
                            GeneradorCodigo generadorCodigo) {
        this.solicitudRepository = solicitudRepository;
        this.tipoRepository = tipoRepository;
        this.historialRepository = historialRepository;
        this.contenidosClient = contenidosClient;
        this.catalogoClient = catalogoClient;
        this.generadorCodigo = generadorCodigo;
    }

    // ---------- Público ----------

    public SolicitudColaboracion crear(SolicitudRequest request) {
        TipoColaboracion tipo = tipoRepository.findByCodigo(request.getTipo().trim().toUpperCase())
                .orElseThrow(() -> new BadRequestException("El tipo de colaboración indicado no existe"));

        // Validación contra OTROS microservicios (solo si la persona indicó un programa o un producto)
        if (request.getProgramaId() != null && !contenidosClient.existePrograma(request.getProgramaId())) {
            throw new BadRequestException("El programa indicado no existe");
        }
        if (request.getProductoId() != null && !catalogoClient.existeProducto(request.getProductoId())) {
            throw new BadRequestException("El producto indicado no existe");
        }

        LocalDateTime ahora = LocalDateTime.now();
        SolicitudColaboracion solicitud = new SolicitudColaboracion();
        solicitud.setCodigoSeguimiento(generarCodigoUnico());
        solicitud.setNombre(request.getNombre().trim());
        solicitud.setMedioContacto(request.getMedioContacto().trim());
        solicitud.setTipo(tipo);
        solicitud.setMensaje(request.getMensaje());
        solicitud.setMonto(request.getMonto());
        solicitud.setFechaContacto(request.getFechaContacto());
        solicitud.setProgramaId(request.getProgramaId());
        solicitud.setProductoId(request.getProductoId());
        solicitud.setPrioritario(request.isPrioritario());
        solicitud.setAceptaPrivacidad(request.isAceptaPrivacidad());
        solicitud.setEstado(EstadoSolicitud.RECIBIDA);
        solicitud.setObservacion("");
        solicitud.setFechaCreacion(ahora);
        solicitud.setFechaActualizacion(ahora);

        SolicitudColaboracion guardada = solicitudRepository.save(solicitud);
        registrarHistorial(guardada.getId(), null, EstadoSolicitud.RECIBIDA, "Solicitud recibida", SISTEMA);
        return guardada;
    }

    // Consulta del estado con el código de seguimiento (sin iniciar sesión)
    public SolicitudColaboracion obtenerPorCodigo(String codigo) {
        return solicitudRepository.findByCodigoSeguimiento(codigo.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("No encontramos una solicitud con ese código"));
    }

    public List<TipoColaboracion> listarTipos() {
        return tipoRepository.findAll();
    }

    // ---------- Solo gestores (con token) ----------

    // estado = null o vacío: devuelve todas. Con estado: solo las de ese estado
    public List<SolicitudColaboracion> listar(String estado) {
        if (estado == null || estado.isBlank()) {
            return solicitudRepository.findAllByOrderByFechaCreacionDesc();
        }
        String codigo = estado.trim().toUpperCase();
        if (!EstadoSolicitud.esValido(codigo)) {
            throw new BadRequestException("El estado indicado no es válido");
        }
        return solicitudRepository.findByEstadoOrderByFechaCreacionDesc(codigo);
    }

    public SolicitudColaboracion obtenerPorId(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no existe"));
    }

    public List<HistorialSolicitud> obtenerHistorial(Long id) {
        obtenerPorId(id); // si la solicitud no existe, lanza 404
        return historialRepository.findBySolicitudIdOrderByFechaAsc(id);
    }

    public SolicitudColaboracion cambiarEstado(Long id, CambioEstadoRequest request, String gestor) {
        SolicitudColaboracion solicitud = obtenerPorId(id);

        String nuevoEstado = request.getEstado().trim().toUpperCase();
        if (!EstadoSolicitud.esValido(nuevoEstado)) {
            throw new BadRequestException("El estado indicado no es válido");
        }
        // REGLA DE NEGOCIO: una solicitud cerrada no se reabre
        if (EstadoSolicitud.esFinal(solicitud.getEstado())) {
            throw new BadRequestException("La solicitud ya está cerrada y no se puede modificar");
        }

        String observacion = request.getObservacion() == null ? "" : request.getObservacion().trim();
        String estadoAnterior = solicitud.getEstado();

        solicitud.setEstado(nuevoEstado);
        solicitud.setObservacion(observacion);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        SolicitudColaboracion guardada = solicitudRepository.save(solicitud);

        registrarHistorial(id, estadoAnterior, nuevoEstado, observacion, gestor);
        return guardada;
    }

    // @Transactional: borrar el historial y la solicitud ocurre como una sola operación (o se hacen las dos o ninguna)
    @Transactional
    public void eliminar(Long id) {
        if (!solicitudRepository.existsById(id)) {
            throw new ResourceNotFoundException("Solicitud no existe");
        }
        historialRepository.deleteBySolicitudId(id);
        solicitudRepository.deleteById(id);
    }

    // ---------- Métodos auxiliares ----------

    private void registrarHistorial(Long solicitudId, String estadoAnterior, String estadoNuevo,
                                    String observacion, String gestor) {
        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setSolicitudId(solicitudId);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setObservacion(observacion);
        historial.setGestor(gestor);
        historial.setFecha(LocalDateTime.now());
        historialRepository.save(historial);
    }

    // Genera un código y, por si acaso, repite hasta que no exista otro igual en la base de datos
    private String generarCodigoUnico() {
        String codigo = generadorCodigo.generar();
        while (solicitudRepository.existsByCodigoSeguimiento(codigo)) {
            codigo = generadorCodigo.generar();
        }
        return codigo;
    }
}
