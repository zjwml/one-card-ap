package com.maplestory.onecard.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
