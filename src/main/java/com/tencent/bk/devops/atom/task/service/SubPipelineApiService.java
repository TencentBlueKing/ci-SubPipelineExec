package com.tencent.bk.devops.atom.task.service;

import com.tencent.bk.devops.atom.task.api.SubPipelineApi;
import com.tencent.bk.devops.atom.task.pojo.ProjectBuildId;
import com.tencent.bk.devops.atom.task.pojo.Status;
import com.tencent.bk.devops.atom.task.pojo.SubPipelineStartUpInfo;
import com.tencent.bk.devops.atom.task.utils.RetryUtils;
import java.util.List;
import java.util.Map;

public class SubPipelineApiService {

    private SubPipelineApi subPipelineApi = new SubPipelineApi();

    public List<SubPipelineStartUpInfo> getSubPipelineStartUpInfo(String userId, String projectId, String subPip) {
        return RetryUtils.retry(() -> subPipelineApi.getSubPipelineStartUpInfo(userId, projectId, subPip));
    }

    public String getPipelineIdByName(String projectId, String pipelineName) {
        return RetryUtils.retry(() -> subPipelineApi.getPipelineIdByName(projectId, pipelineName));
    }

    public Map<String, String> getSubPipelineBuildVariable(String taskId) {
        return RetryUtils.retry(() -> subPipelineApi.getSubPipelineBuildVariable(taskId));
    }

    public ProjectBuildId startPipeline(String parentProjectId, String parentPipelineId, String subProjectId,
            String subPipelineId, String taskId, String runMode, Map<String, Object> values) {
        return subPipelineApi
                .startPipeline(parentProjectId, parentPipelineId, subProjectId, subPipelineId, taskId, runMode, values);
    }

    public Status getSubPipelineStatus(String projectId, String pipelineId, String buildId, String pollingInterval) {
        return RetryUtils.statusRetry(() ->
                        subPipelineApi.getSubPipelineStatus(projectId, pipelineId, buildId), pollingInterval);
    }
}
