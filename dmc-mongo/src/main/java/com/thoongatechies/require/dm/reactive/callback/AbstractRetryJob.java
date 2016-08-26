package com.thoongatechies.require.dm.reactive.callback;

import com.thoongatechies.require.dm.dao.CallbackDataDao;
import com.thoongatechies.require.dm.entity.CallbackDataEntity;
import com.thoongatechies.require.dm.reactive.CallbackUtils;
import org.quartz.*;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.text.MessageFormat;

import static com.thoongatechies.require.dm.entity.Constants.*;

/**
 * Created by mages_000 on 6/4/2016.
 */
public abstract class AbstractRetryJob implements Job {

    private CallbackDataDao repository;
    private RestTemplate restTemplate;
    private CallbackUtils callbackUtils;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String callbackId = context.getMergedJobDataMap().getString(CALLBACK_ID);
        if (callbackId != null) {
            restNotify(repository.callbackById(callbackId), context);
        } else {
            log.warn("Callback data id is empty. Skipping callback trigger job");
        }
    }

    private void restNotify(CallbackDataEntity callbackData, JobExecutionContext context) {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        int retryCount = jobDataMap.containsKey(RETRY_COUNT) ? jobDataMap.getInt(RETRY_COUNT) : 1;

        try {
            restTemplate.postForObject(callbackData.getCallback().getUrl(),
                    callbackUtils.createPayload(callbackData), Object.class);
            callbackData.setStatus(CALLBACK_INSTANCE_STATUS_PROCESSED);
        } catch (RestClientException e) {
            log.error("Retry attempt {} of callback {} failed due to {} ", retryCount, callbackData.getCallback(), e.getMessage());
            rescheduleJob(callbackData, retryCount, context);
        } catch (Exception e) {
            String message = MessageFormat.format("Retry attempt {} of callback {} failed due to {} ", retryCount, callbackData.getCallback(), e.getMessage());
            log.error("Retry attempt {} of callback {} failed due to {} ", retryCount, callbackData.getCallback(), e.getMessage());
            setFailureStatus(callbackData, message);
        } finally {
            repository.update(callbackData);
        }

    }

    private void rescheduleJob(CallbackDataEntity callbackData, int retryCnt, JobExecutionContext context) {
        int retryLimit = context.getMergedJobDataMap().getInt(RETRY_LIMIT);
        int retryTime = context.getMergedJobDataMap().getInt(RETRY_TIME);
        if (retryCnt < retryLimit) {
            context.getJobDetail().getJobDataMap().put(RETRY_COUNT, ++retryCnt);
            SimpleTriggerImpl retryTrigger = new SimpleTriggerImpl();
            retryTrigger.setName("RT" + System.nanoTime());
            retryTrigger.setStartTime(DateBuilder.futureDate(getNextRetryTime(retryCnt, retryTime), DateBuilder.IntervalUnit.SECOND));
            try {
                context.getScheduler().addJob(context.getJobDetail(), true, true);
                context.getScheduler().rescheduleJob(context.getTrigger().getKey(), retryTrigger);
            } catch (SchedulerException e) {
                String message = "Retry callback [" + retryCnt + "][" + callbackData.getId() + "] schedule failed due to Exception"
                        + "URL is " + callbackData.getCallback().getUrl() + ". Message is " + e.getMessage();
                log.error(message);
                setFailureStatus(callbackData, message);
            }
        } else {
            String message = "Retry callback [" + retryCnt + "][" + callbackData.getId() + "] failed after exceeding max retry attempts"
                    + "URL is " + callbackData.getCallback().getUrl() ;
            log.error(message);
            setFailureStatus(callbackData, message);
        }
    }

    protected abstract int getNextRetryTime(int retryCnt, int retryTime);

    private void setFailureStatus(CallbackDataEntity callbackData, String message) {
        callbackData.setStatus(CALLBACK_INSTANCE_STATUS_FAILED);
        callbackUtils.sendAlert(callbackData, message);
    }

    @Inject
    public void setRepository(CallbackDataDao repository) {
        this.repository = repository;
    }

    @Inject
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Inject
    public void setCallbackUtils(CallbackUtils callbackUtils) {
        this.callbackUtils = callbackUtils;
    }
}
