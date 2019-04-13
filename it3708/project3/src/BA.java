import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class BA {
    FileParser fp;
    int[] bestMakespanPerGeneration;
    int total,count, populationSize, generations, neighbourhoodSize, errorLimit, bestPossibleMakespan;
    Comparator<BeeSolution> makespanComparator;
    Random random;

    boolean stopOnPercent;

    BeeSolution bestGlobalBeeSolution;
    ArrayList<BeeVertex> vertices;
    BeeVertex root;

    public BA(FileParser fp, int populationSize, int generations, int neighbourhoodSize, int errorLimit, int bestPossibleMakespan, boolean stopOnPercent) {
        this.fp = fp;
        this.total = this.fp.jobCount * this.fp.machineCount;
        this.populationSize = populationSize;
        this.generations = generations;
        this.neighbourhoodSize = neighbourhoodSize;
        this.errorLimit = errorLimit;
        this.bestPossibleMakespan = bestPossibleMakespan;
        this.stopOnPercent = stopOnPercent;
        this.random = new Random();
        this.count = 0;

        this.makespanComparator = new MakespanComparator();
        this.bestMakespanPerGeneration = new int[generations];

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
            BeeVertex neighbour = new BeeVertex(this.fp.jobs[i].requirements[0][0],this.fp.jobs[i].id, this.fp.jobs[i].requirements[0][1]);
            vertices.add(neighbour);
            root.edges[i] = neighbour;
        }
        this.root = root;
        this.vertices = vertices;
    }


    public Solution run() {
        //Step 1: Initialise population

        ArrayList<BeeSolution> flowerPatches = generateInitialPopulation();
        this.bestGlobalBeeSolution = flowerPatches.get(0);
        //Run iterations

        for (int i = 0; i < this.generations; i++) {
            double bestSiteCount = 0.2 * flowerPatches.size(); //Number of best sites are at 20% of the population
            double eliteSiteCount = 0.1 * flowerPatches.size(); // Number of elite sites are at 10% of the population

            double bestSiteBees = 0.8 * this.populationSize;

            // Selecting neighbourhood sizes based on if the flowerpatch is an elite, non-elite best or normal
            for (int j = 0; j < flowerPatches.size(); j++) {
                if (j <= eliteSiteCount) {
                    flowerPatches.get(j).neighbourhood = (int) (this.total * 0.1);
                } else if (j> eliteSiteCount && j <= eliteSiteCount+bestSiteCount) {
                    flowerPatches.get(j).neighbourhood = (int) (this.total * 0.2);
                } else {
                    flowerPatches.get(j).neighbourhood = (int) (this.total * 0.5);
                }
            }

            //Generate new population
            ArrayList<BeeSolution> newPopulation = new ArrayList<>();
            for (int j = 0; j < this.populationSize; j++) {
                BeeSolution flowerPatch = flowerPatches.get(j);

                if (j < bestSiteBees) {
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
            this.bestMakespanPerGeneration[i] = flowerPatches.get(0).makespan;
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
        int[] visited = new int[this.fp.jobCount];
        int[] jobTime = new int[this.fp.jobCount];
        int[] machineTime = new int[this.fp.jobCount];
        int[][][] path = new int[this.fp.machineCount][this.fp.jobCount][2];

        int makespan = 0;
        BeeVertex current = this.root;
        ArrayList<Integer> vertexPath = new ArrayList<>();

        // Perform neighbourhood search
        if (beeSolution != null){
            for (int k = 0; k < beeSolution.path.size() - neighbourhoodSize; k++) {
                vertexPath.add(beeSolution.path.get(k));
                current = current.edges[beeSolution.path.get(k)];
                visited[current.jobNumber] ++;

                int machineNumber = current.machineNumber;
                int jobNumber = current.jobNumber;
                int timeRequired = current.timeRequired;

                //Start time
                int startTime = Math.max(jobTime[jobNumber], machineTime[machineNumber]);
                path[machineNumber][jobNumber][0] = startTime;
                //Time required
                path[machineNumber][jobNumber][1] = timeRequired;
                //Updating variables
                int time = startTime + timeRequired;
                jobTime[jobNumber] = time;
                machineTime[machineNumber] = time;
                if (time > makespan) {
                    makespan = time;
                }
            }
        }

        while (vertexPath.size() != this.total) {
            int index = selectPath(current, jobTime, machineTime, makespan);

            if (index == -1) {
                return findSolution(null,0);
            }

            vertexPath.add(index);
            current = current.edges[index];
            visited[current.jobNumber] ++;

            int machineNumber = current.machineNumber;
            int jobNumber = current.jobNumber;
            int timeRequired = current.timeRequired;

            //Start time
            int startTime = Math.max(jobTime[jobNumber],machineTime[machineNumber]);
            path[machineNumber][jobNumber][0] = startTime;
            //Time required
            path[machineNumber][jobNumber][1] = timeRequired;
            // Updating variables
            int time = startTime + timeRequired;
            jobTime[jobNumber] = time;
            machineTime[machineNumber] = time;
            if (time > makespan) {
                makespan = time;
            }

            // New Vertex
            if (current.edges == null) {
                this.count++;

                //Adding next option
                ArrayList<BeeVertex> choices = new ArrayList<>();
                for(int i = 0; i < this.fp.jobCount; i++) {
                    if (visited[i] < this.fp.machineCount) {
                        int neighbourMachineNumber = this.fp.jobs[i].requirements[visited[i]][0];
                        int neighbourTimeRequired = this.fp.jobs[i].requirements[visited[i]][1];
                        BeeVertex neighbour = new BeeVertex(neighbourMachineNumber,this.fp.jobs[i].id, neighbourTimeRequired);
                        choices.add(neighbour);
                        addVertex(neighbour);
                    }
                }
                current.edges = new BeeVertex[choices.size()];
                choices.toArray(current.edges);
            }
        }
        BeeSolution newSolution = new BeeSolution(path,vertexPath);
        return newSolution;
    }


    private synchronized  int selectPath(BeeVertex current, int[] jobTime, int[] machineTime, int makespan) {
        double a = 1.0, b = 1.0;
        double denominator = 0;
        final double[] probability = new double[current.edges.length];
        for (int i = 0; i< probability.length; i++) {
            probability[i] = Math.pow((heuristic(current.edges[i], jobTime,machineTime,makespan)),b);
            denominator += probability[i];
        }

        if (denominator == 0.0) {
            return this.random.nextInt(current.edges.length);
        }

        double cumulativeProbability = 0;
        double threshold = Math.random();
        for(int i = 0; i < current.edges.length; i++) {
            cumulativeProbability += probability[i] / denominator;
            if(threshold <= cumulativeProbability) {
                return i;
            }
        }
        return -1;
    }

    private synchronized void addVertex(BeeVertex vertex) {
        this.vertices.add(vertex);
    }

    private synchronized double heuristic(BeeVertex vertex, int[] jobTime, int[] machineTime, int makespan) {
        int startTime = Math.max(jobTime[vertex.jobNumber],machineTime[vertex.machineNumber]);
        double heuristic = makespan -(startTime+ vertex.timeRequired);
        if(heuristic < 0.0){
            return 0;
        }
        return heuristic;
    }
}