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
package cl.niclabs.skandium.events;

import cl.niclabs.skandium.system.events.BeforeListener;
import cl.niclabs.skandium.system.events.NoParamListener;
import cl.niclabs.skandium.system.events.PipeListener;
import cl.niclabs.skandium.system.events.SkeletonListener;

/**
 * Abstract class intended to be extended in order to include a {@link SkandiumEventListener} to the
 * {@link When#BEFORE} {@link Pipe} event.
 * 
 * @param <P> <code>param</code> type before {@link Pipe} is executed
 * @param <R> <code>param</code> type after {@link Pipe} is executed
 */
public abstract class PipeBeforeListener<P,R> extends NoParamListener<P> implements PipeListener<P,R>, BeforeListener, SkeletonListener {

}
