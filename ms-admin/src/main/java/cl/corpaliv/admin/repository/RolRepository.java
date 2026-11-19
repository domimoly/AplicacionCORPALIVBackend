package cl.corpaliv.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.corpaliv.admin.model.Rol;

// EXPLICAR: JpaRepository ya trae guardar, buscar, listar y eliminar. Spring arma la consulta a partir del NOMBRE del método
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}
