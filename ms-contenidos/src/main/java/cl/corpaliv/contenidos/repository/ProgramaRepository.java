package cl.corpaliv.contenidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.corpaliv.contenidos.model.Programa;

// REPOSITORIO: la capa que habla con la base de datos. Al extender JpaRepository, Spring entrega GRATIS los métodos
// findAll, findById, save, deleteById, existsById, etc. No hace falta escribir SQL.
@Repository
public interface ProgramaRepository extends JpaRepository<Programa, Long> {

    // EXPLICAR: Spring crea la consulta SOLO a partir del nombre del método (no se escribe SQL).
    List<Programa> findByEstadoPublicacion(String estadoPublicacion);
}
