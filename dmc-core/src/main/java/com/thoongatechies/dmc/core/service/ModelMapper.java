package com.thoongatechies.dmc.core.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by mages_000 on 6/4/2016.
 */
@Named
public class ModelMapper {

    ObjectMapper mapper;

    Logger log = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void init() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public RuleDefinitionEntity toDMRuleDefinition(RuleDefinition rule, Sender sender) {
        String jsonString = toJsonString(rule, "Rule -> JSON");
        RuleDefinitionEntity ruleDef = toObject(jsonString, RuleDefinitionEntity.class, "JSON -> DMRuleDefinition");
        ruleDef.setCreatedBy(sender.getName());
        ruleDef.setLastUpdatedOn(new Date());
        return ruleDef;
    }

    public Event toEvent(EventEntity in) {
        return Event.newBuilder()
                .withId(""+in.getId())
                .withName(in.getName())
                .withExtRef("" + in.getExtRef())
                .withData(in.getData())
                .withQualifier(in.getHeaders())
                .withOwner(in.getOwner())
                .withCreatedBy(in.getCreatedBy())
                .withCreatedOn(in.getCreatedOn())
                .withOccuredAt(in.getOccuredAt())
                .build();
    }

    public Flow toFlow(EventEntity event) {
        Flow flow = new Flow();
        flow.setId(event.getExtRef());
        flow.setType("Event");
        flow.setName(event.getName());
        flow.setStatus("SUCCESS");
        return flow;
    }

    public Flow toFlow(CallbackDataEntity callbackData) {
        Flow flow = new Flow();
        flow.setId(callbackData.getSender().getUuid());
        flow.setType("Callback");
        if (callbackData.getRule() != null)
            flow.setName(callbackData.getRule().getName());
        flow.setStatus(callbackData.getStatus());
        return flow;
    }

    public Flow toFlow(CallbackDataViewEntity callbackData) {
        Flow flow = new Flow();
        flow.setId(callbackData.getSender().getUuid());
        flow.setType("Callback");
        if (callbackData.getRule() != null)
            flow.setName(callbackData.getRule().getName());
        flow.setStatus(callbackData.getStatus());
        return flow;
    }

    public EventEntity toDmEvent(Event in, Sender sender) {
        EventEntity evt = new EventEntity();
        evt.setName(in.getName());
        evt.setCreatedBy(in.getCreatedBy());
        evt.setOccuredAt(in.getOccuredAt());
        evt.setExtRef(sender.getUuid());
        evt.setHeaders(in.getQualifier());
        evt.setData(in.getData());
        if (sender.getName() != null)
            evt.setOwner(sender.getName());
        else
            evt.setOwner(in.getOwner());
        return evt;
    }

    public RuleDefinition toRule(RuleDefinitionEntity ruleDef) {
        if (ruleDef != null) {
            return toObject(toJsonString(ruleDef, "DMRuleDefinition -> JSON"), RuleDefinition.class, "DMRuleDefinition:JSON -> Rule");
        } else return null;
    }

    public CallbackDefinition toCallback(CallbackDefinitionEntity def) {
        if (def != null) {
            return toObject(toJsonString(def, "DMCallbackDefinition -> JSON"), CallbackDefinition.class, "DMCallbackDefinition:JSON -> Callback");
        } else return null;
    }

    public CallbackDefinitionEntity toCallbackDef(CallbackDefinition def) {
        if (def != null) {
            return toObject(toJsonString(def, "Callback -> JSON"), CallbackDefinitionEntity.class, "Callback:JSON -> DMCallbackDefinition");
        } else return null;
    }

    public CallbackData toCallbackData(CallbackDataEntity callbackData) {
        if (callbackData == null)
            return null;

        CallbackData result = new CallbackData();
        result.setCallbackName(callbackData.getCallback().getName());
        result.setErrorCode("" + callbackData.getErrorCode());
        result.setId(callbackData.getId());
        result.setLastUpdatedBy(callbackData.getLastUpdatedBy());
        result.setLastUpdatedOn(callbackData.getLastUpdatedOn());
        result.setStatus(callbackData.getStatus());
        Sender sender = callbackData.getSender();
        if (sender != null)
            result.setUuid(sender.getUuid());
        result.setPayload(toJsonString(callbackData,"CallbackData -> JSON as Payload String"));
        return result;
    }

    public CallbackRequest toCallbackRequest(CallbackDataEntity callbackData){
        CallbackRequest request = new CallbackRequest();
        request.setSender(callbackData.getSender());
        request.setRule(toRule(callbackData.getRule()));
        request.setQualifier(callbackData.getQualifier());
        request.setData(callbackData.getData());
        Set<Event> collect = callbackData.getEvents().stream()
                .map(me -> toObject(toJsonString(me, "MutableEvent -> JSON"), Event.class, "MutableEvent:JSON -> Event"))
                .collect(Collectors.toSet());
        request.setEvents(collect);
        return request;
    }

    public <T> String toJsonString(T obj, String op) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Unable to convert Object to JSON String while doing " + op, e);
        }
    }

    public <T> T toObject(String json, Class<T> tgtClass, String op) {
        try {
            return mapper.readValue(json, tgtClass);
        } catch (IOException e) {
            throw new RuntimeException("Unable to convert JSON String to Object while doing " + op, e);
        }
    }

}
