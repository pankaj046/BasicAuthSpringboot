package sharma.pankaj.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import sharma.pankaj.auth.dto.RegisterResponse;
import sharma.pankaj.auth.exception.CustomExceptionHandler;

@ControllerAdvice
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomExceptionHandler.class)
    public ResponseEntity<RegisterResponse> handleNullPointerExceptions(Exception e) {
        return new ResponseEntity<RegisterResponse>(new RegisterResponse(true, e.getMessage()), HttpStatus.OK);
    }
}
