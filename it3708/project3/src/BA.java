import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class BA {
    FileParser fp;
    int bestPossibleMakespan, count;
    Comparator<BeeSolution> makespanComparator;
    Random random;
    ArrayList<BeeVertex> vertices;
    BeeVertex root;
    public BA(FileParser fp, int bestPossibleMakespan) {
        this.fp = fp;
        this.bestPossibleMakespan = bestPossibleMakespan;
        this.random = new Random();
        this.count = 0;

        makeGraph();
    }
    
    /**
     * makeGraph() Generates a graph from the  jobs in the problem
     */
    private void makeGraph() {
        BeeVertex root = new BeeVertex(-1,-1,-1);
        root.edges = new BeeVertex[this.fp.jobCount];
        ArrayList<BeeVertex> vertices = new ArrayList<>();
        vertices.add(root);
        for (int i = 0; i < this.fp.jobCount; i++) {
            BeeVertex neighbour = new BeeVertex(this.fp.jobs[i].requirements[0][0], this.fp.jobs[i].requirements[0][1],this.fp.jobs[i].id);
            vertices.add(neighbour);
            root.edges[i] = neighbour;
        }
        this.root = root;
        this.vertices = vertices;
    }
}