package com.ms;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.ms.TetrisApplication.Tetrimon.Board;

public class TetrisApplication implements Runnable {

	public static void main(String[] args) {
		TetrisApplication app = new TetrisApplication();
		Thread t = new Thread(app);
		t.start();
	}

	@Override
	public void run() {
		// Create board
		Scanner scanner = new Scanner(System.in);
		Board board = createBoard(scanner);

		// Run game
		while (true) {
			String tetrimon = null;
			int x = -1;
			int y = -1;
			try {
				// getTetrimon Input
				Tetrimon tetrimonObj = getTetrimon();

				// get location input for tetrimon
				System.out.println("Enter Location in format \"X Y\": ");
				x = scanner.nextInt();
				y = scanner.nextInt();

				// Print input
				System.out.println("Input: ");
				tetrimonObj.print();
				System.out.println("Location: " + x + "," + y);

				// Print inital state of board
				board.printBoard();
				// Start filling tetrimon to board
				board.fill(tetrimonObj.shapeFill, x, y);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Invalid Type or Location: [" + tetrimon + " " + x + "," + y + "]");
				continue;
			}
		}
	}

	private Board createBoard(Scanner scanner) {
		Board board = null;
		System.out.println("Enter Board Size in format \"X Y\": ");
		// Initialize board
		int sizeX = scanner.nextInt();
		int sizeY = scanner.nextInt();
		if (sizeX > 0 && sizeY > 0) {
			board = new Board(sizeX, sizeY);
		} else {
			System.out.println("Invalid Board Size: " + sizeX + " " + sizeY);
		}
		return board;
	}

	// Method to get Tetrimon from input
	private Tetrimon getTetrimon() {
		System.out.println("Enter TetriMon Type: ");
		Scanner scanner = new Scanner(System.in);
		String tetrimon = scanner.nextLine();
		Tetrimon tetrimonObj = Enum.valueOf(Tetrimon.class, tetrimon.toUpperCase());
		return tetrimonObj;
	}

	enum Tetrimon {
		I(new int[][] { { 1, 1, 1, 1 } }), L(new int[][] { { 1, 0, 0 }, { 1, 1, 1 } }),
		O(new int[][] { { 1, 1 }, { 1, 1 } }), T(new int[][] { { 1, 1, 1 }, { 0, 1, 0 } });
		public final int[][] shapeFill;

		public int[][] getShapeFill() {
			return shapeFill;
		}

		Tetrimon(int[][] shapeFill) {
			this.shapeFill = shapeFill;
		}

		public void print() {
			for (int i = 0; i < shapeFill.length; i++) {
				for (int j = 0; j < shapeFill[i].length; j++) {
					System.out.print(shapeFill[i][j] + " ");
				}
				System.out.println();
			}

		}

		static class Board {
			int[][] board;

			public Board(int x, int y) {
				board = new int[x][y];
			}

			/*
			 * Validate the Tetrimon location X,Y means the position on which we are trying
			 * to put Tetrimon int[][] Tetrimon shapeFill move to identify if its first
			 * insert or subsequent moves
			 */
			private boolean isValidLocationForTetrimon(int[][] tetrimon, int x, int y, int move) {
				if (x + tetrimon.length > board.length || y + tetrimon[0].length > board[0].length) {
					if (move == 0)
						System.out.println("Invalid location: " + x + " " + y);
					return false;
				}
				for (int i = x; i < x + tetrimon.length; i++) {
					for (int j = y; j < y + tetrimon[0].length; j++) {
						if (board[i][j] == 1 && tetrimon[i - x][j - y] == 1) {
							if (move == 0)
								System.out.println("Invalid location: As Board is already full at provided location "
										+ x + " " + y);
							return false;
						}
					}
				}

				return true;

			}

			/**
			 * This method fill the tetrimon on appropriate location
			 * 
			 * @param tetrimon
			 * @param x
			 * @param y
			 */
			public void fill(int[][] tetrimon, int x, int y) {
				int move = 0;
				while (isValidLocationForTetrimon(tetrimon, x++, y, move)) {
					System.out.println("Moving to: " + (x - 1) + "," + y);
					move++;
				}
				if (move > 0) {
					fillTetrimonOnBoard(tetrimon, x - 2, y);
					printBoard();
					boolean isRemoved = removePair();
					gameCheck();
					if (isRemoved) {
						System.out.println("======After Remove=====");
						printBoard();
					}
				}

			}

			/**
			 * This method checks if any element in first row of board is set to 1, then its
			 * Game over
			 */
			private void gameCheck() {
				for (int j = 0; j < board[0].length; j++) {
					if (board[0][j] == 1) {
						System.out.println("========Game Over========");
						System.exit(0);
					}
				}

			}

			/**
			 * This method checks if any row is all 1 then remove the row and shift
			 * subsequent rows above
			 */
			private boolean removePair() {
				List<Integer> toRemove = new ArrayList<>();
				for (int i = board.length - 1; i >= 0; i--) {
					int r = 1;
					for (int j = 0; j < board[0].length; j++) {
						r &= board[i][j];
					}
					if (r == 1) {
						toRemove.add(i);
					}
				}
				System.out.println("Removing Rows: " + toRemove);

				if (toRemove.size() > 0) {
					for (int i : toRemove) {
						shfit(i);
					}
					return true;
				}
				return false;

			}

			// Array shift utility
			private void shfit(int ind) {
				// System.out.println("here " + ind);
				for (int i = ind; i >= 0; i--) {
					for (int j = 0; j < board[0].length; j++) {
						if (i > 0) {
							// System.out.println("here " + i + j);

							board[i][j] = board[i - 1][j];
							board[i - 1][j] = 0;
						}
					}
				}
			}

			// Fills the board at particular position
			private void fillTetrimonOnBoard(int[][] tetrimon, int x, int y) {
				for (int i = x; i < x + tetrimon.length; i++) {
					for (int j = y; j < y + tetrimon[0].length; j++) {
						board[i][j] |= tetrimon[i - x][j - y];
					}
				}
			}

			// Print board
			public void printBoard() {
				System.out.println("=====Board====");
				for (int i = 0; i < board.length; i++) {
					for (int j = 0; j < board[i].length; j++) {
						System.out.print(board[i][j] + " ");
					}
					System.out.println();
				}
			}

		}
	}
}
