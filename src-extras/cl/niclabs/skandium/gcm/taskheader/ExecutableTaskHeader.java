package cl.niclabs.skandium.gcm.taskheader;

import java.util.Stack;
import cl.niclabs.skandium.instructions.Instruction;

public class ExecutableTaskHeader extends AbstractTaskHeader {

	private static final long serialVersionUID = 1L;

	
	public ExecutableTaskHeader(long originId, Object data, Stack<Instruction> stack) {
		
		this.originId = originId;
		this.data = data;
		this.stack = stack;
	}

}
