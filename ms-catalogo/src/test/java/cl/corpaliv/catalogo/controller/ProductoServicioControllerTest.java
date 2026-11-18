package cl.corpaliv.catalogo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cl.corpaliv.catalogo.exception.BadRequestException;
import cl.corpaliv.catalogo.exception.GlobalExceptionHandler;
import cl.corpaliv.catalogo.exception.ResourceNotFoundException;
import cl.corpaliv.catalogo.model.ProductoServicio;
import cl.corpaliv.catalogo.service.ProductoServicioService;

// PRUEBAS DEL CONTROLADOR con MockMvc: simulan peticiones HTTP (GET, POST...) sin levantar el servidor.
// El servicio es un mock. Se incluye GlobalExceptionHandler para comprobar los códigos de error (404, 400).
@ExtendWith(MockitoExtension.class)
class ProductoServicioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoServicioService productoService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductoServicioController(productoService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ProductoServicio crearProducto() {
        ProductoServicio producto = new ProductoServicio();
        producto.setId(1L);
        producto.setCategoriaId(1L);
        producto.setNombre("Bolsa Alba Lab");
        producto.setPublicado(true);
        return producto;
    }

    @Test
    void listarPublicados_responde200ConLaLista() throws Exception {
        when(productoService.listarPublicados()).thenReturn(List.of(crearProducto()));

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Bolsa Alba Lab"));
    }

    @Test
    void obtener_inexistente_responde404ConMensaje() throws Exception {
        when(productoService.obtenerPublicado(99L)).thenThrow(new ResourceNotFoundException("Producto no existe"));

        mockMvc.perform(get("/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Producto no existe"));
    }

    @Test
    void existe_responde200ConTrue() throws Exception {
        when(productoService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/productos/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void crear_conDatosValidos_responde201() throws Exception {
        when(productoService.guardar(any(ProductoServicio.class))).thenReturn(crearProducto());
        String json = "{\"categoriaId\":1,\"nombre\":\"Bolsa Alba Lab\",\"publicado\":true}";

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_sinNombre_responde400YNoLlamaAlServicio() throws Exception {
        String json = "{\"categoriaId\":1,\"nombre\":\"\",\"publicado\":true}";

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(productoService, never()).guardar(any());
    }

    @Test
    void crear_conCategoriaInexistente_responde400ConMensaje() throws Exception {
        when(productoService.guardar(any(ProductoServicio.class)))
                .thenThrow(new BadRequestException("La categoría indicada no existe"));
        String json = "{\"categoriaId\":99,\"nombre\":\"Bolsa\",\"publicado\":true}";

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La categoría indicada no existe"));
    }

    @Test
    void eliminar_responde204() throws Exception {
        mockMvc.perform(delete("/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(1L);
    }
}
