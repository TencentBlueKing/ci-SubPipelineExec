package com.tencent.bk.devops.atom.task.service;

import java.util.Map;

public interface ParamParser {

    /**
     * 解析参数
     */
    Map<String, Object> parseParam();

    /**
     * 得到流水线ID
     */
    String getSubPipelineId();
}
