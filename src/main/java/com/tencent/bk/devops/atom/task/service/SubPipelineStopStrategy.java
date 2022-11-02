package com.tencent.bk.devops.atom.task.service;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.StopStrategy;

public class SubPipelineStopStrategy implements StopStrategy {

    private final int maxAttemptNumber;
    private int exceptionAttemptNumber = 0;

    public SubPipelineStopStrategy(int maxAttemptNumber) {
        this.maxAttemptNumber = maxAttemptNumber;
    }

    @Override
    public boolean shouldStop(Attempt attempt) {
        // 如果连续出现异常到maxAttemptNumber，就停止
        if (attempt.hasException()) {
            exceptionAttemptNumber++;
        } else {
            exceptionAttemptNumber = 0;
        }
        return exceptionAttemptNumber > maxAttemptNumber;
    }
}
