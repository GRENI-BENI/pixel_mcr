package com.vady.photoservice.exception;




import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions of type {@link ResourceNotFoundException}.
     *
     * @param ex      the exception to be handled
     * @param request the current request
     * @return a response entity containing the error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions of type {@link Exception}, which are not handled by any
     * other exception handler.
     *
     * @param ex      the exception to be handled
     * @param request the current request
     * @return a response entity containing an error response with a status of
     * {@link HttpStatus#INTERNAL_SERVER_ERROR}
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(Exception ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    /**
     * Handles exceptions of type {@link ResponseStatusException}.
     *
     * @param ex      the exception to be handled
     * @param request the current request
     * @return a response entity containing an error response with the status
     * {@link HttpStatus} value of the exception
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.valueOf(ex.getStatusCode().value()),
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getStatusCode().value()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }





    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<ErrorResponseDto> handleMessagingException(MailSendException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
        /**
     * Handles exceptions of type {@link ResourceAlreadyExistsException}.
     *
     * @param ex      the exception to be handled
     * @param request the current request
     * @return a response entity containing an error response with a status of
     * {@link HttpStatus#CONFLICT}
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
