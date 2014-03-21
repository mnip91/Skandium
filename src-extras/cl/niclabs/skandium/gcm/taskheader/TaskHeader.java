package cl.niclabs.skandium.gcm.taskheader;

import java.io.Serializable;
import java.util.Stack;

import cl.niclabs.skandium.instructions.Instruction;

public interface TaskHeader extends Serializable {

	public Object getData();
	public Object getOriginId();
	public Stack<Instruction> getStack();
	public void setData(Object p);
}
