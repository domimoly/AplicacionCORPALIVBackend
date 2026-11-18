package cl.corpaliv.contenidos.controller;

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

import cl.corpaliv.contenidos.exception.GlobalExceptionHandler;
import cl.corpaliv.contenidos.exception.ResourceNotFoundException;
import cl.corpaliv.contenidos.model.Programa;
import cl.corpaliv.contenidos.service.ProgramaService;

// PRUEBAS DEL CONTROLADOR con MockMvc: simulan peticiones HTTP (GET, POST...) sin levantar el servidor.
// El servicio es un mock. Se incluye GlobalExceptionHandler para comprobar los códigos de error (404, 400).
@ExtendWith(MockitoExtension.class)
class ProgramaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProgramaService programaService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProgramaController(programaService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Programa crearPrograma() {
        Programa programa = new Programa();
        programa.setId(1L);
        programa.setNombre("Taller Sociolaboral");
        programa.setResumen("Formación para jóvenes y adultos");
        programa.setEstadoPublicacion("PUBLICADO");
        return programa;
    }

    @Test
    void listarPublicados_responde200ConLaLista() throws Exception {
        when(programaService.listarPublicados()).thenReturn(List.of(crearPrograma()));

        mockMvc.perform(get("/programas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Taller Sociolaboral"));
    }

    @Test
    void obtener_inexistente_responde404ConMensaje() throws Exception {
        when(programaService.obtenerPublicado(99L)).thenThrow(new ResourceNotFoundException("Programa no existe"));

        mockMvc.perform(get("/programas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Programa no existe"));
    }

    @Test
    void existe_responde200ConTrue() throws Exception {
        when(programaService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/programas/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void crear_conDatosValidos_responde201() throws Exception {
        when(programaService.guardar(any(Programa.class))).thenReturn(crearPrograma());
        String json = "{\"nombre\":\"Taller Sociolaboral\",\"resumen\":\"Formación\",\"estadoPublicacion\":\"PUBLICADO\"}";

        mockMvc.perform(post("/programas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_sinNombre_responde400YNoLlamaAlServicio() throws Exception {
        String json = "{\"nombre\":\"\",\"resumen\":\"Formación\",\"estadoPublicacion\":\"PUBLICADO\"}";

        mockMvc.perform(post("/programas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(programaService, never()).guardar(any());
    }

    @Test
    void eliminar_responde204() throws Exception {
        mockMvc.perform(delete("/programas/1"))
                .andExpect(status().isNoContent());

        verify(programaService).eliminar(1L);
    }
}
