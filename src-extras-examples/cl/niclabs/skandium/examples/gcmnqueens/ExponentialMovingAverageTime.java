package cl.niclabs.skandium.examples.gcmnqueens;

class ExponentialMovingAverageTime extends MovingAverage {
    private long w;
    private double alpha;
    
    ExponentialMovingAverageTime(double alpha, long w) {
        super();
        this.w = w;
        this.alpha = alpha;
    }
    
    synchronized void addOutcome(long p, long delta) {
        long e = getE();
        if(e == -1) {
            setE(p);
            return;
        }
        double alpha = alphaD(delta);
        setE((long)((alpha*((double)p)) + ((1d-alpha)*((double)e)))); 
    }
    
    private double alphaD(long delta) {
        double epsilon = -1d*Math.log(1d-alpha)*((double)w);
        delta = Math.abs(delta);
        if (((double)delta) <= epsilon) return alpha;
        return 1d-Math.exp(-1d*((double)delta)/((double)w));
    }
    
}
