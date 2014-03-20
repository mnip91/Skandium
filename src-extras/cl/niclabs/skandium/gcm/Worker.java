package cl.niclabs.skandium.gcm;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

public interface Worker {

	public BooleanWrapper isAvailable();
	
	public void doTask(TaskHead head);

}
