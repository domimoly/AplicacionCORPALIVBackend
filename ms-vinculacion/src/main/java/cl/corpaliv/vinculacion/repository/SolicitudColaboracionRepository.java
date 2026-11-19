package cl.corpaliv.vinculacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.corpaliv.vinculacion.model.SolicitudColaboracion;

@Repository
public interface SolicitudColaboracionRepository extends JpaRepository<SolicitudColaboracion, Long> {

    Optional<SolicitudColaboracion> findByCodigoSeguimiento(String codigoSeguimiento);

    boolean existsByCodigoSeguimiento(String codigoSeguimiento);

    // Todas, las más nuevas primero
    List<SolicitudColaboracion> findAllByOrderByFechaCreacionDesc();

    // Solo las de un estado, las más nuevas primero
    List<SolicitudColaboracion> findByEstadoOrderByFechaCreacionDesc(String estado);
}
