package com.tencent.bk.devops.atom.task.service;

import com.tencent.bk.devops.atom.pojo.AtomResult;
import com.tencent.bk.devops.atom.pojo.StringData;
import com.tencent.bk.devops.atom.task.SubPipelineMain;
import com.tencent.bk.devops.atom.task.constant.ErrorCode;
import com.tencent.bk.devops.atom.task.enums.BuildStatus;
import com.tencent.bk.devops.atom.task.enums.RunMode;
import com.tencent.bk.devops.atom.task.exception.TaskExecuteException;
import com.tencent.bk.devops.atom.task.pojo.AtomParam;
import com.tencent.bk.devops.atom.task.pojo.ProjectBuildId;
import com.tencent.bk.devops.atom.task.pojo.Status;
import com.tencent.bk.devops.atom.task.utils.ParseParamBuilder;
import com.tencent.bk.devops.plugin.pojo.ErrorType;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 访问backend后台接口，启动流水线和获取状态
 *
 * @version 1.0.0
 */
public class SubPipelineExecService {

    private static final Logger logger = LoggerFactory.getLogger(SubPipelineMain.class);
    private SubPipelineApiService subPipelineApiService = new SubPipelineApiService();

    private AtomParam param;
    private AtomResult atomResult;

    public SubPipelineExecService(AtomParam param, AtomResult atomResult) {
        this.param = param;
        this.atomResult = atomResult;
    }

    /**
     * 访问后台启动子流水线接口
     */
    public void startSubPipeline() {
        ParamParser paramParser = ParseParamBuilder.buildParseParam(param);
        String subProjectId = param.getProjectId();
        if (StringUtils.isBlank(subProjectId)) {
            subProjectId = param.getProjectName();
        }
        Map<String, Object> values = paramParser.parseParam();
        String subPipelineId = paramParser.getSubPipelineId();
        logger.groupStart("starting sub pipeline");
        ProjectBuildId projectBuildId = subPipelineApiService.startPipeline(param.getProjectName(),
                param.getPipelineId(), subProjectId, subPipelineId, param.getPipelineTaskId(), param.getRunMode(),
                values);
        String url = String.format("/console/pipeline/%s/%s/detail/%s", subProjectId, subPipelineId,
                projectBuildId.getId());
        logger.info("<a target='_blank' href='" + url + "'>查看子流水线执行详情</a>");
        logger.groupEnd("");
        atomResult.getData().put("sub_pipeline_buildId", new StringData(projectBuildId.getId()));
        atomResult.getData().put("sub_pipeline_url", new StringData("http://devops.oa.com" + url));

        if (RunMode.SYN.getValue().equals(param.getRunMode())) {
            logger.groupStart("sub pipeline status");
            final Status status = subPipelineApiService
                    .getSubPipelineStatus(subProjectId, subPipelineId, projectBuildId.getId(),
                            param.getPollingInterval());
            logger.info("<a target='_blank' href='" + url + "'>查看子流水线执行详情</a>");
            logger.groupEnd("");
            outputSubPipelineVar();
            if (status != null && !BuildStatus.isSuccess(status.getStatus())) {
                throw new TaskExecuteException(ErrorType.USER, ErrorCode.CONFIG_ERROR, "子流水线运行失败");
            }
        }
    }

    private void outputSubPipelineVar() {
        String outParameters = param.getOutputSubBuildVariable();
        String subPipelineFieldNamespace = param.getFieldNamespace();
        if (subPipelineFieldNamespace == null) {
            subPipelineFieldNamespace = "sub_pipeline_";
        } else {
            if (!subPipelineFieldNamespace.endsWith("_")) {
                subPipelineFieldNamespace += "_";
            }
        }

        // 输出子流水构建变量
        if (outParameters != null) {
            Map<String, String> buildVariableResult = subPipelineApiService.getSubPipelineBuildVariable(
                    param.getPipelineTaskId());
            String[] outPara = outParameters.replace("\n", "").split(",");
            for (String key : outPara) {
                if (buildVariableResult.get(key) != null) {
                    atomResult.getData().put(subPipelineFieldNamespace + key,
                            new StringData(buildVariableResult.get(key)));
                } else {
                    logger.warn("子流水线输出变量不存在：" + key);
                }
            }
        }
    }
}
