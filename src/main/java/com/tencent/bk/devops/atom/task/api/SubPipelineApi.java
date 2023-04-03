package com.tencent.bk.devops.atom.task.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.bk.devops.atom.api.BaseApi;
import com.tencent.bk.devops.atom.exception.RemoteServiceException;
import com.tencent.bk.devops.atom.task.exception.ApiException;
import com.tencent.bk.devops.atom.task.exception.ExceptionTranslator;
import com.tencent.bk.devops.atom.task.exception.ParamInvalidException;
import com.tencent.bk.devops.atom.task.pojo.PipelineId;
import com.tencent.bk.devops.atom.task.pojo.ProjectBuildId;
import com.tencent.bk.devops.atom.task.pojo.Status;
import com.tencent.bk.devops.atom.task.pojo.SubPipelineStartUpInfo;
import com.tencent.bk.devops.atom.utils.json.JsonUtil;
import com.tencent.bk.devops.plugin.pojo.Result;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SubPipelineApi extends BaseApi {

    private static final Logger logger = LoggerFactory.getLogger(SubPipelineApi.class);
    private OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
            .connectTimeout(5L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(30L, TimeUnit.SECONDS)
            .build();

    /**
     * 获取子流水线全局变量定义
     */
    public List<SubPipelineStartUpInfo> getSubPipelineStartUpInfo(String userId, String projectId, String subPip) {
        String url = String.format("process/api/build/subpipeline/projects/%s/pipelines/%s/manualStartupInfo?userId=%s",
                projectId, subPip, userId);
        try {
            Request request = buildGet(url);
            String responseContent = request(request, "get sub-pipeline startUpInfo error");
            Result<List<SubPipelineStartUpInfo>> result = JsonUtil.fromJson(responseContent,
                    new TypeReference<Result<List<SubPipelineStartUpInfo>>>() {
                    });
            if (result.isNotOk() || result.getData() == null) {
                logger.warn("result:{}", result);
                return new ArrayList<>();
            }
            return result.getData();
        } catch (Throwable e) {
            throw ExceptionTranslator.translator(e);
        }
    }

    /**
     * 根据子流水线名称获取对应的id
     */
    public String getPipelineIdByName(String projectId, String pipelineName) {
        if (pipelineName.isEmpty()) {
            throw new ParamInvalidException("pipelineName is empty");
        }
        try {
            String url = String.format(
                    "process/api/build/subpipeline/projects/%s/pipelines/getPipelineIdByName?pipelineName=%s",
                    projectId, encode(pipelineName));
            Request request = buildGet(url);
            logger.info("url:{}", url);
            String responseContent = request(request, "getPipelineIdByName error");
            Result<List<PipelineId>> result = JsonUtil.fromJson(responseContent,
                    new TypeReference<Result<List<PipelineId>>>() {
                    });
            if (result.isNotOk() || result.getData() == null || result.getData().isEmpty()) {
                logger.warn("result:{}", result);
                return null;
            }
            return result.getData().get(0).getId();
        } catch (Throwable e) {
            throw ExceptionTranslator.translator(e);
        }
    }

    /**
     * 获取子流水线输出参数
     */
    public Map<String, String> getSubPipelineBuildVariable(String taskId) {
        String url = String.format("process/api/build/builds/taskIds/%s/subVar", taskId);
        Request request = buildGet(url);
        try {
            String responseContent = request(request, "getPipelineBuildVariable error");
            Result<Map<String, String>> result = JsonUtil.fromJson(responseContent,
                    new TypeReference<Result<Map<String, String>>>() {
                    });
            if (result.isNotOk() || result.getData() == null) {
                logger.warn("result:{}", result);
                return Collections.emptyMap();
            }
            return result.getData();
        } catch (Throwable e) {
            throw ExceptionTranslator.translator(e);
        }
    }

    /**
     * 启动子流水线
     */
    public ProjectBuildId startPipeline(String parentProjectId, String parentPipelineId, String subProjectId,
            String subPipelineId, String taskId, String runMode, Map<String, Object> values) {
        logger.info(
                "start pipeline, parentProjectId:{}, parentPipelineId:{}, subProjectId:{}, subPipelineId:{}, "
                        + "taskId:{}, runMode:{}",
                parentProjectId, parentPipelineId, subProjectId, subPipelineId, taskId, runMode);
        String url = String.format(
                "process/api/build/subpipeline/projects/%s/pipelines/%s/atoms/SubPipelineExec/startByPipeline?"
                        + "taskId=%s&runMode=%s",
                subProjectId, subPipelineId, taskId, runMode);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-DEVOPS-PROJECT-ID", parentProjectId);
        headers.put("X-DEVOPS-PIPELINE-ID", parentPipelineId);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), JsonUtil.toJson(values));

        Request request = buildPost(url, body, headers);
        try {
            String responseContent = request(request, "start pipeline error");
            Result<ProjectBuildId> result = JsonUtil.fromJson(responseContent,
                    new TypeReference<Result<ProjectBuildId>>() {
                    });
            if (result.isNotOk() || result.getData() == null) {
                logger.warn("result:{}", result);
                logger.info("<a target='_blank' href='/console/pipeline/" + subProjectId + "/" + subPipelineId
                        + "/history'>View sub-pipeline</a>");
                throw new ApiException("start pipeline error");
            }
            return result.getData();
        } catch (Throwable e) {
            logger.info("<a target='_blank' href='/console/pipeline/" + subProjectId + "/" + subPipelineId
                    + "/history'>View sub-pipeline</a>");
            throw ExceptionTranslator.translator(e);
        }
    }

    public Status getSubPipelineStatus(String projectId, String pipelineId, String buildId) {
        String url = String.format("process/api/build/subpipeline/subPipeline/%s/%s/%s/detail", projectId, pipelineId,
                buildId);
        Request request = buildGet(url);
        try {
            String responseContent = request(request, "get sub-pipeline status error");
            Result<Status> result = JsonUtil.fromJson(responseContent, new TypeReference<Result<Status>>() {
            });
            if (result.isNotOk() || result.getData() == null) {
                logger.warn("result:{}", result);
                return null;
            }
            return result.getData();
        } catch (Throwable e) {
            throw ExceptionTranslator.translator(e);
        }
    }

    protected String request(Request request, String errorMessage) throws IOException {
        OkHttpClient httpClient = okHttpClient.newBuilder().build();
        try (Response response = httpClient.newCall(request).execute()) {
            String responseContent = response.body() != null ? response.body().string() : null;
            if (!response.isSuccessful()) {
                logger.error("Fail to request(" + request + ") with code " + response.code()
                        + " , message " + response.message() + " and response" + responseContent);
                logger.info("excep>>>>>>>>>>>>" + response);
                throw new RemoteServiceException(errorMessage, response.code(), responseContent);
            }
            return responseContent;
        }
    }
}
