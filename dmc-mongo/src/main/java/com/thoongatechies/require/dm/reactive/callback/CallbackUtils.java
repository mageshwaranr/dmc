package com.thoongatechies.require.dm.reactive.callback;

import com.thoongatechies.require.dm.entity.CallbackDataEntity;
import com.thoongatechies.require.dm.entity.Sender;
import com.thoongatechies.require.dm.reactive.AlertPublisher;
import com.thoongatechies.require.dm.vo.CallbackRequest;
import com.thoongatechies.require.dm.reactive.callback.ExponentialRetryJob;
import com.thoongatechies.require.dm.reactive.callback.StandardRetryJob;
import com.thoongatechies.require.dm.service.ModelMapper;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.UUID;

import static com.thoongatechies.require.dm.entity.Constants.DEPENDENCY_SYSTEM_USER;
import static java.net.InetAddress.getLocalHost;

/**
 * Created by mages_000 on 6/4/2016.
 */
@Named
public class CallbackUtils {

    private static final String STANDARD_TYPE = "Standard", EXPONENTIAL_TYPE = "Exponential";

    @Value("${component.name:DMC}")
    private String componentName;
    private String hostName;
    @Inject
    private ModelMapper mapper;
    @Inject
    private Optional<AlertPublisher> alerter;

    public HttpEntity<CallbackRequest> createPayload(CallbackDataEntity callbackData) {
        callbackData.setSender(new Sender());
        callbackData.getSender().setName(DEPENDENCY_SYSTEM_USER);
        callbackData.getSender().setUuid(getUuid());
        HttpHeaders headers= new HttpHeaders();
        headers.add("UUID",callbackData.getSender().getUuid());
        headers.add("SENDER_NAME",callbackData.getSender().getName());
        return new HttpEntity<>(mapper.toCallbackRequest(callbackData),headers);
    }

    @PostConstruct
    public void init() throws UnknownHostException {
        hostName = getLocalHost().getCanonicalHostName();
    }

    public Class<? extends Job> getRetryJob(String retryType) {
        if (STANDARD_TYPE.equals(retryType))
            return StandardRetryJob.class;
        else
            return ExponentialRetryJob.class;
    }

    String getUuid() {
        return UUID.randomUUID().toString();
    }

    public void sendAlert(CallbackDataEntity callbackData, String message) {
        alerter.ifPresent(alertPublisher -> alertPublisher.alert(callbackData, message));
    }
}
