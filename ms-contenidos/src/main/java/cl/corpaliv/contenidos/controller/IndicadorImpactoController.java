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

import cl.corpaliv.contenidos.model.IndicadorImpacto;
import cl.corpaliv.contenidos.service.IndicadorImpactoService;
import jakarta.validation.Valid;

// CONTROLADOR: define los ENDPOINTS (las direcciones que la app puede llamar). No tiene lógica: recibe la
// petición, se la pasa al servicio y devuelve la respuesta con su código HTTP.
// EXPLICAR: @RestController = clase que responde en JSON. @RequestMapping = ruta base de todos sus endpoints.
// @RequestBody convierte el JSON que llega en un objeto Java; @Valid activa las validaciones de la entidad.
@RestController
@RequestMapping("/indicadores")
public class IndicadorImpactoController {

    private final IndicadorImpactoService indicadorService;

    public IndicadorImpactoController(IndicadorImpactoService indicadorService) {
        this.indicadorService = indicadorService;
    }

    // GET /indicadores  ->  PÚBLICO: solo los indicadores publicados (los que ve la app)
    @GetMapping
    public ResponseEntity<List<IndicadorImpacto>> listarPublicados() {
        return ResponseEntity.ok(indicadorService.listarPublicados());
    }

    // GET /indicadores/admin  ->  CON TOKEN: todos, incluidos los no publicados (para el gestor)
    @GetMapping("/admin")
    public ResponseEntity<List<IndicadorImpacto>> listarTodos() {
        return ResponseEntity.ok(indicadorService.listarTodos());
    }

    // GET /indicadores/{id}  ->  PÚBLICO: un indicador publicado
    @GetMapping("/{id}")
    public ResponseEntity<IndicadorImpacto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(indicadorService.obtenerPublicado(id));
    }

    // POST /indicadores  ->  CON TOKEN: crea. Responde 201 (Created) con el objeto creado
    @PostMapping
    public ResponseEntity<IndicadorImpacto> crear(@Valid @RequestBody IndicadorImpacto indicadorImpacto) {
        IndicadorImpacto nuevo = indicadorService.guardar(indicadorImpacto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // PUT /indicadores/{id}  ->  CON TOKEN: modifica
    @PutMapping("/{id}")
    public ResponseEntity<IndicadorImpacto> actualizar(@PathVariable Long id, @Valid @RequestBody IndicadorImpacto indicadorImpacto) {
        return ResponseEntity.ok(indicadorService.actualizar(id, indicadorImpacto));
    }

    // DELETE /indicadores/{id}  ->  CON TOKEN: elimina. Responde 204 (No Content), sin cuerpo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        indicadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
