package com.thoongatechies.dmc.spec.service;


import com.thoongatechies.dmc.spec.core.Spec;
import com.thoongatechies.dmc.spec.core.SpecGenerator;
import com.thoongatechies.dmc.spec.core.dsl.builder.DslBuilder;
import com.thoongatechies.dmc.spec.vo.SpecExecutionState;
import com.thoongatechies.dmc.spec.vo.Trigger;
import com.thoongatechies.dmc.spec.vo.TriggerGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by mageshwaranr on 7/24/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SpecServiceImplTest {


    @Mock
    private SpecGenerator<String> mockedSpecGenerator;
    @InjectMocks
    private SpecServiceImpl serviceImpl;
    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<String,URL> urlMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        Spec spec = Mockito.mock(Spec.class);
        when(mockedSpecGenerator.generate(anyString(),eq(urlMap))).thenReturn(spec);
    }

    @Test
    public void testParseSpec() throws Exception {
        assertNotNull(serviceImpl.parseSpec("Some spec",urlMap));
        verify(mockedSpecGenerator).generate("Some spec", urlMap);
    }

    @Test
    public void testEvaluateSpecWithoutGlobalGroups() throws Exception {
        log.info("Setting up a simple rule");
        Spec spec = new DslBuilder()
                .event("Account")
                .set("LoadName").fromHeader("LOADNAME")
                .set("BusDate").fromHeader("BusinessDate")
                .and()
                .event("Product")
                .set("LoadName").fromHeader("LOADNAME")
                .set("BusDate").fromHeader("BusinessDate")
                .groupBy("BusDate,LoadName")
                .addToResponse("BusDate").fromQualifier("Product","BusDate")
                .addToResponse("LoadName").fromQualifier("Account","LOADNAME")
                .build();

        SpecExecutionState state = new SpecExecutionState();

        //test
        Trigger evt = newEvent("Account", toMap("LOADNAME","ASIA","BusinessDate","today","SomeK","SomeV"));
        Collection<TriggerGroup> TriggerGroups = serviceImpl.evaluateSpec(evt,state,spec);
        assertTrue(TriggerGroups.isEmpty());
        assertEquals(1, state.getPendingTriggerGroups().size());

        evt = newEvent("Product", toMap("LOADNAME","ASIA","BusinessDate","yesterday","SomeK","SomeV"));
        TriggerGroups = serviceImpl.evaluateSpec(evt, state, spec);
        assertTrue(TriggerGroups.isEmpty());
        assertEquals(2, state.getPendingTriggerGroups().size());

        evt = newEvent("Product", toMap("LOADNAME","ASIA","BusinessDate","today","SomeK","SomeV"));
        TriggerGroups = serviceImpl.evaluateSpec(evt, state, spec);
        assertFalse(TriggerGroups.isEmpty());
        assertEquals(1, state.getPendingTriggerGroups().size());
        assertEquals(1, TriggerGroups.size());
        //verify pending groups
        TriggerGroup TriggerGroup = state.getPendingTriggerGroups().stream().findFirst().get();
        assertEquals(2, TriggerGroup.getGroupedData().size());
        assertEquals("ASIA", TriggerGroup.getGroupedData().get("LoadName"));
        assertEquals("yesterday", TriggerGroup.getGroupedData().get("BusDate"));
        assertTrue(TriggerGroup.getEventsByName().containsKey("Product"));
        //verify processed groups
        TriggerGroup = TriggerGroups.stream().findFirst().get();
        assertEquals(2, TriggerGroup.getGroupedData().size());
        assertEquals("ASIA", TriggerGroup.getGroupedData().get("LoadName"));
        assertEquals("today", TriggerGroup.getGroupedData().get("BusDate"));
        assertTrue(TriggerGroup.getEventsByName().containsKey("Product"));
        assertTrue(TriggerGroup.getEventsByName().containsKey("Account"));
        //verify response context data
        assertEquals(2, TriggerGroup.getResponseContext().size());
        assertEquals("ASIA", TriggerGroup.getGroupedData().get("LoadName"));
        assertEquals("today", TriggerGroup.getGroupedData().get("BusDate"));


        evt = newEvent("Account", toMap("LOADNAME","ASIA","BusinessDate","yesterday","SomeK","SomeV"));
        TriggerGroups = serviceImpl.evaluateSpec(evt, state, spec);
        assertFalse(TriggerGroups.isEmpty());
        assertTrue(state.getPendingTriggerGroups().isEmpty());
        assertEquals(1, TriggerGroups.size());
    }

    @Test
    public void testEvaluateSpecWithGlobalGroups() throws Exception {
        log.info("Setting up a multi dimension spec rule");
        Spec spec = new DslBuilder()
                .event("Dim_1")
                .set("LoadName").asAny()
                .set("BusDate").asAny()
                .set("sourceTs").fromHeader("sts")
                .latest()
                .and()
                .event("Dim_2")
                .set("LoadName").asAny()
                .set("BusDate").asAny()
                .set("sourceTs").fromHeader("sts")
                .latest()
                .and()
                .event("Fact_1")
                .set("LoadName").fromHeader("LOADNAME")
                .set("BusDate").fromHeader("BusinessDate")
                .set("sourceTs").fromHeader("sts")
                .and()
                .event("Fact_2")
                .set("LoadName").fromHeader("LOADNAME")
                .set("BusDate").fromHeader("BusinessDate")
                .set("sourceTs").fromHeader("sts")
                .and()
                .is("Dim_1","sourceTs").gte("Fact_1","sourceTs")
                .and()
                .is("Dim_2","sourceTs").gte("Fact_1","sourceTs")
                .and()
                .is("Fact_1","sourceTs").eq("Fact_2","sourceTs")
                .groupBy("BusDate,LoadName")
                .build();

        SpecExecutionState state = new SpecExecutionState();
        long today = System.currentTimeMillis();
        long yesterday = today - TimeUnit.DAYS.toDays(1);
        Map<String,Object> factHeadersYesterday = toMap("LOADNAME","SOD","BusinessDate","yesterday","sts",yesterday);
        Map<String,Object> factHeadersToday = toMap("LOADNAME","SOD","BusinessDate","today","sts",today);

        //test
        Trigger evt = newEvent("Dim_1", Collections.singletonMap("sts", today + TimeUnit.HOURS.toDays(1)));
        Collection<TriggerGroup> TriggerGroups = serviceImpl.evaluateSpec(evt,state,spec);
        assertTrue(TriggerGroups.isEmpty());
        assertEquals(1, state.getGlobalTriggerGroups().size());
        assertEquals(2, state.getGlobalTriggerGroups().stream().findAny().get().getGroupedData().size());

        evt = newEvent("Dim_2", Collections.singletonMap("sts", today + TimeUnit.HOURS.toDays(1)));
        TriggerGroups = serviceImpl.evaluateSpec(evt,state,spec);
        assertTrue(TriggerGroups.isEmpty());
        assertEquals(1, state.getGlobalTriggerGroups().size());
        assertEquals(2, state.getGlobalTriggerGroups().stream().findAny().get().getGroupedData().size());

        evt = newEvent("Fact_1", factHeadersYesterday);
        TriggerGroups = serviceImpl.evaluateSpec(evt,state,spec);
        assertTrue(TriggerGroups.isEmpty());
        assertEquals(1, state.getPendingTriggerGroups().size());
        TriggerGroup pendingGroup = state.getPendingTriggerGroups().stream().findAny().get();
        Trigger event = pendingGroup.getEventsByName().get("Fact_1");
        assertEquals(toMap("LoadName","SOD","BusDate","yesterday","sourceTs",yesterday),event.getQualifier());
        assertEquals(2, pendingGroup.getGroupedData().size());


        evt = newEvent("Fact_2", factHeadersToday);
        TriggerGroups = serviceImpl.evaluateSpec(evt, state, spec);
        assertTrue(TriggerGroups.isEmpty());
        assertEquals(2, state.getPendingTriggerGroups().size());

        evt = newEvent("Fact_2", factHeadersYesterday);
        TriggerGroups = serviceImpl.evaluateSpec(evt, state, spec);
        assertFalse(TriggerGroups.isEmpty());
        assertEquals(1, state.getPendingTriggerGroups().size());
        assertEquals(1, TriggerGroups.size());
        assertEquals(1, state.getGlobalTriggerGroups().size());
        assertEquals(2, state.getGlobalTriggerGroups().stream().findAny().get().getGroupedData().size());

        evt = newEvent("Fact_1", factHeadersToday);
        TriggerGroups = serviceImpl.evaluateSpec(evt, state, spec);
        assertFalse(TriggerGroups.isEmpty());
        assertTrue(state.getPendingTriggerGroups().isEmpty());
        assertEquals(1, TriggerGroups.size());
        assertEquals(1, state.getGlobalTriggerGroups().size());
        assertEquals(2, state.getGlobalTriggerGroups().stream().findAny().get().getGroupedData().size());

    }

    Trigger newEvent(String name, Map<String,Object> headers){
        Trigger evt = new Trigger();
        evt.setName(name);
        evt.setHeaders(headers);
        return evt;
    }

    private Map<String,Object> toMap(String k1, Object v1,String k2, Object v2, String k3, Object v3){
        Map<String,Object> val = new HashMap<>();
        val.put(k1,v1);
        val.put(k2,v2);
        val.put(k3,v3);
        return val;
    }
}