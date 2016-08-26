package com.thoongatechies.dmc.core.service;

import com.thoongatechies.require.dm.dao.CallbackDataDao;
import com.thoongatechies.require.dm.dao.EventDao;
import com.thoongatechies.require.dm.dao.RuleInstanceStateDao;
import com.thoongatechies.require.dm.dao.mongo.RuleDefinitionDaoImpl;
import com.thoongatechies.require.dm.entity.CallbackDataEntity;
import com.thoongatechies.require.dm.entity.EventEntity;
import com.thoongatechies.require.dm.entity.RuleInstanceStateEntity;
import com.thoongatechies.require.dm.entity.Sender;
import com.thoongatechies.require.dm.reactive.EventPublisher;
import com.thoongatechies.require.dm.reactive.handlers.CallbackDataHandler;
import com.thoongatechies.require.dm.vo.CallbackData;
import com.thoongatechies.require.dm.vo.Event;
import com.thoongatechies.require.dm.vo.Relationship;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mages_000 on 09-Jun-16.
 */
@Service
public class DependencyInstanceServiceImpl implements DependencyInstanceService {

    @Inject private EventPublisher publisher;
    @Inject
    private EventDao eventRepository;
    @Inject private CallbackDataDao callbacksRepository;
    @Inject private RuleDefinitionDaoImpl ruleDao;
    @Inject private CallbackDataHandler callbackDataHandler;
    @Inject private RuleInstanceStateDao barrierStateRepository;
    @Inject private ModelMapper mapper;

    @Override
    public Event registerNewEvent(Event event, Sender sender) {
        EventEntity dmEvent = mapper.toDmEvent(event, sender);
        eventRepository.newEvent(dmEvent);
        publisher.postIncomingEvent(dmEvent);
        return mapper.toEvent(dmEvent);
    }


    @Override
    public List<Event> findEventByExternalRef(String externalRef) {
        return eventRepository.eventByExtRef(externalRef)
                .stream()
                .map(mapper::toEvent)
                .collect(Collectors.toList());
    }

    @Override
    public Event findEventById(String id) {
        try {
            return mapper.toEvent(eventRepository.eventById(Long.parseLong(id)));
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid Id");
        }
    }

    @Override
    public List<CallbackData> findCallbackDataByEventId(String eventId) {
        try {
            Long evtId = Long.parseLong(eventId);
            List<CallbackDataEntity> dmCallbackDatas = callbacksRepository.callbacksByEventId(evtId);
            return toCallbackData(dmCallbackDatas);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid eventId");
        }
    }

    private List<CallbackData> toCallbackData(List<CallbackDataEntity> dmCallbackDatas) {
        return dmCallbackDatas.stream().map(mapper::toCallbackData).collect(Collectors.toList());
    }

    @Override
    public List<CallbackData> findCallbackDataByEventExtRef(String extRef) {
        return toCallbackData( callbacksRepository.callbacksByEventExtRef(extRef));
    }

    @Override
    public List<CallbackData> findCallbackDataByRuleId(String ruleId, int limit, int page) {
        return toCallbackData( callbacksRepository.callbacksByRuleId(ruleId, limit, page));
    }

    @Override
    public List<CallbackData> findCallbackDataByRuleName(String ruleName, int limit, int page) {
        return toCallbackData( callbacksRepository.callbacksByRuleName(ruleName, limit, page));
    }

    @Override
    public CallbackData findCallbackDataById(String id) {
        return mapper.toCallbackData(callbacksRepository.callbackById(id));
    }

    @Override
    public CallbackData retryCallbackDataById(String id, String user) {
        CallbackDataEntity dmCallbackData = callbacksRepository.callbackById(id);
        if(dmCallbackData != null){
            dmCallbackData.setId(dmCallbackData.getId()+":retry"+System.currentTimeMillis());
            dmCallbackData.setErrorCode(null);
            dmCallbackData.setLastUpdatedBy(user);
            dmCallbackData = callbacksRepository.newCallbackDate(dmCallbackData);
            callbackDataHandler.handle(dmCallbackData);
        }
        return mapper.toCallbackData(dmCallbackData);
    }

    @Override
    public RuleInstanceStateEntity findCurrentState(String ruleId) {
        return barrierStateRepository.findByRuleId(ruleId);
    }

    @Override
    public Relationship findForwardFlow(String extRef) {
        return null;
    }

    @Override
    public Relationship findBackwardFlow(String extRef) {

        return null;
    }

}
