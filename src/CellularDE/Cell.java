package CellularDE;
import java.util.*;

public class Cell implements Comparable{
    public CellState currentState;
    public CellState nextState;

    public int[] partition;
    public Cell[] neighbors;

    ChromosomeMem currentBest;
    ChromosomeMem localBest;
    Cell bestNCell;
    

    double Cl=0.5;
    double Cf=0.5;
        
    public Cell(int[] partition){
        this.partition=partition;
    }
    
    public void init(){
        currentState=new CellState();
        nextState=null;
        currentBest=null;
        localBest=null;
        Cl=0.5;
        Cf=0.5;
        bestNCell=null;
    }

    public void initNextState(){
        nextState=new CellState();

        nextState.F=currentState.F;
        nextState.Cr=currentState.Cr;
        nextState.theta=currentState.theta;
        nextState.cellMem.update(currentState.cellMem);
    }
    
    public void update(){
        initNextState();
        if(currentState.population.size()>0){
            refreshMems();
            setCurrentBest();
            setLocalBest();
            evolve();
            controlDensity();
        }
    }
    
    public void evolve(){
        if (EvolutionUtils.benchmark.itSinceLastChange<=EvolutionUtils.pars.localSearchNum){
            randomLocalSearch();
        }else{
            DEEvolve();
        }
    }

    public void DEEvolve(){
        double F=currentState.F;
        double Cr=currentState.Cr;
        
        ArrayList<Chromosome> popPool=new ArrayList<Chromosome>();
        popPool.addAll(currentState.population);

        int winNum=0;
        for(int i=0;i<currentState.population.size();i++){
            Chromosome x1,x2,x3,x4,xb,res,currentCh;
            currentCh=popPool.get(i);
            x1=currentCh;
            x2=popPool.get(EvolutionUtils.rnd.nextInt(popPool.size()));
            x3=popPool.get(EvolutionUtils.rnd.nextInt(popPool.size()));
            x4=popPool.get(EvolutionUtils.rnd.nextInt(popPool.size()));
            xb=localBest.getAliveClone();

            res=DEOperator(F,xb,x1,x2,x3,x4);
            int jRand;
            jRand=EvolutionUtils.rnd.nextInt(res.gens.length);

            for(int j=0;j<res.gens.length;j++){
                if(EvolutionUtils.rnd.nextDouble()>Cr && j!=jRand){
                    res.gens[j]=currentCh.gens[j];
                }
            }

            clipToBound(res,EvolutionUtils.benchmark.minCoordinate,EvolutionUtils.benchmark.maxCoordinate);
            res.fitness=eval(res);
            if(res.fitness>=currentCh.fitness){
                addNewInd(res);
            }else{
                addInd(currentCh,false);
            }

            if(res.fitness>currentCh.fitness)
                winNum++;
        }
        
    }

    public Chromosome DEOperator(double F,Chromosome xb,Chromosome x1,Chromosome x2,Chromosome x3,Chromosome x4){
        Chromosome newCh=new Chromosome(xb.gens.length);
        for(int i=0;i<newCh.gens.length;i++){
            if(EvolutionUtils.pars.DEScheme==1){
                newCh.gens[i]=x1.gens[i]+F*(x2.gens[i]-x3.gens[i]);
            }else{
            if(EvolutionUtils.pars.DEScheme==2){
                newCh.gens[i]=x1.gens[i]+F*(xb.gens[i]-x2.gens[i]);
            }else{
            if(EvolutionUtils.pars.DEScheme==3){
                newCh.gens[i]=xb.gens[i]+EvolutionUtils.rnd.nextDouble()*(x1.gens[i]-x3.gens[i])+EvolutionUtils.rnd.nextDouble()*(x2.gens[i]-x4.gens[i]);
            }else{
            if(EvolutionUtils.pars.DEScheme==4){
                newCh.gens[i]=x1.gens[i]+F*(xb.gens[i]-x1.gens[i])+EvolutionUtils.rnd.nextDouble()*(x2.gens[i]-x3.gens[i]);
            }
            }
            }
            }

        }
        if(EvolutionUtils.rnd.nextDouble()<EvolutionUtils.pars.mutProb){
            int ttt=EvolutionUtils.rnd.nextInt(newCh.gens.length);
            newCh.gens[ttt]=newCh.gens[ttt]+EvolutionUtils.rnd.nextGaussian();
        }
        return newCh;
    }

    public void randomLocalSearch(){
        ArrayList<Chromosome> popPool=new ArrayList<Chromosome>();
        popPool.addAll(currentState.population);
        for(int i=0;i<currentState.population.size();i++){
            Chromosome currentCh;
            currentCh=popPool.get(i);
            double sum;
            Chromosome newCh;
            newCh=new Chromosome(currentCh.gens.length);
            sum=0;
            for(int j=0;j<newCh.gens.length;j++){
                newCh.gens[j]=EvolutionUtils.rnd.nextDouble()-0.5;
                sum=sum+newCh.gens[j]*newCh.gens[j];
            }
            sum=Math.sqrt(sum);
            double r;
            r=EvolutionUtils.rnd.nextDouble();
            for(int j=0;j<currentCh.gens.length;j++){
                newCh.gens[j]=(newCh.gens[j]/sum)*r+localBest.gens[j];
            }
            clipToBound(newCh,EvolutionUtils.benchmark.minCoordinate,EvolutionUtils.benchmark.maxCoordinate);
            newCh.fitness=eval(newCh);
            if(currentCh.fitness>=newCh.fitness)
                addInd(currentCh,false);
            else
                addNewInd(newCh);
        }
    }
    

    public void controlDensity(){
        int theta=currentState.theta;

        if(EvolutionUtils.benchmark.itSinceLastChange>=EvolutionUtils.pars.localSearchNum && EvolutionUtils.benchmark.itSinceLastChange<=EvolutionUtils.pars.localSearchNum)
            theta=EvolutionUtils.pars.thetaInit/2;

        Collections.sort((List)nextState.population);
        if(nextState.population.size()>theta){
            int nOD=EvolutionUtils.benchmark.genNum();
            while(nextState.population.size()>theta){
                Chromosome ch;
                ch=new Chromosome(nOD,EvolutionUtils.benchmark.minCoordinate,EvolutionUtils.benchmark.maxCoordinate);
                ch.fitness=eval(ch);
                addNewInd(ch);
                nextState.population.remove(nextState.population.size()-1);
            }
        }
    }

    public void refreshMems(){
        if(!currentState.cellMem.isEmpty())
            currentState.cellMem.fitness=EvolutionUtils.benchmark.evalDummy(currentState.cellMem.gens);
        if(currentState.cellMem.periodNum!=EvolutionUtils.benchmark.periodNum){
            if(!currentState.cellMem.isEmpty())
                currentState.cellMem.fitness=EvolutionUtils.benchmark.evalDummy(currentState.cellMem.gens);
        }
        currentState.cellMem.periodNum=EvolutionUtils.benchmark.periodNum;
    }
    
    public void setCurrentBest(){
        Chromosome best=null;
        for(int i=0;i<currentState.population.size();i++){
            Chromosome tmp;
            tmp=currentState.population.get(i);
            if(best==null || tmp.fitness>=best.fitness)
                best=tmp;
        }
        if(currentBest==null)
            currentBest=new ChromosomeMem();
        currentBest.update(best);
    }
    
    public void cMemUpdate(){
        refreshMems();
        if(currentBest!=null){
            if(currentState.cellMem.isEmpty()){
                currentState.cellMem.update(currentBest);
            }else{
                if(currentBest.fitness>=currentState.cellMem.fitness)
                    currentState.cellMem.update(currentBest);
            }
        }
    }
    
    public void setLocalBest(){
        bestNCell=this;
        if(localBest==null)
            localBest=new ChromosomeMem();
        localBest.update(currentState.cellMem);
        
        for(int i=0;i<neighbors.length;i++){
            if(!neighbors[i].currentState.cellMem.isEmpty()){
                if(neighbors[i].currentState.cellMem.periodNum!=EvolutionUtils.benchmark.periodNum){
                    neighbors[i].currentState.cellMem.fitness=EvolutionUtils.benchmark.evalDummy(neighbors[i].currentState.cellMem.gens);
                    neighbors[i].currentState.cellMem.periodNum=EvolutionUtils.benchmark.periodNum;
                }

                if(localBest.isEmpty()){
                    localBest.update(neighbors[i].currentState.cellMem);
                    bestNCell=neighbors[i];
                }else{
                    if(neighbors[i].currentState.cellMem.fitness>=localBest.fitness){
                        localBest.update(neighbors[i].currentState.cellMem);
                        bestNCell=neighbors[i];
                    }
                }
            }
        }
    }
    
    public void addNewInd(Chromosome ind){
        clipToBound(ind,EvolutionUtils.benchmark.minCoordinate,EvolutionUtils.benchmark.maxCoordinate);
        int[] indPartition=EvolutionUtils.getPartition(ind,EvolutionUtils.benchmark);
        if(EvolutionUtils.samePartition(indPartition,partition)){
            addInd(ind,false);
        }else{
            ind.desCell=EvolutionUtils.getLinearPos(indPartition,EvolutionUtils.benchmark);
            ind.hop=getDist(partition,indPartition);
            addInd(ind,false);
        }
    }

    public void addInd(Chromosome ind,boolean isCurrent){
        if(ind.hop>0){
            EvolutionUtils.inactivePopulation.add(ind);
        }else{
            EvolutionUtils.updateCandidates.add(this);
            if(isCurrent)
                currentState.addIndivisual(ind);
            else{
                nextState.addIndivisual(ind);
            }
        }
    }
    
    public double eval(Chromosome ch){
        double tmp;
        tmp=eval(ch.gens);
        return tmp;
    }
    
    public double eval(double[] gens){
        double tmp;
        tmp=EvolutionUtils.benchmark.eval(gens);
        return tmp;
    }
    
    public int getDist(int[] src,int[] des){
        int max=0;
        for(int i=0;i<src.length;i++){
            if(Math.abs(src[i]-des[i])>max)
                max=Math.abs(src[i]-des[i]);
        }
        return (int)Math.ceil(((double)max)/EvolutionUtils.pars.neighborhoodSize);
    }
    
    public void clipToBound(Chromosome ch,double[] min,double[] max){
        for(int i=0;i<ch.gens.length;i++){
            if(ch.gens[i]<min[i])
                ch.gens[i]=min[i];
            if(ch.gens[i]>max[i])
                ch.gens[i]=max[i];
        }
    }
    
    public int compareTo(Object ob1){
        for(int i=0;i<((Cell)ob1).partition.length;i++){
            if(((Cell)ob1).partition[i]<this.partition[i] )
                return 1;
            if(((Cell)ob1).partition[i]>this.partition[i] )
                return -1;
        }
        return 0;
    }
    
    public boolean equals(Object ob1){
        for(int i=0;i<((Cell)ob1).partition.length;i++){
            if(((Cell)ob1).partition[i]!=this.partition[i] )
                return false;
        }
        return true;
    }
}