package com.tencent.bk.devops.atom.task.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.bk.devops.atom.task.exception.ParamInvalidException;
import com.tencent.bk.devops.atom.task.pojo.IdParam;
import com.tencent.bk.devops.atom.task.pojo.SubPipelineStartUpInfo;
import com.tencent.bk.devops.atom.utils.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IdParamParser implements ParamParser {

    private String userId;
    private String subProjectId;
    private String subPipelineId;
    private String providedParam;
    private SubPipelineApiService subPipelineApiService = new SubPipelineApiService();

    public IdParamParser(String userId, String subProjectId, String subPipelineId, String providedParam) {
        this.userId = userId;
        this.subProjectId = subProjectId;
        this.subPipelineId = subPipelineId;
        this.providedParam = providedParam;
    }

    private static final Logger logger = LoggerFactory.getLogger(IdParamParser.class);

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map<String, Object> parseParam() {
        logger.groupStart("parse sub pipeline param");
        if (providedParam.isEmpty()) {
            return Collections.emptyMap();
        }
        List<IdParam> idParams = JsonUtil.fromJson(providedParam, new TypeReference<List<IdParam>>() {
        });
        if (idParams == null) {
            throw new ParamInvalidException("子流水线动态参数解析失败: NULL");
        }
        List<SubPipelineStartUpInfo> startUpInfoList = subPipelineApiService.getSubPipelineStartUpInfo(userId,
                subProjectId, subPipelineId);
        Map<String, SubPipelineStartUpInfo> startUpInfoMap = startUpInfoList.stream()
                .collect(Collectors.toMap(SubPipelineStartUpInfo::getKey, Function.identity(), (e1, e2) -> e2));

        HashMap<String, Object> values = new HashMap<>(startUpInfoList.size());
        idParams.forEach(idParam -> {
            String key = idParam.getKey();
            Object value = idParam.getValue();
            if (key != null && value != null && idParam.isEnable()) {
                Object paramValue = idParam.getValue();
                SubPipelineStartUpInfo startUpInfo = startUpInfoMap.get(idParam.getKey());
                if (startUpInfo != null) {
                    // 如果是下拉框并且是多选的,前端传过来的是列表对象，转换成字符串
                    String valueType = startUpInfo.getValueType();
                    Boolean valueMultiple = startUpInfo.getValueMultiple();
                    if ("select".equals(valueType) && valueMultiple && paramValue instanceof List) {
                        paramValue = String.join(",", (List) paramValue);
                    }
                    values.put(idParam.getKey(), paramValue);
                }
            }
        });
        logger.info("id param parser result:{}", JsonUtil.toJson(values));
        logger.groupEnd("");
        return values;
    }

    @Override
    public String getSubPipelineId() {
        return subPipelineId;
    }
}
