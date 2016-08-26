package com.thoongatechies.require.dm.reactive.reactor;

import com.thoongatechies.require.dm.entity.CallbackDataEntity;
import com.thoongatechies.require.dm.reactive.handlers.CallbackDataHandler;
import org.springframework.context.annotation.Scope;
import reactor.event.Event;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by mages_000 on 6/4/2016.
 */
@Named
@Scope("prototype")
public class CallbackDataEventListener implements reactor.function.Consumer<Event<CallbackDataEntity>> {

    @Inject
    private CallbackDataHandler callbackDataHandler;

    @Override
    public void accept(Event<CallbackDataEntity> dmCallbackDataEvent) {
        listenEvents(dmCallbackDataEvent.getData());
    }

    private void listenEvents(CallbackDataEntity entity) {
        callbackDataHandler.handle(entity);
    }
}
