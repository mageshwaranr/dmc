package com.thoongatechies.dmc.spec.core.dsl.builder;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by mages_000 on 5/27/2016.
 */
public class ExpressionBuilder {

    String EXISTENCE_CHECK_TEMPLATE = " hasEvent('%s') ";
    String GT_OPERATOR_TEMPLATE = " qualifier('%s').%s > qualifier('%s').%s ";
    String GTE_OPERATOR_TEMPLATE = " qualifier('%s').%s >= qualifier('%s').%s ";
    String LT_OPERATOR_TEMPLATE = " qualifier('%s').%s < qualifier('%s').%s ";
    String LTE_OPERATOR_TEMPLATE = " qualifier('%s').%s <= qualifier('%s').%s ";
    String EQ_OPERATOR_TEMPLATE = " qualifier('%s').%s == qualifier('%s').%s ";
    String EXPIRE_TEMPLATE = " qualifier('%s').%s > currentTime() ";
    String AND_OPERATOR = " && ";
    String OR_OPERATOR = " || ";
    String OPEN_BRACKET = " ( ";
    String CLOSE_BRACKET = " ) ";
    private StringBuilder expression = new StringBuilder();
    private List<String> sequentialEventNames = new ArrayList<>();

    public static ExpressionBuilder newBuilder() {
        return new ExpressionBuilder();
    }

    public ExpressionBuilder addEventNameCheck(String name) {
        exists(name);
        return this;
    }

    private void exists(String name) {
        String formatted = format(EXISTENCE_CHECK_TEMPLATE, name);
        sequentialEventNames.add(name);
        expression.append(formatted);
    }
    public ExpressionBuilder eq(String leftEvt, String leftFld, String rightEvt, String rightFld) {
        expression.append(format(EQ_OPERATOR_TEMPLATE, leftEvt, leftFld, rightEvt, rightFld));
        return this;
    }
    public ExpressionBuilder gt(String leftEvt, String leftFld, String rightEvt, String rightFld) {
        expression.append(format(GT_OPERATOR_TEMPLATE, leftEvt, leftFld, rightEvt, rightFld));
        return this;
    }

    public ExpressionBuilder gte(String leftEvt, String leftFld, String rightEvt, String rightFld) {
        expression.append(format(GTE_OPERATOR_TEMPLATE, leftEvt, leftFld, rightEvt, rightFld));
        return this;
    }

    public ExpressionBuilder lte(String leftEvt, String leftFld, String rightEvt, String rightFld) {
        expression.append(format(LTE_OPERATOR_TEMPLATE, leftEvt, leftFld, rightEvt, rightFld));
        return this;
    }

    public ExpressionBuilder lt(String leftEvt, String leftFld, String rightEvt, String rightFld) {
        expression.append(format(LT_OPERATOR_TEMPLATE, leftEvt, leftFld, rightEvt, rightFld));
        return this;
    }

    public ExpressionBuilder expiresInMin(int minutes, String evt, String field) {
        expression.append(format(EXPIRE_TEMPLATE, evt, field, (minutes * 60 * 10000)));
        return this;
    }

    public ExpressionBuilder and() {
        expression.append(AND_OPERATOR);
        return this;
    }

    public ExpressionBuilder or() {
        expression.append(OR_OPERATOR);
        return this;
    }

    public ExpressionBuilder openBracket() {
        expression.append(OPEN_BRACKET);
        return this;
    }

    public ExpressionBuilder closeBracket() {
        expression.append(CLOSE_BRACKET);
        return this;
    }

    public String build() {
        return expression.toString();
    }



}
