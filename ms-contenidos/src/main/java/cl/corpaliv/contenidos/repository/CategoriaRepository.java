package cl.corpaliv.contenidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.corpaliv.contenidos.model.Categoria;

// REPOSITORIO: la capa que habla con la base de datos. Al extender JpaRepository, Spring entrega GRATIS los métodos
// findAll, findById, save, deleteById, existsById, etc. No hace falta escribir SQL.
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
