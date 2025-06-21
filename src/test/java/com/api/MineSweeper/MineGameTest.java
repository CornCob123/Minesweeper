package com.api.MineSweeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

public class MineGameTest {
    @Autowired
    private MineGame game;

    @BeforeEach
    void setUp() {
        game = new MineGame(4, 0); // 4x4 grid with 0 mines to test logic
    }

    @Test
    void testRevealCellPrivateMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = MineGame.class.getDeclaredMethod("revealCell", int.class, int.class);
        method.setAccessible(true);

        game.getInternalBoard()[0][0] = 1;
        method.invoke(game, 0, 0);

        assertTrue(game.getOpened()[0][0]);
        assertEquals('1', game.getVisibleBoard()[0][0]);
    }

    @Test
    void testRevealCellCascadePrivate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = MineGame.class.getDeclaredMethod("revealCell", int.class, int.class);
        method.setAccessible(true);

        game.getInternalBoard()[0][0] = 0;
        game.getInternalBoard()[0][1] = 0;
        game.getInternalBoard()[1][0] = 0;
        game.getInternalBoard()[1][1] = 0;

        method.invoke(game, 0, 0);

        assertTrue(game.getOpened()[0][0]);
        assertTrue(game.getOpened()[0][1]);
        assertTrue(game.getOpened()[1][0]);
        assertTrue(game.getOpened()[1][1]);
    }

    @Test
    void testRevealCellOutOfBoundsIgnored() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = MineGame.class.getDeclaredMethod("revealCell", int.class, int.class);
        method.setAccessible(true);

        // Should not throw
        method.invoke(game, -1, -1);
        method.invoke(game, 99, 99);

        for (boolean[] row : game.getOpened()) {
            for (boolean cell : row) {
                assertFalse(cell);
            }
        }
    }

    // Additional basic tests remain unchanged
    @Test
    void testDeployMinesRespectsMineCount() {
        MineGame g = new MineGame(5, 5);
        int count = 0;
        for (int[] row : g.getInternalBoard()) {
            for (int cell : row) {
                if (cell == -1) count++;
            }
        }
        assertEquals(5, count);
    }

    @Test
    void testIsVictoryTrueWhenAllSafeRevealed() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MineGame g = new MineGame(2, 1);
        g.getInternalBoard()[0][0] = -1; // set a mine
        g.getInternalBoard()[0][1] = 0;
        g.getInternalBoard()[1][0] = 1;
        g.getInternalBoard()[1][1] = 1;
        g.getOpened()[0][1] = true;
        g.getOpened()[1][0] = true;
        g.getOpened()[1][1] = true;
        Method method = MineGame.class.getDeclaredMethod("isVictory");
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(g);
        assertTrue(result);
    }

    @Test
    void testIsVictoryFalseWhenUnrevealedSafeCells() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MineGame g = new MineGame(2, 1);
        g.getInternalBoard()[0][0] = -1;
        g.getInternalBoard()[0][1] = 0;
        g.getInternalBoard()[1][0] = 1;
        g.getInternalBoard()[1][1] = 1;
        g.getOpened()[1][0] = true;
        g.getOpened()[1][1] = true;
        Method method = MineGame.class.getDeclaredMethod("isVictory");
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(g);
        assertFalse(result);
    }

    @Test
    void testDeployMinesCorrectCount() {
        MineGame g = new MineGame(5, 5);
        int count = 0;
        for (int[] row : g.getInternalBoard()) {
            for (int cell : row) {
                if (cell == -1) count++;
            }
        }
        assertEquals(5, count);
    }

    @Test
    void testVictoryTrueWhenAllSafeRevealed() throws Exception {
        MineGame g = new MineGame(2, 1);
        g.getInternalBoard()[0][0] = -1;
        g.getInternalBoard()[0][1] = 0;
        g.getInternalBoard()[1][0] = 1;
        g.getInternalBoard()[1][1] = 1;
        g.getOpened()[0][1] = true;
        g.getOpened()[1][0] = true;
        g.getOpened()[1][1] = true;

        Method isVictory = MineGame.class.getDeclaredMethod("isVictory");
        isVictory.setAccessible(true);
        assertTrue((Boolean) isVictory.invoke(g));
    }

    @Test
    void testVictoryFalseWhenSafeCellUnrevealed() throws Exception {
        MineGame g = new MineGame(2, 1);
        g.getInternalBoard()[0][0] = -1;
        g.getInternalBoard()[0][1] = 0;
        g.getInternalBoard()[1][0] = 1;
        g.getInternalBoard()[1][1] = 1;
        g.getOpened()[1][0] = true;
        g.getOpened()[1][1] = true;

        Method isVictory = MineGame.class.getDeclaredMethod("isVictory");
        isVictory.setAccessible(true);
        assertFalse((Boolean) isVictory.invoke(g));
    }

    @Test
    void testVisibleBoardInitialized() {
        for (char[] row : game.getVisibleBoard()) {
            for (char c : row) {
                assertEquals('_', c);
            }
        }
    }

    @Test
    void testInternalBoardNotNull() {
        assertNotNull(game.getInternalBoard());
    }

    @Test
    void testOpenedArrayFalseInitially() {
        for (boolean[] row : game.getOpened()) {
            for (boolean b : row) {
                assertFalse(b);
            }
        }
    }

    @Test
    void testCalculateHintsNonMine() throws Exception {
        game.getInternalBoard()[0][0] = -1;
        Method calc = MineGame.class.getDeclaredMethod("computeHints");
        calc.setAccessible(true);
        calc.invoke(game);

        assertTrue(game.getInternalBoard()[0][1] > 0);
    }

    @Test
    void testNoExceptionOnRepeatedReveal() throws Exception {
        Method reveal = MineGame.class.getDeclaredMethod("revealCell", int.class, int.class);
        reveal.setAccessible(true);
        reveal.invoke(game, 0, 0);
        reveal.invoke(game, 0, 0);
        reveal.invoke(game, 0, 0); // Should not throw
    }

    @Test
    void testRevealCornerCell() throws Exception {
        Method reveal = MineGame.class.getDeclaredMethod("revealCell", int.class, int.class);
        reveal.setAccessible(true);
        reveal.invoke(game, 3, 3);
        assertTrue(game.getOpened()[3][3]);
    }

    @Test
    void testRevealEdgeCell() throws Exception {
        Method reveal = MineGame.class.getDeclaredMethod("revealCell", int.class, int.class);
        reveal.setAccessible(true);
        reveal.invoke(game, 0, 2);
        assertTrue(game.getOpened()[0][2]);
    }

    @Test
    void testMineFieldNoNegativeIndexes() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            int test = game.getInternalBoard()[-1][-1];
        });
    }

    @Test
    void testVictoryEmptyBoardTrue() throws Exception {
        MineGame g = new MineGame(2, 0);
        g.getOpened()[0][0] = true;
        g.getOpened()[0][1] = true;
        g.getOpened()[1][0] = true;
        g.getOpened()[1][1] = true;

        Method isVictory = MineGame.class.getDeclaredMethod("isVictory");
        isVictory.setAccessible(true);
        assertTrue((Boolean) isVictory.invoke(g));
    }

    @Test
    void testComputeHintsNoCrashOnAllZero() throws Exception {
        Method calc = MineGame.class.getDeclaredMethod("computeHints");
        calc.setAccessible(true);
        calc.invoke(game);
    }

    @Test
    void testMineGameConstructorNotNullFields() {
        assertNotNull(game);
        assertNotNull(game.getVisibleBoard());
        assertNotNull(game.getOpened());
        assertNotNull(game.getInternalBoard());
    }

    @Test
    void testMineBoardHintValuesBounded() throws Exception {
        game.getInternalBoard()[1][1] = -1;
        Method calc = MineGame.class.getDeclaredMethod("computeHints");
        calc.setAccessible(true);
        calc.invoke(game);

        for (int[] row : game.getInternalBoard()) {
            for (int val : row) {
                assertTrue(val >= -1 && val <= 8);
            }
        }
    }
}
