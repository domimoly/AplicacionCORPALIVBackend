package cl.corpaliv.contenidos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.corpaliv.contenidos.exception.ResourceNotFoundException;
import cl.corpaliv.contenidos.model.Programa;
import cl.corpaliv.contenidos.repository.ProgramaRepository;

// SERVICIO: aquí va la lógica del negocio (las reglas). El controlador recibe la petición y se la pasa al servicio;
// el servicio decide qué hacer y usa el repositorio para leer o guardar en la base de datos.
@Service
public class ProgramaService {

    private final ProgramaRepository programaRepository;

    // Spring entrega el repositorio automáticamente (inyección de dependencias por constructor)
    public ProgramaService(ProgramaRepository programaRepository) {
        this.programaRepository = programaRepository;
    }

    // Lista pública: solo los programas con estado PUBLICADO
    public List<Programa> listarPublicados() {
        return programaRepository.findByEstadoPublicacion("PUBLICADO");
    }

    // Lista para gestores: todos, incluidos los borradores
    public List<Programa> listarTodos() {
        return programaRepository.findAll();
    }

    // Búsqueda pública por id: si el programa es un borrador se responde como si no existiera (404)
    public Programa obtenerPublicado(Long id) {
        Programa programa = obtenerPorId(id);
        if (!"PUBLICADO".equals(programa.getEstadoPublicacion())) {
            throw new ResourceNotFoundException("Programa no existe");
        }
        return programa;
    }

    public Programa obtenerPorId(Long id) {
        return programaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programa no existe"));
    }

    // Lo usa ms-vinculacion para comprobar que un programa existe antes de aceptar una solicitud
    public boolean existePorId(Long id) {
        return programaRepository.existsById(id);
    }

    public Programa guardar(Programa programa) {
        LocalDateTime ahora = LocalDateTime.now();
        // setId(null) asegura que se CREE uno nuevo aunque el cliente haya enviado un id
        programa.setId(null);
        programa.setFechaCreacion(ahora);
        programa.setFechaActualizacion(ahora);
        return programaRepository.save(programa);
    }

    public Programa actualizar(Long id, Programa datos) {
        Programa existente = obtenerPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setResumen(datos.getResumen());
        existente.setDescripcion(datos.getDescripcion());
        existente.setCategoria(datos.getCategoria());
        existente.setImagenUrl(datos.getImagenUrl());
        existente.setEstadoPublicacion(datos.getEstadoPublicacion());
        existente.setFechaActualizacion(LocalDateTime.now());
        return programaRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!programaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Programa no existe");
        }
        programaRepository.deleteById(id);
    }
}
