package br.com.contae.api.advice;

import jakarta.validation.ConstraintViolationException;
import br.com.contae.domain.exception.ConflitoDeNegocioException;
import br.com.contae.domain.exception.CredenciaisInvalidasException;
import br.com.contae.domain.exception.RecursoNaoEncontradoException;
import br.com.contae.domain.exception.SaldoInsuficienteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Requisição inválida");
        return response(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation() {
        return response(HttpStatus.BAD_REQUEST, "Requisição inválida");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableMessage() {
        return response(HttpStatus.BAD_REQUEST, "Requisição inválida");
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleSaldoInsuficiente(SaldoInsuficienteException exception) {
        return response(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(ConflitoDeNegocioException.class)
    public ResponseEntity<Map<String, Object>> handleConflitoDeNegocio(ConflitoDeNegocioException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleViolacaoDeIntegridade() {
        return response(HttpStatus.CONFLICT, "Conflito com dados existentes");
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNaoEncontrado(RecursoNaoEncontradoException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({BadCredentialsException.class, DisabledException.class, CredenciaisInvalidasException.class})
    public ResponseEntity<Map<String, Object>> handleCredenciaisInvalidas() {
        return response(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConversionException.class})
    public ResponseEntity<Map<String, Object>> handleConversaoInvalida() {
        return response(HttpStatus.BAD_REQUEST, "Requisição inválida");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(Exception exception) {
        log.error("Erro inesperado ao processar requisição", exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message == null ? status.getReasonPhrase() : message
        ));
    }
}
