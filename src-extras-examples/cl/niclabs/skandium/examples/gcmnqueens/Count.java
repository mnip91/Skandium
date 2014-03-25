package cl.niclabs.skandium.examples.gcmnqueens;

import java.io.Serializable;

public class Count implements Serializable {

	private static final long serialVersionUID = 1L;
	private int n;
	
	public Count() {
		n = 0;
	}
	
	public void increase() {
		n++;
	}

	public void add(Count count) {
		this.n += count.getValue();
	}
	
	public int getValue() {
		return n;
	}

}
