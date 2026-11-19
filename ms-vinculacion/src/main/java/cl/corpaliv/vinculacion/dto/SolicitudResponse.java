package cl.corpaliv.vinculacion.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import cl.corpaliv.vinculacion.model.SolicitudColaboracion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Vista COMPLETA de una solicitud (incluye datos de contacto). Solo la reciben los gestores con token.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResponse {
    private Long id;
    private String codigoSeguimiento;
    private String nombre;
    private String medioContacto;
    private String tipo;
    private String mensaje;
    private Integer monto;
    private LocalDate fechaContacto;
    private Long programaId;
    private Long productoId;
    private boolean prioritario;
    private String estado;
    private String observacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Convierte la entidad (tabla) en este DTO
    public static SolicitudResponse desde(SolicitudColaboracion s) {
        return new SolicitudResponse(s.getId(), s.getCodigoSeguimiento(), s.getNombre(), s.getMedioContacto(),
                s.getTipo().getCodigo(), s.getMensaje(), s.getMonto(), s.getFechaContacto(), s.getProgramaId(),
                s.getProductoId(), s.isPrioritario(), s.getEstado(), s.getObservacion(),
                s.getFechaCreacion(), s.getFechaActualizacion());
    }
}
