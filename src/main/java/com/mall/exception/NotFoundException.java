package com.mall.exception;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(ResultCode.NOT_FOUND.getCode(), message);
    }

    public NotFoundException(ResultCode resultCode) {
        super(resultCode);
    }
}
