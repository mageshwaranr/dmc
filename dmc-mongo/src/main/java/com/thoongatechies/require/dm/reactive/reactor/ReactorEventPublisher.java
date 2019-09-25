package com.thoongatechies.require.dm.reactive.reactor;

import com.thoongatechies.require.dm.entity.CallbackDataEntity;
import com.thoongatechies.require.dm.entity.EventEntity;
import com.thoongatechies.require.dm.entity.EventInstanceEntity;
import com.thoongatechies.require.dm.reactive.EventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.Reactor;
import reactor.event.Event;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by mages_000 on 6/4/2016.
 */
@Named
public class ReactorEventPublisher implements EventPublisher {

    @Inject
    @Qualifier("THREADPOOL")
    private Reactor threadPool;

    @Inject
    @Qualifier("WORKQUEUE")
    private Reactor workQueue;

    @Inject
    private IncomingEventListener eventListener;

    @Override
    public void postIncomingEvent(EventEntity evt) {
//        workQueue.notify(evt.getClass(), Event.wrap(evt));
        eventListener.accept(Event.wrap(evt));
    }

    @Override
    public void postCallbackEvent(CallbackDataEntity callbackData) {
        threadPool.notify(callbackData.getClass(), Event.wrap(callbackData));
    }

}
