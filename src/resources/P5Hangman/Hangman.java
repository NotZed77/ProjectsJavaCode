package resources.P5Hangman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Hangman extends JFrame implements ActionListener {
    private final WordDB wordDB;
    private JLabel hangmanImage, categoryLabel, hiddenWordLabel, resultLabel, wordLabel;
    private JButton[] letterButtons, restartButton;
    private JDialog resultDialog;
    private Font customFont;

    private String[] wordChallenge; // Store the challenge from the WordDB here

    private int incorrectGuesses; // Counts the number of incorrect guesses player has made
    public Hangman(){
        super("Hangman Game (Java Edition)");
        setSize(CommonConstants.FRAME_SIZE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(CommonConstants.BACKGROUND_COLOR);

        // init vars
        wordDB = new WordDB();
        letterButtons = new JButton[26];
        wordChallenge = wordDB.loadChallenge();
        customFont = CustomTools.createFont(CommonConstants.FONT_PATH);
        createResultDialog();



        addGuiComponents();
    }
    private void addGuiComponents(){
        hangmanImage = CustomTools.loadImage(CommonConstants.IMAGE_PATH);
        hangmanImage.setBounds(0, 0, hangmanImage.getPreferredSize().width, hangmanImage.getPreferredSize().height);

        // Category display
        categoryLabel = new JLabel(wordChallenge[0]);
        categoryLabel.setFont(customFont.deriveFont(30f));
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        categoryLabel.setOpaque(true);
        categoryLabel.setForeground(Color.WHITE);
        categoryLabel.setBackground(CommonConstants.SECONDARY_COLOR);
        categoryLabel.setBorder(BorderFactory.createLineBorder(CommonConstants.SECONDARY_COLOR));
        categoryLabel.setBounds(
                0,
                hangmanImage.getPreferredSize().height - 28,
                CommonConstants.FRAME_SIZE.width,
                categoryLabel.getPreferredSize().height
        );

        // Hidden word
        hiddenWordLabel = new JLabel(CustomTools.hideWords(wordChallenge[1]));
        hiddenWordLabel.setFont(customFont.deriveFont(64f));
        hiddenWordLabel.setForeground(Color.WHITE);
        hiddenWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hiddenWordLabel.setBounds(
                0,
                categoryLabel.getY() + categoryLabel.getPreferredSize().height + 50,
                CommonConstants.FRAME_SIZE.width,
                hiddenWordLabel.getPreferredSize().height
        );

        // Letter buttons
        GridLayout gridLayout = new GridLayout(4,7);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(
                -5,
                hiddenWordLabel.getY() + hiddenWordLabel.getPreferredSize().height,
                CommonConstants.BUTTON_PANEL_SIZE.width,
                CommonConstants.BUTTON_PANEL_SIZE.height
        );
        buttonPanel.setLayout(gridLayout);

        // Create the letter buttons
        for(char c = 'A'; c<= 'Z'; c++){
            JButton button = new JButton(Character.toString(c));
            button.setBackground(CommonConstants.PRIMARY_COLOR);
            button.setFont(customFont.deriveFont(22f));
            button.setForeground(Color.WHITE);
            button.addActionListener(this);

            // Using ASCII Values to calculate the current index
            int currentIndex = c - 'A';
             letterButtons[currentIndex] = button;
             buttonPanel.add(letterButtons[currentIndex]);
        }

        // Reset Button
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(customFont.deriveFont(22f));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBackground(CommonConstants.SECONDARY_COLOR);
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);

        // Quit Button
        JButton quitButton = new JButton("Quit");
        quitButton.setFont(customFont.deriveFont(22f));
        quitButton.setForeground(Color.WHITE);
        quitButton.setBackground(CommonConstants.SECONDARY_COLOR);
        quitButton.addActionListener(this);
        buttonPanel.add(quitButton);

        getContentPane().add(categoryLabel);
        getContentPane().add(hangmanImage);
        getContentPane().add(hiddenWordLabel);
        getContentPane().add(buttonPanel);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equals("Reset") || command.equals("Restart")){
            resetGame();

            if(command.equals("Restart")){
                resultDialog.setVisible(false);
            }
        }else if(command.equals("Quit")){
            dispose();
            return;
        }else{
            // Letter buttons

            // Disable button
            JButton button = (JButton) e.getSource();
            button.setEnabled(false);

            // Check if the word contains the user's guess
            if(wordChallenge[1].contains(command)){
                // Indicate that the user got it right
                button.setBackground(Color.GREEN);

                // Store the hidden words in a char array, so update the hidden text
                char[] hiddenWord = hiddenWordLabel.getText().toCharArray();

                for(int i = 0; i < wordChallenge[1].length(); i++){
                    // Update _ to correct letter
                    if(wordChallenge[1].charAt(i) == command.charAt(0)){
                        hiddenWord[i] = command.charAt(0);
                    }
                }

                // Update hiddenWordLabel
                hiddenWordLabel.setText(String.valueOf(hiddenWord));

                // The user guessed the word right
                if(!hiddenWordLabel.getText().contains("*")){
                    // Display dialog with success result
                    resultLabel.setText("You got it right!");
                    resultDialog.setVisible(true);

                }

            }else{
                // Indicate that the user chose the wrong letter
                button.setBackground(Color.RED);

                // Increase incorrect counter
                ++incorrectGuesses;

                // Update hangman image
                CustomTools.updateImage(hangmanImage, "/resources/" + (incorrectGuesses + 1) + ".png");

                // User failed to guess the word right
                if(incorrectGuesses >= 6){
                    // Display result dialog with game over label
                    resultLabel.setText("Too Bad, Try Again?");
                    resultDialog.setVisible(true);
                }
            }
            wordLabel.setText("Word: " + wordChallenge[1]);
        }

    }

    private void createResultDialog(){
        resultDialog = new JDialog();
        resultDialog.setTitle("Result");
        resultDialog.setSize(CommonConstants.RESULT_DIALOG_SIZE);
        resultDialog.getContentPane().setBackground(CommonConstants.BACKGROUND_COLOR);
        resultDialog.setResizable(false);
        resultDialog.setLocationRelativeTo(this);
        resultDialog.setModal(true);
        resultDialog.setLayout(new GridLayout(3,1));
        resultDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                resetGame();
            }
        });

        resultLabel = new JLabel();
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        wordLabel = new JLabel();
        wordLabel.setForeground(Color.WHITE);
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton restartButton = new JButton("Restart");
        restartButton.setForeground(Color.WHITE);
        restartButton.setBackground(CommonConstants.SECONDARY_COLOR);
        restartButton.addActionListener(this);

        resultDialog.add(resultLabel);
        resultDialog.add(wordLabel);
        resultDialog.add(restartButton);


    }

    private void resetGame(){
        // Load new challenge
        wordChallenge = wordDB.loadChallenge();
        incorrectGuesses = 0;

        // Load starting image
        CustomTools.updateImage(hangmanImage, CommonConstants.IMAGE_PATH);

        // Update category
        categoryLabel.setText(wordChallenge[0]);

        // Update hiddenWord
        String hiddenWord = CustomTools.hideWords(wordChallenge[1]);
        hiddenWordLabel.setText(hiddenWord);

        // Enable all buttons again
        for(int i= 0; i<letterButtons.length; i++) {
            letterButtons[i].setEnabled(true);
            letterButtons[i].setBackground(CommonConstants.PRIMARY_COLOR);

        }
    }
}
