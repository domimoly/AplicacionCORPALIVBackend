package cl.corpaliv.contenidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// CATEGORÍA: agrupa programas y productos (por ejemplo 'Bienestar', 'Servicios').
// EXPLICAR: @Entity convierte esta clase en una TABLA de la base de datos (tabla 'categoria') y cada atributo en una columna.
// Lombok: @Data crea los getters y setters; @NoArgsConstructor y @AllArgsConstructor crean los constructores.
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    // @Id = clave primaria. IDENTITY = la base de datos le asigna el número automáticamente (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 300, message = "La descripción no puede superar los 300 caracteres")
    @Column(length = 300)
    private String descripcion;

    // A qué se aplica la categoría: PROGRAMA, PRODUCTO o SERVICIO
    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 30, message = "El tipo no puede superar los 30 caracteres")
    @Column(nullable = false, length = 30)
    private String tipo;
}
