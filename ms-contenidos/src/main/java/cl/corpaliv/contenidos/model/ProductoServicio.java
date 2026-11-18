package cl.corpaliv.contenidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// PRODUCTO O SERVICIO del catálogo de Alba Lab. Tabla 'producto_servicio'.
// EXPLICAR: NO hay pagos en línea; la persona solo consulta y CORPALIV la contacta (por eso 'modalidadConsulta').
// 'categoriaId' guarda el id de una Categoria (se revisa en ProductoServicioService que esa categoría exista).
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La categoría es obligatoria")
    @Column(nullable = false)
    private Long categoriaId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @Size(max = 300, message = "La URL de la imagen no puede superar los 300 caracteres")
    @Column(length = 300)
    private String imagenUrl;

    // Precio de referencia en texto (por ejemplo '$7.500' o 'A cotizar'). Es solo informativo
    @Size(max = 40, message = "El precio referencial no puede superar los 40 caracteres")
    @Column(length = 40)
    private String precioReferencial;

    // Cómo se consulta: por ejemplo 'Escribir desde la app', 'Cotización'
    @Size(max = 150, message = "La modalidad de consulta no puede superar los 150 caracteres")
    @Column(length = 150)
    private String modalidadConsulta;

    // ¿Está disponible hoy?
    private boolean disponible;

    // true = se muestra en la app
    private boolean publicado;
}
