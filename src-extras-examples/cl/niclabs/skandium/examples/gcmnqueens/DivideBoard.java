package cl.niclabs.skandium.examples.gcmnqueens;

import java.util.ArrayList;

import cl.niclabs.skandium.muscles.Split;

public class DivideBoard implements Split<Board, Board> {

	private static final long serialVersionUID = 1L;


	@Override
	public Board[] split(Board board) throws Exception {
		ArrayList<Board> result = new ArrayList<Board>();
		for (int r = 1; r <= board.getSize(); r++) {
			if (Solve.safe(board, board.getDepth() + 1, r)) {
				Board copy = board.copy();
				copy.putQueen(board.getDepth() + 1, r);
				copy.setDepth(board.getDepth() + 1);
				result.add(copy);
			}
		}
		return result.toArray(new Board[result.size()]);
	}

}
