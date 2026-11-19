package cl.corpaliv.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.corpaliv.admin.dto.UsuarioRequest;
import cl.corpaliv.admin.exception.BadRequestException;
import cl.corpaliv.admin.exception.ResourceNotFoundException;
import cl.corpaliv.admin.model.Rol;
import cl.corpaliv.admin.model.Usuario;
import cl.corpaliv.admin.repository.RolRepository;
import cl.corpaliv.admin.repository.UsuarioRepository;

// Pruebas de las reglas de negocio de usuarios: correo único, rol existente y clave siempre con hash.
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, rolRepository, passwordEncoder);
    }

    private UsuarioRequest crearRequest(String correo, String clave) {
        return new UsuarioRequest("Gestor nuevo", correo, clave, 1L, true);
    }

    @Test
    void crear_conCorreoRepetido_lanzaBadRequestYNoGuarda() {
        when(usuarioRepository.existsByCorreo("nuevo@ejemplo.cl")).thenReturn(true);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> usuarioService.crear(crearRequest("nuevo@ejemplo.cl", "Clave12345")));

        assertEquals("Ya existe un usuario con ese correo", error.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void crear_sinClave_lanzaBadRequest() {
        when(usuarioRepository.existsByCorreo("nuevo@ejemplo.cl")).thenReturn(false);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> usuarioService.crear(crearRequest("nuevo@ejemplo.cl", null)));

        assertEquals("La clave es obligatoria", error.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void crear_conRolInexistente_lanzaBadRequest() {
        when(usuarioRepository.existsByCorreo("nuevo@ejemplo.cl")).thenReturn(false);
        when(rolRepository.findById(1L)).thenReturn(Optional.empty());

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> usuarioService.crear(crearRequest("nuevo@ejemplo.cl", "Clave12345")));

        assertEquals("El rol indicado no existe", error.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void crear_conDatosValidos_guardaLaClaveConHashYCorreoEnMinusculas() {
        Rol rol = new Rol(1L, "GESTOR", "Gestor");
        when(usuarioRepository.existsByCorreo("nuevo@ejemplo.cl")).thenReturn(false);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Usuario guardado = usuarioService.crear(crearRequest("Nuevo@Ejemplo.cl", "Clave12345"));

        assertEquals("nuevo@ejemplo.cl", guardado.getCorreo());
        assertNotEquals("Clave12345", guardado.getClaveHash());
        assertTrue(passwordEncoder.matches("Clave12345", guardado.getClaveHash()));
        assertEquals("GESTOR", guardado.getRol().getNombre());
    }

    @Test
    void actualizar_sinClaveNueva_mantieneElHashActual() {
        Rol rol = new Rol(1L, "GESTOR", "Gestor");
        String hashActual = passwordEncoder.encode("ClaveVieja1");
        Usuario existente = new Usuario(5L, "Nombre viejo", "viejo@ejemplo.cl", hashActual, true, rol);
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        // Mismo correo que ya tenía y sin clave: no se revisa duplicado ni se cambia el hash
        Usuario actualizado = usuarioService.actualizar(5L, crearRequest("viejo@ejemplo.cl", null));

        assertEquals("Gestor nuevo", actualizado.getNombre());
        assertEquals(hashActual, actualizado.getClaveHash());
        verify(usuarioRepository, never()).existsByCorreo(any());
    }

    @Test
    void actualizar_conCorreoDeOtroUsuario_lanzaBadRequest() {
        Rol rol = new Rol(1L, "GESTOR", "Gestor");
        Usuario existente = new Usuario(5L, "Nombre", "viejo@ejemplo.cl", "hash", true, rol);
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByCorreo("otro@ejemplo.cl")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> usuarioService.actualizar(5L, crearRequest("otro@ejemplo.cl", null)));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_inexistente_lanzaNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.obtenerPorId(99L));
    }

    @Test
    void eliminar_inexistente_lanzaNotFoundYNoElimina() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.eliminar(99L));

        verify(usuarioRepository, never()).deleteById(any());
    }
}
