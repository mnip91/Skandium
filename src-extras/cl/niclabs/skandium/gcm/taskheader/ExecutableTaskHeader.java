package cl.niclabs.skandium.gcm.taskheader;

import java.util.Stack;

import cl.niclabs.skandium.instructions.EventInst;
import cl.niclabs.skandium.instructions.Instruction;

public class ExecutableTaskHeader extends AbstractTaskHeader {

	private static final long serialVersionUID = 1L;

	
	public ExecutableTaskHeader(long originId, Object data, Stack<Instruction> instStack) {
		
		this.originId = originId;
		this.data = data;
		
		// Remuevo todos los SkeletonStrace, estos sirve para poder notificar
		// a los listeners, como los del algoritmo de gustabo.
		// Esto es problematico ya que se intentara hacer notificaciones remotas
		// violando algunas leyes de componentes y causando un colapso del
		// programa.
		
		// como skandium esta pensado para aprobechar la memoria compartida,
		// el strace de skeletons solo hace referencia a los skeletons reales,
		// por lo tanto eliminar un listeners desde aqui seria eliminarlo
		// en todos lados.
		//
		// Por ahora, debido a que es mas facil, se elimina todo el skeleton
		// strace, haciendo que los EventInst generados no hagan nada.
		this.stack = new Stack<Instruction>();
		for (Instruction i : instStack) {
			if (i instanceof EventInst)
				continue;
			Instruction newInst = i.copy();
			newInst.removeSkeletonStrace();
			stack.add(newInst);
		}
	}

}
