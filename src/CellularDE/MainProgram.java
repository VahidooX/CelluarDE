package CellularDE;

public class MainProgram {
    public CA myCA;
    
    public static void main(String[] args) {
        int runsNum=100;
        Benchmark benchmark;
        MainProgram testCA=null;
        double sum=0;
        for(int i=1;i<=runsNum;i++){
            double err;
            benchmark=new MPBenchmark();
            benchmark.init();
            EvolutionUtils.benchmark=benchmark;
            if(i==1){
                EvolutionUtils.pars.printParams();
                testCA=new MainProgram();
            }else{
                testCA.myCA.reInit();
            }
            err=testCA.run(EvolutionUtils.pars.evalsMax);
            System.gc();
            sum=sum+err;
            System.out.println(i+" "+err+" "+sum/i);
        }
    }
    
    public double run(long maxEval){
        while(EvolutionUtils.benchmark.evalsNum()<maxEval){
            myCA.update();
        }
        return EvolutionUtils.benchmark.offlineError();
    }

    public MainProgram(){
        myCA=new CA();
    }   
}