package cl.corpaliv.vinculacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.corpaliv.vinculacion.model.HistorialSolicitud;

@Repository
public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitud, Long> {

    // Los cambios de una solicitud, del más antiguo al más nuevo
    List<HistorialSolicitud> findBySolicitudIdOrderByFechaAsc(Long solicitudId);

    // Se usa al eliminar una solicitud (necesita estar dentro de una transacción)
    void deleteBySolicitudId(Long solicitudId);
}
