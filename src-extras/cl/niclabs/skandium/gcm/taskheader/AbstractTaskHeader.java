package cl.niclabs.skandium.gcm.taskheader;

import java.util.Stack;

import cl.niclabs.skandium.instructions.Instruction;

public abstract class AbstractTaskHeader implements TaskHeader {

	private static final long serialVersionUID = 1L;

	long originId;
	Object data;
	Stack<Instruction> stack;


	public Object getData() {
		return data;
	}

	public Object getOriginId() {
		return originId;
	}

	public Stack<Instruction> getStack() {
		return stack;
	}

	public void setData(Object p) {
		this.data = p;
	}

}
