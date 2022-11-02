package com.tencent.bk.devops.atom.task.exception;

import com.tencent.bk.devops.atom.exception.RemoteServiceException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ExceptionTranslator {

    public static RuntimeException translator(Throwable exception) {
        if (exception instanceof UnknownHostException) {
            return new ApiRetryException(exception.getMessage());
        } else if (exception instanceof ConnectException) {
            return new ApiRetryException(exception.getMessage());
        } else if (exception instanceof SocketTimeoutException) {
            if ("timeout".equals(exception.getMessage()) || "connect timed out".equals(exception.getMessage())) {
                return new ApiRetryException("SocketTimeoutException:" + exception.getMessage());
            }
            throw new ApiException(exception.getMessage());
        } else if (exception instanceof RemoteServiceException) {
            if (((RemoteServiceException) exception).getHttpStatus() >= 500) {
                return new ApiRetryException(exception.getMessage());
            }
            return new ApiException(exception.getMessage());
        } else {
            return new ApiException(exception.getMessage());
        }
    }
}
