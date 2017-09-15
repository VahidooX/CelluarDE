package CellularDE;

public class Parameters {

    public long evalsMax;
    public int populationSize;
    public int partitionsNum;
    public int thetaInit;
    public int localSearchNum;
    public int neighborhoodSize;
    
    public double FInit;
    public double CrInit;
    public int DEScheme;
    public double mutProb;

    public double hillMut;

    public double ssInit;
    public double stepMax;
    public double acc;


    public int dimensionsNum;
    public int peaksNum;
    public int changePeriod;
    public double moveSeverity;
    public double heightSeverity;
    public double widthSeverity;
    public double minWidth;
    public double maxWidth;
    public double standardWidth;
    public double lambda;
    public int peaksType;

    
    public Parameters(){

        dimensionsNum=5;
        peaksNum=10;
        changePeriod=5000;
        populationSize=100;
        partitionsNum=10;
        thetaInit=10;
        neighborhoodSize=2;
        localSearchNum=3;
        evalsMax=500000;
        
        DEScheme=3;
        FInit=0.5;
        CrInit=0.5;
        mutProb=0.5;
        
        hillMut=0.2;
        
        ssInit=5;
        stepMax=4;
        acc=0.2;

        moveSeverity=1.0;
        heightSeverity=7.0;
        widthSeverity=1.0;
        minWidth=1.0;
        maxWidth=12.0;
        standardWidth=0.0;
        lambda=0.0;
        peaksType=1;
    }
    
    public void printParams(){
        System.out.println("Population Size="+populationSize+", "+
                            "Number of Dimensions="+dimensionsNum+", "+
                            "Number of Peaks="+peaksNum+", "+
                            "Change Period="+changePeriod+", "+
                            "Number of Partitions="+partitionsNum+", "+
                            "Initial Theta="+thetaInit+", "+
                            "Neighborhood Size="+neighborhoodSize+", "+
                            "Number of LocalSearch="+localSearchNum+", "+
                            "DE Scheme="+DEScheme+", "+
                            "Mutation Probability="+mutProb+", "+
                            "Initial F="+FInit+", "+
                            "initial Cr="+CrInit+", "+
                            "Move Severity="+moveSeverity+", "+
                            "Maximum Number of Evaluations="+evalsMax);
    }
}