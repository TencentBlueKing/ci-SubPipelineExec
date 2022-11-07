package com.tencent.bk.devops.atom.task.exception;

import com.tencent.bk.devops.atom.task.constant.ErrorCode;
import com.tencent.bk.devops.plugin.pojo.ErrorType;

public class ParamInvalidException extends TaskExecuteException {

    public ParamInvalidException(String errorMsg) {
        super(ErrorType.USER, ErrorCode.CONFIG_ERROR, errorMsg);
    }
}
