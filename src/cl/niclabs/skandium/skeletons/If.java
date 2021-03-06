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
package cl.niclabs.skandium.skeletons;

import java.util.ArrayList;
import java.util.Arrays;

import cl.niclabs.skandium.events.ConditionListener;
import cl.niclabs.skandium.events.IndexListener;
import cl.niclabs.skandium.events.When;
import cl.niclabs.skandium.events.Where;
import cl.niclabs.skandium.muscles.Condition;
import cl.niclabs.skandium.muscles.Execute;
import cl.niclabs.skandium.muscles.Muscle;

/**
 * An <code>If</code> {@link cl.niclabs.skandium.skeletons.Skeleton} represents condition branching.
 * Depending on the evaluation of a {@link Condition}, either one or another skeleton
 * program is executed.
 * 
 * @author mleyton
 *
 * @param <P> The input type of the {@link cl.niclabs.skandium.skeletons.Skeleton}.
 * @param <R> The result type of the {@link cl.niclabs.skandium.skeletons.Skeleton}. 
 * */
public class If<P,R> extends AbstractSkeleton<P,R> {
	private static final long serialVersionUID = 1L;
	Skeleton<P,R> trueCase, falseCase;
	Condition<P> condition;
	
	/**
	 * The constructor.
	 * 
	 * @param condition Used to determine for each parameter, which skeleton is executed. 
	 * @param trueCase Executed if the condition evaluates to true.
	 * @param falseCase Executed if the conditino evaulates to false.
	 */
	public If(Condition<P> condition, Skeleton<P,R> trueCase, Skeleton<P,R> falseCase){
		super();
		this.condition = condition;
		this.trueCase=trueCase;
		this.falseCase=falseCase;
	}
	
	public Condition<P> getCondition() {
		return condition;
	}
	
	/**
	 * The constructor.
	 * 
	 * @param condition Used to determine for each parameter, which skeleton is executed. 
	 * @param trueCase Executed if the condition evaluates to true.
	 * @param falseCase Executed if the conditino evaulates to false.
	 */
	public If(Condition<P> condition, Execute<P,R> trueCase, Execute<P,R> falseCase){
		this(condition,new Seq<P,R>(trueCase),new Seq<P,R>(falseCase));
	}
	
	/**
	 * {@inheritDoc}
	 */
    public void accept(SkeletonVisitor visitor) {
        visitor.visit(this);
    }

    public boolean addBeforeCondition(IndexListener<P> l) {
    	return eregis.addListener(When.BEFORE, Where.CONDITION, l);
    }

    public boolean removeBeforeCondition(IndexListener<P> l) {
    	return eregis.removeListener(When.BEFORE, Where.CONDITION, l);
    }

    public boolean addAfterCondition(ConditionListener<P> l) {
    	return eregis.addListener(When.AFTER, Where.CONDITION, l);
    }

    public boolean removeAfterCondition(ConditionListener<P> l) {
    	return eregis.removeListener(When.AFTER, Where.CONDITION, l);
    }

	public Skeleton<P, R> getTrueCase() {
		return trueCase;
	}

	public Skeleton<P, R> getFalseCase() {
		return falseCase;
	}
	
	@Override
	public Muscle<?,?>[] getMuscles() {
		ArrayList<Muscle<?,?>> muscles = new ArrayList<Muscle<?,?>>();
		muscles.add(getCondition());
		muscles.addAll(Arrays.asList(getTrueCase().getMuscles()));
		muscles.addAll(Arrays.asList(getFalseCase().getMuscles()));
		return muscles.toArray(new Muscle<?,?>[muscles.size()]);
	}
}