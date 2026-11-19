package cl.corpaliv.vinculacion;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.corpaliv.vinculacion.dto.CambioEstadoRequest;
import cl.corpaliv.vinculacion.dto.SolicitudRequest;
import cl.corpaliv.vinculacion.model.EstadoSolicitud;
import cl.corpaliv.vinculacion.model.SolicitudColaboracion;
import cl.corpaliv.vinculacion.model.TipoColaboracion;
import cl.corpaliv.vinculacion.repository.TipoColaboracionRepository;
import cl.corpaliv.vinculacion.service.SolicitudService;

// DATOS DE EJEMPLO: se cargan al iniciar, pero SOLO si no hay tipos de colaboración (así no se duplican al reiniciar).
// EXPLICAR: CommandLineRunner es una interfaz de Spring: su método run() se ejecuta una vez, justo después de arrancar.
// Las solicitudes de ejemplo son FICTICIAS (nombres y contactos inventados, correos @ejemplo.cl). Se crean con el servicio
// (no con el repositorio) para que se apliquen las mismas reglas y quede el historial.
@Component
public class DataLoader implements CommandLineRunner {

    private final TipoColaboracionRepository tipoRepository;
    private final SolicitudService solicitudService;

    public DataLoader(TipoColaboracionRepository tipoRepository, SolicitudService solicitudService) {
        this.tipoRepository = tipoRepository;
        this.solicitudService = solicitudService;
    }

    @Override
    public void run(String... args) {
        if (tipoRepository.count() > 0) {
            return;
        }

        // ---------- Tipos de colaboración ----------
        crearTipo("DONACION", "Donación", "Aporte en dinero para apoyar los programas");
        crearTipo("ALIANZA_RSE", "Alianza empresarial (RSE)", "Alianzas con empresas y organizaciones");
        crearTipo("VOLUNTARIADO", "Voluntariado", "Participar con tiempo y habilidades");
        crearTipo("COMPRA", "Compra de productos o servicios", "Productos y servicios de Alba Lab");
        crearTipo("CAPACITACION", "Capacitación o charla", "Capacitaciones en inclusión para equipos");
        crearTipo("OTRO", "Otro", "Cualquier otra consulta o forma de colaborar");

        // ---------- Solicitudes de ejemplo (FICTICIAS) ----------
        SolicitudColaboracion empresa = solicitudService.crear(nuevaSolicitud("Empresa Ejemplo S.A.",
                "contacto@empresa-ejemplo.cl", "ALIANZA_RSE", "Nos interesa conocer cómo sumarnos a la red de empresas aliadas.", null));
        solicitudService.cambiarEstado(empresa.getId(),
                new CambioEstadoRequest(EstadoSolicitud.EN_REVISION, "Estamos revisando tu solicitud."), "gestor@ejemplo.cl");

        solicitudService.crear(nuevaSolicitud("Persona Voluntaria Ejemplo",
                "voluntaria@ejemplo.cl", "VOLUNTARIADO", "Me gustaría acompañar actividades los fines de semana.", null));

        SolicitudColaboracion donante = solicitudService.crear(nuevaSolicitud("Donante Ejemplo",
                "donante@ejemplo.cl", "DONACION", "Quiero aportar a los talleres.", 20000));
        solicitudService.cambiarEstado(donante.getId(),
                new CambioEstadoRequest(EstadoSolicitud.CONTACTADO, "Te escribimos al correo esta semana."), "gestor@ejemplo.cl");
    }

    private void crearTipo(String codigo, String nombre, String descripcion) {
        TipoColaboracion tipo = new TipoColaboracion();
        tipo.setCodigo(codigo);
        tipo.setNombre(nombre);
        tipo.setDescripcion(descripcion);
        tipoRepository.save(tipo);
    }

    private SolicitudRequest nuevaSolicitud(String nombre, String contacto, String tipo, String mensaje, Integer monto) {
        SolicitudRequest request = new SolicitudRequest();
        request.setNombre(nombre);
        request.setMedioContacto(contacto);
        request.setTipo(tipo);
        request.setMensaje(mensaje);
        request.setMonto(monto);
        request.setAceptaPrivacidad(true);
        return request;
    }
}
