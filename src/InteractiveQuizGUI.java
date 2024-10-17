import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InteractiveQuizGUI {

    private final JFrame mainFrame;
    private final JTextArea inputArea;
    private JFrame quizFrame;
    private JLabel questionLabel;
    private JRadioButton[] options;
    private int currentQuestionIndex = 0;
    private List<QuizQuestion> questions;
    private int score = 0;

    public InteractiveQuizGUI() {
        mainFrame = new JFrame("Interactive Quiz Program");
        mainFrame.setSize(600, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        inputArea = new JTextArea(5, 40);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(inputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Enter Paragraph:"));

        JButton generateQuizButton = getButton();

        mainFrame.add(scrollPane, BorderLayout.NORTH);
        mainFrame.add(generateQuizButton, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    private JButton getButton() {
        JButton generateQuizButton = new JButton("Generate Quiz");
        generateQuizButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateQuizButton.addActionListener(_ -> {
            String documentText = inputArea.getText().trim();
            if (documentText.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Please enter a paragraph.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            questions = generateQuiz(documentText);
            if (questions.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Not enough content to generate quiz.", "Quiz Generation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            currentQuestionIndex = 0;
            score = 0;
            showQuizWindow();
        });
        return generateQuizButton;
    }

    private List<QuizQuestion> generateQuiz(String documentText) {
        List<QuizQuestion> quizQuestions = new ArrayList<>();
        String[] sentences = documentText.split("(?<=[.!?])\\s*");

        for (String sentence : sentences) {
            boolean isTrueStatement = Math.random() < 0.5;
            String modifiedStatement = isTrueStatement ? sentence : negateStatement(sentence);
            int correctAnswerIndex = isTrueStatement ? 0 : 1; // "True" is 0, "False" is 1
            quizQuestions.add(new QuizQuestion("True or False: " + modifiedStatement, new String[]{"True", "False"}, correctAnswerIndex));
        }

        Collections.shuffle(quizQuestions);
        return quizQuestions;
    }

    private String negateStatement(String sentence) {
        // Simple logic to negate the statement by adding "not" or modifying
        if (sentence.toLowerCase().contains("is")) {
            return sentence.replaceFirst("(?i)is", "is not");
        } else if (sentence.toLowerCase().contains("are")) {
            return sentence.replaceFirst("(?i)are", "are not");
        } else {
            return "It is not true that " + sentence;
        }
    }

    private void showQuizWindow() {
        quizFrame = new JFrame("Quiz");
        quizFrame.setSize(600, 400);
        quizFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        quizFrame.setLayout(new BorderLayout());

        JPanel quizPanel = new JPanel();
        quizPanel.setLayout(new BoxLayout(quizPanel, BoxLayout.Y_AXIS));

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        quizPanel.add(questionLabel);

        options = new JRadioButton[2];
        ButtonGroup optionGroup = new ButtonGroup();
        for (int i = 0; i < options.length; i++) {
            options[i] = new JRadioButton();
            options[i].setFont(new Font("Arial", Font.PLAIN, 14));
            optionGroup.add(options[i]);
            quizPanel.add(options[i]);
        }

        JButton nextButton = getNextButton();
        JButton prevButton = getPrevButton();

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        quizPanel.add(buttonPanel);
        quizFrame.add(quizPanel, BorderLayout.CENTER);
        displayQuestion();
        quizFrame.setVisible(true);
    }

    private JButton getNextButton() {
        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.addActionListener(_ -> {
            checkAnswer();
            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                JOptionPane.showMessageDialog(quizFrame,
                        "Quiz Completed! Your score: " + score + "/" + questions.size() +
                                "\nYour score percentage: " + ((score * 100) / questions.size()) + "%",
                        "Quiz Results", JOptionPane.INFORMATION_MESSAGE);
                quizFrame.dispose();
            }
        });
        return nextButton;
    }

    private JButton getPrevButton() {
        JButton prevButton = new JButton("Previous");
        prevButton.setFont(new Font("Arial", Font.BOLD, 14));
        prevButton.addActionListener(_ -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayQuestion();
            }
        });
        return prevButton;
    }

    private void displayQuestion() {
        QuizQuestion question = questions.get(currentQuestionIndex);
        questionLabel.setText((currentQuestionIndex + 1) + ". " + question.getQuestion());
        String[] optionsText = question.getOptions();
        for (int i = 0; i < options.length; i++) {
            options[i].setText(optionsText[i]);
            options[i].setSelected(false);
        }
    }

    private void checkAnswer() {
        int selectedOption = -1;
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSelected()) {
                selectedOption = i;
                break;
            }
        }

        if (selectedOption == questions.get(currentQuestionIndex).getCorrectAnswerIndex()) {
            score++;
            JOptionPane.showMessageDialog(quizFrame, "Correct Answer!", "Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(quizFrame, "Wrong Answer! The correct answer was: "
                    + questions.get(currentQuestionIndex).getOptions()[questions.get(currentQuestionIndex).getCorrectAnswerIndex()], "Result", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class QuizQuestion {
        private final String question;
        private final String[] options;
        private final int correctAnswerIndex;

        public QuizQuestion(String question, String[] options, int correctAnswerIndex) {
            this.question = question;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
        }

        public String getQuestion() {
            return question;
        }

        public String[] getOptions() {
            return options;
        }

        public int getCorrectAnswerIndex() {
            return correctAnswerIndex;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InteractiveQuizGUI::new);
    }
}
