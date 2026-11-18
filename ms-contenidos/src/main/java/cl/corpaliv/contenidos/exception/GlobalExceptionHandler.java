package cl.corpaliv.contenidos.exception;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

// MANEJO DE ERRORES: captura las excepciones de toda la API y las convierte en una respuesta JSON ordenada
// (ApiErrorResponse) con el código HTTP correcto: 404, 400 o 500.
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    // Se activa cuando el JSON enviado no pasa las validaciones (@NotBlank, @Size, etc.).
    // Junta los mensajes de todos los campos con error en un solo texto
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return construir(HttpStatus.BAD_REQUEST, "Bad Request", mensaje, request);
    }

    // Se activa cuando el cuerpo de la petición no es un JSON válido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleJsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST, "Bad Request", "El cuerpo de la petición no es un JSON válido", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        logger.error("Exception no controlada: {}", ex.getMessage(), ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Ha ocurrido un error interno", request);
    }

    // Arma el ApiErrorResponse. Está en un solo lugar para no repetir el mismo código en cada método
    private ResponseEntity<ApiErrorResponse> construir(HttpStatus estado, String error, String mensaje, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(estado.value())
                .error(error)
                .message(mensaje)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(estado).body(body);
    }
}
