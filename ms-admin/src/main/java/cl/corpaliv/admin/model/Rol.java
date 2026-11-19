package cl.corpaliv.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ROL de un usuario: GESTOR (gestiona solicitudes y contenidos) o ADMIN (además administra usuarios).
// EXPLICAR: @Entity convierte esta clase en una TABLA de la base de datos (tabla 'rol') y cada atributo en una columna.
// Lombok: @Data crea los getters y setters; @NoArgsConstructor y @AllArgsConstructor crean los constructores.
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    // @Id = clave primaria. IDENTITY = la base de datos le asigna el número automáticamente (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true: no puede haber dos roles con el mismo nombre
    @Column(nullable = false, unique = true, length = 30)
    private String nombre;

    @Column(length = 200)
    private String descripcion;
}
