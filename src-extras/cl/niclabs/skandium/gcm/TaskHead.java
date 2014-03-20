package cl.niclabs.skandium.gcm;

import java.io.Serializable;
import java.util.Stack;

import cl.niclabs.skandium.instructions.Instruction;

public class TaskHead implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long originId;
	private Object data;
	private Stack<Instruction> stack;
	
	public TaskHead(long originId, Object data, Stack<Instruction> stack) {
		
		this.originId = originId;
		this.data = data;
		this.stack = stack;
	}

	public Object getOriginId() {
		return originId;
	}
	
	Object getData() {
		return data;
	}
	
	public void setData(Object p) {
		data = p;
	}

	Stack<Instruction> getStack() {
		return stack;
	}
}
