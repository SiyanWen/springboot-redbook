package com.chuwa.redbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {
    private String resourceName;
    private String fieldName;
    private String fieldValue;

    public DuplicateResourceException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s already exists with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }


    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }


    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}
