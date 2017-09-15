package CellularDE;

public abstract class Benchmark {
    public boolean isChanged;

    public int dimensionsNum;
    public int peaksNum;
    
    public int changePeriod;
    public double[] minCoordinate;
    public double[] maxCoordinate;
    public long mainSeed;
    
    public int periodNum;
    public int itSinceLastChange;
    
    public abstract void  init();

    public abstract long evalsNum();
    public abstract void printPeaksData();
    public abstract void printErrors();

    public abstract double eval(Chromosome ch);
    public abstract double eval(double[] gens);

    public abstract double evalDummy(Chromosome ch);
    public abstract double evalDummy(double[] gens);

    public abstract int genNum();
    public abstract double offlineError();
    public abstract double currentError();
}
