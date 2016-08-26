package com.thoongatechies.require.dm.resource;

import com.thoongatechies.require.dm.entity.RuleInstanceStateEntity;
import com.thoongatechies.require.dm.entity.Sender;
import com.thoongatechies.require.dm.vo.CallbackData;
import com.thoongatechies.require.dm.vo.Event;
import com.thoongatechies.require.dm.vo.Relationship;
import com.thoongatechies.require.dm.service.DependencyInstanceService;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

import java.util.Collections;
import java.util.List;

import static javax.ws.rs.core.MediaType.*;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/v1/dependency/instance")
public class DependencyInstanceResource {

    private static int MIN_LIMIT = 5;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private DependencyInstanceService service;

    private HttpServletRequest request;

    @POST
    @Path("/event")
    @ApiOperation(value = "Registers the occurance of an event",
            notes = "Qualifier is very important to match similar events so pay extra attention while deiciding the qualifier",
            response = Event.class)
    public Event registerEvent(Event evt) {
        Sender sender = createSender();
        return service.registerNewEvent(evt, sender);
    }

    @GET
    @Path("/event")
    public List<Event> findEventBy(@QueryParam("extRef") String extRef,
                                   @QueryParam("eventId") String eventId) {
        List<Event> events;
        if (extRef != null) {
            events = service.findEventByExternalRef(extRef);
        } else if (eventId != null)
            events = Collections.singletonList(service.findEventById(eventId));
        else
            throw new BadRequestException("Need one of the following input (extRef) or (eventId) ");
        return events;
    }


    @GET
    @Path("/callback")
    public List<CallbackData> findCallbacks(@QueryParam("extRef") String extRef,
                                            @QueryParam("eventId") String eventId,
                                            @QueryParam("ruleId") String ruleId,
                                            @QueryParam("ruleName") String ruleName,
                                            @QueryParam("id") String callbackId,
                                            @QueryParam("limit") int limit,
                                            @QueryParam("page") int page) {
        List<CallbackData> callbackDatas;
        limit = limit < MIN_LIMIT ? MIN_LIMIT : limit;
        if (eventId != null)
            callbackDatas = service.findCallbackDataByEventId(eventId);
        else if (extRef != null)
            callbackDatas = service.findCallbackDataByEventExtRef(extRef);
        else if (ruleId != null)
            callbackDatas = service.findCallbackDataByRuleId(ruleId, limit, page);
        else if (ruleName != null)
            callbackDatas = service.findCallbackDataByRuleName(ruleName, limit, page);
        else if (callbackId != null)
            callbackDatas = Collections.singletonList(service.findCallbackDataById(callbackId));
        else throw new BadRequestException("In sufficient argument to find callback datas");
        return callbackDatas;
    }

    @POST
    @Path("/retryCallback/{id}")
    public CallbackData retryCallback(@PathParam("id") String id) {
        return service.retryCallbackDataById(id, this.request.getRemoteUser());
    }

    @GET
    @Path("/state/ruleid/{id}")
    public RuleInstanceStateEntity findCurrentState(@PathParam("id") String ruleId) {
        return service.findCurrentState(ruleId);
    }


    @GET
    @Path("/ui/flow/forward/{uuid}")
    public Relationship forwardFlow(@PathParam("uuid") String uuid) {
        return service.findForwardFlow(uuid);
    }

    @GET
    @Path("/ui/flow/backward/{uuid}")
    public Relationship backwardFlow(@PathParam("uuid") String uuid) {
        return service.findBackwardFlow(uuid);
    }

    @Context
    void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    private Sender createSender() {
        Sender sender = new Sender();
        sender.setName(request.getRemoteUser());
        sender.setUuid("SENDER_UUID");
        return sender;
    }

}
