package cl.corpaliv.vinculacion.dto;

import java.time.LocalDateTime;

import cl.corpaliv.vinculacion.model.SolicitudColaboracion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Vista PÚBLICA de una solicitud: lo que ve quien la envió al consultar con su código.
// OJO: no incluye nombre ni medio de contacto ni mensaje (datos personales), solo el estado.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoResponse {
    private String codigoSeguimiento;
    private String tipo;
    private String estado;
    private String observacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public static SeguimientoResponse desde(SolicitudColaboracion s) {
        return new SeguimientoResponse(s.getCodigoSeguimiento(), s.getTipo().getCodigo(), s.getEstado(),
                s.getObservacion(), s.getFechaCreacion(), s.getFechaActualizacion());
    }
}
