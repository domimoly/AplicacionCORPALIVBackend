package cl.corpaliv.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cl.corpaliv.admin.dto.LoginRequest;
import cl.corpaliv.admin.dto.LoginResponse;
import cl.corpaliv.admin.exception.GlobalExceptionHandler;
import cl.corpaliv.admin.exception.UnauthorizedException;
import cl.corpaliv.admin.service.AuthService;

// PRUEBAS DEL CONTROLADOR con MockMvc: simulan peticiones HTTP sin levantar el servidor.
// El servicio es un mock. Se incluye GlobalExceptionHandler para comprobar los códigos de error (401, 400).
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_conDatosCorrectos_responde200ConToken() throws Exception {
        LoginResponse respuesta = new LoginResponse("token-de-prueba", "Bearer", 28800L,
                "Gestor demo", "gestor@ejemplo.cl", "GESTOR");
        when(authService.login(any(LoginRequest.class))).thenReturn(respuesta);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"gestor@ejemplo.cl\",\"clave\":\"Gestor2026\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-de-prueba"))
                .andExpect(jsonPath("$.rol").value("GESTOR"));
    }

    @Test
    void login_conClaveIncorrecta_responde401ConMensaje() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new UnauthorizedException("Correo o clave incorrectos"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"gestor@ejemplo.cl\",\"clave\":\"mala\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Correo o clave incorrectos"));
    }

    @Test
    void login_sinCorreo_responde400YNoLlamaAlServicio() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"\",\"clave\":\"Gestor2026\"}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }
}
