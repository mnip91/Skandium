package cl.niclabs.skandium.examples.gcmnqueens;

import cl.niclabs.skandium.muscles.Condition;

public class ShouldDivide implements Condition<Board> {

	private static final long serialVersionUID = 1L;
	private int THRESHOLD;

	public ShouldDivide(int threshold) {
		THRESHOLD = threshold;
	}

	@Override
	public boolean condition(Board board) throws Exception {
		return board.getDepth() < THRESHOLD && board.getDepth() < board.getSize();
	}

}
