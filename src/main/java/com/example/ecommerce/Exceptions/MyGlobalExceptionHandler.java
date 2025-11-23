package com.example.ecommerce.Exceptions;

import com.example.ecommerce.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)  //Modifying the response we want to give for a pre built excep
    public ResponseEntity<Map<String, String>>ArgNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResourceNotFound.class)  //Our own excep
    public ResponseEntity<ApiResponse> ResNotFound(ResourceNotFound e) {
        String Message = e.getMessage(); //It's in parent class RunTimeExcep that's why we used super
        ApiResponse apires = new ApiResponse(Message, false);
        return new ResponseEntity<>(apires,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIexception.class)   //Our own excep If you try to create an already existing category
    public ResponseEntity<ApiResponse> myapiexcep(APIexception e) {
        String Message = e.getMessage(); //It's in parent class RunTimeExcep that's why we used super
        ApiResponse apires = new ApiResponse(Message, false);
        return new ResponseEntity<>(apires,HttpStatus.BAD_REQUEST);
    }
}
