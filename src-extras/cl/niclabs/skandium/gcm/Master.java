package cl.niclabs.skandium.gcm;

import cl.niclabs.skandium.gcm.taskheader.TaskHeader;

public interface Master {
	
	public void receiveResult(TaskHeader head);

}
