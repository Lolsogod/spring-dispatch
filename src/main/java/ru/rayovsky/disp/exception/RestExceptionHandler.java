package ru.rayovsky.disp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Object> handleAuthorizationException(AuthorizationException e, WebRequest request){
        return new ResponseEntity<Object>(new ApiError(e.getMessage(), 401,LocalDateTime.now()), HttpStatus.UNAUTHORIZED);
    }
    //delete?
    @ExceptionHandler({ AuthenticationException.class })
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
    @ExceptionHandler({NumberFormatException.class})
    public ResponseEntity<Object> handleNumberFormatException(NumberFormatException e) {
        return new ResponseEntity<Object>(new ApiError("Incorrect number format " + e.getMessage(), 400,LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler({NothingFoundException.class})
    public ResponseEntity<Object> handleNothingFoundException(NothingFoundException e) {
        return new ResponseEntity<Object>(new ApiError(e.getMessage(),404, LocalDateTime.now()), HttpStatus.NOT_FOUND);
    }
}
