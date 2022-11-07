package com.tencent.bk.devops.atom.task.exception;

import com.tencent.bk.devops.atom.task.constant.ErrorCode;
import com.tencent.bk.devops.plugin.pojo.ErrorType;

public class ApiException extends TaskExecuteException {

    public ApiException(String errorMsg) {
        super(ErrorType.THIRD_PARTY, ErrorCode.DEPEND_ERROR, errorMsg);
    }
}
