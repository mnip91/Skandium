package cl.niclabs.skandium.examples.gcmnqueens;

class WeightedMovingAverage extends MovingAverage {
    private double alpha;
    
    WeightedMovingAverage(double alpha) {
        super();
        this.alpha = alpha;
    }
    
    synchronized void addOutcome(long p) {
        long e = getE();
        if(e == -1) {
            setE(p);
            return;
        }
        setE((long)(((alpha*((double)p)) + (((double)getN())*((double)e)))/
            (alpha+((double)getN()))));
    }
    
}
