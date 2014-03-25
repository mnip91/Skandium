package cl.niclabs.skandium.examples.gcmnqueens;

import cl.niclabs.skandium.muscles.Execute;

public class Solve implements Execute<Board, Count> {

	private static final long serialVersionUID = 1L;

	@Override
	public Count execute(Board board) throws Exception {
		return place(board, board.getDepth() + 1);
	}

	private Count place(Board board, int col) {
		Count count = new Count();
		for (int row = 1; row <= board.getSize(); row++) {
			if (safe(board, col, row)) {
				if (col == board.getSize()) {
					count.increase();
					break; // --- ??
				}
				else {
					board.putQueen(col, row);
					count.add(place(board, col + 1));
					board.removeQueen(col);
				}
			}
		}
		return count;
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
