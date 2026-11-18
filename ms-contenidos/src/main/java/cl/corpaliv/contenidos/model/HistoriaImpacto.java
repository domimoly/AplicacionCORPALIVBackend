package cl.corpaliv.contenidos.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// HISTORIA DE IMPACTO: relato breve sobre lo que logra CORPALIV. Tabla 'historia_impacto'.
// REGLA DE NEGOCIO (Propuesta Técnica): una historia solo se puede PUBLICAR si está autorizada Y anonimizada
// (sin nombres ni fotos reales). Esa regla se revisa en HistoriaImpactoService.
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaImpacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "El resumen es obligatorio")
    @Size(max = 200, message = "El resumen no puede superar los 200 caracteres")
    @Column(nullable = false, length = 200)
    private String resumen;

    @NotBlank(message = "El contenido es obligatorio")
    @Size(max = 4000, message = "El contenido no puede superar los 4000 caracteres")
    @Column(nullable = false, length = 4000)
    private String contenido;

    @Size(max = 300, message = "La URL de la imagen no puede superar los 300 caracteres")
    @Column(length = 300)
    private String imagenUrl;

    // ¿La persona o su familia dio permiso para contar la historia?
    private boolean autorizada;

    // ¿Se quitaron nombres y datos que permitan reconocer a la persona?
    private boolean anonimizada;

    @NotBlank(message = "El estado de publicación es obligatorio")
    @Pattern(regexp = "BORRADOR|PUBLICADO", message = "El estado de publicación debe ser BORRADOR o PUBLICADO")
    @Column(nullable = false, length = 20)
    private String estadoPublicacion = "BORRADOR";

    // La pone el servicio cuando la historia pasa a PUBLICADO
    private LocalDateTime fechaPublicacion;
}
