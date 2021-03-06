/*   Skandium: A Java(TM) based parallel skeleton library. 
 *   
 *   Copyright (C) 2013 NIC Labs, Universidad de Chile.
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

package cl.niclabs.skandium.gcm.autonomic;

import java.io.Serializable;

/**
 * Box is an utility class that encloses a variable of type <T> into
 * a class in order to allow mutation on final variables or pass by reference
 * of primitives. 
 * 
 * @author Gustavo Pabon &lt;gustavo.pabon&#64;gmail.com&gt;
 *
 * @param &lt;T&gt; Type of variable to be enclosed by the Box
 */
class Box<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private T var;
	Box(T var) {
		this.var = var;
	}
	void set(T var) {
		this.var = var;
	}
	T get() {
		return var;
	}
}
