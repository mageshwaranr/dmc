package com.thoongatechies.require.dm.reactive.handlers;

import com.thoongatechies.require.dm.dao.CallbackDataDao;
import com.thoongatechies.require.dm.entity.CallbackDataEntity;
import com.thoongatechies.require.dm.entity.Constants;
import com.thoongatechies.require.dm.reactive.callback.CallbackUtils;
import com.thoongatechies.require.dm.reactive.callback.ExponentialRetryJob;
import org.quartz.DateBuilder;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by mageshwaranr on 7/25/2016.
 */
@Named
public class CallbackDataHandler {

    @Inject
    private Scheduler scheduler;

    @Inject
    private CallbackUtils callbackUtils;
    @Inject
    private CallbackDataDao callbacksRepository;

    @Value("${callback.retry.limit:5}")
    private int defaultRetryLimit;
    @Value("${callback.retry.time:20}")
    private int defaultRetryTime;


    public void handle(CallbackDataEntity callbackDataEntity) {
        JobDetailImpl job = new JobDetailImpl();
        job.setJobClass(ExponentialRetryJob.class);
        job.getJobDataMap().put(Constants.CALLBACK_ID, callbackDataEntity.getId());
        job.setKey(new JobKey("CR_" + System.nanoTime()));
        job.getJobDataMap().put(Constants.RETRY_TIME, defaultRetryTime);
        job.getJobDataMap().put(Constants.RETRY_LIMIT, defaultRetryLimit);
        SimpleTriggerImpl retry = new SimpleTriggerImpl();
        retry.setName("RT"+System.nanoTime());
        retry.setStartTime(DateBuilder.futureDate(0,DateBuilder.IntervalUnit.SECOND));
        try {
            scheduler.scheduleJob(job,retry);
        } catch (SchedulerException e) {
            setFailureStatus(callbackDataEntity,e);
        }
    }

    private void setFailureStatus(CallbackDataEntity callback, Exception e) {
        callback.setStatus(Constants.CALLBACK_INSTANCE_STATUS_FAILED);
        callbackUtils.sendAlert(callback, e.getMessage());
        callbacksRepository.update(callback);
    }
}
