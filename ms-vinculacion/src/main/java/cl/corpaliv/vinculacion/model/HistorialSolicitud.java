package cl.corpaliv.vinculacion.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// HISTORIAL: una fila por cada cambio de estado de una solicitud (quién, cuándo y con qué observación). Tabla 'historial_solicitud'.
// EXPLICAR: sirve para auditoría; así se sabe qué gestor cambió una solicitud y cuándo.
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Id de la solicitud (misma base de datos, pero se guarda solo el número para mantenerlo simple)
    @Column(nullable = false)
    private Long solicitudId;

    // null cuando la solicitud recién se creó
    @Column(length = 20)
    private String estadoAnterior;

    @Column(nullable = false, length = 20)
    private String estadoNuevo;

    @Column(length = 200)
    private String observacion;

    // Correo del gestor que hizo el cambio (viene del token) o 'sistema' al crear la solicitud
    @Column(length = 120)
    private String gestor;

    private LocalDateTime fecha;
}
