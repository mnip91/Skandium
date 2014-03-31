package cl.niclabs.skandium.examples.gcmnqueens;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.control.PABindingController;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactory;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import cl.niclabs.skandium.Skandium;
import cl.niclabs.skandium.Stream;
import cl.niclabs.skandium.autonomic.AutonomicThreads;
import cl.niclabs.skandium.gcm.DelegationCondition;
import cl.niclabs.skandium.gcm.GCMSConstants;
import cl.niclabs.skandium.gcm.GCMSkandium;
import cl.niclabs.skandium.gcm.GCMSkandiumBuilder;
import cl.niclabs.skandium.gcm.ResultReceiver;
import cl.niclabs.skandium.gcm.SkandiumComponentController;
import cl.niclabs.skandium.muscles.Muscle;
import cl.niclabs.skandium.skeletons.Map;
import cl.niclabs.skandium.skeletons.While;


public class Main {

	static public void main(String[] args) throws Exception {

		int BOARD_SIZE = 17;
		int DEPTH = 1;
		boolean GCM_MODE = false;
		int STACK_SIZE = 8;
	
    	if(args.length != 0) {
    		BOARD_SIZE   = Integer.parseInt(args[0]);
    		DEPTH   = Integer.parseInt(args[1]);
    		GCM_MODE = Boolean.parseBoolean(args[2]);
    		STACK_SIZE = Integer.parseInt(args[3]);
    	}
    	        
        if(GCM_MODE) {
        	gcmExecution(BOARD_SIZE, DEPTH, STACK_SIZE);
        }
        else {
        	normalExecution(BOARD_SIZE, DEPTH);
        }
	}
	
	private static void gcmExecution(int BOARD_SIZE, int DEPTH, final int STACK_SIZE) throws Exception {
		String descriptorPath = "file:///run/netsop/u/sop-nas2a/vol/home_oasis/mibanez/"
				+ "Workspace/Skandium/src-extras-examples/cl/niclabs/skandium/gcm/examples/GCMApp.xml";
		
		descriptorPath = (new URL(descriptorPath)).toURI().getPath();
		File appDescriptor = new File(descriptorPath);
		
		GCMApplication gcmad;
		gcmad = PAGCMDeployment.loadApplicationDescriptor(appDescriptor);
		gcmad.startDeployment();
		gcmad.waitReady();
		
		GCMVirtualNode VN1 = gcmad.getVirtualNode("VN1");
		GCMVirtualNode VN2 = gcmad.getVirtualNode("VN2");
		GCMVirtualNode VN3 = gcmad.getVirtualNode("VN3");
		VN1.waitReady();
		VN2.waitReady();
		VN3.waitReady();
	    
	    Node N1example = VN1.getANode();
	    Node N1sc = VN1.getANode();
	    Node N2sc = VN2.getANode();
	    Node N3sc = VN3.getANode();

	    // Components
		Component boot = Utils.getBootstrapComponent();
	    PAGCMTypeFactory tf = Utils.getPAGCMTypeFactory(boot);
	    PAGenericFactory gf = Utils.getPAGenericFactory(boot);
	    
	    ComponentType tExample = tf.createFcType(new InterfaceType[] {
	    		tf.createGCMItfType("nqueens", NQueens.class.getName(), GCMTypeFactory.SERVER,GCMTypeFactory.MANDATORY, GCMTypeFactory.SINGLETON_CARDINALITY),
		    	tf.createGCMItfType(GCMSConstants.GCMSKANDIUM_ITF, GCMSkandium.class.getName(), GCMTypeFactory.CLIENT, GCMTypeFactory.MANDATORY, GCMTypeFactory.SINGLETON_CARDINALITY),
				tf.createGCMItfType("scrr", ResultReceiver.class.getName(), GCMTypeFactory.SERVER, GCMTypeFactory.MANDATORY, GCMTypeFactory.SINGLETON_CARDINALITY)
	    });
	    Component exampleComp = gf.newFcInstance(tExample, new ControllerDescription("exampleComp", Constants.PRIMITIVE), new ContentDescription(NQueensImpl.class.getName()), N1example);
	    
	    Component SC1 = GCMSkandiumBuilder.build("SC1", N1sc);
	    Component SC2 = GCMSkandiumBuilder.build("SC2", N2sc);
	    Component SC3 = GCMSkandiumBuilder.build("SC3", N3sc);

        PABindingController bcExample = Utils.getPABindingController(exampleComp);
        bcExample.bindFc(GCMSConstants.GCMSKANDIUM_ITF, SC1.getFcInterface(GCMSConstants.GCMSKANDIUM_ITF));
        
        PABindingController bcSC1 = Utils.getPABindingController(SC1);
        bcSC1.bindFc(GCMSConstants.WORKER_CLIENT_ITF, SC2.getFcInterface(GCMSConstants.WORKER_SERVER_ITF));
        bcSC1.bindFc(GCMSConstants.WORKER_CLIENT_ITF, SC3.getFcInterface(GCMSConstants.WORKER_SERVER_ITF));
        bcSC1.bindFc("scrr", exampleComp.getFcInterface("scrr"));

        PABindingController bcSC2 = Utils.getPABindingController(SC2);
        bcSC2.bindFc(GCMSConstants.MASTER_CLIENT_ITF, SC1.getFcInterface(GCMSConstants.MASTER_SERVER_ITF));
        
        PABindingController bcSC3 = Utils.getPABindingController(SC3);
        bcSC3.bindFc(GCMSConstants.MASTER_CLIENT_ITF, SC1.getFcInterface(GCMSConstants.MASTER_SERVER_ITF));
        
        Utils.getPAGCMLifeCycleController(exampleComp).startFc();
        Utils.getPAGCMLifeCycleController(SC1).startFc();
        Utils.getPAGCMLifeCycleController(SC2).startFc();
        Utils.getPAGCMLifeCycleController(SC3).startFc();
        
        SkandiumComponentController scc = (SkandiumComponentController) SC1.getFcInterface("scc");
        SkandiumComponentController scc2 = (SkandiumComponentController) SC2.getFcInterface("scc");
        SkandiumComponentController scc3 = (SkandiumComponentController) SC2.getFcInterface("scc");
        
        DelegationCondition cond = new DelegationCondition() {
			private static final long serialVersionUID = 2L;
        	public boolean condition(int stackSize, int maxThreads) {
        		return stackSize > STACK_SIZE;
        	}
        };

        scc.setDelegationCondition(cond);
        scc2.setDelegationCondition(cond);
        scc3.setDelegationCondition(cond);

        NQueens example = (NQueens) exampleComp.getFcInterface("nqueens");
		example.setParameters(BOARD_SIZE, DEPTH);
		example.run();
	}

	static private void normalExecution(int BOARD_SIZE, int DEPTH) throws InterruptedException, ExecutionException {

		
		System.out.println("iniciando normal ejecucion " + BOARD_SIZE + " " + DEPTH);
		Map<Board, Board> skel = new Map<Board, Board>(new DivideBoardV2(), new Solve(), new ConquerCount());
   	 	Skandium skandium = new Skandium(Runtime.getRuntime().availableProcessors());
		//AutonomicThreads.start(skandium, skel,
		//		AutonomicThreads.DEFAULT_POLL_CHECK,
		//		new HashMap<Muscle<?, ?>, Long>(),
		//		new HashMap<Muscle<?, ?>, Integer>(), AutonomicThreads.RHO,
		//		AutonomicThreads.DEFAULT_WALL_CLOCK_TIME_GOAL,
		//		Runtime.getRuntime().availableProcessors() * 2, true);  	 
   	 	//Thread.sleep(2000);
		long init = System.currentTimeMillis();
		Stream<Board, Board> stream = skandium.newStream(skel);
        Future<Board> future = stream.input(new Board(BOARD_SIZE));
        Board result = future.get();
        System.out.println(result.getSolutions() +" in "+(System.currentTimeMillis() - init)+"[ms]");
        
        skandium.shutdown();
	}

}
