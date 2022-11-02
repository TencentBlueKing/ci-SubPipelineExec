package com.tencent.bk.devops.atom.task.constant;

public interface ErrorCode {

    int DEFAULT_ERROR = 2199001; // 插件默认异常
    int CONFIG_ERROR = 2199002; // 用户配置有误
    int DEPEND_ERROR = 2199003; // 插件依赖异常
    int EXEC_FAILED = 2199004; // 用户任务执行失败
    int GITCI_ERROR = 2199006; // 工蜂服务异常
}
