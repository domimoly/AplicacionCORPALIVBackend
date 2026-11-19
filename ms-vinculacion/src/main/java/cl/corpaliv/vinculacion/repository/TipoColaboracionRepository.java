package cl.corpaliv.vinculacion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.corpaliv.vinculacion.model.TipoColaboracion;

// EXPLICAR: JpaRepository ya trae guardar, buscar, listar y eliminar. Spring arma la consulta a partir del NOMBRE del método
@Repository
public interface TipoColaboracionRepository extends JpaRepository<TipoColaboracion, Long> {
    Optional<TipoColaboracion> findByCodigo(String codigo);
}
