package cl.niclabs.skandium.examples.gcmnqueens;

import java.math.BigDecimal;

abstract class MovingAverage {
    private static final int SCALE = 100;

    private long e;
    private long n;
    private BigDecimal ssd;
    private long min;
    private long max;
    
    MovingAverage() {
        e = -1;
        n = 0l;
        ssd = BigDecimal.valueOf(0l);
        min = Long.MAX_VALUE;
        max = Long.MIN_VALUE;
    }
    
    void sumOfSquareDistance(long actual) {
        n++;
        if (getE() != -1) {
            BigDecimal a = BigDecimal.valueOf(actual);
            BigDecimal e = BigDecimal.valueOf(getE());
            e = e.subtract(a);
            e = e.pow(2);
            ssd = ssd.add(e);
        }
        if (actual < min) min = actual;
        if (actual > max) max = actual;
    }
    
    double[] rootMeanSquare() {
        if (n > 1) {
            BigDecimal rms = 
                ssd.divide(BigDecimal.valueOf(n-1l),SCALE,BigDecimal.ROUND_HALF_UP);
            rms = Utils.sqrt(rms);
            double rmsd = rms.doubleValue();
            long range = max - min;
            double nrmsd = rmsd / ((double)range);
            double[] toreturn = {rmsd,nrmsd};
            return toreturn;
        }
        double[] toreturn = {-1d,-1d};
        return toreturn;
    }
    
    long getN() {
        return n;
    }
    
    long getE() {
        return e;
    }
    
    void setE(long e) {
        this.e = e;
    }
}