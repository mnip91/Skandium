package cl.niclabs.skandium.gcm.examples.nqueensnaive;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.control.PABindingController;
import org.objectweb.proactive.core.component.control.PAContentController;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactory;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import cl.niclabs.skandium.gcm.DelegationCondition;
import cl.niclabs.skandium.gcm.GCMSConstants;
import cl.niclabs.skandium.gcm.Worker;
import cl.niclabs.skandium.gcm.ResultReceiver;
import cl.niclabs.skandium.gcm.Master;
import cl.niclabs.skandium.gcm.GCMSkandium;
import cl.niclabs.skandium.gcm.SkandiumComponentController;
import cl.niclabs.skandium.gcm.GCMSkandiumImpl;
import cl.niclabs.skandium.gcm.GCMSkandiumBuilder;

public class Main {
	
	public static void main(String[] args) throws InstantiationException,
			NoSuchInterfaceException, IllegalContentException,
			IllegalLifeCycleException, IllegalBindingException,
			ProActiveException, MalformedURLException, URISyntaxException, InterruptedException {
		
		int SIZE = 17;
		int DEPTH = 3;
		try {
			SIZE = Integer.parseInt(args[0]);
			DEPTH = Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.out.println("ERROR: size and depth must be specified." +
					"\n Using the default configuration (17,3)");
		}
		
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

		Component boot = Utils.getBootstrapComponent();
	    PAGCMTypeFactory tf = Utils.getPAGCMTypeFactory(boot);
	    PAGenericFactory gf = Utils.getPAGenericFactory(boot);
	    
	    ComponentType tComposite = tf.createFcType(new InterfaceType[] {
	    		tf.createGCMItfType(
	    				"nqueens",
	    				NQueens.class.getName(),
	    				GCMTypeFactory.SERVER,
	    				GCMTypeFactory.MANDATORY,
	    				GCMTypeFactory.SINGLETON_CARDINALITY),
				tf.createGCMItfType(
						"scc",
						SkandiumComponentController.class.getName(),
						GCMTypeFactory.SERVER,
						GCMTypeFactory.OPTIONAL,
						GCMTypeFactory.SINGLETON_CARDINALITY)
	    });
	    
	    ComponentType tExample = tf.createFcType(new InterfaceType[] {
	    		tf.createGCMItfType(
			    		"nqueens",
			    		NQueens.class.getName(),
			    		GCMTypeFactory.SERVER,
			    		GCMTypeFactory.MANDATORY,
			    		GCMTypeFactory.SINGLETON_CARDINALITY),
		    	tf.createGCMItfType(
		    			GCMSConstants.GCMSKANDIUM_ITF,
					    GCMSkandium.class.getName(),
					    GCMTypeFactory.CLIENT,
					    GCMTypeFactory.MANDATORY,
					    GCMTypeFactory.SINGLETON_CARDINALITY),
				tf.createGCMItfType(
					    "scrr",
					    ResultReceiver.class.getName(),
					    GCMTypeFactory.SERVER,
						GCMTypeFactory.MANDATORY,
						GCMTypeFactory.SINGLETON_CARDINALITY)
	    });
	    
	    
	    Node N1composite = VN1.getANode();
	    Node N1example = VN1.getANode();
	    Node N1sc = VN1.getANode();
	    Node N2sc = VN2.getANode();
	    Node N3sc = VN3.getANode();

	    Component compositeComp = gf.newFcInstance(
		    	tComposite,
		    	new ControllerDescription("compositeComp", Constants.COMPOSITE),
		    	null,
		    	N1composite);
	    
	    Component exampleComp = gf.newFcInstance(
		    	tExample,
		    	new ControllerDescription("exampleComp", Constants.PRIMITIVE),
		    	new ContentDescription(NQueensImpl.class.getName()),
		    	N1example);
	    
	    Component SC1 = GCMSkandiumBuilder.build("SC1", N1sc);
	    Component SC2 = GCMSkandiumBuilder.build("SC2", N2sc);
	    Component SC3 = GCMSkandiumBuilder.build("SC3", N3sc);
	    
	    PAContentController cc = Utils.getPAContentController(compositeComp);
	    cc.addFcSubComponent(exampleComp);
	    cc.addFcSubComponent(SC1);
	    cc.addFcSubComponent(SC2);
	    cc.addFcSubComponent(SC3);
	    
	    PABindingController bcTest = Utils.getPABindingController(compositeComp);
        bcTest.bindFc("nqueens", exampleComp.getFcInterface("nqueens"));
        bcTest.bindFc("scc", SC1.getFcInterface("scc"));
        
        PABindingController bcMergeSort = Utils.getPABindingController(exampleComp);
        bcMergeSort.bindFc(GCMSConstants.GCMSKANDIUM_ITF, SC1.getFcInterface(GCMSConstants.GCMSKANDIUM_ITF));
        
        PABindingController bcSC1 = Utils.getPABindingController(SC1);
        bcSC1.bindFc(GCMSConstants.WORKER_CLIENT_ITF, SC2.getFcInterface(GCMSConstants.WORKER_SERVER_ITF));
        bcSC1.bindFc(GCMSConstants.WORKER_CLIENT_ITF, SC3.getFcInterface(GCMSConstants.WORKER_SERVER_ITF));
        bcSC1.bindFc("scrr", exampleComp.getFcInterface("scrr"));

        
        PABindingController bcSC2 = Utils.getPABindingController(SC2);
        bcSC2.bindFc(GCMSConstants.MASTER_CLIENT_ITF, SC1.getFcInterface(GCMSConstants.MASTER_SERVER_ITF));
        
        PABindingController bcSC3 = Utils.getPABindingController(SC3);
        bcSC3.bindFc(GCMSConstants.MASTER_CLIENT_ITF, SC1.getFcInterface(GCMSConstants.MASTER_SERVER_ITF));
        
        Utils.getPAGCMLifeCycleController(compositeComp).startFc();

        SkandiumComponentController scc = (SkandiumComponentController) SC1.getFcInterface("scc");
        SkandiumComponentController scc2 = (SkandiumComponentController) SC2.getFcInterface("scc");
        SkandiumComponentController scc3 = (SkandiumComponentController) SC2.getFcInterface("scc");
        
        DelegationCondition cond = new DelegationCondition() {

      
			private static final long serialVersionUID = 2L;

        	public boolean condition(int stackSize, int maxThreads) {
        		return stackSize > 4;
        	}
        };
		
        scc.setDelegationCondition(cond);
        scc2.setDelegationCondition(cond);
        scc3.setDelegationCondition(cond);

        NQueens example = (NQueens) compositeComp.getFcInterface("nqueens");
		example.setParameters(SIZE, DEPTH);
		example.run();
	}
}
