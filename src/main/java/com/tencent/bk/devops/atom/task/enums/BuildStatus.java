package com.tencent.bk.devops.atom.task.enums;

public enum BuildStatus {

    RUNNING("运行中"),
    SUCCEED("成功"),
    CANCELED("取消"),
    QUEUE_TIMEOUT("排队超时"),
    ERROR("异常"),
    FAILED("失败"),
    TERMINATE("终止");

    private String statusName;

    BuildStatus(String statusName) {
        this.statusName = statusName;
    }

    public static boolean isFinish(String status) {
        return isSuccess(status) || isFailed(status);
    }

    public static boolean isSuccess(String status) {
        return SUCCEED.name().equals(status);
    }

    public static boolean isFailed(String status) {
        return CANCELED.name().equals(status)
                || QUEUE_TIMEOUT.name().equals(status)
                || ERROR.name().equals(status)
                || FAILED.name().equals(status)
                || TERMINATE.name().equals(status);
    }
}
