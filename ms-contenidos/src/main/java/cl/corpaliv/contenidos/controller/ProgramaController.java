package cl.corpaliv.contenidos.controller;

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

import cl.corpaliv.contenidos.model.Programa;
import cl.corpaliv.contenidos.service.ProgramaService;
import jakarta.validation.Valid;

// CONTROLADOR: define los ENDPOINTS (las direcciones que la app puede llamar). No tiene lógica: recibe la
// petición, se la pasa al servicio y devuelve la respuesta con su código HTTP.
// EXPLICAR: @RestController = clase que responde en JSON. @RequestMapping = ruta base de todos sus endpoints.
// @RequestBody convierte el JSON que llega en un objeto Java; @Valid activa las validaciones de la entidad.
@RestController
@RequestMapping("/programas")
public class ProgramaController {

    private final ProgramaService programaService;

    public ProgramaController(ProgramaService programaService) {
        this.programaService = programaService;
    }

    // GET /programas  ->  PÚBLICO: solo los programas publicados (los que ve la app)
    @GetMapping
    public ResponseEntity<List<Programa>> listarPublicados() {
        return ResponseEntity.ok(programaService.listarPublicados());
    }

    // GET /programas/admin  ->  CON TOKEN: todos, incluidos los no publicados (para el gestor)
    @GetMapping("/admin")
    public ResponseEntity<List<Programa>> listarTodos() {
        return ResponseEntity.ok(programaService.listarTodos());
    }

    // GET /programas/{id}  ->  PÚBLICO: un programa publicado
    @GetMapping("/{id}")
    public ResponseEntity<Programa> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(programaService.obtenerPublicado(id));
    }

    // GET /programas/{id}/exists  ->  true o false. Lo usa ms-vinculacion para validar referencias
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return ResponseEntity.ok(programaService.existePorId(id));
    }

    // POST /programas  ->  CON TOKEN: crea. Responde 201 (Created) con el objeto creado
    @PostMapping
    public ResponseEntity<Programa> crear(@Valid @RequestBody Programa programa) {
        Programa nuevo = programaService.guardar(programa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // PUT /programas/{id}  ->  CON TOKEN: modifica
    @PutMapping("/{id}")
    public ResponseEntity<Programa> actualizar(@PathVariable Long id, @Valid @RequestBody Programa programa) {
        return ResponseEntity.ok(programaService.actualizar(id, programa));
    }

    // DELETE /programas/{id}  ->  CON TOKEN: elimina. Responde 204 (No Content), sin cuerpo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        programaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
