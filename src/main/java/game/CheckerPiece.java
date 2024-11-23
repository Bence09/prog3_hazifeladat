package game;

import java.util.ArrayList;
import java.util.List;

/**
 * The CheckerPiece class represents a piece on the checkerboard.
 * Each piece has a color ("RED" or "BLACK") and a boolean flag to indicate if it is a king.
 * The class has methods to get and set the color, check if the piece is a king, crown the piece as a king,
 * get the possible moves for the piece, and get the list of possible moves.
 */
public class CheckerPiece {
    private boolean isKing;
    private final String color; // "RED" or "BLACK"

    private List<int[]> list;

    /**
     * Constructs a new CheckerPiece object with the given color and sets the king flag to false.
     * @param color the color of the piece ("RED" or "BLACK")
     */
    public CheckerPiece(String color) {
        this.color = color;
        this.isKing = false;
        list = new ArrayList<>();
    }

    /**
     * Sets the list of possible moves for the piece.
     * @param list the list of possible moves
     */
    public void setList(List<int[]> list) {
        this.list = list;
    }

    /**
     * Returns the list of possible moves for the piece.
     * @return the list of possible moves
     */
    public List<int[]> getList() {
        return list;
    }

    /**
     * Returns the color of the piece.
     * @return the color of the piece
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns whether the piece is a king.
     * @return true if the piece is a king, false otherwise
     */
    public boolean isKing() {
        return isKing;
    }

    /**
     * Crowns the piece as a king.
     */
    public void crown() {
        isKing = true;
    }

    /**
     * Returns a list of possible moves for the piece at the given position on the board.
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @param board the CheckerBoard object representing the board
     * @return a list of possible moves for the piece at the given position
     */
    public List<int[]> getPossibleMoves(int x, int y, CheckerBoard board) {
        List<int[]> moves = new ArrayList<>();
        int direction = color.equals("RED") ? -1 : 1;

        // Normal moves
        addMoveIfValid(moves, x + direction, y - 1, board);
        addMoveIfValid(moves, x + direction, y + 1, board);

        // King moves
        if (isKing) {
            addMoveIfValid(moves, x - direction, y - 1, board);
            addMoveIfValid(moves, x - direction, y + 1, board);
        }

        // Capture moves
        addCaptureMoveIfValid(moves, x, y, x + 2 * direction, y - 2, board);
        addCaptureMoveIfValid(moves, x, y, x + 2 * direction, y + 2, board);
        if (isKing) {
            addCaptureMoveIfValid(moves, x, y, x - 2 * direction, y - 2, board);
            addCaptureMoveIfValid(moves, x, y, x - 2 * direction, y + 2, board);
        }

        return moves;
    }

    /**
     * Adds a move to the list of possible moves if the move is valid.
     * @param moves the list of possible moves
     * @param x the x-coordinate of the move
     * @param y the y-coordinate of the move
     * @param board the CheckerBoard object representing the board
     */
    private void addMoveIfValid(List<int[]> moves, int x, int y, CheckerBoard board) {
        if (x >= 0 && x < 8 && y >= 0 && y < 8 && board.getPiece(x, y) == null) {
            moves.add(new int[]{x, y});
        }
    }

    /**
     * Adds a capture move to the list of possible moves if the move is valid.
     * @param moves the list of possible moves
     * @param startX the x-coordinate of the start position
     * @param startY the y-coordinate of the start position
     * @param endX the x-coordinate of the end position
     * @param endY the y-coordinate of the end position
     * @param board the CheckerBoard object representing the board
     */
    private void addCaptureMoveIfValid(List<int[]> moves, int startX, int startY, int endX, int endY, CheckerBoard board) {
        int midX = (startX + endX) / 2;
        int midY = (startY + endY) / 2;
        if (endX >= 0 && endX < 8 && endY >= 0 && endY < 8 && board.getPiece(endX, endY) == null) {
            CheckerPiece midPiece = board.getPiece(midX, midY);
            if (midPiece != null && !midPiece.getColor().equals(this.color)) {
                moves.add(new int[]{endX, endY});
            }
        }
    }
}