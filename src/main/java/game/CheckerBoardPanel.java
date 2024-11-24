package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * The CheckerBoardPanel class represents the panel that displays the checkerboard.
 * It extends the JPanel class and contains a CheckerBoard object to represent the board,
 * a JLabel to display a message if a capture move is required, and fields to keep track of the selected position.
 */
public class CheckerBoardPanel extends JPanel {
    private CheckerBoard board;

    private JLabel captureLabel;
    private static final int TILE_SIZE = 50;
    private int selectedX = -1;
    private int selectedY = -1;
    private Game game;
    private GameFrame gameFrame;

    /**
     * Constructs a new CheckerBoardPanel object with the given CheckerBoard, Game, and GameFrame objects.
     * @param board the CheckerBoard object representing the board
     * @param game the Game object representing the game
     * @param gameFrame the GameFrame object representing the game frame
     */
    public CheckerBoardPanel(CheckerBoard board, Game game, GameFrame gameFrame) {
        this.board = board;
        this.game = game;
        this.gameFrame = gameFrame;
        setPreferredSize(new Dimension(TILE_SIZE * 8, TILE_SIZE * 8));

        captureLabel = new JLabel();
        captureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        captureLabel.setFont(new Font("Arial", Font.BOLD, 16));
        captureLabel.setForeground(Color.RED);
        captureLabel.setBounds(0, 0, getWidth(), 30);
        add(captureLabel);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = (e.getY() - (getHeight() - TILE_SIZE * 8) / 2) / TILE_SIZE;
                int y = (e.getX() - (getWidth() - TILE_SIZE * 8) / 2) / TILE_SIZE;

                if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                    if (selectedX == -1 && selectedY == -1) {
                        if (board.getPiece(x, y) != null && board.getPiece(x, y).getColor().equals(game.getCurrentPlayer())) {
                            selectedX = x;
                            selectedY = y;
                        }
                    } else {
                        if (game.move(selectedX, selectedY, x, y)) {
                            selectedX = -1;
                            selectedY = -1;
                            gameFrame.updateLabels();
                            repaint();
                        } else {
                            selectedX = -1;
                            selectedY = -1;
                        }
                    }
                    updateCaptureLabel();
                    repaint();
                }
            }
        });
    }

    /**
     * Updates the captureLabel to indicate if a capture move is required.
     */
    private void updateCaptureLabel() {
        String currentPlayer = game.getCurrentPlayer();
        if (board.hasCaptureMoves(currentPlayer)) {
            captureLabel.setText(currentPlayer + " player must capture!");
        } else {
            captureLabel.setText("");
        }
    }

    /**
     * Paints the checkerboard and pieces on the panel.
     * @param g the Graphics object used to paint the components
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateCaptureLabel();
        int offsetX = (getWidth() - TILE_SIZE * 8) / 2;
        int offsetY = (getHeight() - TILE_SIZE * 8) / 2;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
                g.fillRect(offsetX + j * TILE_SIZE, offsetY + i * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                CheckerPiece piece = board.getPiece(i, j);
                if (piece != null) {
                    if (i == selectedX && j == selectedY) {
                        g.setColor(Color.GREEN);
                    } else if (piece.getColor().equals("RED")) {
                        g.setColor(Color.RED);
                    } else {
                        g.setColor(Color.BLACK);
                    }
                    g.fillOval(offsetX + j * TILE_SIZE + 5, offsetY + i * TILE_SIZE + 5, TILE_SIZE - 10, TILE_SIZE - 10);
                    if (piece.isKing()) {
                        g.setColor(Color.YELLOW);
                        g.drawString("K", offsetX + j * TILE_SIZE + TILE_SIZE / 2 - 5, offsetY + i * TILE_SIZE + TILE_SIZE / 2 + 5);
                    }
                }
            }
        }

        // Highlight possible moves
        List<int[]> possibleMoves = getPossibleMoves();
        g.setColor(Color.YELLOW);
        for (int[] move : possibleMoves) {
            int x = move[0];
            int y = move[1];
            g.fillRect(offsetX + y * TILE_SIZE, offsetY + x * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        if (selectedX != -1 && selectedY != -1) {
            g.setColor(Color.BLUE);
            g.drawRect(offsetX + selectedY * TILE_SIZE, offsetY + selectedX * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    /**
     * Sets the CheckerBoard object for the panel.
     * @param board the CheckerBoard object representing the board
     */
    public void setBoard(CheckerBoard board) {
        this.board = board;
        repaint();
    }

    /**
     * Updates the board by repainting the panel.
     */
    public void updateBoard() {
        repaint();
    }

    /**
     * Returns the CheckerBoard object representing the board.
     * @return the CheckerBoard object representing the board
     */
    public CheckerBoard getBoard() {
        return board;
    }

    /**
     * Returns a list of possible moves for the selected piece.
     * @return a list of possible moves for the selected piece
     */
    private List<int[]> getPossibleMoves() {
        if (selectedX != -1 && selectedY != -1) {
            CheckerPiece piece = board.getPiece(selectedX, selectedY);
            if (piece != null) {
                List<int[]> moves = piece.getPossibleMoves(selectedX, selectedY, board, false);
                if (board.hasCaptureMoves(piece.getColor())) {
                    // Filter out non-capture moves if there are capture moves available
                    moves.removeIf(move -> Math.abs(move[0] - selectedX) != 2);
                }
                return moves;
            }
        }
        return new ArrayList<>();
    }
}