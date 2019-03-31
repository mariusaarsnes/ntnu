import java.util.ArrayList;
import java.util.Comparator;

public class BeeSolution extends Solution{

    ArrayList<Integer> path;
    int neighbourhood;
    int age = 0;

    public BeeSolution(int[][][] schedule, ArrayList<Integer> path){
        super(schedule);
        this.path = path;
    }
}

class MakespanComparator implements Comparator<BeeSolution> {
    @Override
    public int compare(BeeSolution x , BeeSolution y) {
        return Double.compare(y.makespan,x.makespan);
    }
}

