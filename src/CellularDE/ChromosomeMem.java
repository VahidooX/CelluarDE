package CellularDE;

public class ChromosomeMem{
    public double[] gens;
    public double fitness;
    boolean isEmpty=true;
    public int periodNum;

    public ChromosomeMem(){
        isEmpty=true;
        fitness=EvolutionUtils.bigMin;
        periodNum=EvolutionUtils.benchmark.periodNum;
        gens=null;
    }
    
    public boolean isEmpty(){
        return isEmpty;
    }

    public void update(ChromosomeMem mem){
        if(mem==null || mem.isEmpty()){
            gens=null;
            fitness=EvolutionUtils.bigMin;
            periodNum=EvolutionUtils.benchmark.periodNum;
            isEmpty=true;
            return;
        }
        if(gens==null)
            gens=new double[mem.gens.length];
        System.arraycopy(mem.gens, 0, gens, 0, mem.gens.length);
        fitness=mem.fitness;
        periodNum=mem.periodNum;
        isEmpty=false;
    }

    public void update(Chromosome ch){
        periodNum=EvolutionUtils.benchmark.periodNum;
        if(ch==null){
            gens=null;
            fitness=EvolutionUtils.bigMin;
            isEmpty=true;
            return;
        }
        if(gens==null)
            gens=new double[ch.gens.length];
        System.arraycopy(ch.gens, 0, gens, 0, ch.gens.length);

        fitness=ch.fitness;
        isEmpty=false;
    }

    public Chromosome getAliveClone(){
        Chromosome ch=new Chromosome(gens.length);
        System.arraycopy(gens, 0, ch.gens, 0, gens.length);
        ch.fitness=fitness;
        return ch;
    }
}
