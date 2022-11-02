package com.tencent.bk.devops.atom.task.pojo;

import lombok.Data;

/**
 * 子流水线运行状态
 *
 * @version 1.0.0
 */
@Data
public class Status {

    String status;

    @Override
    public String toString() {
        return "status: " + status;
    }
}
