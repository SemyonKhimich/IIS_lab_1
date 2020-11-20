package org.example.logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rule {
    private final List<Property<String, String>> ifProperties;
    private final Property<String, String> thenProperty;

    public Rule(JSONObject jsonObject) {
        ifProperties = new ArrayList<>();
        JSONArray if_properties = (JSONArray) jsonObject.get("if");
        for (Object jsonArray_item : if_properties) {
            String key = (String) ((JSONObject) jsonArray_item).get("property");
            String value = (String) ((JSONObject) jsonArray_item).get("value");
            ifProperties.add(new Property<>(key, value));
        }
        JSONObject then_result = (JSONObject) jsonObject.get("then");
        thenProperty = new Property<>(
                (String) then_result.get("property"),
                (String) then_result.get("value"));
    }

    public RuleResult calculateRuleResult(Map<String, String> context_stack) {
        for (Property<String, String> ifProperty : ifProperties) {
            if (!context_stack.containsKey(ifProperty.getKey())) {
                return RuleResult.UNKNOWN;
            }
            if (!context_stack.get(ifProperty.getKey()).equals(ifProperty.getValue())) {
                return RuleResult.FALSE;
            }
        }
        return RuleResult.TRUE;
    }

    public List<Property<String, String>> getIfProperties() {
        return ifProperties;
    }

    public Property<String, String> getThenProperty() {
        return thenProperty;
    }
}
