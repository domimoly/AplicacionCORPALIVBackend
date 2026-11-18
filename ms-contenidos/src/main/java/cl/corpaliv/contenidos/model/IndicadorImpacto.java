package cl.corpaliv.contenidos.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// INDICADOR DE IMPACTO: un número que muestra un resultado (por ejemplo 40 personas en formación en 2026).
// Tabla 'indicador_impacto'.
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicadorImpacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nombre;

    @Size(max = 300, message = "La descripción no puede superar los 300 caracteres")
    @Column(length = 300)
    private String descripcion;

    @NotNull(message = "El valor es obligatorio")
    @PositiveOrZero(message = "El valor no puede ser negativo")
    @Column(nullable = false)
    private Double valor;

    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 40, message = "La unidad no puede superar los 40 caracteres")
    @Column(nullable = false, length = 40)
    private String unidad;

    @NotBlank(message = "El período es obligatorio")
    @Size(max = 40, message = "El período no puede superar los 40 caracteres")
    @Column(nullable = false, length = 40)
    private String periodo;

    @Size(max = 150, message = "La fuente no puede superar los 150 caracteres")
    @Column(length = 150)
    private String fuente;

    // La pone el servicio cada vez que se crea o se modifica
    private LocalDateTime fechaActualizacion;

    // true = se muestra en la app
    private boolean publicado;
}
