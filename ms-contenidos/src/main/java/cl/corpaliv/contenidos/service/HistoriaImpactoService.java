package cl.corpaliv.contenidos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.corpaliv.contenidos.exception.BadRequestException;
import cl.corpaliv.contenidos.exception.ResourceNotFoundException;
import cl.corpaliv.contenidos.model.HistoriaImpacto;
import cl.corpaliv.contenidos.repository.HistoriaImpactoRepository;

@Service
public class HistoriaImpactoService {

    private final HistoriaImpactoRepository historiaRepository;

    public HistoriaImpactoService(HistoriaImpactoRepository historiaRepository) {
        this.historiaRepository = historiaRepository;
    }

    public List<HistoriaImpacto> listarPublicadas() {
        return historiaRepository.findByEstadoPublicacion("PUBLICADO");
    }

    public List<HistoriaImpacto> listarTodas() {
        return historiaRepository.findAll();
    }

    public HistoriaImpacto obtenerPublicada(Long id) {
        HistoriaImpacto historia = obtenerPorId(id);
        if (!"PUBLICADO".equals(historia.getEstadoPublicacion())) {
            throw new ResourceNotFoundException("Historia no existe");
        }
        return historia;
    }

    public HistoriaImpacto obtenerPorId(Long id) {
        return historiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historia no existe"));
    }

    public HistoriaImpacto guardar(HistoriaImpacto historia) {
        validarPublicacion(historia);
        historia.setId(null);
        // Si se crea ya publicada, se anota la fecha de publicación
        historia.setFechaPublicacion("PUBLICADO".equals(historia.getEstadoPublicacion()) ? LocalDateTime.now() : null);
        return historiaRepository.save(historia);
    }

    public HistoriaImpacto actualizar(Long id, HistoriaImpacto datos) {
        HistoriaImpacto existente = obtenerPorId(id);
        validarPublicacion(datos);
        existente.setTitulo(datos.getTitulo());
        existente.setResumen(datos.getResumen());
        existente.setContenido(datos.getContenido());
        existente.setImagenUrl(datos.getImagenUrl());
        existente.setAutorizada(datos.isAutorizada());
        existente.setAnonimizada(datos.isAnonimizada());
        existente.setEstadoPublicacion(datos.getEstadoPublicacion());
        // La fecha de publicación se pone UNA sola vez: la primera vez que la historia pasa a PUBLICADO
        if ("PUBLICADO".equals(datos.getEstadoPublicacion()) && existente.getFechaPublicacion() == null) {
            existente.setFechaPublicacion(LocalDateTime.now());
        }
        return historiaRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!historiaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Historia no existe");
        }
        historiaRepository.deleteById(id);
    }

    // REGLA DE NEGOCIO: una historia solo puede quedar PUBLICADA si está autorizada Y anonimizada.
    // Si no, se lanza BadRequestException y la API responde 400 con este mensaje.
    private void validarPublicacion(HistoriaImpacto historia) {
        if ("PUBLICADO".equals(historia.getEstadoPublicacion())
                && !(historia.isAutorizada() && historia.isAnonimizada())) {
            throw new BadRequestException("Una historia solo se puede publicar si está autorizada y anonimizada");
        }
    }
}
