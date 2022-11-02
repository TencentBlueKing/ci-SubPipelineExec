package com.tencent.bk.devops.atom.task.service;

import com.tencent.bk.devops.atom.task.exception.ParamInvalidException;
import com.tencent.bk.devops.atom.utils.json.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameParamParser implements ParamParser {

    private static final Logger logger = LoggerFactory.getLogger(NameParamParser.class);
    // 验证是否是流水线id
    private final Pattern pattern = Pattern.compile("(p-)?[a-f\\d]{32}");

    private String subProjectId;
    private String pipelineName;
    private String providedParam;
    private SubPipelineApiService subPipelineApiService = new SubPipelineApiService();

    public NameParamParser(String subProjectId, String pipelineName, String providedParam) {
        this.subProjectId = subProjectId;
        this.pipelineName = pipelineName;
        this.providedParam = providedParam;
    }

    @Override
    public Map<String, Object> parseParam() {
        logger.groupStart("parse sub pipeline param");
        Map<String, Object> values = new HashMap<>();
        if (providedParam != null && !providedParam.isEmpty()) {
            String[] paramList = providedParam.split("\n");
            for (String str : paramList) {
                if (!str.contains("=")) {
                    continue;
                }
                String[] item = str.split("=", 2);
                String key = item[0].trim();
                String val = item[1].trim();
                values.put(key, val);
            }
        }
        logger.info("name param parser result:{}", JsonUtil.toJson(values));
        logger.groupEnd("");
        return values;
    }

    @Override
    public String getSubPipelineId() {
        String subPipelineId = subPipelineApiService.getPipelineIdByName(subProjectId, pipelineName);
        // 判断传入的是否是流水线ID
        if (StringUtils.isBlank(subPipelineId) && pattern.matcher(pipelineName).matches()) {
            subPipelineId = pipelineName;
        }
        if (StringUtils.isBlank(subPipelineId)) {
            throw new ParamInvalidException(String.format("【%s】流水线名不存在", pipelineName));
        }
        return subPipelineId;
    }
}
