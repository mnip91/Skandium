package cl.niclabs.skandium.gcm;

import java.util.concurrent.ConcurrentHashMap;

import cl.niclabs.skandium.system.Task;

public class DistributionRegistry {

	private ConcurrentHashMap<Long, Task> transmitted;
	private ConcurrentHashMap<Long, TaskHead> received;
	
	DistributionRegistry() {
		transmitted = new ConcurrentHashMap<Long, Task>();
		received = new ConcurrentHashMap<Long, TaskHead>();
	}
	
	
	TaskHead registerTransmittedTask(Task task) {
		
		TaskHead head = new TaskHead(task.id, task.getP(), task.getStack());
		
		transmitted.put(task.id, task);
		return head;
	}
	
	Task registerReceivedTask(TaskHead head, GCMSTaskExecutor executor) {
		
		Task task = new Task(head.getData(), head.getStack(), executor);
		received.put(task.id, head);
		return task;
	}
	
	Task eraseTransmittedTask(TaskHead head) {
		
		Task task =  transmitted.remove(head.getOriginId());
		if(task != null) {
			task.setP(head.getData());
		}
		
		return task;
	}
	
	TaskHead eraseReceivedTask(Task task) {
		
		return received.remove(task.id);		
	}
}
