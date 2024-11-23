import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;
import game.*;

import javax.swing.*;
import java.io.File;

public class CheckersJUnitTest {
    private CheckerBoard board;
    private CheckerPiece redPiece;
    private CheckerPiece blackPiece;
    private CheckerPiece redKing;
    private CheckerPiece blackKing;

    @Before
    public void setUp() {
        board = new CheckerBoard();
        redPiece = new CheckerPiece("RED");
        blackPiece = new CheckerPiece("BLACK");
        redKing = new CheckerPiece("RED");
        redKing.crown();
        blackKing = new CheckerPiece("BLACK");
        blackKing.crown();
    }

    @Test
    public void testCheckerPieceColor() {
        assertEquals("RED", redPiece.getColor());
        assertEquals("BLACK", blackPiece.getColor());
    }

    @Test
    public void testCheckerPieceIsKing() {
        assertFalse(redPiece.isKing());
        assertFalse(blackPiece.isKing());
        assertTrue(redKing.isKing());
        assertTrue(blackKing.isKing());
    }

    @Test
    public void testCheckerPieceCrown() {
        redPiece.crown();
        blackPiece.crown();
        assertTrue(redPiece.isKing());
        assertTrue(blackPiece.isKing());
    }

    @Test
    public void testCheckerPiecePossibleMoves() {
        List<int[]> moves = new ArrayList<>();
        moves.add(new int[]{1, 1});
        moves.add(new int[]{1, -1});
        redPiece.setList(moves);
        assertEquals(moves, redPiece.getList());
    }

    @Test
    public void testDefaultTableSetup() {
        CheckerPiece piece;
        for (int i = 0; i < 3; i++) {
            for (int j = (i % 2); j < 8; j += 2) {
                piece = board.getPiece(i, j);
                assertNotNull(piece);
                assertEquals("BLACK", piece.getColor());
            }
        }
        for (int i = 5; i < 8; i++) {
            for (int j = (i % 2); j < 8; j += 2) {
                piece = board.getPiece(i, j);
                assertNotNull(piece);
                assertEquals("RED", piece.getColor());
            }
        }
    }

    @Test
    public void testCheckerBoardGetPiece() {
        CheckerPiece piece = board.getPiece(0, 0);
        assertNotNull(piece);
        assertEquals("BLACK", piece.getColor());
        piece = board.getPiece(7, 7);
        assertNotNull(piece);
        assertEquals("RED", piece.getColor());
        piece = board.getPiece(4, 4);
        assertNull(piece);
    }

    @Test
    public void testGame() {
        Game game = new Game();
        assertEquals("RED", game.getCurrentPlayer());
        game.switchPlayer();
        assertEquals("BLACK", game.getCurrentPlayer());
        game.switchPlayer();
        assertEquals("RED", game.getCurrentPlayer());
    }

    @Test
    public void testGameMove() {
        Game game = new Game();
        game.move(2, 1, 3, 0);
        assertNull(game.getBoard().getPiece(2, 1));
        assertEquals("RED", game.getCurrentPlayer());
        game.move(5, 0, 4, 1);
        assertNull(game.getBoard().getPiece(5, 0));
        assertEquals("RED", game.getCurrentPlayer());
    }

    @Test
    public void testFileSave() {
        Game game = new Game();
        game.move(2, 1, 3, 0);
        game.move(5, 0, 4, 1);
        game.saveGame();

        File savedFile = new File("game.json");
        assertTrue(savedFile.exists());
    }

    @Test
    public void testGraphic() {
        //create a window and check if it is visible
        SwingUtilities.invokeLater(() -> new GameFrame().setVisible(true));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertFalse(JFrame.getFrames()[0].isResizable());

    }

}