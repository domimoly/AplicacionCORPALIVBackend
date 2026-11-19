package cl.corpaliv.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.corpaliv.admin.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // SELECT * FROM usuario WHERE correo = ?
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
