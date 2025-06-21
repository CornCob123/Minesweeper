package com.api.MineSweeper;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import lombok.Data;

@Data
public class MineGame {
    private final int gridSize;
    private final int mineTotal;
    private final char[][] visibleBoard;
    private final int[][] internalBoard;
    private final boolean[][] opened;
    private boolean firstBoard = true;
    private boolean isGameOver = false;

    public MineGame(int gridSize, int mineTotal)
    {
        this.gridSize = gridSize;
        this.mineTotal = mineTotal;
        this.visibleBoard = new char[gridSize][gridSize];
        this.internalBoard = new int[gridSize][gridSize];
        this.opened = new boolean[gridSize][gridSize];
        initializeBoard();
    }

    private void initializeBoard()
    {
        for (char[] row : visibleBoard)
        {
            Arrays.fill(row, '_');
        }
        deployMines();
        computeHints();
    }

    private void deployMines()
    {
        Random rng = new Random();
        int count = 0;
        while (count < mineTotal)
        {
            int row = rng.nextInt(gridSize);
            int col = rng.nextInt(gridSize);
            if (internalBoard[row][col] != -1)
            {
                internalBoard[row][col] = -1;
                count++;
            }
        }
    }

    private void computeHints() {
        /*  c is current cell (dx, dy)-> goes to (dx + x , dy + y) check all neighbouring cells
        *    -1 0 1
        *  -1 x x x
        *   0 x c x
        *   1 x x x
        * */
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                if (internalBoard[x][y] == -1) continue;
                int nearbyMines = 0;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int nx = x + dx, ny = y + dy;
                        if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize && internalBoard[nx][ny] == -1) {
                            nearbyMines++;
                        }
                    }
                }
                internalBoard[x][y] = nearbyMines;
            }
        }
    }

    public void start(Scanner scanner) {
        while (!isGameOver) {
            displayBoard();
            System.out.println(" ");
            System.out.print("Select a square to reveal (e.g. A1): ");
            String command = scanner.nextLine().toUpperCase();
            if (command.length() < 2) continue;
            int row = command.charAt(0) - 'A';
            int col = Integer.parseInt(command.substring(1)) - 1;

            if (row < 0 || row >= gridSize || col < 0 || col >= gridSize || opened[row][col]) {
                System.out.println("Invalid or already revealed spot. Try again.");
                continue;
            }

            revealCell(row, col);
            if (internalBoard[row][col] == -1) {
                System.out.println("Oh no, you detonated a mine! Game over.");
                isGameOver = true;
                return;
            }
            else{
                System.out.printf("This square contains %d adjacent mines.", internalBoard[row][col]);
                System.out.println(" ");
            }

            if (isVictory()) {
                displayBoard();
                System.out.println("Congratulations, you have won the game!");
                isGameOver = true;
            }
        }
    }

    private void revealCell(int row, int col) {
        //base case if cell is opened or if out of bounds return
        if (row < 0 || row >= gridSize || col < 0 || col >= gridSize || opened[row][col]) return;
        opened[row][col] = true;

        if (internalBoard[row][col] == 0) {
            // reveal neighbouring cells via recursive call if current cell has 0 adjacent mines
            visibleBoard[row][col] = '0';
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    revealCell(row + dx, col + dy);
                }
            }
        } else {
            //change visible board for current cell and don't recursive call
            //This line converts an integer (e.g., number of adjacent mines) from your internalBoard into the corresponding character, and stores it in visibleBoard.
            //'0' + integer  converts to numeric ASCII
            visibleBoard[row][col] = (char) ('0' + internalBoard[row][col]);
        }
    }
    private void DisplayBoardMessage(){
        if(isFirstBoard()){
            System.out.println("\nHere is your minefield:");
            setFirstBoard(false);
        }else{
            System.out.println("\nHere is your updated minefield:");
        }
    }
    private void displayBoard() {
        DisplayBoardMessage();
        System.out.print("  ");
        for (int i = 1; i <= gridSize; i++) System.out.print(i + " ");
        System.out.println();
        for (int r = 0; r < gridSize; r++) {
            System.out.print((char) ('A' + r) + " ");
            for (int c = 0; c < gridSize; c++) {
                System.out.print(visibleBoard[r][c] + " ");
            }
            System.out.println();
        }
    }

    private boolean isVictory() {
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                if (internalBoard[x][y] != -1 && !opened[x][y]) return false;
            }
        }
        return true;
    }
}
