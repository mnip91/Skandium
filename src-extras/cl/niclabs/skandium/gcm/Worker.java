package cl.niclabs.skandium.gcm;

import cl.niclabs.skandium.gcm.taskheader.TaskHeader;


public interface Worker {
	
	public void doTask(TaskHeader head);

}
