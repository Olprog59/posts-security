package com.formation.blog_security.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException exception) {
        log.info("Handling MethodArgumentNotValidException");

        Map<String, String> errors = collectErrors(exception);
        log.debug("Validation errors: {}", errors);

        String jsonErrors = prepareResponse(errors);
        if (jsonErrors.equals("Internal server error")) {
            return new ResponseEntity<>(jsonErrors, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("Responding with errors: {}", jsonErrors);
        return new ResponseEntity<>(jsonErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class, ServletException.class})
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("Handling Exception");

        Map<String, String> errors = Map.of(exception.getClass().getSimpleName(), exception.getMessage());

        String jsonErrors = prepareResponse(errors);
        if (jsonErrors.equals("Internal server error")) {
            return new ResponseEntity<>(jsonErrors, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("Responding with errors: {}", jsonErrors);
        return new ResponseEntity<>(jsonErrors, HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> collectErrors(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        DefaultMessageSourceResolvable::getDefaultMessage));
    }

    private String prepareResponse(Map<String, String> errors) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errors", errors);

        try {
            return mapper.writeValueAsString(responseBody);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert errors to JSON", e);
            return "Internal server error";
        }
    }
}
