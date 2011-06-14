/*   Skandium: A Java(TM) based parallel skeleton library. 
 *   
 *   Copyright (C) 2011 NIC Labs, Universidad de Chile.
 * 
 *   Skandium is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Skandium is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with Skandium.  If not, see <http://www.gnu.org/licenses/>.
 */
package cl.niclabs.skandium.system.events;

import cl.niclabs.skandium.skeletons.Skeleton;

/**
 * A <code>RBranchParamListener</code> is a listener for events that passes as parameter 
 * the recursive tree branch for DaC events. 
 */
public abstract class RBranchParamListener<P> implements SkandiumEventListener {

	/**
	 * Default implementation of compareTo method inherited from Comparable interface
	 * of {@link SkandiumEventListener#compareTo(SkandiumEventListener)} where the 
	 * {@link Integer.MAX_VALUE} is returned.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(SkandiumEventListener o) {
		return Integer.MAX_VALUE;
	}

	/**
	 * When the event related to this Listener is fired, for each Listener registered,
	 * it executes listener's guard, and if true is returned, then the handler is called.
	 * 
	 * This is the default implementation when true is returned.
	 * @param param Current <code>param</code> value
	 * @param strace Nested skeleton tree branch of the current execution.
	 * @param rbranch Recursive tree branch of current execution
	 * @return true if the handler must be executed, false otherwise. 
	 */
	@SuppressWarnings("rawtypes")
	public boolean guard(P param, Skeleton[] strace, Integer[] rbranch) {
		return true;
	}

	/**
	 * When the event related to this Listener is fired and the guard returned true, the handler
	 * is called.
	 * 
	 * @param param Current <code>param</code> value
	 * @param strace Nested skeleton tree branch of the current execution.
	 * @param rbranch Recursive tree branch of current execution
	 * @return New <code>param</code> value. 
	 */
	@SuppressWarnings("rawtypes")
	public abstract P handler(P param, Skeleton[] strace, Integer[] rbranch);

}
