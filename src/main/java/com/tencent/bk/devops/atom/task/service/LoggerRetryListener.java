package com.tencent.bk.devops.atom.task.service;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import com.tencent.bk.devops.atom.task.enums.BuildStatus;
import com.tencent.bk.devops.atom.task.pojo.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerRetryListener implements RetryListener {

    private final long logInterval;
    private static final Logger logger = LoggerFactory.getLogger(LoggerRetryListener.class);

    public LoggerRetryListener(long logInterval) {
        this.logInterval = logInterval <= 0 ? 1 : logInterval;
    }

    @Override
    public <V> void onRetry(Attempt<V> attempt) {
        if (attempt.hasResult() && (attempt.getAttemptNumber() % logInterval == 0
                || BuildStatus.isFinish(((Status) attempt.getResult()).getStatus()))) {
            logger.info("sub pipeline {}", attempt.getResult());
        }
    }
}
