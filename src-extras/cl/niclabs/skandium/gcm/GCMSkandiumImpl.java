package cl.niclabs.skandium.gcm;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.PriorityBlockingQueue;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.component.body.ComponentInitActive;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

import cl.niclabs.skandium.skeletons.Skeleton;
import cl.niclabs.skandium.system.StackBuilder;
import cl.niclabs.skandium.system.Task;


public class GCMSkandiumImpl implements GCMSkandium, 
		SkandiumComponentController, Worker, Master,
		LifeCycleController, BindingController, ComponentInitActive {

	// Client Interfaces
	private Master master;
	private Worker worker;
	private GCMSResultReceiver scrr;
	private String hostname;
	
	// Internal Resources
	private GCMSTaskExecutor executor;
	private DistributionRegistry registry; 
	
	private final boolean VERBOSE = true;

	@Override
	public <P extends Serializable, R extends Serializable> void execute(Skeleton<P, R> skeleton, P param) {
		
		StackBuilder builder = new StackBuilder();
		skeleton.accept(builder);
		
		Task task = new Task(param, builder.stack, executor);
		if(VERBOSE) 
			System.out.println("Task from user accepted on host " + hostname);

		executor.execute(task);
	}

	// As Worker

	@Override
	public void doTask(TaskHead head) {
		
		try {
			Task task = registry.registerReceivedTask(head, executor);
			if(VERBOSE) System.out.println("Task received on host " + hostname);
			executor.execute(task);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public BooleanWrapper isAvailable() {
		//TODO
		return new BooleanWrapper(true);
	}

	// As Master

	@Override
	public void receiveResult(TaskHead head) {
		
		Task task = registry.eraseTransmittedTask(head);
		
		if(task == null) {
			System.err.println("Received result of non transmitted task or " +
					"already received result");
			(new NullPointerException()).printStackTrace();
			return;
		}
		
		if(VERBOSE) System.out.println("Result received on host " + hostname);
		
		if( !task.isRoot() ) {
			task.notifyParent();
			return;
		}
		
		returnFinishedTaskResult(task);
	}

	protected void returnFinishedTaskResult(Task task) {
		
		TaskHead originalHead = registry.eraseReceivedTask(task);
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

		TaskHead head = registry.registerTransmittedTask(task);
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
		executor.setMaximumPoolSize(maxThreads);
	}


	@Override
	public void setDelegationCondition(DelegationCondition condition) {
		executor.setDelegationCondition(condition);
	}
	
	
	// LIFECYCLE CONTROLLER
	
	@Override
	public void startFc() throws IllegalLifeCycleException {
		registry = new DistributionRegistry();
		executor = new GCMSTaskExecutor(Runtime.getRuntime().availableProcessors(), new PriorityBlockingQueue<Runnable>(), this);
	}

	@Override
	public void stopFc() throws IllegalLifeCycleException {
		executor.shutdown();
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
			worker = (Worker) itf;
		} else if(name.compareTo("scrr") == 0) {
			scrr = (GCMSResultReceiver) itf;
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
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			System.out.println("InetAddress.getLocalHost().getHostName(); ==> " + hostname);
		} catch (UnknownHostException e) {
			hostname = "unknown";
			e.printStackTrace();
		}
	}

}
