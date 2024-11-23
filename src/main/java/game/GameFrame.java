package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * The GameFrame class represents the main frame of the game.
 * It extends the JFrame class and contains a Game object to represent the game,
 * a CheckerBoardPanel object to display the checkerboard, and JLabels to display information about the game.
 */
public class GameFrame extends JFrame {
    private Game game;
    private CheckerBoardPanel boardPanel;
    private JLabel currentPlayerLabel;
    private JLabel redCapturedLabel;
    private JLabel blackCapturedLabel;

    /**
     * Constructs a new GameFrame object with a new Game object and a new CheckerBoardPanel object.
     * The frame is set up with a title, size, default close operation, layout, and menu bar.
     */
    public GameFrame() {
        game = new Game();
        boardPanel = new CheckerBoardPanel(game.getBoard(), game, this);

        setTitle("Dáma Játék");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        currentPlayerLabel = new JLabel("Current Player: " + game.getCurrentPlayer());
        redCapturedLabel = new JLabel("Red Captured: " + game.getRedCaptured());
        blackCapturedLabel = new JLabel("Black Captured: " + game.getBlackCaptured());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 3));
        infoPanel.add(currentPlayerLabel);
        infoPanel.add(redCapturedLabel);
        infoPanel.add(blackCapturedLabel);

        add(infoPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);

        createMenuBar();
    }

    /**
     * Creates the menu bar for the frame with File and Help menus.
     * The File menu contains Load and Save menu items.
     * The Help menu contains a Game Rules menu item.
     * The Load menu item loads a saved game.
     * The Save menu item saves the current game.
     * The Game Rules menu item displays the game rules.
     * The menu items have action listeners to perform the corresponding actions.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem loadMenuItem = new JMenuItem("Load");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem gameRulesMenuItem = new JMenuItem("Game Rules");
        helpMenu.add(gameRulesMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        loadMenuItem.addActionListener(new ActionListener() {
            /**
             * Invoked when the action occurs.
             * Loads a saved game and updates the board panel.
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Game loadedGame = Game.loadGame();
                if (loadedGame != null) {
                    game = loadedGame;
                    remove(boardPanel);
                    boardPanel = new CheckerBoardPanel(game.getBoard(), game, GameFrame.this);
                    add(boardPanel, BorderLayout.CENTER);
                    updateLabels();
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(GameFrame.this, "Error loading game", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        saveMenuItem.addActionListener(new ActionListener() {
            /**
             * Invoked when the action occurs.
             * Saves the current game to a JSON file.
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                game.saveGame();
            }
        });

        gameRulesMenuItem.addActionListener(new ActionListener() {
            /**
             * Invoked when the action occurs.
             * Displays the game rules in a dialog.
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameRules();
            }
        });
    }

    /**
     * Displays the game rules in a dialog.
     * The rules are read from a text file and displayed in a message dialog.
     */
    private void showGameRules() {
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/help.txt"))) {
            StringBuilder rules = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                rules.append(line).append("\n");
            }
            JOptionPane.showMessageDialog(this, rules.toString(), "Game Rules", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading help file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the labels to display the current player and the number of captured pieces for each player.
     * If the game is over, a message dialog is displayed with the winner.
     */
    public void updateLabels() {
        currentPlayerLabel.setText("Current Player: " + game.getCurrentPlayer());
        redCapturedLabel.setText("Red Captured: " + game.getRedCaptured());
        blackCapturedLabel.setText("Black Captured: " + game.getBlackCaptured());

        if (Game.getEndGame()) {
            showEndGameWindow(game.getWinner());
        }
    }

    /**
     * Displays a message dialog with the winner of the game.
     * @param winner the winner of the game
     */
    private void showEndGameWindow(String winner) {
        String message = "Winner: " + winner;
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Creates a new GameFrame object and sets it to be visible.
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame().setVisible(true));
    }
}