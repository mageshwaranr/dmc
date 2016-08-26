package com.thoongatechies.require.dm.resource;

import com.thoongatechies.require.dm.vo.CallbackDefinition;
import com.thoongatechies.require.dm.vo.CallbackData;
import com.thoongatechies.require.dm.vo.Event;
import com.thoongatechies.require.dm.vo.RuleDefinition;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by mages_000 on 17-Jul-16.
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/v1/dependency")
public interface DependencyResource {

    @POST @Path("/definition/rule/validate")
    boolean validateRule(String expression);

    @POST @Path("/definition/rule")
    RuleDefinition registerRule(RuleDefinition rule);

    @DELETE @Path("/definition/rule")
    RuleDefinition deleteRule(@QueryParam("name") String name, @QueryParam("id") String id);

    @GET @Path("/definition/rule/{id}")
    RuleDefinition findRuleById(@PathParam("id") String id);

    @GET @Path("/definition/rule")
    RuleDefinition findRule(@QueryParam("name") String name);

    @GET @Path("/definition/rule/{ruleId}/events")
    List<String> findAllEventsOfARule(@PathParam("ruleId") String ruleId);

    @GET @Path("/definition/rules/active")
    List<RuleDefinition> findAllActiveRules();

    @POST @Path("/definition/callback")
    CallbackDefinition addCallback(@QueryParam("ruleId") String ruleId, @QueryParam("ruleName") String ruleName, CallbackDefinition callback);

    @DELETE @Path("/definition/callback")
    CallbackDefinition deleteCallback(@QueryParam("ruleId") String ruleId, @QueryParam("ruleName") String ruleName, @QueryParam("callbackName") String callbackName);

    @GET @Path("/definition/callback")
    List<CallbackDefinition> getCallbacks(@QueryParam("ruleId") String ruleId, @QueryParam("ruleName") String ruleName, @QueryParam("callbackName") String callbackName);

    @POST @Path("/instance/event")
    Event registerEvent(Event event);

    @GET @Path("/instance/callback")
    List<CallbackData> findCallbackInstancesBy(@QueryParam("eventId") String eventId, @QueryParam("extRef") String extRef,
                                               @QueryParam("ruleId") String ruleId, @QueryParam("ruleName") String ruleName,
                                               @QueryParam("limit") int limit, @QueryParam("page") int page);
}
