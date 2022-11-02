package com.tencent.bk.devops.atom.task.exception;

import com.tencent.bk.devops.plugin.pojo.ErrorType;

public class TaskExecuteException extends RuntimeException {

    private  ErrorType errorType;
    private  Integer errorCode;
    private  String errorMsg;

    public TaskExecuteException(ErrorType errorType, Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public TaskExecuteException(String errorMsg) {
        super(errorMsg);
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
