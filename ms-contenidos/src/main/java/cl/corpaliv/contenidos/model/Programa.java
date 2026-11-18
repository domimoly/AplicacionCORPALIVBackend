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

// PROGRAMA: una iniciativa de CORPALIV (Escuela Jan Van Dijk, Taller Sociolaboral, etc.). Tabla 'programa'.
// Los nombres de los campos siguen la Propuesta Técnica (en Java van en camelCase y en la base de datos Spring
// los pasa a snake_case: estadoPublicacion -> estado_publicacion).
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Programa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Validaciones: si el JSON que llega no las cumple, la API responde 400 con el mensaje indicado
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nombre;

    @NotBlank(message = "El resumen es obligatorio")
    @Size(max = 200, message = "El resumen no puede superar los 200 caracteres")
    @Column(nullable = false, length = 200)
    private String resumen;

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    @Column(length = 2000)
    private String descripcion;

    @Size(max = 100, message = "La categoría no puede superar los 100 caracteres")
    @Column(length = 100)
    private String categoria;

    // Solo la dirección de la imagen (URL). No se guardan fotos de personas
    @Size(max = 300, message = "La URL de la imagen no puede superar los 300 caracteres")
    @Column(length = 300)
    private String imagenUrl;

    // BORRADOR = todavía no se ve en la app. PUBLICADO = lo ve cualquier visitante
    @NotBlank(message = "El estado de publicación es obligatorio")
    @Pattern(regexp = "BORRADOR|PUBLICADO", message = "El estado de publicación debe ser BORRADOR o PUBLICADO")
    @Column(nullable = false, length = 20)
    private String estadoPublicacion = "BORRADOR";

    // Estas dos fechas las pone el servicio automáticamente (el cliente no las envía)
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
