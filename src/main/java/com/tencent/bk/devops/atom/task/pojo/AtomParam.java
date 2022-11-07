package com.tencent.bk.devops.atom.task.pojo;

import com.tencent.bk.devops.atom.pojo.AtomBaseParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件参数定义
 *
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AtomParam extends AtomBaseParam {

    /**
     * 子流水线所属项目ID
     */
    private String projectId;

    /**
     * 子流水线类型
     */
    private String subPipelineType;

    /**
     * 子流水线名称
     */
    private String subPipelineName;

    /**
     * 子流水线ID
     */
    private String subPip;

    /**
     * 同步还是异步运行
     */
    private String runMode;

    /**
     * 子流水线启动参数
     */
    private String params;

    /**
     * 子流水线启动参数-输入
     */
    private String paramsStr;

    /**
     * 轮询间隔
     */
    private String pollingInterval;

    /**
     * 输出子流水构建变量
     */
    private String outputSubBuildVariable;

    /**
     * 子流水下输出变量命名空间
     */
    private String fieldNamespace;
}
