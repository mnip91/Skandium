package cl.niclabs.skandium.gcm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.objectweb.fractal.api.NoSuchInterfaceException;

import cl.niclabs.skandium.muscles.Muscle;
import cl.niclabs.skandium.skeletons.Skeleton;
import cl.niclabs.skandium.system.Task;
import cl.niclabs.skandium.system.TaskExecutor;

public class GCMSTaskExecutor extends TaskExecutor implements Serializable {

	private static final long serialVersionUID = 1L;

	private GCMSkandiumImpl gcmskandium;
	public DelegationCondition condition;
	public PriorityBlockingQueue<Runnable> workQueue;
	
	/**
	 * 
	 * @param maxThreads
	 * @param workQueue
	 * @param gcmskandium
	 */
	public GCMSTaskExecutor(int maxThreads, PriorityBlockingQueue<Runnable> workQueue, GCMSkandiumImpl gcmskandium) {

		super(maxThreads, workQueue);
		this.workQueue = workQueue;
		this.gcmskandium = gcmskandium;
		this.condition = new DelegationCondition() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean condition(int stackSize, int maxThreads) {
				return false;
			}
		}; 
	}

	/**
	 * 
	 * @param condition
	 */
	public void setDelegationCondition(DelegationCondition condition) {
		this.condition = condition;
	}



	@Override
	public void execute(Runnable r) {
		
		if( !(r instanceof Task) ) return;
		Task task = (Task) r;
		/*
		// Calculate the task esteemed time
		if (gcmskandium.autonomicController != null) {
			Skeleton<?,?>[] trace = task.getStack().get(0).getSkeletonTrace();
			long time = 0;
			for (Muscle<?, ?> m : trace[trace.length - 1].getMuscles()) {
				Long muscleTime = gcmskandium.autonomicController.t.get(m);
				if (muscleTime != null) time += muscleTime.longValue();
			}
			task.setPredictedExecutionTime(time);
		}
		else {
			task.setPredictedExecutionTime(0);
		}
		
		boolean queueOverflow = condition.condition(getQueue().size(), getMaximumPoolSize());
		boolean workersExist = false;
		try {
			workersExist = gcmskandium.lookupFc(GCMSConstants.WORKER_CLIENT_ITF) != null;
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}

		if(queueOverflow && workersExist) {
			Task taskToSend = task;
			long longerTime = task.getPredictedExecutionTime();
			synchronized(workQueue) {
				for (Runnable runnable : workQueue) {
					Task t = (Task) runnable;
					long l = t.getPredictedExecutionTime();
					if (l > longerTime) {
						taskToSend = t;
						longerTime = l;
					}
				}
				if (taskToSend != task) {
					remove(taskToSend);
					super.execute(task);
				}
			}
			System.out.println("Sending task with time: " + longerTime/1000000L + " [ms]");
			gcmskandium.sendTask(taskToSend);
			return;
		}*/
		super.execute(task);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		
		if(t != null) t.printStackTrace();
		if( !(r instanceof Task) ) return;
		
		Task task = (Task) r;
		
		synchronized(task) {
		
			if( !task.isFinished() )
				return;
	
			if( ! task.isNotified() ) {
				
				if( !task.isRoot() ) {
					task.notifyParent();
				}
				else {
					gcmskandium.returnFinishedTaskResult(task);
				}
				
				task.setAsNotified();
			}
		}
	}
}
