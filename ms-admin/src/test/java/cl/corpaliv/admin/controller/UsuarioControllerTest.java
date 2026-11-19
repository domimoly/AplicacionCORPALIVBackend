package cl.corpaliv.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import cl.corpaliv.admin.dto.UsuarioRequest;
import cl.corpaliv.admin.exception.GlobalExceptionHandler;
import cl.corpaliv.admin.model.Rol;
import cl.corpaliv.admin.model.Usuario;
import cl.corpaliv.admin.service.UsuarioService;

// Comprueba que la API de usuarios responde con los códigos correctos y que NUNCA devuelve la clave ni su hash.
@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UsuarioController(usuarioService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Usuario crearUsuario() {
        Rol rol = new Rol(1L, "GESTOR", "Gestor");
        return new Usuario(1L, "Gestor demo", "gestor@ejemplo.cl", "hash-secreto", true, rol);
    }

    @Test
    void listar_responde200SinMostrarElHash() throws Exception {
        when(usuarioService.listar()).thenReturn(List.of(crearUsuario()));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo").value("gestor@ejemplo.cl"))
                .andExpect(jsonPath("$[0].rol").value("GESTOR"))
                .andExpect(jsonPath("$[0].claveHash").doesNotExist())
                .andExpect(jsonPath("$[0].clave").doesNotExist());
    }

    @Test
    void crear_conDatosValidos_responde201() throws Exception {
        when(usuarioService.crear(any(UsuarioRequest.class))).thenReturn(crearUsuario());
        String json = "{\"nombre\":\"Gestor demo\",\"correo\":\"gestor@ejemplo.cl\",\"clave\":\"Clave12345\",\"rolId\":1}";

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_conCorreoInvalido_responde400YNoLlamaAlServicio() throws Exception {
        String json = "{\"nombre\":\"Gestor demo\",\"correo\":\"esto-no-es-un-correo\",\"clave\":\"Clave12345\",\"rolId\":1}";

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).crear(any());
    }

    @Test
    void eliminar_responde204() throws Exception {
        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService).eliminar(1L);
    }
}
