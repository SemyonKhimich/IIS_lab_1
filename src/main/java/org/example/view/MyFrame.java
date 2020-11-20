package org.example.view;

import org.example.logic.Asker;
import org.example.logic.Game;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class MyFrame extends JFrame implements Asker {
    public static final Font DEFAULT_FONT = new Font("Dialog", Font.BOLD, 18);
    private final JLabel[] jLabels;
    private final JLabel conditionJLabel;
    private final JComboBox<String> jComboBox;
    private final JButton okJButton;
    private final JPanel jPanel;
    private Game game;

    public MyFrame() {
        super("Lab 1");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(500, 200, 300, 200);
        jPanel = new JPanel(new GridLayout(2, 2));
        jLabels = new JLabel[2];
        for (int i = 0; i < jLabels.length; i++) {
            jLabels[i] = new JLabel();
            jLabels[i].setFont(DEFAULT_FONT);
            jLabels[i].setVisible(false);
        }
        conditionJLabel = new JLabel();
        conditionJLabel.setFont(DEFAULT_FONT);
        conditionJLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        jComboBox = new JComboBox<>();
        jComboBox.setFont(DEFAULT_FONT);
        okJButton = new JButton("Start Game");
        okJButton.setFont(DEFAULT_FONT);
        okJButton.addActionListener(e -> {
            game = new Game(this);
            Optional<String> optionalResult = game.startGame("skier");
            if (optionalResult.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Skier not found", "Game Result",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "skier is " + optionalResult.get(), "Game Result",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        jPanel.add(jLabels[0]);
        jPanel.add(jLabels[1]);
        jPanel.add(conditionJLabel);
        jPanel.add(jComboBox);
        add(okJButton);
        setVisible(true);
    }

    @Override
    public String askQuestion(String goal, List<String> acceptableGoalValues, String currentOut) {
        jLabels[0].setVisible(!currentOut.isEmpty());
        jLabels[0].setText(currentOut);
        conditionJLabel.setText(goal + "=");
        jComboBox.removeAllItems();
        acceptableGoalValues.forEach(jComboBox::addItem);
        jComboBox.setSelectedIndex(-1);
        int result = JOptionPane.showConfirmDialog(this, jPanel,
                "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            return (String) jComboBox.getSelectedItem();
        }
        return "";
    }
}
