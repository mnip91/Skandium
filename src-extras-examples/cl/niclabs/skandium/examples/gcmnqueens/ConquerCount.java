package cl.niclabs.skandium.examples.gcmnqueens;

import cl.niclabs.skandium.muscles.Merge;

public class ConquerCount implements Merge<Board, Board> {

	private static final long serialVersionUID = 1L;
	
	
	@Override
	public Board merge(Board[] boards) throws Exception {
		Board result = new Board(boards[0].getSize());
		for (Board b : boards) {
			result.add(b);
		}
		return result;
	}

}
