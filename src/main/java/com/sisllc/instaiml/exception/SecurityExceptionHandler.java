/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.exception;

import java.nio.file.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
@RestControllerAdvice in Spring Boot is a specialized annotation used for global exception handling and other cross-cutting concerns within 
    RESTful web services. It combines the functionality of two other Spring annotations:
@ControllerAdvice:
    This annotation allows you to define methods that apply globally across multiple controllers. These methods can include:
        @ExceptionHandler: For handling specific exceptions thrown by any controller method and providing a custom response.
        @ModelAttribute: For adding common model attributes to all controller methods.
        @InitBinder: For customizing data binding for all controller methods.
@ResponseBody:
    This annotation indicates that the return value of a method should be bound directly to the web response body, typically serialized into
        JSON or XML format, rather than being used for view resolution.
*/
@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(value = { AccessDeniedException.class })
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponseException(HttpStatus.FORBIDDEN, new ErrorResponseException(HttpStatus.FORBIDDEN));
        //ErrorResponse errorResponse = new ErrorResponse("Access Denied", "You don't have permission to access this resource",  HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = { AuthenticationException.class })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        //ErrorResponse errorResponse = new ErrorResponse("Authentication Failed", ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        ErrorResponse errorResponse = new ErrorResponseException(HttpStatus.UNAUTHORIZED);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}