package com.thoongatechies.require.dm.reactive.reactor;

import com.thoongatechies.require.dm.entity.CallbackDataEntity;
import com.thoongatechies.require.dm.entity.EventEntity;
import com.thoongatechies.require.dm.entity.EventInstanceEntity;
import com.thoongatechies.require.dm.reactive.EventPublisher;
import com.thoongatechies.require.dm.reactive.handlers.EventInstanceHandler;
import com.thoongatechies.require.dm.reactive.handlers.IncomingEventHandler;
import org.springframework.context.annotation.Scope;
import reactor.event.Event;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;

/**
 * Created by mages_000 on 6/4/2016.
 */
@Named
@Scope("prototype")
public class IncomingEventListener implements reactor.function.Consumer<Event<EventEntity>> {

    @Inject
    private IncomingEventHandler handleIncomingEvent;

    @Inject
    private EventInstanceHandler evtInstanceHandler;
    @Inject
    private EventPublisher evtPublisher;


    @Override
    public void accept(Event<EventEntity> wrapper) {
        listenEvents(wrapper.getData());
    }

    private void listenEvents(EventEntity evt) {
        Collection<EventInstanceEntity> eventInstances = handleIncomingEvent.handle(evt);
        Collection<CallbackDataEntity> callbacks = evtInstanceHandler.handle(evt, eventInstances);
        callbacks.forEach(evtPublisher::postCallbackEvent);
    }
}
