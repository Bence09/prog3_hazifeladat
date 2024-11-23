package game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * The Game class represents a game of checkers.
 * The game has a CheckerBoard object to represent the board, a currentPlayer field to keep track of the current player,
 * and redCaptured and blackCaptured fields to keep track of the number of pieces captured by each player.
 * It also has a static endGame field to keep track of whether the game has ended.
 * The class has methods to get the board, get the current player, switch the current player, move a piece on the board,
 * get the number of pieces captured by each player, check if the game has ended, get the winner, and save and load the game.
 */
public class Game {
    private final CheckerBoard board;
    private String currentPlayer;
    private int redCaptured;
    private int blackCaptured;

    private static boolean endGame;

    /**
     * Constructs a new Game object with a new CheckerBoard, the current player set to "RED",
     * and the number of pieces captured by each player set to 0.
     * The endGame field is set to false.
     */
    public Game() {
        board = new CheckerBoard();
        currentPlayer = "RED";
        redCaptured = 0;
        blackCaptured = 0;
        endGame = false;
    }

    /**
     * Returns the CheckerBoard object representing the board of the game.
     * @return the CheckerBoard object
     */
    public CheckerBoard getBoard() {
        return board;
    }

    /**
     * Returns the current player.
     * @return the current player
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Switches the current player.
     */
    public void switchPlayer() {
        currentPlayer = currentPlayer.equals("RED") ? "BLACK" : "RED";
    }

    /**
     * Moves a piece on the board from the start position to the end position.
     * If the move is valid, the piece is moved, and the current player is switched.
     * If a piece is captured, the number of pieces captured by the corresponding player is incremented.
     * If the game has ended, the endGame field is set to true.
     * @param startX the x-coordinate of the start position
     * @param startY the y-coordinate of the start position
     * @param endX the x-coordinate of the end position
     * @param endY the y-coordinate of the end position
     * @return true if the move is valid and the piece is moved, false otherwise
     */
    public boolean move(int startX, int startY, int endX, int endY) {
        if (board.isMoveValid(startX, startY, endX, endY)) {
            if (Math.abs(endX - startX) == 2 && Math.abs(endY - startY) == 2) {
                CheckerPiece capturedPiece = board.getPiece((startX + endX) / 2, (startY + endY) / 2);
                if (capturedPiece != null) {
                    if (capturedPiece.getColor().equals("RED")) {
                        redCaptured++;
                    } else {
                        blackCaptured++;
                    }
                }
            }
            board.movePiece(startX, startY, endX, endY);
            switchPlayer();
            if (isEndGame()) {
                endGame = true;
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the number of red pieces captured.
     * @return the number of red pieces captured
     */
    public int getRedCaptured() {
        return redCaptured;
    }

    /**
     * Returns the number of black pieces captured.
     * @return the number of black pieces captured
     */
    public int getBlackCaptured() {
        return blackCaptured;
    }

    /**
     * Checks if the game has ended.
     * The game has ended if all pieces of one color are taken or if the current player has no possible moves.
     * @return true if the game has ended, false otherwise
     */
    public boolean isEndGame() {
        // Check if all pieces of one color are taken
        if (redCaptured == 12 || blackCaptured == 12) {
            return true;
        }

        // Check if the current player has no possible moves
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CheckerPiece piece = board.getPiece(i, j);
                if (piece != null && piece.getColor().equals(currentPlayer)) {
                    if (!board.getPossibleMoves(i, j).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the winner of the game.
     * The winner is the player with all pieces captured or the other player if the current player has no possible moves.
     * @return the winner of the game
     */
    public String getWinner() {
        if (redCaptured == 12) {
            return "BLACK";
        } else if (blackCaptured == 12) {
            return "RED";
        } else {
            return currentPlayer.equals("RED") ? "BLACK" : "RED";
        }
    }

    /**
     * Returns the value of the endGame field.
     * @return the value of the endGame field
     */
    public static boolean getEndGame() {
        return endGame;
    }

    /**
     * Saves the game to a JSON file.
     */
    public void saveGame() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);
        try (FileWriter writer = new FileWriter("game.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a game from a JSON file.
     * @return the loaded Game object
     */
    public static Game loadGame() {
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader("game.json"))) {
            Game game = gson.fromJson(reader, Game.class);
            // Reconstruct the board's piece positions
            game.getBoard().reconstructPiecePositions();
            return game;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}