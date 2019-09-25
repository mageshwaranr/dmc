package com.thoongatechies.require.dm.reactive.handlers;

import com.thoongatechies.require.dm.dao.EventDao;
import com.thoongatechies.require.dm.entity.EventEntity;
import com.thoongatechies.require.dm.entity.EventInstanceEntity;
import com.thoongatechies.require.dm.service.EventRuleRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.thoongatechies.require.dm.entity.Constants.EVENT_INSTANCE_STATUS_FAN_OUT;

/**
 * Created by mageshwaranr on 7/24/2016.
 */
@Named
public class IncomingEventHandler {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private EventRuleRelation ruleRelation;

    @Inject
    private EventDao eventRepo;

    /**
     * For incoming event, find all related rule defs. Create an instance at each rule level to track status at rule : event combination
     *
     * @param evt
     * @return Event instances
     */
    public Collection<EventInstanceEntity> handle(EventEntity evt) {
        List<EventInstanceEntity> savedInstances = fanoutEvents(evt);
        //set event processing is complete as
        eventRepo.setProcessed(evt);
        log.debug("Created {} event instances for event with id {}", savedInstances.size(), evt.getId());
        //defer the event instance processing to other handler
        return savedInstances;
    }

    private List<EventInstanceEntity> fanoutEvents(EventEntity evt) {
        final List<String> relatedRuleId = ruleRelation.getRelatedRuleId(evt.getName());
        log.info("Event {} fanout nos. is {}", evt.getName() + ":" + evt.getId(), relatedRuleId.size());
        return relatedRuleId
                .stream()
                .map(ruleId -> {
                    EventInstanceEntity toBeSaved = newEventInstance(ruleId, evt.getId());
                    eventRepo.newEventInstance(toBeSaved);
                    return toBeSaved;
                })
                .collect(Collectors.toList());
    }


    private EventInstanceEntity newEventInstance(String ruleId, long eventId) {
        EventInstanceEntity ei = new EventInstanceEntity();
        ei.setEventId(eventId);
        ei.setRuleId(ruleId);
        ei.setStatus(EVENT_INSTANCE_STATUS_FAN_OUT);
        ei.setId(ruleId + ":" + eventId);
        return ei;
    }


}
