package com.api.MineSweeper;

import java.util.Scanner;

public class MineSweeper {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to Mine Sweeper!\n");

        while (true) {
            System.out.print("Enter the size of the grid (e.g. 4 for a 4x4 grid): ");
            int gridSize = input.nextInt();
            int maxMines = (int) (gridSize * gridSize * 0.35);
            System.out.printf("Enter the number of mines to place on the grid (maximum is 35%% of the total squares) %d:",maxMines);
            int totalMines = input.nextInt();
            input.nextLine();

            MineGame session = new MineGame(gridSize, totalMines);
            session.start(input);

            System.out.println("Press any key to play again...");
            input.nextLine();
        }
    }
}

