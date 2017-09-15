package CellularDE;
import java.util.*;

public class CA {
    public Cell[] cells;
    public int numberOfCells;
    public ArrayList<Chromosome> inactivePopulation;
    TreeSet<Cell> updateCandidates;

    public CA(){
        updateCandidates=new TreeSet<Cell>();
        inactivePopulation=new ArrayList<Chromosome>();
        EvolutionUtils.updateCandidates=updateCandidates;
        EvolutionUtils.inactivePopulation=inactivePopulation;
        
        EvolutionUtils.benchmark.itSinceLastChange=0;
        EvolutionUtils.benchmark.isChanged=false;
        EvolutionUtils.benchmark.periodNum=0;
        
        numberOfCells=(int)Math.pow(EvolutionUtils.pars.partitionsNum,EvolutionUtils.pars.dimensionsNum);
        cells=new Cell[numberOfCells];

        for(int i=0;i<numberOfCells;i++)
            cells[i]=new Cell(getPartition(i));
     
        for(int i=0;i<numberOfCells;i++){
            cells[i].neighbors=getNeighbors(i,EvolutionUtils.pars.neighborhoodSize);
        }

        for(int i=0;i<numberOfCells;i++){
            cells[i].init();
        }
        
        initEvolution();
    }
    
    public void initEvolution(){
        initPars();
        initPopulation();
        initBests();
    }
    
    public void reInit(){
        updateCandidates.clear();
        inactivePopulation.clear();
        EvolutionUtils.benchmark.itSinceLastChange=0;
        EvolutionUtils.benchmark.isChanged=false;
        EvolutionUtils.benchmark.periodNum=0;
        numberOfCells=(int)Math.pow(EvolutionUtils.pars.partitionsNum,EvolutionUtils.pars.dimensionsNum);
        for(int i=0;i<numberOfCells;i++)
            cells[i].init();
        initEvolution();
    }
    
    public void initPopulation(){
        for(int i=0;i<EvolutionUtils.pars.populationSize;i++){
            Chromosome ch=new Chromosome(EvolutionUtils.benchmark.dimensionsNum);
            for(int j=0;j<EvolutionUtils.benchmark.dimensionsNum;j++){
                ch.gens[j]=EvolutionUtils.toBound(EvolutionUtils.rnd.nextDouble(),EvolutionUtils.benchmark.minCoordinate[j],EvolutionUtils.benchmark.maxCoordinate[j]);
            }
            ch.fitness=EvolutionUtils.benchmark.eval(ch);
            cells[EvolutionUtils.getLinearPos(EvolutionUtils.getPartition(ch,EvolutionUtils.benchmark),EvolutionUtils.benchmark)].addInd(ch,true);
        }
    }
    
    public void initPars(){
        for(int i=0;i<cells.length;i++){
            cells[i].currentState.theta=EvolutionUtils.pars.thetaInit;
            cells[i].currentState.F=EvolutionUtils.pars.FInit;
            cells[i].currentState.Cr=EvolutionUtils.pars.CrInit;
        }
    }

    public void initBests(){
        Iterator<Cell> it;
        it=updateCandidates.iterator();
        while(it.hasNext()){
            Cell c;
            c=it.next();
            c.setCurrentBest();
            c.currentState.cellMem.update(c.currentBest);
        }
        it=updateCandidates.iterator();
        while(it.hasNext()){
            Cell c;
            c=it.next();
            c.setLocalBest();
        }
    }

    public void update(){
        EvolutionUtils.benchmark.itSinceLastChange=EvolutionUtils.benchmark.itSinceLastChange+1;
        Iterator<Cell> it;
        ArrayList<Cell> actionUpdateCands=new ArrayList<Cell>();
        
        TreeSet<Cell> cellForUpdate=(TreeSet<Cell>)updateCandidates.clone();
        TreeSet<Cell> cellForNeighborUpdate=(TreeSet<Cell>)updateCandidates.clone();
        updateCandidates.clear();
        
        it=cellForUpdate.iterator();
        while(it.hasNext()){
            Cell c;
            c=it.next();
            updateCandidates.add(c);
            if(EvolutionUtils.benchmark.isChanged){
                c.nextState=c.currentState;
            }else{
                c.update();
            }
            actionUpdateCands.add(c);
        }

        movInactives(cellForUpdate);

        it=cellForUpdate.iterator();
        while(it.hasNext()){
            Cell c;
            c=it.next();
            c.currentState=c.nextState;
            c.nextState=null;
            if(c.currentState.population.isEmpty()){
                updateCandidates.remove(c);
            }
        }
        
        if(EvolutionUtils.benchmark.isChanged){
            for(int j=0;j<inactivePopulation.size();j++){
                Chromosome ch;
                ch=inactivePopulation.get(j);
                ch.fitness=EvolutionUtils.benchmark.eval(ch.gens);
            }
            it=updateCandidates.iterator();
            while(it.hasNext()){
                Cell c;
                c=it.next();
                for(int j=0;j<c.currentState.population.size();j++){
                    Chromosome ch;
                    ch=c.currentState.population.get(j);
                    ch.fitness=EvolutionUtils.benchmark.eval(ch.gens);
                }
            }
        }
        
        it=cellForUpdate.iterator();
        while(it.hasNext()){
            Cell c;
            c=it.next();
            c.setCurrentBest();
            c.cMemUpdate();
        }

        if(EvolutionUtils.benchmark.isChanged){
            EvolutionUtils.benchmark.itSinceLastChange=0;
            EvolutionUtils.benchmark.isChanged=false;
        }

    }

    public void movInactives(TreeSet<Cell> cellForUpdate){
        for(int i=0;i<inactivePopulation.size();i++){
            Chromosome tmpCh=inactivePopulation.get(i);
            if(tmpCh.hop==1){
                tmpCh.hop=-1;
                if(cells[tmpCh.desCell].nextState==null){
                    cells[tmpCh.desCell].initNextState();
                }
                cells[tmpCh.desCell].addInd(tmpCh, false);
                cellForUpdate.add(cells[tmpCh.desCell]);
                tmpCh.desCell=-1;
                inactivePopulation.remove(i);
            }else{
                tmpCh.hop=tmpCh.hop-1;
            }
        }
    }
    
    public Cell[] getNeighbors(int cellNum,int neighborhoodSize){
        ArrayList<Cell> nTmp =new ArrayList<Cell>((int)Math.pow(neighborhoodSize*2+1,EvolutionUtils.benchmark.dimensionsNum));
        boroBinim(0,nTmp,cellNum,neighborhoodSize);
        Cell[] neighbors=new Cell[nTmp.size()];
        return nTmp.toArray(neighbors);
    }
    
    public void boroBinim(int i,ArrayList<Cell> l,int cellNum,int neighborhoodSize){
        int tmp;
        if(i>=cells[cellNum].partition.length){
            tmp=EvolutionUtils.getLinearPos(cells[cellNum].partition,EvolutionUtils.benchmark);
            if(tmp>=0 && tmp!=cellNum){
                l.add(cells[tmp]);
            }
            return;
        }
        for(int k=-neighborhoodSize;k<=neighborhoodSize;k++){
            if(cells[cellNum].partition[i]+k<EvolutionUtils.pars.partitionsNum && cells[cellNum].partition[i]+k>=0){
                cells[cellNum].partition[i]=cells[cellNum].partition[i]+k;
                boroBinim(i+1,l,cellNum,neighborhoodSize);
                cells[cellNum].partition[i]=cells[cellNum].partition[i]-k;
            }
        }
    }

    public int[] getPartition(int cellNum){
        int[] partition=new int[EvolutionUtils.benchmark.dimensionsNum];
        int order=(int)Math.pow(EvolutionUtils.pars.partitionsNum,EvolutionUtils.benchmark.dimensionsNum-1);
        for(int i=0;i<EvolutionUtils.benchmark.dimensionsNum;i++){
            partition[i]=cellNum/order;
            cellNum=cellNum%order;
            order=order/EvolutionUtils.pars.partitionsNum;
        }
        return partition;
    }
}
