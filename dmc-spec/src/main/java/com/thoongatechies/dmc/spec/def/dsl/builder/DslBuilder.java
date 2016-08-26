package com.thoongatechies.dmc.spec.def.dsl.builder;


import com.thoongatechies.dmc.spec.def.Spec;
import com.thoongatechies.dmc.spec.def.SpecBuilder;
import com.thoongatechies.dmc.spec.def.dsl.BarrierSpecImpl;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

import static com.thoongatechies.dmc.spec.def.Spec.SPECIAL_VALUE_MATCH_ANY;

/**
 * Created by mages_000 on 5/27/2016.
 */
public class DslBuilder implements SpecBuilder {

    private Logger log = LoggerFactory.getLogger(getClass());

    private SelectorBuilder selectorBuilder = SelectorBuilder.newBuilder();
    QualifierFinderBuilder finderBuilder = QualifierFinderBuilder.newBuilder();
    ExpressionBuilder expressionBuilder = ExpressionBuilder.newBuilder();
    ScopeBuilder scopeBuilder = ScopeBuilder.newBuilder();
    GroupBuilder groupBuilder = GroupBuilder.newBuilder();
    ResponseContextBuilder contextBuilder = ResponseContextBuilder.newBuilder();
    List<String> sequentialEventNames = new ArrayList<>();
    Stack<String> eventFieldNames = new Stack<>();
    String userProvidedSpec;
    Map<String,URL> urlBindingMap = new HashMap<>();
//    private RestTemplate template;
    private int expireTime = -1;

    public DslBuilder() {
    }

//    public DslBuilder(RestTemplate restTemplate) {
//    }

    public DslBuilder groupBy(String... commaSeparatedNames){
        groupBuilder.groupBy(commaSeparatedNames);
        return this;
    }
    public DslBuilder groupBy(String commaSeparatedNames){
        this.groupBy(commaSeparatedNames.split(","));
        return this;
    }

    public DslBuilder event(String name){
        sequentialEventNames.add(name);
        selectorBuilder.hasEventWithName(name);
        finderBuilder.event(name);
        expressionBuilder.addEventNameCheck(name);
        return this;
    }

    public DslBuilder set(String varName){
        finderBuilder.set(varName);
        return this;
    }

    public DslBuilder asAny(){
        finderBuilder.asConstant(SPECIAL_VALUE_MATCH_ANY);
        return this;
    }

    public DslBuilder asConstant(String val){
        finderBuilder.asConstant(val);
        return this;
    }

    public DslBuilder fromHeader(String key){
        setExpiryIfRequired(key);
        finderBuilder.fromHeader(key);
        return this;
    }

//    public DslBuilder fromRest(String serviceUrl, String key){
//        setExpiryIfRequired(key);
//        finderBuilder.fromRest(serviceUrl, key);
//        return this;
//    }
//
//    public DslBuilder param(String param){
//        finderBuilder.param(param);
//        return this;
//    }
//
//    public DslBuilder valueOf(String key){
//        finderBuilder.valueOf(key);
//        return this;
//    }

    public DslBuilder toDate(String format){
        finderBuilder.toDate(format);
        return this;
    }

    public DslBuilder latest(){
        scopeBuilder.setRetainLatestFor(getLastEvtName());
        return this;
    }

    private String getLastEvtName() {
        return sequentialEventNames.get(sequentialEventNames.size() - 1);
    }

    public DslBuilder is(String evt, String field){
        sequentialEventNames.add(evt);
        eventFieldNames.push(field);
        return this;
    }

    public DslBuilder eq(String evt, String field){
        expressionBuilder.eq(getLastEvtName(), eventFieldNames.pop(), evt, field);
        sequentialEventNames.add(evt);
        return this;
    }

    public DslBuilder lt(String evt, String field){
        expressionBuilder.lt(getLastEvtName(), eventFieldNames.pop(), evt, field);
        sequentialEventNames.add(evt);
        return this;
    }

    public DslBuilder lte(String evt, String field){
        expressionBuilder.lte(getLastEvtName(), eventFieldNames.pop(), evt, field);
        sequentialEventNames.add(evt);
        return this;
    }

    public DslBuilder gt(String evt, String field){
        expressionBuilder.gt(getLastEvtName(), eventFieldNames.pop(), evt, field);
        sequentialEventNames.add(evt);
        return this;
    }

    public DslBuilder gte(String evt, String field){
        expressionBuilder.gte(getLastEvtName(), eventFieldNames.pop(), evt, field);
        sequentialEventNames.add(evt);
        return this;
    }

    public DslBuilder and(){
        expressionBuilder.and();
        return this;
    }

    public DslBuilder or(){
        expressionBuilder.or();
        return  this;
    }

    public DslBuilder withUserProvidedSpec(String spec){
        this.userProvidedSpec = spec;
        return this;
    }

    @Override
    public SpecBuilder openBracket() {
        expressionBuilder.openBracket();
        return this;
    }

    @Override
    public SpecBuilder closeBracket() {
        expressionBuilder.closeBracket();
        return this;
    }

    @Override
    public SpecBuilder expiresInMin(int minutes) {
        expireTime = minutes;
        return this;
    }

    @Override
    public SpecBuilder addToResponse(String var) {
        contextBuilder.set(var);
        return this;
    }

    @Override
    public SpecBuilder fromQualifier(String evtName, String val) {
        contextBuilder.fromQualifier(evtName, val);
        return this;
    }

    @Override
    public SpecBuilder fromHeader(String evtName, String val) {
        contextBuilder.fromHeader(evtName, val);
        return this;
    }

    @Override
    public SpecBuilder withUrlBindingMap(Map<String, URL> urlBindingMap) {
        this.urlBindingMap = urlBindingMap;
        return this;
    }

    private void setExpiryIfRequired(String key) {
        if(expireTime > 0){
            finderBuilder.set(key);
            expressionBuilder.expiresInMin(expireTime, getLastEvtName(), key);
            expireTime = -1;
        }
    }

    @Override
    public Spec build() {
        BarrierSpecImpl spec = new BarrierSpecImpl();
        String expr = expressionBuilder.build();
        spec.setExpression(expr);
        spec.setExecutable(MVEL.compileExpression(expr));
        spec.setScope(scopeBuilder.build());
        spec.setEventSelector(selectorBuilder.build());
        spec.setQualifierFinder(finderBuilder.build());
        spec.setGrouper(groupBuilder.build());
        spec.setUserProvidedSpec(userProvidedSpec);
        spec.setResponseContextCreator(contextBuilder.build());
        spec.setUrlBindingMap(urlBindingMap);
        spec.setRelatedEventNames(sequentialEventNames);
        return spec;
    }
}
