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

import cl.corpaliv.contenidos.model.HistoriaImpacto;
import cl.corpaliv.contenidos.service.HistoriaImpactoService;
import jakarta.validation.Valid;

// CONTROLADOR: define los ENDPOINTS (las direcciones que la app puede llamar). No tiene lógica: recibe la
// petición, se la pasa al servicio y devuelve la respuesta con su código HTTP.
// EXPLICAR: @RestController = clase que responde en JSON. @RequestMapping = ruta base de todos sus endpoints.
// @RequestBody convierte el JSON que llega en un objeto Java; @Valid activa las validaciones de la entidad.
@RestController
@RequestMapping("/historias")
public class HistoriaImpactoController {

    private final HistoriaImpactoService historiaService;

    public HistoriaImpactoController(HistoriaImpactoService historiaService) {
        this.historiaService = historiaService;
    }

    // GET /historias  ->  PÚBLICO: solo los historias publicados (los que ve la app)
    @GetMapping
    public ResponseEntity<List<HistoriaImpacto>> listarPublicadas() {
        return ResponseEntity.ok(historiaService.listarPublicadas());
    }

    // GET /historias/admin  ->  CON TOKEN: todos, incluidos los no publicados (para el gestor)
    @GetMapping("/admin")
    public ResponseEntity<List<HistoriaImpacto>> listarTodas() {
        return ResponseEntity.ok(historiaService.listarTodas());
    }

    // GET /historias/{id}  ->  PÚBLICO: un historia publicado
    @GetMapping("/{id}")
    public ResponseEntity<HistoriaImpacto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(historiaService.obtenerPublicada(id));
    }

    // POST /historias  ->  CON TOKEN: crea. Responde 201 (Created) con el objeto creado
    @PostMapping
    public ResponseEntity<HistoriaImpacto> crear(@Valid @RequestBody HistoriaImpacto historiaImpacto) {
        HistoriaImpacto nuevo = historiaService.guardar(historiaImpacto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // PUT /historias/{id}  ->  CON TOKEN: modifica
    @PutMapping("/{id}")
    public ResponseEntity<HistoriaImpacto> actualizar(@PathVariable Long id, @Valid @RequestBody HistoriaImpacto historiaImpacto) {
        return ResponseEntity.ok(historiaService.actualizar(id, historiaImpacto));
    }

    // DELETE /historias/{id}  ->  CON TOKEN: elimina. Responde 204 (No Content), sin cuerpo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        historiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
