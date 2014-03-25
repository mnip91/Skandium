package cl.niclabs.skandium.examples.gcmnqueens;

public class ExponentialMovingAverage extends MovingAverage {
    private double alpha;
    
    ExponentialMovingAverage(double alpha) {
        super();
        this.alpha = alpha;
    }
    
    synchronized void addOutcome(long p) {
        long e = getE();
        if(e == -1) {
            setE(p);
            return;
        }
        setE((long)((alpha*((double)p)) + ((1d-alpha)*((double)e)))); 
    }
    
}
