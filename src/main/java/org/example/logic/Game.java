package org.example.logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Game {
    public static final String ATTRIBUTE_VALUES_FILE_PATH =
            "src\\main\\resources\\attribute_values.json";
    public static final String RULES_FILE_PATH =
            "src\\main\\resources\\rules.json";

    private final String rulesFilename;
    private final String attributesFilename;
    private final Asker asker;

    public Game(Asker asker) {
        this(asker, RULES_FILE_PATH, ATTRIBUTE_VALUES_FILE_PATH);
    }

    public Game(Asker asker, String rulesFilename, String attributesFilename) {
        this.asker = asker;
        this.rulesFilename = rulesFilename;
        this.attributesFilename = attributesFilename;
    }

    public Optional<String> startGame(String goal) {
        List<Rule> rules = readRules(rulesFilename);
        Map<String, List<String>> attributeValues = readAttributeValues(attributesFilename);
        assert attributeValues.containsKey(goal);
        StringBuilder currentOut = new StringBuilder();
        Deque<String> goalStack = new LinkedList<>();
        goalStack.addLast(goal);
        Map<String, String> contextStack = new HashMap<>();
        while (true) {
            if (goalStack.isEmpty()) {
                break;
            }
            Optional<Rule> optionalRule = findRuleRelatedToGoal(rules, goalStack.getLast());
            if (optionalRule.isEmpty()) {
                String topGoal = goalStack.removeLast();
                if (goalStack.isEmpty()) {
                    break;
                }
                String result;
                do {
                    result = asker.askQuestion(topGoal, attributeValues.get(topGoal),
                            currentOut.toString());
                } while (!attributeValues.get(topGoal).contains(result));
                contextStack.put(topGoal, result);
                if (!currentOut.toString().isEmpty()) {
                    currentOut.append(" && ");
                }
                currentOut.append(topGoal).append("=").append(result);
                continue;
            }
            Rule currentRule = optionalRule.get();
            RuleResult result = currentRule.calculateRuleResult(contextStack);
            if (result == RuleResult.TRUE) {
                contextStack.put(currentRule.getThenProperty().getKey(),
                        currentRule.getThenProperty().getValue());
                goalStack.removeLast();
                currentOut.setLength(0);
                currentOut.append(currentRule.getThenProperty().getKey())
                        .append("=").append(currentRule.getThenProperty().getValue());
            } else if (result == RuleResult.FALSE) {
                rules.remove(currentRule);
            } else if (result == RuleResult.UNKNOWN) {
                for (int i = 0; i < currentRule.getIfProperties().size(); i++) {
                    if (!contextStack.containsKey(currentRule.getIfProperties().get(i).getKey())) {
                        goalStack.add(currentRule.getIfProperties().get(i).getKey());
                        break;
                    }
                }
            }
        }
        if (!contextStack.containsKey(goal)) {
            return Optional.empty();
        }
        return Optional.of(contextStack.get(goal));
    }

    private Optional<Rule> findRuleRelatedToGoal(List<Rule> rules, String goal) {
        return rules.stream().filter(r -> r.getThenProperty().getKey().equals(goal)).findFirst();
    }

    private Map<String, List<String>> readAttributeValues(String attributeFilename) {
        Map<String, List<String>> attribute_values = new HashMap<>();
        try {
            FileReader reader = new FileReader(attributeFilename);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            for (Object attribute : jsonObject.keySet()) {
                List<String> values = new ArrayList<>();
                JSONArray jsonArray_values = (JSONArray) jsonObject.get(attribute);
                for (Object jsonArray_value : jsonArray_values) {
                    values.add((String) jsonArray_value);
                }
                attribute_values.put((String) attribute, values);
            }
        } catch (IOException | ParseException | NullPointerException exc) {
            System.out.println(exc);
        }
        return attribute_values;
    }

    private List<Rule> readRules(String rulesFilename) {
        List<Rule> rules = new ArrayList<>();
        try {
            FileReader reader = new FileReader(rulesFilename);
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);
            for (Object jsonArray_item : jsonArray) {
                rules.add(new Rule((JSONObject) jsonArray_item));
            }
        } catch (IOException | ParseException | NullPointerException exc) {
            System.out.println(exc);
        }
        return rules;
    }
}
