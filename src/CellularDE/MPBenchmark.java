package CellularDE;
import peaks.*;
import java.util.*;

public class MPBenchmark extends Benchmark{
    movpeaks function;
    double moveSeverity;
    double heightSeverity;
    double widthSeverity;
    double minWidth;
    double maxWidth;
    double standardWidth;
    double lambda;
    int peakType;
    long MPBSeed;

    public void init(){
        dimensionsNum=EvolutionUtils.pars.dimensionsNum;
        peaksNum=EvolutionUtils.pars.peaksNum;
        changePeriod=EvolutionUtils.pars.changePeriod;

        minCoordinate=new double[dimensionsNum];
        maxCoordinate=new double[dimensionsNum];
        for(int i=0;i<dimensionsNum;i++){
            minCoordinate[i]=0.0;
            maxCoordinate[i]=100.0;
        }

        moveSeverity=EvolutionUtils.pars.moveSeverity;
        heightSeverity=EvolutionUtils.pars.heightSeverity;
        widthSeverity=EvolutionUtils.pars.widthSeverity;
        minWidth=EvolutionUtils.pars.minWidth;
        maxWidth=EvolutionUtils.pars.maxWidth;
        standardWidth=EvolutionUtils.pars.standardWidth;
        lambda=EvolutionUtils.pars.lambda;
        peakType=EvolutionUtils.pars.peaksType;
        
        periodNum=1;
        itSinceLastChange=0;
        isChanged=false;

        EvolutionUtils.initRandom((new Random()).nextLong());
        MPBSeed=EvolutionUtils.rnd.nextLong();

        function=new movpeaks(peaksNum,
                                 dimensionsNum,
                                 changePeriod,
                                 moveSeverity,
                                 minWidth,
                                 maxWidth,
                                 standardWidth,
                                 lambda,
                                 heightSeverity,
                                 widthSeverity,
                                 peakType,
                                 MPBSeed);

        function.mincoordinate=minCoordinate[0];
        function.maxcoordinate=maxCoordinate[0];
        function.init_peaks();
    }

    public long evalsNum(){
        return function.get_number_of_evals();
    }

    public void printPeaksData(){
        function.printPeakData();
    }

    public void printErrors(){
        System.out.println("Offline Error:"+function.get_offline_error());
        System.out.println("Best Error:"+function.get_current_error());
        System.out.println("Current Peak:+"+function.getCurrentPeak()+","+function.get_right_peak());
        System.out.println("--------------------------------------------");
    }

    public double eval(Chromosome ch){
        double tmp;
        tmp=eval(ch.gens);
        return tmp;
    }

    public double eval(double[] gens){
        double tmp;
        tmp=function.eval_movpeaks(gens);
        if(function.get_number_of_evals()%changePeriod==0){
            isChanged=true;
            EvolutionUtils.benchmark.periodNum=EvolutionUtils.benchmark.periodNum+1;
        }
       return tmp;
    }

    public double evalDummy(Chromosome ch){
        return function.dummy_eval(ch.gens);
    }

    public double evalDummy(double[] gens){
        return function.dummy_eval(gens);
    }

    public int genNum(){
        return function.geno_size;
    }

    public double offlineError(){
        return function.get_offline_error();
    }

    public double currentError(){
        return function.get_current_error();
    }
}