package com.mall.exception;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(ResultCode.UNAUTHORIZED.getCode(), message);
    }

    public UnauthorizedException(ResultCode resultCode) {
        super(resultCode);
    }
}
