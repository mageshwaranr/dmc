package com.thoongatechies.require.dm.resource;

import com.gs.collections.impl.factory.Lists;
import com.thoongatechies.require.dm.vo.CallbackDefinition;
import com.thoongatechies.require.dm.vo.CallbackData;
import com.thoongatechies.require.dm.vo.Event;
import com.thoongatechies.require.dm.vo.RuleDefinition;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.filter.LoggingFilter;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by mages_000 on 23-Jul-16.
 */
public class DependencyInstanceResourceTester {

    private static int port = 8080;

    private static DependencyResource restService;

    private String acctAndProdAndTxRule = "Matcher" +
            ".event('ACCOUNTS').set('BD').fromHeader('date')" +
            ".and().event('PRODUCTS').set('Premise').fromHeader('premise').set('BD').fromHeader('date')" +
            ".and().event('TRANSACTIONS').set('Premise').fromHeader('premise').set('BD').fromHeader('date')" +
            ".groupBy('BD')";
    private String acctAndProdAndTxAndPc2PremiseRule = "Matcher" +
            ".event('ACCOUNTS').set('Premise').asConstant('PC2').set('BD').fromHeader('date')" +
            ".and().event('PRODUCTS').set('Premise').asConstant('PC2').set('BD').fromHeader('date')" +
            ".and().event('TRANSACTIONS').set('Premise').fromHeader('premise').set('BD').fromHeader('date')" +
            ".and().is('TRANSACTIONS','Premise').eq('PRODUCTS','Premise')" +
            ".and().is('TRANSACTIONS','Premise').eq('ACCOUNTS','Premise')" +
            ".groupBy({'BD','Premise'})";
    private String acctAndProdRule = "Matcher" +
            ".event('ACCOUNTS').set('BD').fromHeader('date')" +
            ".and().event('PRODUCTS').set('BD').fromHeader('date')" +
            ".groupBy('BD')";
    private String acctOrProdRule = "Matcher" +
            ".event('ACCOUNTS').set('BD').fromHeader('date')" +
            ".or().event('PRODUCTS').set('BD').fromHeader('date')" +
            ".groupBy('BD')";
    private String acctOnlyRule = "Matcher" +
            ".event('ACCOUNTS').set('BD').fromHeader('date')" +
            ".groupBy('BD')";
    private String prodOnlyRule = "Matcher" +
            ".event('PRODUCTS').set('BD').fromHeader('date')" +
            ".groupBy('BD')";

    public static void main(String[] args){
        ClientConfig clientConfig = new ClientConfig().register(LoggingFilter.class);
        WebTarget target = ClientBuilder.newClient(clientConfig)
                .target("http://localhost:" + port + "/rest");
        restService = WebResourceFactory.newResource(DependencyResource.class, target);
        fireTests();
    }

    private static void fireTests() {
        DependencyInstanceResourceTester tester = new DependencyInstanceResourceTester();
        tester.createRules();
        tester.sendEvents();
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tester.testResults();
    }


    void createRules(){
        String url = "http://localhost:" + port;
        restService.registerRule(createRule("Test.TransactionRule", acctAndProdAndTxRule, url));
        restService.registerRule(createRule("Test.ReferenceDataRule", acctAndProdRule, url));
        restService.registerRule(createRule("Test.LoadAccountsRule", acctOnlyRule, url));
        restService.registerRule(createRule("Test.LoadProductssRule", prodOnlyRule, url));
        restService.registerRule(createRule("Test.TransactionsDSLRule", acctAndProdAndTxAndPc2PremiseRule, url));
        restService.registerRule(createRule("Test.AnyReferenceDataDSLRule", acctOrProdRule, url));
    }

    void sendEvents() {
        restService.registerEvent(createEvent("ACCOUNTS", createMap(Object.class, "date", "Yesterday")));
        restService.registerEvent(createEvent("TRANSACTIONS", createMap(Object.class, "date", "Yesterday")));
        restService.registerEvent(createEvent("PRODUCTS", createMap(Object.class, "date", "Yesterday")));
        restService.registerEvent(createEvent("ACCOUNTS", createMap(Object.class, "date", "Today")));
        restService.registerEvent(createEvent("TRANSACTIONS", createMap(Object.class, "date", "Today")));
        restService.registerEvent(createEvent("PRODUCTS", createMap(Object.class, "date", "Today")));
        restService.registerEvent(createEvent("ACCOUNTS", createMap(Object.class, "date", "Tomorrow", "premise", "PC2")));
        restService.registerEvent(createEvent("TRANSACTIONS", createMap(Object.class, "date", "Tomorrow", "premise", "PC2")));
        restService.registerEvent(createEvent("PRODUCTS", createMap(Object.class, "date", "Tomorrow", "premise", "PC2")));
    }

    void testResults(){
        //yesterday events
        List<CallbackData> list = restService.findCallbackInstancesBy("2", null, null, null, 0, 0);
        assertEquals(4, list.size());
        list = restService.findCallbackInstancesBy("3", null, null, null, 0, 0);
        assertEquals(1, list.size());
        list = restService.findCallbackInstancesBy("4", null, null, null, 0, 0);
        assertEquals(4, list.size());
        //today
        list = restService.findCallbackInstancesBy("5", null, null, null, 0, 0);
        assertEquals(4, list.size());
        list = restService.findCallbackInstancesBy("6", null, null, null, 0, 0);
        assertEquals(1, list.size());
        list = restService.findCallbackInstancesBy("7", null, null, null, 0, 0);
        assertEquals(4, list.size());
        //premise
        list = restService.findCallbackInstancesBy("8", null, null, null, 0, 0);
        assertEquals(5, list.size());
        list = restService.findCallbackInstancesBy("9", null, null, null, 0, 0);
        assertEquals(2, list.size());
        list = restService.findCallbackInstancesBy("10", null, null, null, 0, 0);
        assertEquals(5, list.size());
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


    private Event createEvent(String name, Map<String, Object> qualifier){
        Event event = Event.newBuilder()
                .withName(name)
                .withOwner("Magesh")
                .withExtRef(name + ":" + qualifier.get("date"))
                .withOccuredAt(new Date())
                .withQualifier(qualifier)
                .build();
        return event;

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

}
