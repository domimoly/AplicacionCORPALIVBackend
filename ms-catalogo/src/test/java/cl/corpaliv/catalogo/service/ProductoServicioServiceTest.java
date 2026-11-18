package cl.corpaliv.catalogo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.corpaliv.catalogo.exception.BadRequestException;
import cl.corpaliv.catalogo.model.ProductoServicio;
import cl.corpaliv.catalogo.repository.CategoriaRepository;
import cl.corpaliv.catalogo.repository.ProductoServicioRepository;

// Prueba que un producto solo se guarda si su categoría existe (este servicio usa DOS repositorios falsos)
@ExtendWith(MockitoExtension.class)
class ProductoServicioServiceTest {

    @Mock
    private ProductoServicioRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    private ProductoServicioService productoService;

    @BeforeEach
    void setUp() {
        productoService = new ProductoServicioService(productoRepository, categoriaRepository);
    }

    private ProductoServicio crearProducto(Long categoriaId) {
        ProductoServicio producto = new ProductoServicio();
        producto.setCategoriaId(categoriaId);
        producto.setNombre("Bolsa Alba Lab");
        producto.setPublicado(true);
        return producto;
    }

    @Test
    void guardar_conCategoriaInexistente_lanzaBadRequestYNoGuarda() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> productoService.guardar(crearProducto(99L)));

        assertEquals("La categoría indicada no existe", error.getMessage());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void guardar_conCategoriaExistente_guardaElProducto() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.save(any(ProductoServicio.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        ProductoServicio guardado = productoService.guardar(crearProducto(1L));

        assertEquals("Bolsa Alba Lab", guardado.getNombre());
        verify(productoRepository).save(any(ProductoServicio.class));
    }
}
