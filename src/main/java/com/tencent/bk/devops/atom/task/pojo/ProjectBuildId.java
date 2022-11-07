package com.tencent.bk.devops.atom.task.pojo;

import lombok.Data;

/**
 * 子流水线信息，用于获取子流水线状态
 *
 * @version 1.0.0
 */
@Data
public class ProjectBuildId {

    /**
     * 构建ID (buildId)
     */
    String id;

    /**
     * 项目ID
     */
    String projectId;

    @Override
    public String toString() {
        return "id = " + id + " projectId = " + projectId;
    }
}
