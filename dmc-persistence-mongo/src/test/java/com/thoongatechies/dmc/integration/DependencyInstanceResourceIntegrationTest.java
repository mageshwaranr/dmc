package com.thoongatechies.dmc.integration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.thoongatechies.dmc.core.beanconfigs.DependencyAppConfig;
import com.thoongatechies.dmc.core.entity.CallbackDataEntity;
import com.thoongatechies.dmc.core.vo.CallbackData;
import com.thoongatechies.dmc.core.vo.CallbackDefinition;
import com.thoongatechies.dmc.core.vo.Event;
import com.thoongatechies.dmc.core.vo.RuleDefinition;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.factory.Multimaps;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.thoongatechies.dmc.integration.DependencyInstanceResourceIntegrationTest.EventConstants.*;
import static com.thoongatechies.dmc.integration.DependencyInstanceResourceIntegrationTest.RuleConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DependencyAppConfig.class,
        DependencyInstanceResourceIntegrationTest.TestMongoConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan("com.thoongatechies.dmc.core")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DependencyInstanceResourceIntegrationTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    private static final Integer MONGO_PORT = 27017;

    @ClassRule
    public static GenericContainer mongo = new GenericContainer("mongo:4.0.8")
//            .withNetwork(network)
//            .withNetworkAliases("mongo1")
            .withExposedPorts(MONGO_PORT);

    @LocalServerPort
    private int port;

    RestContract restContract;

    private static Event[] publishedEvents = new Event[100];

    private ObjectMapper jsonMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
//        ClientConfig clientConfig = new ClientConfig().register(LoggingFilter.class);
        WebTarget target = ClientBuilder.newClient()
                .target("http://localhost:" + port + "/rest");
        restContract = WebResourceFactory.newResource(RestContract.class, target);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }
    @Test
    public void test01CreateRules() {
        String url = "http://www.mocky.io/v2/5d8c59792e00007900abd791";
        RuleDefinition rule = restContract.registerRule(createRule(ACCT_AND_PROD_AND_TX, url));
        verifyRuleAndSingleCallback(rule);
        List<CallbackDefinition> callbackList;
        CallbackDefinition callback;
        rule = restContract.registerRule(createRule(ACCT_AND_PROD, url));
        assertNotNull(rule.getId());
        callback = restContract.addCallback(rule.getId(), rule.getName(), createCallback(CALLBACK_NAME, CALLBACK_URL));
        assertNotNull(callback);
        callbackList = restContract.getCallbacks(rule.getId(), null, null);
        assertEquals(2, callbackList.size());
        assertNotNull(callbackList.get(0));
        assertNotNull(callbackList.get(1));

        rule = restContract.registerRule(createRule(ACCT_ONLY, url));
        verifyRuleAndSingleCallback(rule);

        rule = restContract.registerRule(createRule(PROD_ONLY, url));
        verifyRuleAndSingleCallback(rule);

        rule = restContract.registerRule(createRule(ACCT_AND_PROD_AND_TX_AND_PC_2_PREMISE, url));
        verifyRuleAndSingleCallback(rule);

        rule = restContract.registerRule(createRule(ACCT_OR_PROD, url));
        verifyRuleAndSingleCallback(rule);

    }

    /**
     * Test for sending events and verify callbacks by event id
     */
    @Test
    public void test02SendEvents() throws Exception {
        Event evt;
        evt = restContract.registerEvent(createEvent("ACCOUNTS", createMap(Object.class, "date", "Yesterday")));
        assertNotNull(evt.getId());
        publishedEvents[YESTERDAY_ACCT] = evt;
        evt = restContract.registerEvent(createEvent("TRANSACTIONS", createMap(Object.class, "date", "Yesterday")));
        assertNotNull(evt.getId());
        publishedEvents[YESTERDAY_TRAN] = evt;
        evt = restContract.registerEvent(createEvent("PRODUCTS", createMap(Object.class, "date", "Yesterday")));
        assertNotNull(evt.getId());
        publishedEvents[YESTERDAY_PROD] = evt;
        evt = restContract.registerEvent(createEvent("ACCOUNTS", createMap(Object.class, "date", "Today")));
        assertNotNull(evt.getId());
        publishedEvents[TODAY_ACCT] = evt;
        evt = restContract.registerEvent(createEvent("TRANSACTIONS", createMap(Object.class, "date", "Today")));
        assertNotNull(evt.getId());
        publishedEvents[TODAY_TRAN] = evt;
        evt = restContract.registerEvent(createEvent("PRODUCTS", createMap(Object.class, "date", "Today")));
        assertNotNull(evt.getId());
        publishedEvents[TODAY_PROD] = evt;
        evt = restContract.registerEvent(createEvent("ACCOUNTS", createMap(Object.class, "date", "Tomorrow", "premise", "PC2")));
        assertNotNull(evt.getId());
        publishedEvents[TOMO_ACCT] = evt;
        evt = restContract.registerEvent(createEvent("TRANSACTIONS", createMap(Object.class, "date", "Tomorrow", "premise", "PC2")));
        assertNotNull(evt.getId());
        publishedEvents[TOMO_TRAN] = evt;
        evt = restContract.registerEvent(createEvent("PRODUCTS", createMap(Object.class, "date", "Tomorrow", "premise", "PC2")));
        assertNotNull(evt.getId());
        publishedEvents[TOMO_PROD] = evt;
    }

    @Test
    public void test03VerifyCallbacks() throws Exception {
        EventConstants.initializeRuleMappings();
        Lists.fixedSize.of(YESTERDAY_ACCT,YESTERDAY_PROD,YESTERDAY_TRAN
                ,TODAY_ACCT,TODAY_PROD,TODAY_TRAN
                ,TOMO_ACCT,TOMO_PROD,TOMO_TRAN
        )
                .forEach(idx -> {
                    List<CallbackData> list = restContract.findCallbackInstancesBy(publishedEvents[idx].getId(), null, null, null, 0, 0);
                    final MutableCollection<String> rules = EVENT_TO_RULE_MAPPINGS.get(idx);
                    log.info("Expected rules mappings {} for {}",rules,idx);
                    list.forEach(callbackData -> {
                        try {
                            CallbackDataEntity entity = jsonMapper.readValue(callbackData.getPayload(),CallbackDataEntity.class);
                            log.info("Callback received for {}, {}",entity.getRule().getName(), entity.getEvents());
                            assertTrue("Incorrect rule matching found",rules.contains(entity.getRule().getName()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    assertEquals("All expected rules are matched ?",rules.size(), list.size());

                });
    }

    private void verifyRuleAndSingleCallback(RuleDefinition rule) {
        assertNotNull(rule.getId());
        List<CallbackDefinition> callbackList = restContract.getCallbacks(rule.getId(), null, null);
        assertEquals(1, callbackList.size());
        assertNotNull(callbackList.get(0));
    }

    private RuleDefinition createRule(Tuple2<String,String> rule, String url) {
        return RuleDefinition.newBuilder().withName(rule.v1()).withExpression(rule.v2())
                .withCallbacks(Collections.singletonList(createCallback(rule.v1(), url)))
                .build();
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


    public interface EventConstants {
        int YESTERDAY_ACCT = 0, TODAY_ACCT = 1, TOMO_ACCT = 2;
        int YESTERDAY_PROD = 3, TODAY_PROD = 4, TOMO_PROD = 5;
        int YESTERDAY_TRAN = 6, TODAY_TRAN = 7, TOMO_TRAN = 8;

        MutableMultimap<Integer,String> EVENT_TO_RULE_MAPPINGS = Multimaps.mutable.list.of();

        static void initializeRuleMappings(){
            EVENT_TO_RULE_MAPPINGS.put(YESTERDAY_ACCT,ACCT_ONLY.v1);
            EVENT_TO_RULE_MAPPINGS.put(YESTERDAY_ACCT,ACCT_AND_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(YESTERDAY_ACCT,ACCT_AND_PROD.v1);// there are 2 callbacks for ACCT_AND_PROD
            EVENT_TO_RULE_MAPPINGS.put(YESTERDAY_ACCT,ACCT_OR_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(YESTERDAY_ACCT,ACCT_AND_PROD_AND_TX.v1);

            EVENT_TO_RULE_MAPPINGS.put(YESTERDAY_PROD,PROD_ONLY.v1);
            EVENT_TO_RULE_MAPPINGS.put(YESTERDAY_PROD,ACCT_AND_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(YESTERDAY_PROD,ACCT_AND_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(YESTERDAY_PROD,ACCT_OR_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(YESTERDAY_PROD,ACCT_AND_PROD_AND_TX.v1);

            EVENT_TO_RULE_MAPPINGS.put(YESTERDAY_TRAN,ACCT_AND_PROD_AND_TX.v1);

            EVENT_TO_RULE_MAPPINGS.put(TODAY_ACCT,ACCT_ONLY.v1);
            EVENT_TO_RULE_MAPPINGS.put(TODAY_ACCT,ACCT_AND_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TODAY_ACCT,ACCT_AND_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TODAY_ACCT,ACCT_OR_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TODAY_ACCT,ACCT_AND_PROD_AND_TX.v1);

            EVENT_TO_RULE_MAPPINGS.put(TODAY_PROD,PROD_ONLY.v1);
            EVENT_TO_RULE_MAPPINGS.put(TODAY_PROD,ACCT_AND_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TODAY_PROD,ACCT_AND_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TODAY_PROD,ACCT_OR_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TODAY_PROD,ACCT_AND_PROD_AND_TX.v1);

            EVENT_TO_RULE_MAPPINGS.put(TODAY_TRAN,ACCT_AND_PROD_AND_TX.v1);

            EVENT_TO_RULE_MAPPINGS.put(TOMO_ACCT,ACCT_ONLY.v1);
            EVENT_TO_RULE_MAPPINGS.put(TOMO_ACCT,ACCT_AND_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TOMO_ACCT,ACCT_AND_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TOMO_ACCT,ACCT_OR_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TOMO_ACCT,ACCT_AND_PROD_AND_TX.v1);
            EVENT_TO_RULE_MAPPINGS.put(TOMO_ACCT,ACCT_AND_PROD_AND_TX_AND_PC_2_PREMISE.v1);

            EVENT_TO_RULE_MAPPINGS.put(TOMO_PROD,PROD_ONLY.v1);
            EVENT_TO_RULE_MAPPINGS.put(TOMO_PROD,ACCT_AND_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TOMO_PROD,ACCT_AND_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TOMO_PROD,ACCT_OR_PROD.v1);
            EVENT_TO_RULE_MAPPINGS.put(TOMO_PROD,ACCT_AND_PROD_AND_TX.v1);
            EVENT_TO_RULE_MAPPINGS.put(TOMO_PROD,ACCT_AND_PROD_AND_TX_AND_PC_2_PREMISE.v1);

            EVENT_TO_RULE_MAPPINGS.put(TOMO_TRAN,ACCT_AND_PROD_AND_TX.v1);
            EVENT_TO_RULE_MAPPINGS.put(TOMO_TRAN,ACCT_AND_PROD_AND_TX_AND_PC_2_PREMISE.v1);
        }
    }

    public interface RuleConstants {
        String CALLBACK_URL = "http://www.mocky.io/v2/5d8b05dd3500005c00d46b5e";
        String CALLBACK_NAME = "SERVICE_TEST_CALLBACK";

        String SENDER_NAME = "JUnitTestCase";

        Tuple2<String,String> ACCT_ONLY = Tuple.tuple("ACCT_ONLY_RULE",
                "Matcher.event('ACCOUNTS').set('BD').fromHeader('date').groupBy('BD')");

        String PROD_ONLY_RULE = "Matcher.event('PRODUCTS')set('BD').fromHeader('date').groupBy('BD')";
        Tuple2<String,String> PROD_ONLY = Tuple.tuple("PROD_ONLY_RULE", PROD_ONLY_RULE);


        String ACCT_AND_PROD_RULE = "Matcher.event('ACCOUNTS').set('BD').fromHeader('date')" +
                ".and().event('PRODUCTS').set('BD').fromHeader('date')" +
                ".and().is('ACCOUNTS','BD').eq('PRODUCTS','BD')" +
                ".groupBy('BD')";
        Tuple2<String,String> ACCT_AND_PROD = Tuple.tuple("ACCT_AND_PROD_RULE", ACCT_AND_PROD_RULE);

        String ACCT_OR_PROD_RULE = "Matcher.event('ACCOUNTS').set('BD').fromHeader('date')" +
                ".or().event('PRODUCTS').set('BD').fromHeader('date')" +
                ".groupBy('BD')";
        Tuple2<String,String> ACCT_OR_PROD = Tuple.tuple("ACCT_OR_PROD_RULE", ACCT_OR_PROD_RULE);

        String ACCT_AND_PROD_AND_TX_RULE = "Matcher." +
                "event('ACCOUNTS').set('BD').fromHeader('date')" +
                ".and().event('PRODUCTS').set('BD').fromHeader('date')" +
                ".and().event('TRANSACTIONS').set('BD').fromHeader('date')" +
                ".and().is('ACCOUNTS','BD').eq('PRODUCTS','BD')" +
                ".and().is('TRANSACTIONS','BD').eq('PRODUCTS','BD')" +
                ".groupBy('BD')";
        Tuple2<String,String> ACCT_AND_PROD_AND_TX = Tuple.tuple("ACCT_AND_PROD_AND_TX_RULE", ACCT_AND_PROD_AND_TX_RULE);

        String ACCT_AND_PROD_AND_TX_AND_PC_2_PREMISE_RULE = "Matcher." +
                "event('ACCOUNTS').set('Premise').fromHeader('premise').set('BD').fromHeader('date')" +
                ".and().event('PRODUCTS').set('Premise').asConstant('PC2').set('BD').fromHeader('date')" +
                ".and().event('TRANSACTIONS').set('Premise').fromHeader('premise').set('BD').fromHeader('date')" +
                ".and().is('TRANSACTIONS','Premise').eq('PRODUCTS','Premise')" +
                ".and().is('TRANSACTIONS','Premise').eq('ACCOUNTS','Premise')" +
                ".and().is('TRANSACTIONS','BD').eq('PRODUCTS','BD')" +
                ".and().is('TRANSACTIONS','BD').eq('ACCOUNTS','BD')" +
                ".groupBy({'BD','Premise'})";
        Tuple2<String,String> ACCT_AND_PROD_AND_TX_AND_PC_2_PREMISE = Tuple.tuple("ACCT_AND_PROD_AND_TX_AND_PC_2_PREMISE_RULE",
                ACCT_AND_PROD_AND_TX_AND_PC_2_PREMISE_RULE);

    }

    @Configuration
    @ComponentScan({"com.thoongatechies.dmc.dao.mongodb"})
    @EnableMongoRepositories("com.thoongatechies.dmc.dao.mongodb")
    @EnableAutoConfiguration
    public static class TestMongoConfig {

        @Bean
        public MongoClient mongo() throws UnknownHostException {
            final Integer mappedPort = mongo.getMappedPort(MONGO_PORT);
            System.out.println("Mongo Mapped port is " + mappedPort);
            System.out.println("Mongo Mapped Host is " + mongo.getContainerIpAddress());
            System.out.println("Mongo DB Port bindings " + mongo.getPortBindings());
            return new MongoClient(mongo.getContainerIpAddress(), mappedPort);
        }

        @Bean
        public MongoTemplate mongoTemplate() throws UnknownHostException {
            return new MongoTemplate(mongo(), "TestDB");
        }

        @Bean
        public LocalValidatorFactoryBean dataValidator() {
            return new LocalValidatorFactoryBean();
        }

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }


    @AfterClass
    public static void delayCleanup() throws Exception {
        // bring up your own mongo GUI to explore contents
        Thread.sleep(TimeUnit.MINUTES.toMillis(1000));
    }
}
