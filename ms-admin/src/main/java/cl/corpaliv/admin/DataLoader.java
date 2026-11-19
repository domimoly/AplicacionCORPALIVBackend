package cl.corpaliv.admin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import cl.corpaliv.admin.model.Rol;
import cl.corpaliv.admin.model.Usuario;
import cl.corpaliv.admin.repository.RolRepository;
import cl.corpaliv.admin.repository.UsuarioRepository;

// DATOS DE EJEMPLO: se cargan al iniciar, pero SOLO si no hay roles (así no se duplican al reiniciar).
// EXPLICAR: CommandLineRunner es una interfaz de Spring: su método run() se ejecuta una vez, justo después de arrancar.
// Los usuarios son FICTICIOS (correos @ejemplo.cl). Son solo para probar: en un sistema real habría que cambiar las claves.
//   gestor@ejemplo.cl / Gestor2026  (rol GESTOR, el mismo gestor de demostración de la app)
//   admin@ejemplo.cl  / Admin2026   (rol ADMIN)
@Component
public class DataLoader implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(RolRepository rolRepository, UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (rolRepository.count() > 0) {
            return;
        }

        Rol gestor = crearRol("GESTOR", "Gestiona solicitudes de colaboración y contenidos");
        Rol admin = crearRol("ADMIN", "Gestiona además los usuarios del sistema");

        crearUsuario("Gestor de vinculación (demo)", "gestor@ejemplo.cl", "Gestor2026", gestor);
        crearUsuario("Administrador (demo)", "admin@ejemplo.cl", "Admin2026", admin);
    }

    private Rol crearRol(String nombre, String descripcion) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);
        return rolRepository.save(rol);
    }

    private void crearUsuario(String nombre, String correo, String clave, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setClaveHash(passwordEncoder.encode(clave)); // se guarda el hash, nunca la clave
        usuario.setActivo(true);
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }
}
