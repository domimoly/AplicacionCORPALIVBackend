package cl.corpaliv.contenidos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.corpaliv.contenidos.exception.ResourceNotFoundException;
import cl.corpaliv.contenidos.model.Categoria;
import cl.corpaliv.contenidos.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    public Categoria obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no existe"));
    }

    public Categoria guardar(Categoria categoria) {
        categoria.setId(null);
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria datos) {
        Categoria existente = obtenerPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setTipo(datos.getTipo());
        return categoriaRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no existe");
        }
        categoriaRepository.deleteById(id);
    }
}
