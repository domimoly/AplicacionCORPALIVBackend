package cl.corpaliv.vinculacion.dto;

import java.time.LocalDateTime;

import cl.corpaliv.vinculacion.model.HistorialSolicitud;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialResponse {
    private Long id;
    private String estadoAnterior;
    private String estadoNuevo;
    private String observacion;
    private String gestor;
    private LocalDateTime fecha;

    public static HistorialResponse desde(HistorialSolicitud h) {
        return new HistorialResponse(h.getId(), h.getEstadoAnterior(), h.getEstadoNuevo(),
                h.getObservacion(), h.getGestor(), h.getFecha());
    }
}
