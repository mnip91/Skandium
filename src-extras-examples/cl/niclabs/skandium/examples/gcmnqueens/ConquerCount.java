package cl.niclabs.skandium.examples.gcmnqueens;

import cl.niclabs.skandium.muscles.Merge;

public class ConquerCount implements Merge<Count, Count> {

	private static final long serialVersionUID = 1L;
	
	
	@Override
	public Count merge(Count[] counts) throws Exception {
		Count count = new Count();
		for (Count c : counts) {
			count.add(c);
		}
		return count;
	}

}
