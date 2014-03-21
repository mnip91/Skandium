package cl.niclabs.skandium.gcm;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatch;

import cl.niclabs.skandium.gcm.taskheader.NullTaskHeader;
import cl.niclabs.skandium.gcm.taskheader.TaskHeader;

public class TaskDispatch implements ParamDispatch {

	private static int counter = 0;
	private static int nbOutputReceivers = 0;
	private static List<Object> output;
	private static TaskHeader NULL_TH = new NullTaskHeader();
	
	@Override
	public int expectedDispatchSize(Object arg0, int arg1)
			throws ParameterDispatchException {
		int result = arg1;
		return result;
	}

	@Override
	public boolean match(Type arg0, Type arg1)
			throws ParameterDispatchException {
		return true;
	}

	@Override
	public List<Object> partition(Object inputParameter, int nbOutputReceivers) throws ParameterDispatchException {
		

		if (this.nbOutputReceivers != nbOutputReceivers) {
			this.nbOutputReceivers = nbOutputReceivers;
			output = new ArrayList<Object>();
			for (int i = 0; i <= nbOutputReceivers; i++) {
				output.add(NULL_TH);
			}
		}
		output.set(counter, NULL_TH);
		counter = (counter + 1) % nbOutputReceivers;
		output.set(counter, inputParameter);
		
		return output;
	}

}
