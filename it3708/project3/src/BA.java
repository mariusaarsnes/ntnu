import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class BA {
    FileParser fp;
    int total, populationSize, generations, bestPatchSize, elitePatchSize, bestPatchBeeCount, elitePatchBeeCount, neighbourhoodSize, errorLimit, bestPossibleMakespan;
    Comparator<BeeSolution> makespanComparator;
    Random random;

    boolean stopOnPercent;

    BeeSolution bestGlobalBeeSolution;
    ArrayList<BeeVertex> vertices;
    BeeVertex root;

    public BA(FileParser fp, int populationSize, int generations, int bestPatchSize, int elitePatchSize, int bestPatchBeeCount, int elitePatchBeeCount, int neighbourhoodSize, int errorLimit, int bestPossibleMakespan, boolean stopOnPercent) {
        this.fp = fp;
        this.total = this.fp.jobCount * this.fp.machineCount;
        this.populationSize = populationSize;
        this.generations = generations;
        this.bestPatchSize = bestPatchSize;
        this.elitePatchSize = elitePatchSize;
        this.bestPatchBeeCount = bestPatchBeeCount;
        this.elitePatchBeeCount = elitePatchBeeCount;
        this.neighbourhoodSize = neighbourhoodSize;
        this.errorLimit = errorLimit;
        this.bestPossibleMakespan = bestPossibleMakespan;
        this.stopOnPercent = stopOnPercent;
        this.random = new Random();

        this.makespanComparator = new MakespanComparator();

        makeGraph();
    }

    /**
     * makeGraph() Generates a graph from the  jobs in the problem
     */
    private void makeGraph() {
        BeeVertex root = new BeeVertex(-1, -1, -1);
        root.edges = new BeeVertex[this.fp.jobCount];
        ArrayList<BeeVertex> vertices = new ArrayList<>();
        vertices.add(root);
        for (int i = 0; i < this.fp.jobCount; i++) {
            BeeVertex neighbour = new BeeVertex(this.fp.jobs[i].requirements[0][0], this.fp.jobs[i].requirements[0][1], this.fp.jobs[i].id);
            vertices.add(neighbour);
            root.edges[i] = neighbour;
        }
        this.root = root;
        this.vertices = vertices;
    }


    public Solution run(int iterations, int beeCount, boolean stopOnPercent) {
        //Step 1: Initialise population

        ArrayList<BeeSolution> flowerPatches = generateInitialPopulation();
        this.bestGlobalBeeSolution = flowerPatches.get(0);
        //Run iterations

        for (int i = 0; i < this.generations; i++) {
            //TODO consider implementing dynamic sizes based on population count

            // Selecting neighbourhood sizes based on if the flowerpatch is an elite, non-elite best or normal
            for (int j = 0; j < flowerPatches.size(); j++) {
                if (j <= this.elitePatchSize) {
                    flowerPatches.get(j).neighbourhood = (int) (this.total * 0.1);
                } else if (j <= this.bestPatchSize) {
                    flowerPatches.get(j).neighbourhood = (int) (this.total * 0.2);
                } else {
                    flowerPatches.get(j).neighbourhood = (int) (this.total * 0.5);
                }
            }

            //Generate new population
            ArrayList<BeeSolution> newPopulation = new ArrayList<>();
            for (int j = 0; j < this.populationSize; j++) {
                BeeSolution flowerPatch = flowerPatches.get(j);

                if (j < this.bestPatchBeeCount) {
                    newPopulation.add(findSolution(flowerPatch, this.random.nextInt(flowerPatch.neighbourhood) + 1));
                } else {
                    if (this.random.nextDouble() < 0.5) {
                        BeeSolution randomBee = flowerPatches.get(this.random.nextInt(flowerPatches.size()));
                        newPopulation.add(findSolution(randomBee, randomBee.neighbourhood));
                    } else {
                        newPopulation.add(findSolution(null, 0));
                    }
                }
            }
            flowerPatches = newPopulation;


            //sort and check for termination
            flowerPatches.sort(this.makespanComparator);
            if (flowerPatches.get(0).makespan < this.bestGlobalBeeSolution.makespan) {
                this.bestGlobalBeeSolution = flowerPatches.get(0);

                if ((double) (this.bestPossibleMakespan / this.bestGlobalBeeSolution.makespan) >= 0.9 && this.stopOnPercent) {
                    return this.bestGlobalBeeSolution;
                }
            }
        }
        return this.bestGlobalBeeSolution;
    }

    private ArrayList<BeeSolution> generateInitialPopulation() {
        ArrayList<BeeSolution> flowerPatches = new ArrayList<>();
        for (int i = 0; i < this.populationSize; i++) {
            flowerPatches.add(findSolution(null, 0));
        }
        flowerPatches.sort(this.makespanComparator);
        return flowerPatches;
    }


    private BeeSolution findSolution(BeeSolution beeSolution, int neighbourhoodSize) {
        
    }
}