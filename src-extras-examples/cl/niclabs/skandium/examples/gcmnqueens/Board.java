package cl.niclabs.skandium.examples.gcmnqueens;

import java.io.Serializable;

public class Board implements Serializable {

	private static final long serialVersionUID = 1L;
	private int[] board;
	private int solutions;

	//
	// If the Board is of size N then:
	//
	// * board.length = N+1
	// * board[1] ... board[N]: The columns of the board.
	// * board[i] = 0: there are no queens on the i-th column;
	// * board[i] = j: there is a queen on the j-th row of the i-th column;
	//
	// * board[0]: The depth, shows how many rows are deliberately set on some
	// value, in order to divide the problem in sub-problems.
	// Example:
	//
	// _1_ _2_ _3_
	// 1 |_Q_|___|___|
	// 2 |___|___|___| => board = [2,1,3,0]
	// 3 |___|_Q_|___|
	//
	//

	/**
	 * A Chess Board for the NQueens problem
	 * 
	 * @param size
	 *            The size of the board
	 */
	public Board(int size) {
		board = new int[size + 1];
		solutions = 0;
	}

	/**
	 * Puts a queen on the board
	 * 
	 * @param col
	 *            Column for the new queen's location
	 * @param row
	 *            Row for the new queen's location
	 */
	public void putQueen(int col, int row) {
		board[col] = row;
	}

	public void removeQueen(int col) {
		board[col] = 0;
	}

	/**
	 * Given a column, returns the row on which the queen is located. Returns 0
	 * if there is no queen
	 * 
	 * @param col
	 *            A column of the board
	 * @return The row on which the queen is located, 0 if the is no queen.
	 */
	public int getRow(int col) {
		return board[col];
	}

	/**
	 * Returns the size of the board
	 * 
	 * @return The size of the board
	 */
	public int getSize() {
		return board.length - 1;
	}

	public int getDepth() {
		return board[0];
	}

	public void setDepth(int depth) {
		board[0] = depth;
	}

	/**
	 * Makes a copy of this board
	 * 
	 * @return
	 */
	public Board copy() {
		Board copy = new Board(getSize());
		for (int i = 0; i < board.length; i++) {
			copy.board[i] = board[i];
		}
		return copy;
	}

	public void increase() {
		solutions++;
	}

	public void add(Board board) {
		solutions += board.getSolutions();
	}
	
	public int getSolutions() {
		return solutions;
	}
	
}
