package cl.corpaliv.contenidos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.corpaliv.contenidos.exception.ResourceNotFoundException;
import cl.corpaliv.contenidos.model.IndicadorImpacto;
import cl.corpaliv.contenidos.repository.IndicadorImpactoRepository;

@Service
public class IndicadorImpactoService {

    private final IndicadorImpactoRepository indicadorRepository;

    public IndicadorImpactoService(IndicadorImpactoRepository indicadorRepository) {
        this.indicadorRepository = indicadorRepository;
    }

    public List<IndicadorImpacto> listarPublicados() {
        return indicadorRepository.findByPublicadoTrue();
    }

    public List<IndicadorImpacto> listarTodos() {
        return indicadorRepository.findAll();
    }

    public IndicadorImpacto obtenerPublicado(Long id) {
        IndicadorImpacto indicador = obtenerPorId(id);
        if (!indicador.isPublicado()) {
            throw new ResourceNotFoundException("Indicador no existe");
        }
        return indicador;
    }

    public IndicadorImpacto obtenerPorId(Long id) {
        return indicadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Indicador no existe"));
    }

    public IndicadorImpacto guardar(IndicadorImpacto indicador) {
        indicador.setId(null);
        indicador.setFechaActualizacion(LocalDateTime.now());
        return indicadorRepository.save(indicador);
    }

    public IndicadorImpacto actualizar(Long id, IndicadorImpacto datos) {
        IndicadorImpacto existente = obtenerPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setValor(datos.getValor());
        existente.setUnidad(datos.getUnidad());
        existente.setPeriodo(datos.getPeriodo());
        existente.setFuente(datos.getFuente());
        existente.setPublicado(datos.isPublicado());
        existente.setFechaActualizacion(LocalDateTime.now());
        return indicadorRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!indicadorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Indicador no existe");
        }
        indicadorRepository.deleteById(id);
    }
}
