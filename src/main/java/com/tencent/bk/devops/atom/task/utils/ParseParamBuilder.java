package com.tencent.bk.devops.atom.task.utils;

import com.tencent.bk.devops.atom.task.enums.PipelineType;
import com.tencent.bk.devops.atom.task.exception.ParamInvalidException;
import com.tencent.bk.devops.atom.task.pojo.AtomParam;
import com.tencent.bk.devops.atom.task.service.IdParamParser;
import com.tencent.bk.devops.atom.task.service.NameParamParser;
import com.tencent.bk.devops.atom.task.service.ParamParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将子流水线参数Params解析为Map对象
 */
public class ParseParamBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ParseParamBuilder.class);

    /**
     * 构建参数解析
     */
    public static ParamParser buildParseParam(AtomParam param) {
        ParamParser paramParser;
        String subPipelineType = param.getSubPipelineType();
        String subProjectId = param.getProjectId();
        if (StringUtils.isBlank(subProjectId)) {
            subProjectId = param.getProjectName();
        }
        if (PipelineType.ID.name().equals(subPipelineType)) {
            paramParser = new IdParamParser(param.getPipelineUpdateUserName(), subProjectId, param.getSubPip(),
                    param.getParams());
        } else if (PipelineType.NAME.name().equals(subPipelineType)) {
            paramParser = new NameParamParser(subProjectId, param.getSubPipelineName(), param.getParamsStr());
        } else {
            logger.error("unknown subPipelineType：" + subPipelineType);
            throw new ParamInvalidException("unknown subPipelineType：" + subPipelineType);
        }
        return paramParser;
    }
}
