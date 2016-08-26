package com.thoongatechies.require.dm.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.collections.impl.factory.Lists;
import com.thoongatechies.require.dm.config.ResourceTestSpringConfig;
import com.thoongatechies.require.dm.config.ServiceTestJpaConfig;
import com.thoongatechies.require.dm.dao.EventDao;
import com.thoongatechies.require.dm.entity.Constants;
import com.thoongatechies.require.dm.entity.EventEntity;
import com.thoongatechies.require.dm.vo.CallbackDefinition;
import com.thoongatechies.require.dm.vo.CallbackData;
import com.thoongatechies.require.dm.vo.Event;
import com.thoongatechies.require.dm.vo.RuleDefinition;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by mages_000 on 17-Jul-16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServiceTestJpaConfig.class, ResourceTestSpringConfig.class})
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext
@ActiveProfiles("test,integ-test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DependencyInstanceResourceMITTest {

    String SENDER_NAME = "test";
    String SENDER_UUID = "test123";
    String CALLBACK_NAME = "SERVICE_TEST_CALLBACK";
    String REF_RULE_NAME = "Test.ReferenceDataRule";
    String CHAR_SET = "UTF-8";

    @Value("${local.server.port}")
    int port;

    DependencyResource restService;

    @Inject
    EventDao eventRepo;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        ClientConfig clientConfig = new ClientConfig().register(LoggingFilter.class);
        WebTarget target = ClientBuilder.newClient(clientConfig)
                .target("http://localhost:" + port + "/rest");
        restService = WebResourceFactory.newResource(DependencyResource.class, target);
    }

    private String acctAndProdAndTxRule = new StringBuilder("Matcher.")
            .append("event('ACCOUNTS').").append("set('BD').fromHeader('date')")
            .append(".and().event('PRODUCTS').").append(".set('BD').fromHeader('date')")
            .append(".and().event('TRANSACTIONS').").append(".set('BD').fromHeader('date')")
            .append(".groupBy('BD')").toString();

    private String acctAndProdAndTxAndPc2PremiseRule = new StringBuilder("Matcher.")
            .append("event('ACCOUNTS').").append("set('Premise').fromHeader('premise')").append(".set('BD').fromHeader('date')")
            .append(".and().event('PRODUCTS').").append("set('Premise').asConstant('PC2')").append(".set('BD').fromHeader('date')")
            .append(".and().event('TRANSACTIONS').").append("set('Premise').fromHeader('premise')").append(".set('BD').fromHeader('date')")
            .append(".and().is('TRANSACTIONS','Premise').eq('PRODUCTS','Premise')")
            .append(".and().is('TRANSACTIONS','Premise').eq('ACCOUNTS','Premise')")
            .append(".and().is('TRANSACTIONS','BD').eq('ACCOUNTS','BD')")
            .append(".and().is('TRANSACTIONS','BD').eq('ACCOUNTS','BD')")
            .append(".groupBy({'BD','Premise'})").toString();

    private String acctAndProdRule = new StringBuilder("Matcher.")
            .append("event('ACCOUNTS').").append("set('BD').fromHeader('date')")
            .append(".and().event('PRODUCTS').").append("set('BD').fromHeader('date')")
            .append(".groupBy('BD')").toString();

    private String acctOrProdRule = new StringBuilder("Matcher.")
            .append("event('ACCOUNTS').").append("set('BD').fromHeader('date')")
            .append(".or().event('PRODUCTS').").append("set('BD').fromHeader('date')")
            .append(".groupBy('BD')").toString();


    private String acctOnlyRule = new StringBuilder("Matcher.").append("event('ACCOUNTS')").append("set('BD').fromHeader('date')")
            .append(".groupBy('BD')").toString();


    private String prodOnlyRule = new StringBuilder("Matcher.").append("event('PRODUCTS')").append("set('BD').fromHeader('date')")
            .append(".groupBy('BD')").toString();


    @Test
    public void test01CreateRules() {
        String url = "http://localhost:" + port;
        RuleDefinition rule = restService.registerRule(createRule("Test.TransactionRule", acctAndProdAndTxRule, url));
        verifyRuleAndSingleCallback(rule);
        List<CallbackDefinition> callbackList;

        rule = restService.registerRule(createRule(REF_RULE_NAME, acctAndProdRule, url));
        assertNotNull(rule.getId());
        CallbackDefinition callback = restService.addCallback(rule.getId(), rule.getName(), createCallback(CALLBACK_NAME, "http://localhost:12345"));
        assertNotNull(callback);
        callbackList = restService.getCallbacks(rule.getId(), null, null);
        assertEquals(2, callbackList.size());
        assertNotNull(callbackList.get(0));
        assertNotNull(callbackList.get(1));

        rule = restService.registerRule(createRule("Test.LoadAccountsRule", acctOnlyRule, url));
        verifyRuleAndSingleCallback(rule);

        rule = restService.registerRule(createRule("Test.LoadProductssRule", prodOnlyRule, url));
        verifyRuleAndSingleCallback(rule);

        rule = restService.registerRule(createRule("Test.TransactionsDSLRule", acctAndProdAndTxAndPc2PremiseRule, url));
        verifyRuleAndSingleCallback(rule);

        rule = restService.registerRule(createRule("Test.AnyReferenceDataDSLRule", acctOrProdRule, url));
        verifyRuleAndSingleCallback(rule);
    }

    private void verifyRuleAndSingleCallback(RuleDefinition rule) {
        assertNotNull(rule.getId());
        List<CallbackDefinition> callbackList = restService.getCallbacks(rule.getId(), null, null);
        assertEquals(1, callbackList.size());
        assertNotNull(callbackList.get(0));
    }


    /**
     * Test for sending events and verify callbacks by event id
     */
    @Test
    public void test02SendEvents() throws Exception{
        Event event1 = restService.registerEvent(createEvent("ACCOUNTS", createMap(Object.class, "date", "Yesterday")));
        assertNotNull(event1.getId());
        Event event2 = restService.registerEvent(createEvent("TRANSACTIONS", createMap(Object.class, "date", "Yesterday")));
        assertNotNull(event2.getId());
        Event event3 = restService.registerEvent(createEvent("PRODUCTS", createMap(Object.class, "date", "Yesterday")));
        assertNotNull(event3.getId());
        Event event4 = restService.registerEvent(createEvent("ACCOUNTS", createMap(Object.class, "date", "Today")));
        assertNotNull(event4.getId());
        Event event5 = restService.registerEvent(createEvent("TRANSACTIONS", createMap(Object.class, "date", "Today")));
        assertNotNull(event5.getId());
        Event event6 = restService.registerEvent(createEvent("PRODUCTS", createMap(Object.class, "date", "Today")));
        assertNotNull(event6.getId());
        Event event7 = restService.registerEvent(createEvent("ACCOUNTS", createMap(Object.class, "date", "Tomorrow", "premise", "PC2")));
        assertNotNull(event7.getId());
        Event event8 = restService.registerEvent(createEvent("TRANSACTIONS", createMap(Object.class, "date", "Tomorrow", "premise", "PC2")));
        assertNotNull(event8.getId());
        Event event9 = restService.registerEvent(createEvent("PRODUCTS", createMap(Object.class, "date", "Tomorrow", "premise", "PC2")));
        assertNotNull(event9.getId());
        delay(10);

        //yesterday events
        List<CallbackData> list = restService.findCallbackInstancesBy(event1.getId(), null, null, null, 0, 0);
//        assertEquals(5, list.size());
        assertEquals(4, list.size());
        list = restService.findCallbackInstancesBy(event2.getId(), null, null, null, 0, 0);
        assertEquals(1, list.size());
        list = restService.findCallbackInstancesBy(event3.getId(), null, null, null, 0, 0);
        assertEquals(4, list.size());
        //today
        list = restService.findCallbackInstancesBy(event4.getId(), null, null, null, 0, 0);
        assertEquals(4, list.size());
        list = restService.findCallbackInstancesBy(event5.getId(), null, null, null, 0, 0);
        assertEquals(1, list.size());
        list = restService.findCallbackInstancesBy(event6.getId(), null, null, null, 0, 0);
        assertEquals(4, list.size());
        //premise
        list = restService.findCallbackInstancesBy(event7.getId(), null, null, null, 0, 0);
        assertEquals(5, list.size());
        list = restService.findCallbackInstancesBy(event8.getId(), null, null, null, 0, 0);
        assertEquals(2, list.size());
        list = restService.findCallbackInstancesBy(event9.getId(), null, null, null, 0, 0);
        assertEquals(5, list.size());

        //Event status check
        EventEntity evt = eventRepo.eventById(Long.parseLong(event1.getId()));
        assertEquals(evt.getStatus(), Constants.EVENT_INSTANCE_STATUS_PROCESSED);
        assertEquals(evt.getId(), Long.parseLong(event1.getId()));

    }

    @Test
    public void test03RuleChecks() throws Exception {
        List<RuleDefinition> rules = restService.findAllActiveRules();
        assertEquals(6, rules.size());
        RuleDefinition rule1 = restService.findRule(REF_RULE_NAME);
        assertNotNull(rule1);
        RuleDefinition rule2 = restService.findRuleById(rule1.getId());
        assertNotNull(rule2);
        assertEquals(rule1.getName(), rule2.getName());
        assertEquals(rule1.getStatus(), rule2.getStatus());
        assertEquals(rule1.getExpression(), rule2.getExpression());
        assertEquals(rule1.getId(), rule2.getId());
        assertEquals(rule1.getCreatedOn(), rule2.getCreatedOn());
        assertEquals(rule1.getCallbacks().size(), rule2.getCallbacks().size());

        List<CallbackData> callbacks = restService.findCallbackInstancesBy(null, null, null, rule1.getName(), 10, 0);
        assertEquals(6, callbacks.size());
        callbacks = restService.findCallbackInstancesBy(null, null, rule1.getId(), null, 10, 0);
        assertEquals(6, callbacks.size());

    }

    @Test
    public void test04DeleteRule(){
        restService.deleteCallback(null, REF_RULE_NAME, CALLBACK_NAME);
        List<CallbackDefinition> callbackList = restService.getCallbacks(null,REF_RULE_NAME,null);
        assertEquals(2, callbackList.size());

        restService.deleteRule(REF_RULE_NAME, null);
        List<RuleDefinition> ruleList = restService.findAllActiveRules();
        assertEquals(5,ruleList.size()); //6-1
    }

    private RuleDefinition createRule(String name, String expression, String url) {
        RuleDefinition rule = RuleDefinition.newBuilder().withName(name).withExpression(expression)
                .withCallbacks(Lists.fixedSize.of(createCallback(name, url)))
                .build();
        return rule;
    }

    private CallbackDefinition createCallback(String name, String url) {
        return CallbackDefinition.newBuilder().withName(name).withUrl(url).build();
    }

    private void delay(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Event createEvent(String name, Map<String, Object> qualifier) throws Exception{
        return Event.newBuilder()
                .withName(name)
                .withOwner(SENDER_NAME)
                .withExtRef(name + ":" + qualifier.get("date"))
                .withOccuredAt(new Date())
                .withQualifier(qualifier)
                .build();

    }

    @SuppressWarnings("unchecked")
    private <V> Map<String, V> createMap(Class<V> type, String... args) {
        if (args.length % 2 != 0) {
            throw new RuntimeException("Invalid arguments to create map !!!");
        }
        Map<String, V> map = new HashMap<>();
        for (int i = 0; i < args.length; i = i + 2) {
            map.put(args[i], (V) args[i + 1]);
        }
        return map;
    }

    @After
    public void tearDown() throws Exception {

    }
}