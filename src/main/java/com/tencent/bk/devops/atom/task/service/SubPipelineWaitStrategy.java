package com.tencent.bk.devops.atom.task.service;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.WaitStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubPipelineWaitStrategy implements WaitStrategy {

    private static final Logger logger = LoggerFactory.getLogger(SubPipelineWaitStrategy.class);
    // 单位毫秒
    private final long sleepTime;
    private int exceptionAttemptNumber = 0;
    // 最大的等待时间1分钟
    private static final Long MAXIMUM_WAIT = 60000L;

    public SubPipelineWaitStrategy(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public long computeSleepTime(Attempt failedAttempt) {
        // double exp = Math.pow(2, exceptionAttemptNumber) * sleepTime;
        // long result = Math.round(exp);
        long result = sleepTime + exceptionAttemptNumber * sleepTime;
        if (result > MAXIMUM_WAIT) {
            result = MAXIMUM_WAIT;
        }
        if (failedAttempt.hasException()) {
            exceptionAttemptNumber++;
            logger.error("subPipeline error,errorMsg:{}, exceptionAttemptNumber:{}, sleepTime:{}ms",
                    failedAttempt.getExceptionCause().getMessage(), exceptionAttemptNumber, result);
        } else {
            exceptionAttemptNumber = 0;
        }
        return Math.max(result, 0L);
    }
}
