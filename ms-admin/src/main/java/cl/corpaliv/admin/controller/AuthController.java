package cl.corpaliv.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.corpaliv.admin.dto.LoginRequest;
import cl.corpaliv.admin.dto.LoginResponse;
import cl.corpaliv.admin.service.AuthService;
import jakarta.validation.Valid;

// CONTROLADOR: define los ENDPOINTS (las direcciones que la app puede llamar). No tiene lógica: recibe la
// petición, se la pasa al servicio y devuelve la respuesta con su código HTTP.
// EXPLICAR: @RestController = clase que responde en JSON. @RequestMapping = ruta base de todos sus endpoints.
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /auth/login  ->  PÚBLICO. Recibe {correo, clave}. Responde 200 con el token o 401 si son incorrectos
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
