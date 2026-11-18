package cl.corpaliv.contenidos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.corpaliv.contenidos.exception.BadRequestException;
import cl.corpaliv.contenidos.exception.ResourceNotFoundException;
import cl.corpaliv.contenidos.model.ProductoServicio;
import cl.corpaliv.contenidos.repository.CategoriaRepository;
import cl.corpaliv.contenidos.repository.ProductoServicioRepository;

@Service
public class ProductoServicioService {

    private final ProductoServicioRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    // Este servicio usa DOS repositorios: el de productos y el de categorías (para revisar que la categoría exista)
    public ProductoServicioService(ProductoServicioRepository productoRepository,
                                   CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<ProductoServicio> listarPublicados() {
        return productoRepository.findByPublicadoTrue();
    }

    public List<ProductoServicio> listarTodos() {
        return productoRepository.findAll();
    }

    public ProductoServicio obtenerPublicado(Long id) {
        ProductoServicio producto = obtenerPorId(id);
        if (!producto.isPublicado()) {
            throw new ResourceNotFoundException("Producto no existe");
        }
        return producto;
    }

    public ProductoServicio obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no existe"));
    }

    // Lo usa ms-vinculacion para comprobar que un producto existe antes de aceptar una solicitud
    public boolean existePorId(Long id) {
        return productoRepository.existsById(id);
    }

    public ProductoServicio guardar(ProductoServicio producto) {
        validarCategoria(producto);
        producto.setId(null);
        return productoRepository.save(producto);
    }

    public ProductoServicio actualizar(Long id, ProductoServicio datos) {
        ProductoServicio existente = obtenerPorId(id);
        validarCategoria(datos);
        existente.setCategoriaId(datos.getCategoriaId());
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setImagenUrl(datos.getImagenUrl());
        existente.setPrecioReferencial(datos.getPrecioReferencial());
        existente.setModalidadConsulta(datos.getModalidadConsulta());
        existente.setDisponible(datos.isDisponible());
        existente.setPublicado(datos.isPublicado());
        return productoRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no existe");
        }
        productoRepository.deleteById(id);
    }

    // REGLA DE NEGOCIO: el producto debe pertenecer a una categoría que exista
    private void validarCategoria(ProductoServicio producto) {
        if (!categoriaRepository.existsById(producto.getCategoriaId())) {
            throw new BadRequestException("La categoría indicada no existe");
        }
    }
}
