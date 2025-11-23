package com.example.ecommerce.Exceptions;

public class ResourceNotFound extends RuntimeException{
    String resourcename;//Stores resource name which is not found
    String field;
    String fieldName;
    Long fieldId;

    public ResourceNotFound() {
    }

    public ResourceNotFound(String resourcename, String field, String fieldName) {
        super(String.format("%s not found with %s: %s", resourcename, field,fieldName));
        this.resourcename = resourcename;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceNotFound(String resourcename, String field ,Long fieldId) {
        super(String.format("%s not found with %s: %d", resourcename, field,fieldId));  //Category Not found with categoryId:- id
        // (provokes getMessage() in parent class
        this.resourcename = resourcename;
        this.fieldId = fieldId;
        this.field = field;
    }
}
