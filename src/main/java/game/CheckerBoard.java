package game;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The CheckerBoard class represents the board of a checkers game.
 * The board is a 8x8 grid of CheckerPiece objects.
 * The class has methods to get a piece at a given position, move a piece, get possible moves for a piece,
 * check if a move is valid, and check if there are any capture moves available for a player.
 */
public class CheckerBoard {
    private CheckerPiece[][] board;
    private List<int[]> piecePositions;

    /**
     * Constructs a new CheckerBoard object and initializes the board with pieces in their starting positions.
     */
    public CheckerBoard() {
        board = new CheckerPiece[8][8];
        piecePositions = new ArrayList<>();
        setupBoard();
    }

    /**
     * Initializes the board with pieces in their starting positions.
     */
    private void setupBoard() {
        // Initialize pieces on the board
        for (int i = 0; i < 3; i++) {
            for (int j = (i % 2); j < 8; j += 2) {
                board[i][j] = new CheckerPiece("BLACK");
                piecePositions.add(new int[]{i, j});
            }
        }
        for (int i = 5; i < 8; i++) {
            for (int j = (i % 2); j < 8; j += 2) {
                board[i][j] = new CheckerPiece("RED");
                piecePositions.add(new int[]{i, j});
            }
        }
    }

    /**
     * Returns the piece at the given position on the board.
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return the piece at the given position, or null if there is no piece
     */
    public CheckerPiece getPiece(int x, int y) {
        return board[x][y];
    }

    /**
     * Moves a piece from the start position to the end position on the board.
     * Handles capturing of pieces and promotion to king.
     * @param startX the x-coordinate of the start position
     * @param startY the y-coordinate of the start position
     * @param endX the x-coordinate of the end position
     * @param endY the y-coordinate of the end position
     */
    public void movePiece(int startX, int startY, int endX, int endY) {
        CheckerPiece piece = getPiece(startX, startY);
        if (piece == null || !isMoveValid(startX, startY, endX, endY)) {
            return;
        }

        board[endX][endY] = piece;
        board[startX][startY] = null;
        reconstructPiecePositions();

        // Handle captures
        if (Math.abs(endX - startX) == 2 && Math.abs(endY - startY) == 2) {
            int midX = (startX + endX) / 2;
            int midY = (startY + endY) / 2;
            board[midX][midY] = null;
            piecePositions.removeIf(pos -> pos[0] == midX && pos[1] == midY);

            // Check for additional captures
            List<int[]> additionalMoves = piece.getPossibleMoves(endX, endY, this, true);
            if (!additionalMoves.isEmpty()) {
                handleAdditionalCapture(endX, endY);
            }
        }

        // Check for king
        if (endX == 0 && piece.getColor().equals("RED") || endX == 7 && piece.getColor().equals("BLACK")) {
            piece.crown();
        }
    }
    /**
     * Returns a list of possible moves for the piece at the given position on the board.
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return a list of possible moves for the piece at the given position
     */
    public List<int[]> getPossibleMoves(int x, int y) {
        CheckerPiece piece = getPiece(x, y);
        if (piece != null) {
            return piece.getPossibleMoves(x, y, this, false);
        }
        return new ArrayList<>();
    }

    /**
     * Checks if a move from the start position to the end position is valid.
     * @param startX the x-coordinate of the start position
     * @param startY the y-coordinate of the start position
     * @param endX the x-coordinate of the end position
     * @param endY the y-coordinate of the end position
     * @return true if the move is valid, false otherwise
     */
    public boolean isMoveValid(int startX, int startY, int endX, int endY) {
        CheckerPiece piece = getPiece(startX, startY);
        if (piece == null) {
            return false;
        }

        String color = piece.getColor();
        boolean hasCapture = hasCaptureMoves(color);

        List<int[]> possibleMoves = getPossibleMoves(startX, startY);
        for (int[] move : possibleMoves) {
            if (move[0] == endX && move[1] == endY) {
                if (hasCapture) {
                    // Only allow capture moves if there are any
                    return Math.abs(endX - startX) == 2;
                } else {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Prints the positions of the pieces on the board.
     */
    public void printList() {
        System.out.println("\nPiece Positions:");
        for (int[] pos : piecePositions) {
            System.out.println(pos[0] + " " + pos[1]);
        }
        System.out.println("\n");
    }

    /**
     * Returns the list of piece positions on the board.
     * @return the list of piece positions on the board
     */
    public CheckerPiece[][] getBoard() {
        return board;
    }

    /**
     * Sets the board to the given board.
     * @param board the board to set
     */
    public void setBoard(CheckerPiece[][] board) {
        this.board = board;
    }

    /**
     * Rebuilds the list of piece positions on the board.
     * This method should be called after moving a piece to update the list of piece positions.
     */
    public void reconstructPiecePositions() {
        piecePositions.clear();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    piecePositions.add(new int[]{i, j});
                }
            }
        }
    }

    /**
     * Checks if there are any capture moves available for a player of the given color.
     * @param color the color of the player
     * @return true if there are capture moves available, false otherwise
     */
    public boolean hasCaptureMoves(String color) {
        for (int[] pos : piecePositions) {
            CheckerPiece piece = getPiece(pos[0], pos[1]);
            if (piece != null && piece.getColor().equals(color)) {
                List<int[]> moves = piece.getPossibleMoves(pos[0], pos[1], this, true);
                for (int[] move : moves) {
                    if (Math.abs(move[0] - pos[0]) == 2) { // Capture move
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void handleAdditionalCapture(int x, int y) {
        System.out.println("Handling additional capture for piece at (" + x + ", " + y + ")");

        // Check for all possible capture moves
        List<int[]> possibleMoves = getPossibleMoves(x, y);
        boolean additionalCapture = false;

        for (int[] move : possibleMoves) {
            if (Math.abs(move[0] - x) == 2) {
                movePiece(x, y, move[0], move[1]);
                additionalCapture = true;
                break;
            }
        }

        if (additionalCapture) {
            handleAdditionalCapture(x, y); // Recursively handle additional captures
        } else {
            JLabel label = new JLabel("Surprise! You have multiple capture moves!");
            JOptionPane.showMessageDialog(null, label);
        }
    }

}