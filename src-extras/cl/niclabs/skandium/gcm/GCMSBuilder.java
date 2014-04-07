package cl.niclabs.skandium.gcm;

import java.util.ArrayList;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactory;
import org.objectweb.proactive.core.node.Node;

public class GCMSBuilder {

	public static Component build(String name, Node node) throws InstantiationException, NoSuchInterfaceException {
		
		Component boot = Utils.getBootstrapComponent();
	    PAGCMTypeFactory tf = Utils.getPAGCMTypeFactory(boot);
	    PAGenericFactory gf = Utils.getPAGenericFactory(boot);
		ArrayList<InterfaceType> ital = new ArrayList<InterfaceType>();
	    ital.add(tf.createGCMItfType(GCMSConstants.GCMSKANDIUM_ITF, GCMSkandium.class.getName(), GCMTypeFactory.SERVER, GCMTypeFactory.MANDATORY, GCMTypeFactory.SINGLETON_CARDINALITY));
	    ital.add(tf.createGCMItfType("scrr", ResultReceiver.class.getName(), GCMTypeFactory.CLIENT, GCMTypeFactory.OPTIONAL, GCMTypeFactory.SINGLETON_CARDINALITY));
	    ital.add(tf.createGCMItfType(GCMSConstants.MASTER_SERVER_ITF, Master.class.getName(), GCMTypeFactory.SERVER, GCMTypeFactory.MANDATORY, GCMTypeFactory.SINGLETON_CARDINALITY));
	    ital.add(tf.createGCMItfType(GCMSConstants.MASTER_CLIENT_ITF, Master.class.getName(), GCMTypeFactory.CLIENT, GCMTypeFactory.OPTIONAL, GCMTypeFactory.SINGLETON_CARDINALITY));
	    ital.add(tf.createGCMItfType(GCMSConstants.WORKER_SERVER_ITF, Worker.class.getName(), GCMTypeFactory.SERVER, GCMTypeFactory.MANDATORY, GCMTypeFactory.SINGLETON_CARDINALITY));
	    ital.add(tf.createGCMItfType(GCMSConstants.WORKER_CLIENT_ITF, WorkerMulticast.class.getName(), GCMTypeFactory.CLIENT, GCMTypeFactory.OPTIONAL, GCMTypeFactory.MULTICAST_CARDINALITY));
	    ital.add(tf.createGCMItfType("scc", SkandiumComponentController.class.getName(), GCMTypeFactory.SERVER, GCMTypeFactory.OPTIONAL, GCMTypeFactory.SINGLETON_CARDINALITY));
	    ComponentType tGCMSkandium = tf.createFcType(ital.toArray(new InterfaceType[ital.size()]));
	    return gf.newFcInstance(tGCMSkandium, new ControllerDescription(name, Constants.PRIMITIVE), new ContentDescription(GCMSkandiumImpl.class.getName()), node);
	}
}
