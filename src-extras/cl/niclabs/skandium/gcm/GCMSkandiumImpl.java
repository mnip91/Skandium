package cl.niclabs.skandium.gcm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.component.body.ComponentInitActive;
import org.objectweb.proactive.extensions.dataspaces.Utils;

import cl.niclabs.skandium.Skandium;
import cl.niclabs.skandium.autonomic.AutonomicThreads;
import cl.niclabs.skandium.gcm.taskheader.NullTaskHeader;
import cl.niclabs.skandium.gcm.taskheader.TaskHeader;
import cl.niclabs.skandium.skeletons.Skeleton;
import cl.niclabs.skandium.system.StackBuilder;
import cl.niclabs.skandium.system.Task;
import cl.niclabs.skandium.system.TaskExecutor;


public class GCMSkandiumImpl implements GCMSkandium, 
		SkandiumComponentController, Worker, Master,
		LifeCycleController, BindingController, ComponentInitActive {

	private static final long serialVersionUID = 1L;

	// Client Interfaces
	private Master master;
	private WorkerMulticast worker;
	private ResultReceiver scrr;
	
	// Internal Resources
	private GCMSTaskExecutor gcmexecutor;
	private DistributionRegistry registry; 
	
	private final boolean VERBOSE = true;

	@Override
	public <P extends Serializable, R extends Serializable> void execute(Skeleton<P, R> skeleton, P param) {

		class InternalGCMSkandium extends Skandium {
			public void setExecutor(TaskExecutor te) { executor = te; }
 		}
		
		InternalGCMSkandium igcms = new InternalGCMSkandium();
		igcms.setExecutor(gcmexecutor);
		AutonomicThreads.start(igcms, skeleton);
		
		StackBuilder builder = new StackBuilder();
		skeleton.accept(builder);
		
		Task task = new Task(param, builder.stack, gcmexecutor);
		if(VERBOSE) {
			System.out.println("Task from user accepted on " + Utils.getHostname());
		}

		gcmexecutor.execute(task);
	}

	// As Worker

	@Override
	public void doTask(TaskHeader head) {
		
		if(head instanceof NullTaskHeader) {
			//if(VERBOSE) {
			//	System.out.println("NullTask received on " + Utils.getHostname());
			//}
			return;
		}
	
		try {
			Task task = registry.registerReceivedTask(head, gcmexecutor);
			if(VERBOSE) {
				System.out.println("Task received on " + Utils.getHostname());
			}
			gcmexecutor.execute(task);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	// As Master

	@Override
	public void receiveResult(TaskHeader head) {
		
		if(head instanceof NullTaskHeader) {
			return;
		}

		Task task = registry.eraseTransmittedTask(head);
		
		if(task == null) {
			System.err.println("Received result of non transmitted task or " +
					"already received result");
			(new NullPointerException()).printStackTrace();
			return;
		}
		
		if(VERBOSE) System.out.println("Result received on " + Utils.getHostname());
		
		if( !task.isRoot() ) {
			task.notifyParent();
			return;
		}
		
		returnFinishedTaskResult(task);
	}

	protected void returnFinishedTaskResult(Task task) {
		
		TaskHeader originalHead = registry.eraseReceivedTask(task);
		try {
			if(originalHead == null) {
	
				if(VERBOSE) System.out.println("Returning Execution Result...");
				scrr.receive(task.getP());
			}
			else {
				originalHead.setData(task.getP());
				master.receiveResult(originalHead);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendTask(Task task) {

		TaskHeader head = registry.registerTransmittedTask(task);
		try {
			worker.doTask(head);
			task.getStack().clear();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
	// SKANDIUMCOMPONENT CONTROLLER
	
	
	@Override
	public void setMaxThreads(int maxThreads) {
		gcmexecutor.setMaximumPoolSize(maxThreads);
	}


	@Override
	public void setDelegationCondition(DelegationCondition condition) {
		gcmexecutor.setDelegationCondition(condition);
	}
	
	
	// LIFECYCLE CONTROLLER
	
	@Override
	public void startFc() throws IllegalLifeCycleException {
		registry = new DistributionRegistry();
		gcmexecutor = new GCMSTaskExecutor(Runtime.getRuntime().availableProcessors(), new PriorityBlockingQueue<Runnable>(), this);
	}

	@Override
	public void stopFc() throws IllegalLifeCycleException {
		gcmexecutor.shutdown();
	}
	
	@Override
	public String getFcState() {
		return null;
	}
	
	
	// BINDING CONTROLLER
	
	
	@Override
	public void bindFc(String name, Object itf) throws NoSuchInterfaceException {
		
		if(name.compareTo(GCMSConstants.MASTER_CLIENT_ITF) == 0) {
			master = (Master) itf;
		} else if(name.compareTo(GCMSConstants.WORKER_CLIENT_ITF) == 0) {
			worker = (WorkerMulticast) itf;
		} else if(name.compareTo("scrr") == 0) {
			scrr = (ResultReceiver) itf;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public String[] listFc() {
		return new String[] { GCMSConstants.MASTER_CLIENT_ITF, GCMSConstants.WORKER_CLIENT_ITF, "scrr" };
	}

	@Override
	public Object lookupFc(String name) throws NoSuchInterfaceException {
		
		if(name.compareTo(GCMSConstants.MASTER_CLIENT_ITF) == 0) {
			return master;
		} else if(name.compareTo(GCMSConstants.WORKER_CLIENT_ITF) == 0) {
			return worker;
		} else if(name.compareTo("scrr") == 0) {
			return scrr;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public void unbindFc(String name) throws NoSuchInterfaceException {		
		if(name.compareTo(GCMSConstants.MASTER_CLIENT_ITF) == 0) {
			master = null;
		} 
		else if(name.compareTo(GCMSConstants.WORKER_CLIENT_ITF) == 0) {
			worker = null;
		} else if(name.compareTo("scrr") == 0) {
			scrr = null;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}


	@Override
	public void initComponentActivity(Body arg0) {
		System.out.println("GCMSkandium initiated on " + Utils.getHostname());
	}

}
