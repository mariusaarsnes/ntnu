import java.util.ArrayList;

public class GeneticAlgorithm {

    // Set Genetic algorithm parameters

    Problem problem;
    ArrayList<Integer>[][][] initialPopulation;
    int populationSize, generationNumber, crossoverRate, mutationRate;


    public GeneticAlgorithm(Problem problem, int populationSize, int generationNumber, int crossoverRate, int mutationRate) {
        this.problem = problem;
        this.populationSize = populationSize;
        this.generationNumber = generationNumber;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;

    }

    // Generate initial random population
    public void initRandomPop() {
        ArrayList<Integer>[][][] population = new ArrayList
                [this.populationSize]
                [this.problem.numberOfDepots]
                [this.problem.numberOfCustomers];
        for (int p = 0; p < this.populationSize; p++) {
            population[p] = generateRandomIndividual();
        }
    }

    /**
     * Helper function to create an initial individual
     * The method first creates a grouping.
     * Then, routes are generated out of each depot before scheduling is done
     * The result is
     *
     * @return
     */
    private ArrayList<Integer>[][] generateRandomIndividual() {

        /*
        CLUSTERING STAGE: First we group the customers
        This is done by assigning each customer a depot based on which depot they are the closest to
         */
        int[] grouping = new int[this.problem.numberOfCustomers];
        for (int i = 0; i < grouping.length; i++) {
            grouping[i] = this.problem.closestDepotToCustomers[i];
        }


        /*

        ROUTING STAGE: Now we create the routes the different vehicles should take
        This is done by assigning each customer to a random vehicle for each depot.
         */

        ArrayList<Integer>[][] routing = new ArrayList[this.problem.numberOfDepots][this.problem.maxVehiclesPerDepot];


        //TODO: Add check for validity of routes. This way we can only focus on distance traveled for fitness
        for (int i = 0; i < grouping.length; i++) {
            int depot = grouping[i];
            int vehicle = (int) (Math.random() * (this.problem.maxVehiclesPerDepot));
            if (routing[depot][vehicle] == null) {
                routing[depot][vehicle] = new ArrayList<Integer>();
            }
            routing[depot][vehicle].add(i);
        }

        //TODO: Ask TA if it is necessary to have final depot in genotype or if this can be derived in the phenotype
        for (int i = 0; i < routing.length; i++) {
            for (int j = 0; j < routing[i].length; j++) {
                // In case the route for a given vehicle is empty we skip it, since it will never drive anywhere
                if (routing[i][j].size() == 0) {
                    continue;
                }
                int lastCustomer = routing[i][j].get(routing[i][j].size() - 1);
                routing[i][j].add(this.problem.closestDepotToCustomers[lastCustomer]);
            }
        }
        return routing;
    }

    private boolean isValidIndividual(ArrayList<Integer>[][] routing){
        //TODO: Write loop for checking if individual is valid. use
        for (ArrayList<Integer>[] depotRoutes: routing) {
            for (ArrayList<Integer> route: depotRoutes){
                route.forEach();
            }
        }
    }


    // Evaluate fitness of each chromosome in the population
    public double evaluateFitness(ArrayList<Integer>[][] individual) {
        /**
         * helper function to evaluate fitness of individuals.
         * The function adds up the total distance traveled for the individual
         * and also adds up the difference in duration and capacity if the constraint is exceeded
         *
         */
        double fitness = 0;

        for (int i = 0; i < individual.length; i++) { // for each depot
            double distanceSum = 0;
            for (int j = 0; j < individual[i].length; j++) { // For each route
                int durationSum = 0;
                int capacitySum = 0;

                for (int c = 0; c < individual[i][j].size() - 1; c++) { // We loop through the whole route except the final destination
                    double segmentDistance;
                    if (c < individual[i][j].size() - 2) {
                        segmentDistance = Math.hypot(
                                this.problem.customers[individual[i][j].get(c)].xPos - this.problem.customers[individual[i][j].get(c + 1)].xPos,
                                this.problem.customers[individual[i][j].get(c)].yPos - this.problem.customers[individual[i][j].get(c + 1)].yPos
                        );
                    } else {
                        segmentDistance = Math.hypot(
                                this.problem.customers[individual[i][j].get(c)].xPos - this.problem.depots[individual[i][j].get(c + 1)].xPos,
                                this.problem.customers[individual[i][j].get(c)].yPos - this.problem.depots[individual[i][j].get(c + 1)].yPos);
                    }
                    distanceSum += segmentDistance;
                    durationSum += this.problem.customers[individual[i][j].get(c)].serviceDuration;
                    capacitySum += this.problem.customers[individual[i][j].get(c)].demand;
                }


                fitness += distanceSum;
                // If the duration or capacity threshold is breached, we subtract this from the fitness value
                double durationDiff, capacityDiff;
                if ((durationDiff = durationSum - this.problem.depots[i].maxDuration) > 0) {
                    fitness += durationDiff;
                }
                if ((capacityDiff = capacitySum - this.problem.depots[i].maxLoad) > 0) {
                    fitness += capacityDiff;
                }
            }
        }

        return fitness;
    }

    // If optimization criteria are met end, else

    // Select parents for next generation

    // Crossover of parents chromosomes

    // Mutation of chromosome
}
