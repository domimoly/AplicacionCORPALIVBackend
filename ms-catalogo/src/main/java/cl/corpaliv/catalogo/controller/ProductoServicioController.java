package cl.corpaliv.catalogo.controller;

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

import cl.corpaliv.catalogo.model.ProductoServicio;
import cl.corpaliv.catalogo.service.ProductoServicioService;
import jakarta.validation.Valid;

// CONTROLADOR: define los ENDPOINTS (las direcciones que la app puede llamar). No tiene lógica: recibe la
// petición, se la pasa al servicio y devuelve la respuesta con su código HTTP.
// EXPLICAR: @RestController = clase que responde en JSON. @RequestMapping = ruta base de todos sus endpoints.
// @RequestBody convierte el JSON que llega en un objeto Java; @Valid activa las validaciones de la entidad.
@RestController
@RequestMapping("/productos")
public class ProductoServicioController {

    private final ProductoServicioService productoService;

    public ProductoServicioController(ProductoServicioService productoService) {
        this.productoService = productoService;
    }

    // GET /productos  ->  PÚBLICO: solo los productos publicados (los que ve la app)
    @GetMapping
    public ResponseEntity<List<ProductoServicio>> listarPublicados() {
        return ResponseEntity.ok(productoService.listarPublicados());
    }

    // GET /productos/admin  ->  CON TOKEN: todos, incluidos los no publicados (para el gestor)
    @GetMapping("/admin")
    public ResponseEntity<List<ProductoServicio>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    // GET /productos/{id}  ->  PÚBLICO: un producto publicado
    @GetMapping("/{id}")
    public ResponseEntity<ProductoServicio> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPublicado(id));
    }

    // GET /productos/{id}/exists  ->  true o false. Lo usa ms-vinculacion para validar referencias
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.existePorId(id));
    }

    // POST /productos  ->  CON TOKEN: crea. Responde 201 (Created) con el objeto creado
    @PostMapping
    public ResponseEntity<ProductoServicio> crear(@Valid @RequestBody ProductoServicio productoServicio) {
        ProductoServicio nuevo = productoService.guardar(productoServicio);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // PUT /productos/{id}  ->  CON TOKEN: modifica
    @PutMapping("/{id}")
    public ResponseEntity<ProductoServicio> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoServicio productoServicio) {
        return ResponseEntity.ok(productoService.actualizar(id, productoServicio));
    }

    // DELETE /productos/{id}  ->  CON TOKEN: elimina. Responde 204 (No Content), sin cuerpo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
