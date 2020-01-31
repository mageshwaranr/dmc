package com.thoongatechies.dmc.core.processing;

import com.thoongatechies.dmc.core.entity.CallbackDataEntity;
import com.thoongatechies.dmc.core.entity.EventEntity;
import com.thoongatechies.dmc.core.entity.EventInstanceEntity;
import com.thoongatechies.dmc.core.processing.handler.CallbackDataHandler;
import com.thoongatechies.dmc.core.processing.handler.EventInstanceHandler;
import com.thoongatechies.dmc.core.processing.handler.IncomingEventHandler;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Named
public class DefaultEventPublisher implements EventPublisher {

    @Inject
    private IncomingEventHandler handleIncomingEvent;

    @Inject
    private EventInstanceHandler evtInstanceHandler;

    @Inject
    private CallbackDataHandler callbackDataHandler;

    @Override
    public void postIncomingEvent(EventEntity evt) {
        Collection<EventInstanceEntity> eventInstances = handleIncomingEvent.handle(evt);
        Collection<CallbackDataEntity> callbacks = evtInstanceHandler.handle(evt, eventInstances);
        CompletableFuture.runAsync(() -> callbacks.forEach(this::postCallbackEvent));
    }

    @Override
    public void postCallbackEvent(CallbackDataEntity callbackData) {
        callbackDataHandler.handle(callbackData);
    }
}
