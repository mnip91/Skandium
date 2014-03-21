/*   Skandium: A Java(TM) based parallel skeleton library.
 *   
 *   Copyright (C) 2009 NIC Labs, Universidad de Chile.
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
package cl.niclabs.skandium.gcm.examples.nqueensnaive;

import java.util.Random;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.proactive.core.component.control.PABindingController;

import cl.niclabs.skandium.gcm.GCMSConstants;
import cl.niclabs.skandium.gcm.ResultReceiver;
import cl.niclabs.skandium.gcm.GCMSkandium;
import cl.niclabs.skandium.skeletons.Map;
import cl.niclabs.skandium.skeletons.DaC;
import cl.niclabs.skandium.skeletons.Skeleton;

/**
 * The main class to execute a naive NQueens counting algorithm which does not consider board symmetries.
 * 
 * @author mleyton
 */
public class NQueensImpl implements NQueens, ResultReceiver, PABindingController {

	static Random random = new Random();
	GCMSkandium gcmskandium;
	long time;
	
	int BOARD;
	int DEPTH;
	

	public void setParameters(int size, int depth) {
		BOARD = size;
		DEPTH = depth;
	}

    public void run() {
       

    	
    	System.out.println("Computing NQueens board="+ BOARD+" depth="+DEPTH+ ".");
    	
    	//1. Define the skeleton program structure
    	 Skeleton<Board, Count> subskel = new DaC<Board, Count>(   //We use a divide and conquer skeleton pattern
    			 new ShouldDivide(DEPTH),  //Dive until the depth is "N-3" 
    			 new DivideBoard(), 
    			 new Solve(), 
    			 new ConquerCount());
    	
    	 Skeleton<Board, Count> nqueens = //Always subdivide the first row.
    		 new Map<Board, Count>(new DivideBoard(), subskel, new ConquerCount());
    	 
    	 gcmskandium.execute(nqueens, new Board(BOARD));
         
         time = System.currentTimeMillis();        
    }

	@Override
	public void receive(Object result) {
        Count count = (Count) result;
        System.out.println(count + " in " 
        		+ (System.currentTimeMillis() - time) + "[ms]");
	}

	@Override
	public void bindFc(String itfName, Object itf) 
			throws NoSuchInterfaceException {
		if(itfName.compareTo(GCMSConstants.GCMSKANDIUM_ITF) == 0)
			gcmskandium = (GCMSkandium) itf;
		else throw new NoSuchInterfaceException(itfName);
	}

	@Override
	public String[] listFc() {
		return new String[] {GCMSConstants.GCMSKANDIUM_ITF};
	}

	@Override
	public Object lookupFc(String itfName) throws NoSuchInterfaceException {
		if(itfName.compareTo(GCMSConstants.GCMSKANDIUM_ITF) == 0)
			return gcmskandium;
		else throw new NoSuchInterfaceException(itfName);
	}

	@Override
	public void unbindFc(String itfName) throws NoSuchInterfaceException {
		if(itfName.compareTo(GCMSConstants.GCMSKANDIUM_ITF) == 0)
			gcmskandium = null;
		else throw new NoSuchInterfaceException(itfName);
	}

	@Override
	public Boolean isBound() {
		return null;
	}

	@Override
	public Boolean isBoundTo(Component arg0) {
		return null;
	}
}