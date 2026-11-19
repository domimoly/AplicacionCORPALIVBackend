package cl.corpaliv.vinculacion.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.corpaliv.vinculacion.dto.CambioEstadoRequest;
import cl.corpaliv.vinculacion.dto.HistorialResponse;
import cl.corpaliv.vinculacion.dto.SeguimientoResponse;
import cl.corpaliv.vinculacion.dto.SolicitudRequest;
import cl.corpaliv.vinculacion.dto.SolicitudResponse;
import cl.corpaliv.vinculacion.service.SolicitudService;
import jakarta.validation.Valid;

// CONTROLADOR: define los ENDPOINTS (las direcciones que la app puede llamar). No tiene lógica: recibe la
// petición, se la pasa al servicio y devuelve la respuesta con su código HTTP.
// EXPLICAR: @RestController = clase que responde en JSON. @RequestMapping = ruta base de todos sus endpoints.
// @RequestBody convierte el JSON que llega en un objeto Java; @Valid activa las validaciones del DTO.
@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // POST /solicitudes  ->  PÚBLICO: la persona envía su solicitud. Responde 201 con el código de seguimiento
    @PostMapping
    public ResponseEntity<SeguimientoResponse> crear(@Valid @RequestBody SolicitudRequest request) {
        SeguimientoResponse nueva = SeguimientoResponse.desde(solicitudService.crear(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // GET /solicitudes/seguimiento/{codigo}  ->  PÚBLICO: consulta el estado (sin datos personales)
    @GetMapping("/seguimiento/{codigo}")
    public ResponseEntity<SeguimientoResponse> seguimiento(@PathVariable String codigo) {
        return ResponseEntity.ok(SeguimientoResponse.desde(solicitudService.obtenerPorCodigo(codigo)));
    }

    // GET /solicitudes  ->  CON TOKEN: todas, o solo las de un estado con ?estado=RECIBIDA
    @GetMapping
    public ResponseEntity<List<SolicitudResponse>> listar(@RequestParam(required = false) String estado) {
        List<SolicitudResponse> lista = solicitudService.listar(estado).stream().map(SolicitudResponse::desde).toList();
        return ResponseEntity.ok(lista);
    }

    // GET /solicitudes/{id}  ->  CON TOKEN: una solicitud completa
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(SolicitudResponse.desde(solicitudService.obtenerPorId(id)));
    }

    // GET /solicitudes/{id}/historial  ->  CON TOKEN: los cambios de estado de la solicitud
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialResponse>> historial(@PathVariable Long id) {
        List<HistorialResponse> lista = solicitudService.obtenerHistorial(id).stream().map(HistorialResponse::desde).toList();
        return ResponseEntity.ok(lista);
    }

    // PATCH /solicitudes/{id}/estado  ->  CON TOKEN: cambia el estado. PATCH = modificar solo una parte del recurso.
    // 'Principal' es el usuario del token: su nombre (getName) es el correo del gestor, que queda en el historial
    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudResponse> cambiarEstado(@PathVariable Long id,
                                                           @Valid @RequestBody CambioEstadoRequest request,
                                                           Principal principal) {
        return ResponseEntity.ok(SolicitudResponse.desde(
                solicitudService.cambiarEstado(id, request, principal.getName())));
    }

    // DELETE /solicitudes/{id}  ->  CON TOKEN: elimina la solicitud y su historial. Responde 204 (No Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        solicitudService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
