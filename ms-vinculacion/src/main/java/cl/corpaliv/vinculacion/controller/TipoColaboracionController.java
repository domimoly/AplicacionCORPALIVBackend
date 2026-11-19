package cl.corpaliv.vinculacion.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.corpaliv.vinculacion.model.TipoColaboracion;
import cl.corpaliv.vinculacion.service.SolicitudService;

@RestController
@RequestMapping("/tipos-colaboracion")
public class TipoColaboracionController {

    private final SolicitudService solicitudService;

    public TipoColaboracionController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // GET /tipos-colaboracion  ->  PÚBLICO: los tipos que se pueden elegir en el formulario de la app
    @GetMapping
    public ResponseEntity<List<TipoColaboracion>> listar() {
        return ResponseEntity.ok(solicitudService.listarTipos());
    }
}
