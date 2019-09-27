package com.thoongatechies.dmc.core.resource;

import com.thoongatechies.dmc.core.entity.Sender;
import com.thoongatechies.dmc.core.processing.callback.EntityServiceURLConfig;
import com.thoongatechies.dmc.core.service.DependencyDefinitionService;
import com.thoongatechies.dmc.core.vo.CallbackDefinition;
import com.thoongatechies.dmc.core.vo.RuleDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Named
@Path("/v1/dependency/definition")
public class DependencyDefinitionResource {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private DependencyDefinitionService service;

    @Inject
    private EntityServiceURLConfig config;

    private HttpServletRequest request;

    @POST
    @Path("/rule/validate")
    public boolean validateRule(String expression) {
        return service.validateRule(expression);
    }


    @POST
    @Path("/rule")
    public RuleDefinition registerRule(RuleDefinition rule) {
        Sender sender = createSender();
        if (rule == null)
            throw new BadRequestException("Rule object is mandatory to create a new rule");
        log.info("{} is creating the {} rule from {} host ", sender.getName(), rule.getName(), request.getRemoteHost());
        return service.registerNewRule(rule, sender);
    }

    @DELETE
    @Path("/rule")
    public RuleDefinition deleteRule(@QueryParam("name") String name, @QueryParam("id") String id) {
        if (name == null && id == null)
            throw new BadRequestException("Either rule name or rule id is required to delete the rule");

        Sender sender = createSender();
        RuleDefinition savedRule;
        if (name != null) {
            savedRule = service.deleteRuleByName(name, sender);
        } else {
            savedRule = service.deleteRuleById(id, sender);
        }
        return savedRule;
    }

    @GET
    @Path("/rule/{id}")
    public RuleDefinition findRuleById(@PathParam("id") String id) {
        return service.findRuleById(id);
    }

    @GET
    @Path("/rule")
    public RuleDefinition findRule(@QueryParam("name") String name, @QueryParam("status") String status) {
        if (name == null)
            throw new BadRequestException("Rule name is mandatory to find a rule");

        if (status == null)
            return service.findRuleByName(name);

        return service.findByNameAndStatus(name, status);

    }

    @GET
    @Path("/rule/{ruleId}/events")
    public List<String> findAllEventsOfARule(@PathParam("ruleId") String ruleId) {
        return new ArrayList<>(service.findAllEventsOfARule(ruleId));
    }

    @GET
    @Path("/rules/active")
    public List<RuleDefinition> findAllActiveRules() {
        return new ArrayList<>(service.findActiveRules());
    }

    @GET
    @Path("/rules/status")
    public List<RuleDefinition> findRuleByStatus(@QueryParam("status") String status) {
        if (status == null)
            throw new BadRequestException("status can not be null");
        Collection<RuleDefinition> ruleByStatus = service.findRuleByStatus(status);
        return new ArrayList<>(ruleByStatus);
    }

    @POST
    @Path("/callback")
    public CallbackDefinition addCallback(@QueryParam("ruleId") String ruleId, @QueryParam("ruleName") String ruleName,
                                          @NotNull CallbackDefinition callback) {
        if (ruleId == null && ruleName == null) {
            throw new BadRequestException("Either ruleId or ruleName is required");
        }

        Sender sender = createSender();
        CallbackDefinition savedCallback = null;

        if (ruleId != null) {
            savedCallback = service.addCallbackToRuleById(ruleId, callback, sender);
        } else {
            savedCallback = service.addCallbackToRuleByName(ruleName, callback, sender);
        }
        return savedCallback;
    }


    @DELETE
    @Path("/callback")
    public void deleteCallback(@QueryParam("ruleId") String ruleId, @QueryParam("ruleName") String ruleName, @QueryParam("callbackName") String callbackName) {
        Sender sender = createSender();
        if (ruleId != null && callbackName != null)
             service.deleteCallbackByRuleIdAndCallbackName(ruleId, callbackName, sender);
        else if (ruleName != null && callbackName != null)
            service.deleteCallbackByRuleNameAndCallbackName(ruleName, callbackName, sender);
        else
            throw new BadRequestException("To delete a callback need callback name and one of ruleId/ruleName");
    }

    @GET
    @Path("/callback")
    public List<CallbackDefinition> getCallbacks(@QueryParam("ruleId") String ruleId, @QueryParam("ruleName") String ruleName, @QueryParam("callbackName") String callbackName) {
        List<CallbackDefinition> callbacks;
        if (ruleId != null && callbackName != null)
            callbacks = service.findCallbackByRuleIdAndCallbackName(ruleId, callbackName);
        else if (ruleName != null && callbackName != null)
            callbacks = service.findCallbackByRuleNameAndCallbackName(ruleName, callbackName);
        else if (ruleName != null)
            callbacks = service.findCallbackByRuleName(ruleName);
        else if(ruleId != null)
            callbacks = service.findCallbackByRuleId(ruleId);
        else
            throw new BadRequestException("In sufficient arguments to query callbacks");
        return callbacks;
    }

    @GET
    @Path("/clear/entityUrlConfig")
    public boolean cleanEntityUrlConfig(){
        config.clearConfig();
        return true;
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
