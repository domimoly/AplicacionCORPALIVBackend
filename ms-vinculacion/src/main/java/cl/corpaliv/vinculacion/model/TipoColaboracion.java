package cl.corpaliv.vinculacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TIPO DE COLABORACIÓN (catálogo): DONACION, ALIANZA_RSE, VOLUNTARIADO, COMPRA, CAPACITACION, OTRO. Tabla 'tipo_colaboracion'.
// EXPLICAR: @Entity convierte esta clase en una TABLA de la base de datos y cada atributo en una columna.
// Lombok: @Data crea los getters y setters; @NoArgsConstructor y @AllArgsConstructor crean los constructores.
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoColaboracion {

    // @Id = clave primaria. IDENTITY = la base de datos le asigna el número automáticamente (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código que se usa en la API y en la app (por ejemplo 'DONACION'). unique = no se repite
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    // Nombre para mostrar a las personas (por ejemplo 'Donación')
    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(length = 200)
    private String descripcion;
}
