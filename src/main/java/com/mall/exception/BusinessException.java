package com.mall.exception;

public class BusinessException extends BaseException {
    public BusinessException(String message) {
        super(ResultCode.PARAM_ERROR.getCode(), message);
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode);
    }
}
