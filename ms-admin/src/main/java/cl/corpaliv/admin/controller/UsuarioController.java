package cl.corpaliv.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.corpaliv.admin.dto.UsuarioRequest;
import cl.corpaliv.admin.dto.UsuarioResponse;
import cl.corpaliv.admin.service.UsuarioService;
import jakarta.validation.Valid;

// Todos los endpoints de /usuarios exigen un token con rol ADMIN (se configura en SecurityConfig).
// Siempre se devuelve UsuarioResponse (sin clave) y no la entidad Usuario.
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET /usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> lista = usuarioService.listar().stream().map(UsuarioResponse::desde).toList();
        return ResponseEntity.ok(lista);
    }

    // GET /usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponse.desde(usuarioService.obtenerPorId(id)));
    }

    // POST /usuarios  ->  201 Created
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse nuevo = UsuarioResponse.desde(usuarioService.crear(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // PUT /usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(UsuarioResponse.desde(usuarioService.actualizar(id, request)));
    }

    // DELETE /usuarios/{id}  ->  204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
