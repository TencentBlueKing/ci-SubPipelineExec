package com.tencent.bk.devops.atom.task.exception;

public class ApiRetryException extends ApiException {

    public ApiRetryException(String message) {
        super(message);
    }
}
