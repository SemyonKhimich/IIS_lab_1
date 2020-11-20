package org.example.logic;

import java.util.List;

public interface Asker {
    String askQuestion(String goal, List<String> acceptableGoalValues, String currentOut);
}
