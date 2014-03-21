package cl.niclabs.skandium.gcm;

import org.objectweb.proactive.core.component.type.annotations.multicast.MethodDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMode;
import cl.niclabs.skandium.gcm.taskheader.TaskHeader;


public interface WorkerMulticast {

	@MethodDispatchMetadata(mode = @ParamDispatchMetadata(mode = ParamDispatchMode.CUSTOM, customMode=TaskDispatch.class))
	//@MethodDispatchMetadata(mode = @ParamDispatchMetadata(mode = ParamDispatchMode.BROADCAST))
	public void doTask(TaskHeader head);

}
