package cl.niclabs.skandium.examples.gcmnqueens;

import java.util.ArrayList;

import cl.niclabs.skandium.muscles.Split;

public class DivideBoardV2 implements Split<Board, Board> {

	private static final long serialVersionUID = 1L;


	/**
	 * Since AutonomicThread fails under two MAPs, i.e. Map(Split, Map, Merge),
	 * I create this version of DivideBoard that divides two times the board, 
	 * simulating a normal version of NQueens using depth = 2.
	 * 
	 */
	@Override
	public Board[] split(Board board) throws Exception {
		Board[] aux = internalSplit(board);
		ArrayList<Board> result = new ArrayList<Board>();
		for (Board b : aux) {
			for (Board sb : internalSplit(b)) {
				result.add(sb);
			}
		}
		return result.toArray(new Board[result.size()]);
	}

	private Board[] internalSplit(Board board) throws Exception {
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
