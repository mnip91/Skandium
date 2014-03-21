package cl.niclabs.skandium.gcm;

import java.util.concurrent.ConcurrentHashMap;

import cl.niclabs.skandium.gcm.taskheader.ExecutableTaskHeader;
import cl.niclabs.skandium.gcm.taskheader.TaskHeader;
import cl.niclabs.skandium.system.Task;

public class DistributionRegistry {

	private ConcurrentHashMap<Long, Task> transmitted;
	private ConcurrentHashMap<Long, TaskHeader> received;
	
	DistributionRegistry() {
		transmitted = new ConcurrentHashMap<Long, Task>();
		received = new ConcurrentHashMap<Long, TaskHeader>();
	}
	
	
	TaskHeader registerTransmittedTask(Task task) {
		
		TaskHeader head = new ExecutableTaskHeader(task.id, task.getP(), task.getStack());
		
		transmitted.put(task.id, task);
		return head;
	}
	
	Task registerReceivedTask(TaskHeader head, GCMSTaskExecutor executor) {
		
		Task task = new Task(head.getData(), head.getStack(), executor);
		received.put(task.id, head);
		return task;
	}
	
	Task eraseTransmittedTask(TaskHeader head) {
		
		Task task =  transmitted.remove(head.getOriginId());
		if(task != null) {
			task.setP(head.getData());
		}
		
		return task;
	}
	
	TaskHeader eraseReceivedTask(Task task) {
		
		return received.remove(task.id);		
	}
}
