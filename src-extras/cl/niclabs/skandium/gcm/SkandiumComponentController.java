package cl.niclabs.skandium.gcm;

import java.io.Serializable;

public interface SkandiumComponentController extends Serializable {

	public void setMaxThreads(int maxThreads);
	
	public void setDelegationCondition(DelegationCondition condition);
}
