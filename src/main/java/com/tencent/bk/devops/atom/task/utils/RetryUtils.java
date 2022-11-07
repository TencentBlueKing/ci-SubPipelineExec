package com.tencent.bk.devops.atom.task.utils;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.tencent.bk.devops.atom.task.enums.BuildStatus;
import com.tencent.bk.devops.atom.task.exception.ApiException;
import com.tencent.bk.devops.atom.task.exception.ApiRetryException;
import com.tencent.bk.devops.atom.task.pojo.Status;
import com.tencent.bk.devops.atom.task.service.LoggerRetryListener;
import com.tencent.bk.devops.atom.task.service.SubPipelineStopStrategy;
import com.tencent.bk.devops.atom.task.service.SubPipelineWaitStrategy;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;

public class RetryUtils {

    // 默认10s发送一次请求
    public static final long DEFAULT_SLEEP_TIME = 10L;
    // 默认60s打印一条日志
    public static final long DEFAULT_LOG_INTERVAL_TIME = 60L;
    // 最大重试次数
    public static final int DEFAULT_MAX_ATTEMPT_NUMBER = 5;

    public static <T> T retry(Callable<T> callable) {
        Retryer<T> retryer = RetryerBuilder.<T>newBuilder()
                // retryIf 重试条件
                .retryIfExceptionOfType(ApiRetryException.class)
                // 等待策略
                .withWaitStrategy(new SubPipelineWaitStrategy(TimeUnit.SECONDS.toMillis(DEFAULT_SLEEP_TIME)))
                // 重试次数
                .withStopStrategy(new SubPipelineStopStrategy(DEFAULT_MAX_ATTEMPT_NUMBER))
                .build();
        return call(retryer, callable);
    }

    public static Status statusRetry(Callable<Status> callable, String pollingInterval) {
        long sleepTime =
                StringUtils.isBlank(pollingInterval) ? DEFAULT_SLEEP_TIME : Long.parseLong(pollingInterval) * 60;
        // 定义重试机制
        Retryer<Status> retryer = RetryerBuilder.<Status>newBuilder()
                // retryIf 重试条件
                .retryIfExceptionOfType(ApiRetryException.class)
                // 执行状态不是完成状态,就继续执行
                .retryIfResult(status -> status != null && !BuildStatus.isFinish(status.getStatus()))
                // 等待策略：每次请求间隔sleepTime
                .withWaitStrategy(new SubPipelineWaitStrategy(TimeUnit.SECONDS.toMillis(sleepTime)))
                // 异常重试5次,结果不是完成一直执行
                .withStopStrategy(new SubPipelineStopStrategy(DEFAULT_MAX_ATTEMPT_NUMBER))
                // 默认60s日志输出一次,如果时间间隔超过60s,则按照时间间隔输出日志
                .withRetryListener(new LoggerRetryListener(DEFAULT_LOG_INTERVAL_TIME / sleepTime))
                .build();
        return call(retryer, callable);
    }

    private static <T> T call(Retryer<T> retryer, Callable<T> callable) {
        try {
            return retryer.call(callable);
        } catch (RetryException e) {
            if (e.getLastFailedAttempt().getExceptionCause() != null) {
                throw new ApiException(e.getLastFailedAttempt().getExceptionCause().getMessage());
            }
            throw new ApiException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }
}
