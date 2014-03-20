package cl.niclabs.skandium.gcm;

import java.util.concurrent.PriorityBlockingQueue;

import cl.niclabs.skandium.system.Task;
import cl.niclabs.skandium.system.TaskExecutor;

public class GCMSTaskExecutor extends TaskExecutor {

	private GCMSkandiumImpl gcmskandium;
	private DelegationCondition condition;
	
	/**
	 * 
	 * @param maxThreads
	 * @param workQueue
	 * @param gcmskandium
	 */
	public GCMSTaskExecutor(int maxThreads, PriorityBlockingQueue<Runnable> workQueue, GCMSkandiumImpl gcmskandium) {

		super(maxThreads, workQueue);
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
		try {
			if( condition.condition(getQueue().size(), getMaximumPoolSize())
					&& gcmskandium.lookupFc(GCMSConstants.WORKER_CLIENT_ITF) != null) {
				
				gcmskandium.sendTask(task);
				return;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		super.execute(task);
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
