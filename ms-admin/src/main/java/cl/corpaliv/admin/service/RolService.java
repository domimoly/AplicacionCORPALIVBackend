package cl.corpaliv.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.corpaliv.admin.model.Rol;
import cl.corpaliv.admin.repository.RolRepository;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> listar() {
        return rolRepository.findAll();
    }
}
