package cl.niclabs.skandium.examples.gcmnqueens;

import cl.niclabs.skandium.muscles.Execute;

public class Solve implements Execute<Board, Board> {

	private static final long serialVersionUID = 1L;

	@Override
	public Board execute(Board board) throws Exception {
		return place(board, board.getDepth() + 1);
	}

	private Board place(Board board, int col) {
		Board result = new Board(board.getSize());
		for (int row = 1; row <= board.getSize(); row++) {
			if (safe(board, col, row)) {
				if (col == board.getSize()) {
					result.increase();
					break;
				}
				else {
					board.putQueen(col, row);
					result.add(place(board, col + 1));
					board.removeQueen(col);
				}
			}
		}
		return result;
	}

	static boolean safe(Board board, int col, int row) {
		for (int c = 1; c < col; c++) {
			if (board.getRow(col-c) == row || board.getRow(col-c) == row-c || board.getRow(col-c) == row+c) {
				return false;
			}
		}
		return true;
	}

}
