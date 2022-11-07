package com.tencent.bk.devops.atom.task.service;

import com.tencent.bk.devops.atom.exception.RemoteServiceException;
import com.tencent.bk.devops.atom.task.enums.BuildStatus;
import com.tencent.bk.devops.atom.task.exception.ExceptionTranslator;
import com.tencent.bk.devops.atom.task.pojo.Status;
import com.tencent.bk.devops.atom.task.utils.RetryUtils;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class RetryUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(RetryUtilsTest.class);

    @Test
    public void retry502Exception() {
        AtomicInteger attemptNumber = new AtomicInteger();
        try {
            RetryUtils.retry(() -> {
                try {
                    attemptNumber.getAndIncrement();
                    throw new RemoteServiceException("get sub pipeline name error", 502, "服务正在部署");
                } catch (Exception e) {
                    throw ExceptionTranslator.translator(e);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Assert.assertEquals(RetryUtils.DEFAULT_MAX_ATTEMPT_NUMBER, attemptNumber.get() - 1);
    }

    @Test
    public void retryNormal() {
        AtomicInteger attemptNumber = new AtomicInteger();
        try {
            attemptNumber.getAndIncrement();
            RetryUtils.retry(() -> {
              logger.info("retry normal, retry times: 0");
              return 0;
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Assert.assertEquals(0, attemptNumber.get() - 1);
    }

    @Test
    public void retryCrossException() {
        AtomicInteger attemptNumber = new AtomicInteger();
        try {
            RetryUtils.retry(() -> {
                try {
                    int number = attemptNumber.getAndIncrement();
                    if (number == 3) {
                        return number;
                    }
                    throw new RemoteServiceException("get sub pipeline name error", 502, "服务正在部署");
                } catch (Exception e) {
                    throw ExceptionTranslator.translator(e);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Assert.assertEquals(3, attemptNumber.get() - 1);
    }

    @Test
    public void statusRetry502Exception() {
        AtomicInteger attemptNumber = new AtomicInteger();
        try {
            RetryUtils.statusRetry(() -> {
                try {
                    attemptNumber.getAndIncrement();
                    throw new RemoteServiceException("get sub pipeline name error", 502, "服务正在部署");
                } catch (Exception e) {
                    throw ExceptionTranslator.translator(e);
                }
            }, null);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Assert.assertEquals(RetryUtils.DEFAULT_MAX_ATTEMPT_NUMBER, attemptNumber.get() - 1);
    }

    @Test
    public void statusRetryCrossException() {
        AtomicInteger attemptNumber = new AtomicInteger();
        try {
            RetryUtils.statusRetry(() -> {
                try {
                    int number = attemptNumber.getAndIncrement();
                    if (number == 3) {
                        Status status = new Status();
                        status.setStatus(BuildStatus.RUNNING.name());
                        return status;
                    }
                    if (number == 4) {
                        Status status = new Status();
                        status.setStatus(BuildStatus.SUCCEED.name());
                        return status;
                    }
                    throw new RemoteServiceException("get sub pipeline name error", 502, "服务正在部署");
                } catch (Exception e) {
                    throw ExceptionTranslator.translator(e);
                }
            }, null);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Assert.assertEquals(4, attemptNumber.get() - 1);
    }
}
