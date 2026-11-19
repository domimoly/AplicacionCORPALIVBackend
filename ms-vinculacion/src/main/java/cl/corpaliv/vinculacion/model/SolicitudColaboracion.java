package cl.corpaliv.vinculacion.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// SOLICITUD DE COLABORACIÓN que envía una persona o empresa desde la app. Tabla 'solicitud_colaboracion'.
// EXPLICAR: tiene datos de contacto, así que SOLO los gestores con token pueden verla completa. La persona que la envía
// recibe un 'codigoSeguimiento' (difícil de adivinar) para consultar el estado sin iniciar sesión.
// 'programaId' y 'productoId' son ids de OTROS microservicios (ms-contenidos y ms-catalogo): no hay relación entre tablas
// porque cada microservicio tiene su propia base de datos; por eso se validan llamando a esos servicios por HTTP.
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudColaboracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigoSeguimiento;

    @Column(nullable = false, length = 100)
    private String nombre;

    // Correo o teléfono con el que CORPALIV puede contactar a la persona
    @Column(nullable = false, length = 120)
    private String medioContacto;

    // Muchas solicitudes tienen el mismo tipo. EAGER = el tipo se carga junto con la solicitud
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tipo_id")
    private TipoColaboracion tipo;

    @Column(length = 500)
    private String mensaje;

    // Monto en pesos (solo tiene sentido en donaciones). Opcional
    private Integer monto;

    // Día en que la persona prefiere que la contacten. Opcional
    private LocalDate fechaContacto;

    // Id de un programa de ms-contenidos o de un producto de ms-catalogo. Ambos opcionales
    private Long programaId;
    private Long productoId;

    private boolean prioritario;

    // La persona aceptó la política de privacidad (es obligatorio para enviar la solicitud)
    private boolean aceptaPrivacidad;

    // RECIBIDA, EN_REVISION, CONTACTADO, CONCRETADO o DESCARTADO (ver EstadoSolicitud)
    @Column(nullable = false, length = 20)
    private String estado;

    // Mensaje del gestor para la persona (se muestra en el seguimiento)
    @Column(length = 200)
    private String observacion;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
