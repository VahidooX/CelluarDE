package CellularDE;
import java.util.*;

public class CellState{
    public ArrayList<Chromosome> population;
    public int theta;

    public ChromosomeMem cellMem;
    
    public double F;
    public double Cr;

    public void addIndivisual(Chromosome ind){
        if(!population.contains(ind))
            population.add(ind);
    }

    public CellState(){
        theta=-1;
        population=new ArrayList<Chromosome>();

        cellMem=new ChromosomeMem();
        F=-1;
        Cr=-1;
    }
}