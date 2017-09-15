package CellularDE;
import java.util.*;

public class EvolutionUtils {
    public static Parameters pars=new Parameters();;
    public static Random rnd;
    public static Benchmark benchmark;
    public static ArrayList<Chromosome> inactivePopulation;
    public static TreeSet<Cell> updateCandidates;
    
    public final static double bigMin=Double.NEGATIVE_INFINITY;


    public static void initRandom(long seed){
        rnd=new Random(seed);
    }

    public static int getLinearPos(int[] partition,Benchmark bm){
        int pos=0;
        int order=1;
        for(int i=partition.length-1;i>=0;i--){
            if(partition[i]<0 || partition[i]>=pars.partitionsNum)
                return -1;
            pos=pos+order*partition[i];
            order=order*pars.partitionsNum;
        }
        return pos;
    }

    public static boolean samePartition(int[] p1,int[] p2){
        for(int i=0;i<p1.length;i++){
            if(p1[i]!=p2[i])
                return false;
        }
        return true;
    }

    public static double toBound(double num,double min,double max){
        return (num*(max-min))+min;
    }

    public static int[] getPartition(Chromosome ch,Benchmark bm){
        int[] partition=new int[ch.gens.length];
        for(int i=0;i<partition.length;i++){
            partition[i]=(int) ((ch.gens[i]-bm.minCoordinate[i]) / ((bm.maxCoordinate[i]-bm.minCoordinate[i])/(double)pars.partitionsNum));
            if(partition[i]>=pars.partitionsNum)
                partition[i]=pars.partitionsNum-1;
        }
        return partition;
    }    
}
