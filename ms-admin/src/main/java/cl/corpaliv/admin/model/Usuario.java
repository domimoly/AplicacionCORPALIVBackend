package cl.corpaliv.admin.model;

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

// USUARIO del equipo de CORPALIV que puede iniciar sesión (gestor o administrador). Tabla 'usuario'.
// EXPLICAR: la clave NUNCA se guarda en texto: se guarda 'claveHash', el resultado de aplicarle BCrypt.
// Esta entidad no se devuelve nunca en las respuestas de la API (se usan los DTO), así el hash no sale del servidor.
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    // El correo es el nombre de usuario: no se puede repetir
    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Column(nullable = false, length = 100)
    private String claveHash;

    // Un usuario desactivado ya no puede iniciar sesión, pero se conserva en la base de datos
    private boolean activo;

    // Muchos usuarios pueden tener el mismo rol. EAGER = el rol se carga junto con el usuario
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id")
    private Rol rol;
}
