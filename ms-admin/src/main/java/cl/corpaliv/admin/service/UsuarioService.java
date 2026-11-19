package cl.corpaliv.admin.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.corpaliv.admin.dto.UsuarioRequest;
import cl.corpaliv.admin.exception.BadRequestException;
import cl.corpaliv.admin.exception.ResourceNotFoundException;
import cl.corpaliv.admin.model.Rol;
import cl.corpaliv.admin.model.Usuario;
import cl.corpaliv.admin.repository.RolRepository;
import cl.corpaliv.admin.repository.UsuarioRepository;

// Administración de usuarios (solo la puede usar un ADMIN). Aquí están las reglas de negocio:
// el correo no se repite, el rol debe existir y la clave siempre se guarda con hash BCrypt.
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no existe"));
    }

    public Usuario crear(UsuarioRequest request) {
        String correo = normalizar(request.getCorreo());
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new BadRequestException("Ya existe un usuario con ese correo");
        }
        if (request.getClave() == null || request.getClave().isBlank()) {
            throw new BadRequestException("La clave es obligatoria");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(correo);
        usuario.setClaveHash(passwordEncoder.encode(request.getClave()));
        usuario.setActivo(request.isActivo());
        usuario.setRol(buscarRol(request.getRolId()));
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, UsuarioRequest request) {
        Usuario existente = obtenerPorId(id);
        String correo = normalizar(request.getCorreo());

        // El correo puede repetirse solo si es el del mismo usuario que se está modificando
        boolean correoCambio = !correo.equals(existente.getCorreo());
        if (correoCambio && usuarioRepository.existsByCorreo(correo)) {
            throw new BadRequestException("Ya existe un usuario con ese correo");
        }

        existente.setNombre(request.getNombre());
        existente.setCorreo(correo);
        existente.setActivo(request.isActivo());
        existente.setRol(buscarRol(request.getRolId()));
        // La clave solo se cambia si viene en la petición
        if (request.getClave() != null && !request.getClave().isBlank()) {
            existente.setClaveHash(passwordEncoder.encode(request.getClave()));
        }
        return usuarioRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no existe");
        }
        usuarioRepository.deleteById(id);
    }

    private Rol buscarRol(Long rolId) {
        return rolRepository.findById(rolId)
                .orElseThrow(() -> new BadRequestException("El rol indicado no existe"));
    }

    // Los correos se guardan sin espacios y en minúsculas para que 'Gestor@X.cl' y 'gestor@x.cl' sean el mismo
    private String normalizar(String correo) {
        return correo.trim().toLowerCase();
    }
}
