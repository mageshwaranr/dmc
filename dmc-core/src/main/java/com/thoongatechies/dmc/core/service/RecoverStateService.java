package com.thoongatechies.dmc.core.service;


import com.thoongatechies.dmc.core.dao.CallbackDataDao;
import com.thoongatechies.dmc.core.dao.EventDao;
import com.thoongatechies.dmc.core.entity.EventInstanceEntity;
import com.thoongatechies.dmc.core.processing.EventPublisher;
import com.thoongatechies.dmc.core.processing.handler.EventInstanceHandler;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by mages_000 on 6/4/2016.
 */
@Service
public class RecoverStateService {

    @Inject
    private EventPublisher publisher;
    @Inject
    private EventDao eventRepository;
    @Inject
    private CallbackDataDao callbacksRepository;
    @Inject
    private EventInstanceHandler evtInstanceHandler;

    public void recoverState() {
        eventRepository.eventToBeProcessed().forEach(publisher::postIncomingEvent);
        processEventInstances();
        callbacksRepository.callbacksToBeProcessed().forEach(publisher::postCallbackEvent);
    }

    private void processEventInstances() {
        List<EventInstanceEntity> eventInstanceEntities = eventRepository.instancesToBeProcessed();
        Map<Long, Collection<EventInstanceEntity>> evtInstancesByEventId = new HashMap<>();
        eventInstanceEntities.forEach(eventInstanceEntity -> {
                    Collection<EventInstanceEntity> evtInstances = evtInstancesByEventId.getOrDefault(eventInstanceEntity.getEventId(), new ArrayList<>());
                    evtInstances.add(eventInstanceEntity);
                    evtInstancesByEventId.put(eventInstanceEntity.getEventId(), evtInstances);
                }
        );

        evtInstancesByEventId.forEach((evtId, evtInstances) -> evtInstanceHandler.handle(eventRepository.eventById(evtId), evtInstances));
    }
}
