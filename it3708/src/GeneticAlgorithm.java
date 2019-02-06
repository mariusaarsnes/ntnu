import java.util.ArrayList;
import java.util.Comparator;

public class GeneticAlgorithm {

    // Set Genetic algorithm parameters

    Problem problem;
    ArrayList<Genotype> population;
    double[] populationFitness;


    int populationSize, generationNumber, crossoverRate, mutationRate;


    public GeneticAlgorithm(Problem problem, int populationSize, int generationNumber, int crossoverRate, int mutationRate) {
        this.problem = problem;
        this.populationSize = populationSize;
        this.generationNumber = generationNumber;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.populationFitness = new double[this.populationSize];

    }

    public void run() {

        // Initialise random population
        initRandomPop();
        for (int currentGeneration = 0; currentGeneration < this.generationNumber; currentGeneration++) {

            evaluatePopulation();

            // If optimization criteria are met end, else
            if (thresholdIsMet()) {
                break;
            }

            // Select parents for next generation


            // Crossover of parents chromosomes

            // Mutation of chromosome

        }
    }

    // Generate initial random population
    public void initRandomPop() {

        ArrayList<Genotype> population = new ArrayList<Genotype>();
        for (int p =0; p< this.populationSize; p++) {
            population.add(new Genotype(this.problem));
        }
        this.population = population;
    }

    /**
     * Helper function to create an initial individual
     * The method first creates a grouping.
     * Then, routes are generated out of each depot before scheduling is done
     * The result is
     *
     * @return
     */
    public ArrayList<Integer>[][] generateNewIndividual() {

        int[] grouping = new int[this.problem.numberOfCustomers];
        for (int i = 0; i < grouping.length; i++) {
            grouping[i] = this.problem.closestDepotToCustomers[i];
        }

        ArrayList<Integer>[][] routing = new ArrayList[this.problem.numberOfDepots][this.problem.maxVehiclesPerDepot];


        for (int i = 0; i < grouping.length; i++) {
            int depot = grouping[i];

            int vehicle = (int) (Math.random() * this.problem.maxVehiclesPerDepot);

            if (routing[depot][vehicle] == null) {
                routing[depot][vehicle] = new ArrayList<Integer>();
            }
            routing[depot][vehicle].add(i);

        }

        //TODO: Ask TA if it is necessary to have final depot in genotype or if this can be derived in the phenotype.
        // If not, then remove this part, else update distance and validity tests

        for (int i = 0; i < routing.length; i++) {
            System.out.println("Depot: " + i);
            for (int j = 0; j < routing[i].length; j++) {
                // In case the route for a given vehicle is empty we skip it, since it will never drive anywhere
                System.out.println("Route: " + routing[i][j]);
                if (routing[i][j].size() == 0) {
                    continue;
                }
                int lastCustomer = routing[i][j].get(routing[i][j].size() - 1);
                routing[i][j].add(this.problem.closestDepotToCustomers[lastCustomer]);
            }
        }
        return routing;
    }

    /**
     * Check if an individual is valid
     *
     * @param routing 2d array containing all routes for an individual
     * @return
     */
    private boolean isValidIndividual(ArrayList<Integer>[][] routing) {
        for (int d = 0; d < routing.length; d++) {
            for (int r = 0; r < routing[d].length; r++) {
                if (!isValidRoute(d, routing[d][r])) {
                    return false;
                }
            }
        }
        return true;
    }

    private int getRouteDurationOfRoute(ArrayList<Integer> route) {
        int duration = 0;
        for (int customer : route
        ) {
            duration += this.problem.customers[customer].serviceDuration;
        }
        return duration;
    }

    private int getCapacityOfRoute(ArrayList<Integer> route) {
        int capacity = 0;
        for (int customer : route
        ) {
            capacity += this.problem.customers[customer].demand;
        }
        return capacity;
    }

    /**
     * Check whether or not a route is valid based on problem constraints
     *
     * @param depot the position of the start depot
     * @param route the route to be checked
     * @return True if duration and capacity constraints are not exceeded
     */
    private boolean isValidRoute(int depot, ArrayList<Integer> route) {
        int durationSum = 0;
        int capacitySum = 0;
        for (int c = 0; c < route.size(); c++) {
            durationSum += this.problem.customers[route.get(c)].serviceDuration;
            capacitySum += this.problem.customers[route.get(c)].demand;
            if (
                    durationSum > this.problem.depots[depot].maxDuration || capacitySum > this.problem.depots[depot].maxLoad) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the position of a depot or customer
     *
     * @param type     "customer" or "depot" depending on what you want the position of
     * @param arrayPos position of the element in either problem.depots or problem.customer array
     * @return an array containing x and y position
     */
    private int[] getPosition(String type, int arrayPos) {
        switch (type) {
            case "depot":
                return new int[]{this.problem.depots[arrayPos].x, this.problem.depots[arrayPos].y};
            case "customer":
                return new int[]{this.problem.customers[arrayPos].x, this.problem.customers[arrayPos].y};
        }
        return new int[]{};
    }

    /**
     * Get Euclidean distance between two positions
     *
     * @param startPos
     * @param endPos
     * @return distance
     */
    private double getDistance(int[] startPos, int[] endPos) {
        return Math.hypot(startPos[0] - endPos[0], startPos[1] + endPos[1]);
    }

    /**
     * Calculate the total distance traveled for a route.
     *
     * @param depot starting depot of route
     * @param route the customers on the route
     * @return total distance traveled on the route
     */
    private double getDistanceOfRoute(int depot, ArrayList<Integer> route) {
        double distance = getDistance(
                getPosition("depot", depot),
                getPosition("customer", route.get(0))
        );
        int c;
        for (c = 1; c < route.size(); c++) {
            distance += getDistance(
                    getPosition("customer", route.get(c - 1)),
                    getPosition("customer", route.get(c))
            );
        }
        distance += getDistance(
                getPosition("customer", route.get(c)),
                getPosition("depot", this.problem.closestDepotToCustomers[route.get(c)])
        );
        return distance;
    }


    /**
     * Return the fitness of an individual
     *
     * @param individual
     * @return
     */
    public double getFitness(ArrayList<Integer>[][] individual) {
        //TODO: Ask TA if this representation of fitness is ok
        double totalDistanceOfIndividual = 0;
        for (int d = 0; d < individual.length; d++) {
            for (int r = 0; r < individual[d].length; r++) {
                totalDistanceOfIndividual += getDistanceOfRoute(d, individual[d][r]);
            }
        }
        return totalDistanceOfIndividual - this.problem.solutionThreshold;
    }


    private boolean thresholdIsMet() {
        for (double individualFitness : this.populationFitness) {
            if (individualFitness <= this.problem.solutionThreshold) return true;
        }
        return false;
    }

    public void evaluatePopulation() {
        this.populationFitness = new double[this.populationSize];
        for (int i = 0; i< this.populationSize; i++){
            this.populationFitness[i] = this.population.get(i).getFitness();
        }

    }
}
