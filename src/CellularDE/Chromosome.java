package CellularDE;

public class Chromosome implements Comparable{
    public double[] gens;
    public double fitness;
    public int desCell;
    public int hop;
   
    public int compareTo(Object e){
        if(((Chromosome)e).fitness<this.fitness)
            return -1;
        if(((Chromosome)e).fitness>this.fitness)
            return 1;
        return 0;
    }

    public Chromosome(int numberOfDimensions){
        gens=new double[numberOfDimensions];
        fitness=EvolutionUtils.bigMin;
        hop=-1;
        desCell=-1;
    }

    public Chromosome(int numberOfDimensions,double[] min,double[] max){
        this(numberOfDimensions);
        for(int j=0;j<numberOfDimensions;j++)
            gens[j]=EvolutionUtils.toBound(EvolutionUtils.rnd.nextDouble(),min[j],max[j]);

    }
}