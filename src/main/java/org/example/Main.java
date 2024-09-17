package org.example;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Color;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main extends JFrame {
    public static final String TITLE_INTRO = "Intro";
    public static final String TITLE_SORT = "Sort";
    public static final Dimension BUTTON_SIZE = new Dimension(100, 30);
    private static final Color COLOR_BLUE = new Color(68,115,197);
    private static final Color COLOR_GREEN = new Color(0,175,80);

    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private final java.util.List<Integer> numbers;
    private boolean isAscendingOrder;
    private final Random random;

    private int highlightIndex1 = -1;
    private int highlightIndex2 = -1;

    public Main() {
        setTitle("Single Page Application");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        numbers = new ArrayList<>();
        random = new Random();

        // CardLayout for screen switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create two screens
        JPanel introScreen = createIntroScreen();

        // Add screens to main panel
        mainPanel.add(introScreen, TITLE_INTRO);
        mainPanel.add(new JPanel(), TITLE_SORT);

        // Set Intro as the default screen
        cardLayout.show(mainPanel, TITLE_INTRO);

        add(mainPanel);
    }

    private JPanel createIntroScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel questionLabel = new JLabel("How many numbers to display?", SwingConstants.CENTER);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(questionLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JTextField numberInputField = new JTextField(16);
        numberInputField.setMaximumSize(new Dimension(100, 30));
        numberInputField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(numberInputField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton enterButton = new JButton("Enter");
        enterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        enterButton.setMaximumSize(BUTTON_SIZE);
        enterButton.setBackground(COLOR_BLUE);
        enterButton.setForeground(Color.WHITE);
        panel.add(enterButton);

        enterButton.addActionListener(e -> {
            String inputText = numberInputField.getText();
            try {
                int numberOfNumbers = Integer.parseInt(inputText);
                if (numberOfNumbers > 0 && numberOfNumbers <= 1000) {
                    // Pass the number to the Sort screen and recreate it
                    mainPanel.add(createSortScreen(numberOfNumbers), TITLE_SORT);
                    cardLayout.show(mainPanel, TITLE_SORT);
                } else {
                    JOptionPane.showMessageDialog(panel, "Please enter a number larger than 0 and lesser than 1000.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input. Please enter an integer.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createSortScreen(int numberOfNumbers) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel to hold the number buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1));
        panel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(330, 440));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel for sorting and reset buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(0, 1, 0, 10));
        panel.add(controlPanel);

        // Sort button to toggle sorting order
        JButton sortButton = new JButton("Sort");
        sortButton.setPreferredSize(BUTTON_SIZE);
        sortButton.setBackground(COLOR_GREEN);
        sortButton.setForeground(Color.WHITE);
        controlPanel.add(sortButton);

        // Reset button to go back to intro screen
        JButton resetButton = new JButton("Reset");
        resetButton.setPreferredSize(BUTTON_SIZE);
        resetButton.setBackground(COLOR_GREEN);
        resetButton.setForeground(Color.WHITE);
        controlPanel.add(resetButton);

        generateRandomNumbers(numberOfNumbers, buttonPanel);
        isAscendingOrder = true;

        // Action listeners
        sortButton.addActionListener(e -> {
            isAscendingOrder = !isAscendingOrder;
            quickSort(numbers, 0, numbers.size() - 1, buttonPanel);
        });

        resetButton.addActionListener(e -> cardLayout.show(mainPanel, TITLE_INTRO));

        return panel;
    }

    private void generateRandomNumbers(int count, JPanel buttonPanel) {
        numbers.clear();
        highlightIndex1 = -1;
        highlightIndex2 = -1;

        for (int i = 0; i < count; i++) {
            numbers.add(random.nextInt(1, 1001)); // Generate numbers between 0 and 1000
        }
        if (numbers.stream().noneMatch(n -> n <= 30)) {
            numbers.set(random.nextInt(count), random.nextInt(1, 31)); // Ensure at least one number ≤ 30
        }
        displayNumbers(buttonPanel);
    }

    private void displayNumbers(JPanel buttonPanel) {
        SwingUtilities.invokeLater(() -> {
            buttonPanel.removeAll();
            buttonPanel.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;

            for (int i = 0; i < numbers.size(); i++) {
                if (i % 10 == 0 && i != 0) {
                    gbc.gridx++;
                    gbc.gridy = 0;
                }

                JButton numberButton = createNumberButton(
                        numbers.get(i),
                        buttonPanel,
                        i == highlightIndex1 || i == highlightIndex2);
                buttonPanel.add(numberButton, gbc);
                gbc.gridy++;
            }

            // Revalidate and repaint the panel to apply the changes
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });
    }

    private JButton createNumberButton(int value, JPanel buttonPanel, boolean isHighlighted) {
        JButton numberButton = new JButton(String.valueOf(value));
        numberButton.setPreferredSize(BUTTON_SIZE);
        numberButton.setMinimumSize(BUTTON_SIZE);

        if (isHighlighted) {
            numberButton.setBackground(Color.CYAN);
            numberButton.setForeground(Color.BLACK);
        } else {
            numberButton.setBackground(COLOR_BLUE);
            numberButton.setForeground(Color.WHITE);
        }

        numberButton.addActionListener(e -> handleNumberButtonClick(value, buttonPanel));
        return numberButton;
    }

    private void handleNumberButtonClick(int value, JPanel buttonPanel) {
        if (value <= 30) {
            isAscendingOrder = true;
            generateRandomNumbers(value, buttonPanel);
        } else {
            JOptionPane.showMessageDialog(buttonPanel, "Please select a value smaller or equal to 30.", "Invalid Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quickSort(java.util.List<Integer> list, int low, int high, JPanel buttonPanel) {
        new Thread(() -> {
            if (low < high) {
                int pi = partition(list, low, high, buttonPanel);
                quickSort(list, low, pi - 1, buttonPanel);
                quickSort(list, pi + 1, high, buttonPanel);
            }

            highlightIndex1 = -1;
            highlightIndex2 = -1;

            displayNumbers(buttonPanel);
        }).start();
    }

    private int partition(java.util.List<Integer> list, int low, int high, JPanel buttonPanel) {
        int pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (isAscendingOrder ? list.get(j) <= pivot : list.get(j) >= pivot) {
                i++;
                Collections.swap(list, i, j);
                highlightButtons(i, j, buttonPanel);
                displayNumbers(buttonPanel);
                delay(200);
            }
        }
        highlightButtons(i + 1, high, buttonPanel);
        Collections.swap(list, i + 1, high);
        displayNumbers(buttonPanel);
        delay(200);
        return i + 1;
    }

    private void highlightButtons(int buttonIndex1, int buttonIndex2, JPanel buttonPanel) {
        SwingUtilities.invokeLater(() -> {
            highlightIndex1 = buttonIndex1;
            highlightIndex2 = buttonIndex2;
            //buttonPanel.repaint();
        });
    }

    private void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}