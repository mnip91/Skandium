package cl.niclabs.skandium.gcm;

import java.io.Serializable;
import java.util.concurrent.PriorityBlockingQueue;

import cl.niclabs.skandium.system.Task;
import cl.niclabs.skandium.system.TaskExecutor;


public class GCMSTaskExecutor extends TaskExecutor implements Serializable {

	private static final long serialVersionUID = 1L;

	private GCMSkandiumImpl gcmskandium;
	
	/**
	 * 
	 * @param maxThreads
	 * @param workQueue
	 * @param gcmskandium
	 */
	public GCMSTaskExecutor(int maxThreads, GCMSkandiumImpl gcmskandium) {
		super(maxThreads, new PriorityBlockingQueue<Runnable>());
		this.gcmskandium = gcmskandium;
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
