package com.snaptle.global.exception;

import lombok.Getter;

@Getter
public class SnaptleException extends RuntimeException {

    private final ErrorCode errorCode;

    public SnaptleException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
