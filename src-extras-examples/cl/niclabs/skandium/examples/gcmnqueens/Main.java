package cl.niclabs.skandium.examples.gcmnqueens;

import java.io.File;
import java.net.URL;
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
import cl.niclabs.skandium.gcm.DelegationCondition;
import cl.niclabs.skandium.gcm.GCMSConstants;
import cl.niclabs.skandium.gcm.GCMSkandium;
import cl.niclabs.skandium.gcm.GCMSkandiumBuilder;
import cl.niclabs.skandium.gcm.ResultReceiver;
import cl.niclabs.skandium.gcm.SkandiumComponentController;
import cl.niclabs.skandium.muscles.Muscle;
import cl.niclabs.skandium.skeletons.DaC;
import cl.niclabs.skandium.skeletons.Map;
import cl.niclabs.skandium.skeletons.Skeleton;
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
    	
        //Controller ctrl = new Controller(0.5d,2d,0.5d,1000000l,true);
		
		//We use a divide and conquer skeleton pattern

    	Skeleton<Board, Count> subskel = new DaC<Board, Count>(   
   			 new ShouldDivide(DEPTH),
   			 new DivideBoard(), 
   			 new Solve(), 
   			 new ConquerCount());

    	
   	 	//Always subdivide the first row.
   	 	Map<Board, Count> nqueens = new Map<Board, Count>(new DivideBoard(), subskel, new ConquerCount());
   	 	
   	 	
   	 	//nqueens.addGeneric(ctrl, Skeleton.class, null, null);
   	 	//Thread.sleep(2000);
   	 	
   	 	Board input = new Board(BOARD_SIZE);
   	 	
        Skandium skandium = new Skandium(Runtime.getRuntime().availableProcessors());
   	 	Stream<Board, Count> stream = skandium.newStream(nqueens);

		long init = System.currentTimeMillis();
	
        Future<Count> future = stream.input(input);
        Count result = future.get();
        
        System.out.println(result.getValue() +" in "+(System.currentTimeMillis() - init)+"[ms]");
        
        
        //java.util.Map<Muscle<?,?>,MovingAverage[]> mats = ctrl.getMATs();
        //java.util.Map<Muscle<?,?>,MovingAverage[]> mals = ctrl.getMALs();
        //printMAs(mats,"mats");
        //printMAs(mals,"mals");
        
        
        
        skandium.shutdown();
	}
	
	private static void printMAs( java.util.Map<Muscle<?, ?>, MovingAverage[]> MAS, String masS) {
		for (java.util.Map.Entry<Muscle<?, ?>, MovingAverage[]> entry : MAS.entrySet()) {
			Muscle<?, ?> m = entry.getKey();
			MovingAverage[] mas = entry.getValue();
			for (int j = 0; j < mas.length; j++) {
				double[] rms = mas[j].rootMeanSquare();
				System.out.println(masS + "," + m + "," + mas[j] + "," + String.format("%1$f,%2$f", rms[0], rms[1]));
			}
		}
	}
 
}
