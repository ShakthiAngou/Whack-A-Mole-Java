import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * @author Shakthi Angou
 */
public class Game implements ActionListener {
    /**
     * This will allow player to start a game.
     */
    private JButton startButton;

    /**
     * Text field that will display time left.
     */
    private JTextField timeLeftField;

    /**
     * Text field that will display the score.
     */
    private JTextField scoreField;

    /**
     * Score.
     */
    private int score;

    /**
     * Color when mole is present and button is "up".
     */
    private Color upColor;

    /**
     * Color when mole is caught and button is "hit".
     */
    private Color hitColor;

    /**
     * Game on boolean.
     */
    private static boolean gameOn;


    /**
     * Button list for holes.
     */
    private final List<JButton> buttonList = new ArrayList<JButton>();

    /**
     * Constructor to create new instance of Game.
     */
    public Game() {
        // font and border instatiation
        Font bigFont = new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 12);
        Border fieldBorder = BorderFactory.createLineBorder(Color.PINK, 2);
        upColor = new Color(255, 102, 178);
        hitColor = new Color(30, 190, 70);

        // step 1: create a frame for the game
        JFrame frame = new JFrame("Whack-a-mole");
        frame.setResizable(false);
        frame.setSize(400, 380);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // step 2: create panels to contain the main elements (controls & holes)
        JPanel pane = new JPanel();
        JPanel controlPanel = new JPanel();
        JPanel holePanel =  new JPanel(new GridLayout(10, 10));

        // step 3: set the background colours of the different panels
        pane.setBackground(Color.pink);
        controlPanel.setBackground(Color.pink);
        holePanel.setBackground(Color.pink);

        // step 4: create a start button in control panel
        startButton = new JButton("Start");
        startButton.setSize(10, 5);
        startButton.addActionListener(this);
        controlPanel.add(startButton);

        // step 5: label for time left, and text field to display time left
        JLabel timeLeftLabel = new JLabel("Time Left: ");
        timeLeftLabel.setFont(bigFont);
        controlPanel.add(timeLeftLabel);
        timeLeftField = new JTextField(6);
        timeLeftField.setEditable(false);
        timeLeftField.setBorder(fieldBorder);
        controlPanel.add(timeLeftField);

        // step 6: label of score, and text field to display score
        JLabel scoreLabel = new JLabel("Score: ");
        scoreLabel.setFont(bigFont);
        controlPanel.add(scoreLabel);
        scoreField = new JTextField(6);
        scoreField.setEditable(false);
        scoreField.setBorder(fieldBorder);
        controlPanel.add(scoreField);

        // step 7: create a panel of holes (buttons)
        for (int i = 0; i < 50; i++) {
            JButton b = new JButton();
            b.setSize(30, 10);
            b.setBackground(Color.pink);
            b.setOpaque(true);
            b.addActionListener(this);
            buttonList.add(b);
            holePanel.add(b);
        }
        pane.add(controlPanel);
        pane.add(holePanel);
        frame.setContentPane(pane);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            gameOn = true;
            score = 0;
            startButton.setEnabled(false);
            Thread t = new TimerThread(20, timeLeftField, startButton, buttonList);
            t.start();
            scoreField.setText(String.valueOf(score));
            for (JButton b: buttonList) {
                Thread tMole = new MoleThread(b, upColor);
                tMole.start();
            }
        } else {
            JButton b = (JButton) e.getSource();
            if (b.getBackground().equals(upColor) && gameOn) {
                score++;
                scoreField.setText(String.valueOf(score));
                b.setBackground(hitColor);
            }
        }

    }
    /**
     * Helper class to create threads for timer.
     */
    private static class TimerThread extends Thread {
        /**
         * The field where time remaining is displayed.
         */
        private JTextField timeField;

        /**
         * The game time.
         */
        private int myTime;

        /**
         * The start button.
         */
        private JButton myButton;

        /**
         * The list of buttons.
         */
        private List<JButton> bList;

        /**
         * Constructor.
         * @param time game time seconds
         * @param f is the timer display field
         * @param b is the start button
         * @param list is the list of hole buttons
         */
        private TimerThread(int time, JTextField f, JButton b, List<JButton> list) {
            myTime = time;
            timeField = f;
            myButton = b;
            bList = list;
        }

        /**
         * Implement run method of Thread class.
         */
        @Override
        public void run() {
            try {
                int i = myTime;
                while (i >= 1) {
                    timeField.setText(" " + String.valueOf(i));
                    Thread.sleep(1000L);
                    i--;
                }
                for (JButton b: bList) {
                    b.setText("");
                    b.setBackground(Color.pink);
                }
                timeField.setText(" " + String.valueOf(i));
                gameOn = false;
                Thread.sleep(5000L);
                myButton.setEnabled(true);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }
    }

    /**
     * Helper class to create threads for timer.
     */
    private static class MoleThread extends Thread {
        /**
         * The button for the specific hole/mole.
         */
        private JButton myButton;

        /**
         * Will generate the randome up times.
         */
        private Random random = new Random();

        /**
         * The color for when the mole is "up".
         */
        private Color upColor;

        /**
         * Constructor.
         * @param b the mole that the thread is made for
         * @param upC is the color for when mole is up
         */
        private MoleThread(JButton b, Color upC) {
            myButton = b;
            upColor = upC;
        }

        /**
         * Implement run method of Thread class.
         */
        @Override
        public void run() {
            try {
                int upTime = random.nextInt(3001);
                Thread.sleep(upTime);
                while (gameOn) {
                    if (gameOn) {
                        myButton.setText("mole");
                        myButton.setBackground(upColor);
                    }
                    if (gameOn) {
                        Thread.sleep(upTime + 1000);
                    }
                    if (gameOn) {
                        myButton.setText("");
                        myButton.setBackground(Color.pink);
                    }
                    if (gameOn) {
                        Thread.sleep(2000L);
                    }
                    upTime = random.nextInt(3);
                }
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }
    }

    /**
     * Main method.
     * @param args which is the command line input argument.
     */
    public static void main(String[] args) throws IOException {
        new Game();
    }
}
