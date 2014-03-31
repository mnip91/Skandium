package cl.niclabs.skandium.gcm;

import java.io.Serializable;

import cl.niclabs.skandium.skeletons.Skeleton;

/** 
 * GCMSkandium user interface
 * 
 * @author Matias
 *
 */
public interface GCMSkandium {

	/**
	 * Execute the given skeleton with the given input.
	 * 
	 * @param skeleton
	 * @param param
	 */
	public <P extends Serializable, R extends Serializable> void execute(Skeleton<P,R> skeleton, P param, Boolean autonomic);

}
