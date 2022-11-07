package com.tencent.bk.devops.atom.task;

import com.tencent.bk.devops.atom.AtomContext;
import com.tencent.bk.devops.atom.common.Status;
import com.tencent.bk.devops.atom.pojo.AtomResult;
import com.tencent.bk.devops.atom.spi.AtomService;
import com.tencent.bk.devops.atom.spi.TaskAtom;
import com.tencent.bk.devops.atom.task.constant.ErrorCode;
import com.tencent.bk.devops.atom.task.exception.ParamInvalidException;
import com.tencent.bk.devops.atom.task.exception.TaskExecuteException;
import com.tencent.bk.devops.atom.task.pojo.AtomParam;
import com.tencent.bk.devops.atom.task.service.SubPipelineExecService;
import com.tencent.bk.devops.plugin.pojo.ErrorType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 插件主类
 *
 * @version 1.0.0
 */
@AtomService(paramClass = AtomParam.class)
public class SubPipelineMain implements TaskAtom<AtomParam> {

    private static final Logger logger = LoggerFactory.getLogger(SubPipelineMain.class);

    /**
     * 执行主入口
     *
     * @param atomContext 插件上下文
     */
    @Override
    public void execute(AtomContext<AtomParam> atomContext) {
        AtomParam param = atomContext.getParam();
        AtomResult result = atomContext.getResult();
        try {
            // 检查必填参数
            checkParam(param);
            SubPipelineExecService subPipelineExecService = new SubPipelineExecService(param, result);
            subPipelineExecService.startSubPipeline();
        } catch (TaskExecuteException e) {
            result.setErrorType(e.getErrorType().getNum());
            result.setErrorCode(e.getErrorCode());
            result.setStatus(Status.failure);
            result.setMessage(e.getMessage());
        } catch (Throwable e) {
            logger.error("sub pipeline execute fail", e);
            result.setErrorType(ErrorType.PLUGIN.getNum());
            result.setErrorCode(ErrorCode.DEFAULT_ERROR);
            result.setStatus(Status.failure);
            result.setMessage(e.getMessage());
        }
    }

    /**
     * 检查参数
     *
     * @param param 请求参数
     */
    private void checkParam(AtomParam param) {
        StringBuilder buffer = new StringBuilder();
        // 参数检查
        String subPipelineType = param.getSubPipelineType();
        if (StringUtils.isBlank(subPipelineType)) {
            buffer.append("subPipelineType 不能为空 | ");
        }
        if ("ID".equals(subPipelineType)) {
            if (StringUtils.isBlank(param.getSubPip())) {
                buffer.append("子流水线不能为空 | ");
            }
        } else {
            if (StringUtils.isBlank(param.getSubPipelineName())) {
                buffer.append("子流水线名称不能为空 | ");
            }
        }

        if (StringUtils.isBlank(param.getRunMode())) {
            buffer.append("运行方式不能为空 | ");
        }

        if (StringUtils.isBlank(param.getPipelineBuildId())) {
            buffer.append("流水线 BuildId 不能为空 | ");
        }

        if (StringUtils.isBlank(param.getPipelineId())) {
            buffer.append("流水线不能为空 | ");
        }

        if (StringUtils.isBlank(param.getPipelineTaskId())) {
            buffer.append("流水线 TaskId 不能为空 | ");
        }

        String pollingInterval = param.getPollingInterval();
        if (pollingInterval != null) {
            boolean pollingIntervalValue = pollingInterval.matches("10|[1-9]");
            if (!pollingIntervalValue) {
                buffer.append("轮询间隔，仅支持1~10之间的整数 | ");
            }
        }

        if (buffer.length() > 0) {
            throw new ParamInvalidException(buffer.toString());
        }
    }
}
