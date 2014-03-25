package cl.niclabs.skandium.examples.gcmnqueens;

import java.util.HashMap;

import cl.niclabs.skandium.events.GenericListener;
import cl.niclabs.skandium.muscles.Muscle;
import cl.niclabs.skandium.events.When;
import cl.niclabs.skandium.events.Where;
import cl.niclabs.skandium.skeletons.Skeleton;
import cl.niclabs.skandium.skeletons.Seq;
import cl.niclabs.skandium.skeletons.If;
import cl.niclabs.skandium.skeletons.While;
import cl.niclabs.skandium.skeletons.Map;
import cl.niclabs.skandium.skeletons.Fork;
import cl.niclabs.skandium.skeletons.DaC;
import cl.niclabs.skandium.muscles.Condition;
import cl.niclabs.skandium.muscles.Split;

class Controller extends GenericListener {
	
	// Time Estimators (one per muscle)
	private HashMap<Muscle<?,?>,MovingAverage[]> mats;
	
	// No time Estimators (one per muscle)
	private HashMap<Muscle<?,?>,MovingAverage[]> mals;
	
	// Structure that holds the initial time (tn) of each muscle execution
	// indexed by the  "index".
	private HashMap<Integer,Long> tns;
	
	// Structure that holds the end time (tn) of each muscle execution
	// indexed by the  "index".
	private HashMap<Muscle<?,?>,Long> tn1s;

    private double alpha1;
    private double alpha2;
    private double alpha3;
    private long w;
    private boolean verbose;
    private long gini;
    
    private HashMap<Integer,Integer> condDaC;
    private HashMap<Integer,Integer> condWhile;

	Controller(double alpha1, double alpha2, double alpha3, long w, 
	    boolean verbose) {
	    mats = new HashMap<Muscle<?,?>,MovingAverage[]>();
	    mals = new HashMap<Muscle<?,?>,MovingAverage[]>();
	    tns = new HashMap<Integer,Long>();
	    tn1s = new HashMap<Muscle<?,?>,Long>();
        this.alpha1 = alpha1;
        this.alpha2 = alpha2;
        this.alpha3 = alpha3;
        this.w = w;
        this.verbose = verbose;
        this.gini = System.nanoTime();
        if (verbose) {
	        System.out.println("mas,muscle,moving average,time,estimated,"+
	            "actual");
        }
        condDaC = new HashMap<Integer,Integer>();
        condDaC.put(0,0);
        condWhile = new HashMap<Integer,Integer>();
	}

	@Override
	public boolean guard(Object param, 
		@SuppressWarnings("rawtypes") Skeleton[] strace, int index,
		boolean cond, int parent, When when, Where where) {
		    
		Skeleton<?,?> current = strace[strace.length-1];
		if (current instanceof Seq) return true;
		if ((current instanceof If)||(current instanceof While)) {
			if (where == Where.CONDITION) return true;
			return false;
		}
		if ((current instanceof Map)||(current instanceof Fork)) {
			if (where == Where.SPLIT) return true;
			if (where == Where.MERGE) return true;
			return false;
		}
		if (current instanceof DaC) {
			if (where == Where.CONDITION) return true;
			if (where == Where.SPLIT) return true;
			if (where == Where.MERGE) return true;
			return false;
		}
		return false;
	}
	
	@Override
	synchronized public Object handler(Object param, 
		@SuppressWarnings("rawtypes") Skeleton[] strace, int index,
		boolean cond, int parent, When when, Where where) {
		if (when == When.BEFORE) setInitialTime(index);
		else {
		    Skeleton<?,?> current = strace[strace.length-1];
		    Muscle<?,?> muscle = getMuscle(current, where);
		    setEndTime(index,muscle,param,cond,current,parent);
		}
		return param;
	}
	
	private void setEndTime(int index, Muscle<?,?> muscle, Object param, 
	    boolean cond, Skeleton<?,?> skel, int parent) {
	    long currTime = System.nanoTime();
	    long tn = tns.remove(index);
        Long tn1 = tn1s.remove(muscle);
        tn1s.put(muscle,currTime);
        long delta = tn1 == null? 0l : Math.abs(tn-tn1);
        {
	        long p = currTime - tn;
            setMAs(muscle, mats, p, delta, currTime, "mats");
        }
        if (muscle instanceof Split) {
            long p = ((long)(((Object[])param).length));
            setMAs(muscle, mals, p, delta, currTime, "mals");
        }
        if (muscle instanceof Condition) {
            if (skel instanceof While) {
                if (!condWhile.containsKey(index)) {
                    condWhile.put(index,0);
                }
                if (cond) {
                    condWhile.put(index,condWhile.get(index)+1);
                } else {
                    long p = (long)condWhile.get(index);
                    setMAs(muscle, mals, p, delta, currTime, "mals");
                }
                return;
            }
            if (cond) {
                condDaC.put(index,condDaC.get(parent)+1);
            } else {
                long p = (long)condDaC.get(parent);
                setMAs(muscle, mals, p, delta, currTime, "mals");
            }
        }
	}
	
	private void setMAs(Muscle<?,?> muscle, 
	    HashMap<Muscle<?,?>,MovingAverage[]> MAS, long p, long delta, 
	    long currTime, String masS) {
        MovingAverage[] mas = null;
        if (MAS.containsKey(muscle)) {
            mas = MAS.get(muscle);
        } else {
            mas = new MovingAverage[3];
            mas[0] = new ExponentialMovingAverage(alpha1);
            mas[1] = new WeightedMovingAverage(alpha2);
            mas[2] = new ExponentialMovingAverageTime(alpha3,w);
            MAS.put(muscle,mas);
        }
        for (int i=0;i<mas.length;i++) {
            ((MovingAverage)mas[i]).sumOfSquareDistance(p);
            if(verbose) {
                printEA(masS,muscle,mas[i], p, currTime);
            }
        }
        ((ExponentialMovingAverage)mas[0]).addOutcome(p);
        ((WeightedMovingAverage)mas[1]).addOutcome(p);
        ((ExponentialMovingAverageTime)mas[2]).addOutcome(p,delta);
	}
	
	private void printEA(String masS, Muscle<?,?> muscle, MovingAverage ma,
	    long actual, long currTime) {
	    if (ma.getE() != -1) {
	        long ptime = currTime-gini;
	        System.out.println(masS + "," + muscle + "," + ma + "," + ptime + 
	            "," + ma.getE() + "," + actual);
	    }
	}
	
	private void setInitialTime(int index) {
	    tns.put(index,System.nanoTime());
	}
	
	private Muscle<?,?> getMuscle(Skeleton<?,?> current, Where where) {
		if (current instanceof Seq) return ((Seq)current).getExecute();
		if (current instanceof If) return ((If)current).getCondition();
		if (current instanceof While) return ((While)current).getCondition();
		if (current instanceof Map) {
			if (where == Where.SPLIT) return ((Map)current).getSplit();
			if (where == Where.MERGE) return ((Map)current).getMerge();
			return null;
		}
		if (current instanceof Fork) {
			if (where == Where.SPLIT) return ((Fork)current).getSplit();
			if (where == Where.MERGE) return ((Fork)current).getMerge();
			return null;
		}
		if (current instanceof DaC) {
			if (where == Where.CONDITION) return ((DaC)current).getCondition();
			if (where == Where.SPLIT) return ((DaC)current).getSplit();
			if (where == Where.MERGE) return ((DaC)current).getMerge();
			return null;
		}
	    
	    return null;
	}
	
	java.util.Map<Muscle<?,?>,MovingAverage[]> getMATs() {
	    return (java.util.Map<Muscle<?,?>,MovingAverage[]>) mats;
	}

	java.util.Map<Muscle<?,?>,MovingAverage[]> getMALs() {
	    return (java.util.Map<Muscle<?,?>,MovingAverage[]>) mals;
	}

}
