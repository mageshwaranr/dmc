package com.thoongatechies.dmc.core.processing.callback;

/**
 * Created by mages_000 on 6/4/2016.
 */
public class ExponentialRetryJob extends AbstractRetryJob {
    @Override
    protected int getNextRetryTime(int retryCnt, int retryTime) {
        return retryCnt * retryTime;
    }
}
