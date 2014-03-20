package cl.niclabs.skandium.gcm;

import java.io.Serializable;

public interface DelegationCondition extends Serializable {

	public boolean condition(int currentStackSize, int maxThreads);
}
