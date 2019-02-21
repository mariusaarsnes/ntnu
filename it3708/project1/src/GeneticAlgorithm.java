import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneticAlgorithm {

    // Set Genetic algorithm parameters

    Problem problem;
    List<Genotype> population;
    double[] populationFitness;
    Genotype bestIndividual;


    int populationSize, generationNumber, crossoverRate, mutationRate, elitismRate;


    public GeneticAlgorithm(Problem problem, int populationSize, int generationNumber, int crossoverRate, int mutationRate, int elitismRate) {
        this.problem = problem;
        this.populationSize = populationSize;
        this.generationNumber = generationNumber;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitismRate = elitismRate;
        this.populationFitness = new double[this.populationSize];
    }

    public void run() {

        // Initialise random population
        initRandomPop();
        int sameBestFitnessCount = 0, mutationBoost = 0;
        this.bestIndividual = getBestIndividualInPopulation();

        for (int currentGeneration = 0; currentGeneration < this.generationNumber; currentGeneration++) {
            sameBestFitnessCount++;
            if (sameBestFitnessCount >= 1000) {
                resetPopulation();
                sameBestFitnessCount = 0;
                mutationBoost = 0;
            }
            evaluatePopulation();
            if (getBestIndividualInPopulation().fitness < bestIndividual.fitness) {
                sameBestFitnessCount = 0;
                mutationBoost = 0;
                this.bestIndividual = getBestIndividualInPopulation();
            }

            if (currentGeneration % 100 == 0) {
                System.out.println("Overall best distance: " + this.bestIndividual.distance + "\t Best fitness: " + this.bestIndividual.fitness);
                System.out.println("Generation: " + currentGeneration + "\t Best distance: " +
                        getBestIndividual().distance + "\t Best fitness: " + getBestIndividual().fitness + "\n");
            }
            if (sameBestFitnessCount % 100 == 0) {
                mutationBoost++;
            }
            if (thresholdIsMet()) {
                break;
            }

            Collections.sort(this.population);
            ArrayList<Genotype> elites = new ArrayList<>(this.population.subList(0, this.populationSize * this.elitismRate / 100));
            List<Genotype> parents = tournamentSelection(this.populationSize / 5, 2);

            Set<Genotype> uniqueParents = new HashSet<>(parents);
            parents.clear();
            parents.addAll(uniqueParents);
            Collections.sort(parents);

            ArrayList<Genotype> newGeneration = new ArrayList<Genotype>();
            Collections.sort(this.population);

            // Crossover of parents chromosomes
            while (newGeneration.size() + elites.size() < this.populationSize) {

                int p1 = (int) (Math.random() * parents.size());
                Genotype parent1 = parents.get(p1);
                Genotype parent2 = parent1;
                while (parent2.equals(parent1)) {
                    int p2 = (int) (Math.random() * parents.size());
                    parent2 = parents.get(p2);
                }
                newGeneration.addAll(crossover(parent1, parent2, currentGeneration));
            }

            for (int i = 0; i < newGeneration.size(); i++) {
                mutate(newGeneration.get(i), mutationBoost);
            }
            newGeneration.addAll(elites);

            this.population = newGeneration;
        }
    }

    public void resetPopulation() {
        Collections.sort(this.population);
        ArrayList<Genotype> elites = new ArrayList<Genotype>(this.population.subList(0, this.populationSize * this.elitismRate * 2 / 100));

        ArrayList<Genotype> newPop = new ArrayList<Genotype>();
        while (newPop.size() < this.populationSize) {
            newPop.addAll(crossover(elites.get((int) (Math.random() * elites.size())), elites.get((int) (Math.random() * elites.size())), 0));
        }
        this.population.subList(this.population.size() - this.populationSize * this.elitismRate / 100, this.population.size()).clear();
        this.population.addAll(elites);
    }

    // Generate initial random population
    public void initRandomPop() {

        List<Genotype> population = new ArrayList<Genotype>();
        for (int p = 0; p < this.populationSize; p++) {
            population.add(new Genotype(this.problem));
        }
        this.population = population;
    }


    public boolean thresholdIsMet() {
        for (double individualFitness : this.populationFitness) {
            if (individualFitness <= this.problem.solutionThreshold + this.problem.solutionThreshold * 0.05)
                return true;
        }
        return false;
    }

    public void evaluatePopulation() {
        this.populationFitness = new double[this.populationSize];
        for (int i = 0; i < this.populationSize; i++) {
            this.population.get(i).updateFitnessVariables(this.problem);
            this.population.get(i).updateFitness(this.problem);
            this.populationFitness[i] = this.population.get(i).getFitness();
        }
    }

    public List<Genotype> tournamentSelection(int tournamentRounds, int tournamentSize) {
        List<Genotype> winners = new ArrayList<Genotype>();

        for (int tr = 0; tr < tournamentRounds; tr++) {
            Collections.shuffle(this.population);
            Genotype p1 = this.population.get(0);

            for (int ts = 1; ts < tournamentSize + 1; ts++) {
                Genotype p2 = this.population.get(ts);

                if (p2.getFitness() < p1.getFitness()) {
                    p1 = p2;
                }
            }
            winners.add(p1);
        }
        return winners;
    }

    public ArrayList<Genotype> crossover(Genotype parent1, Genotype parent2, int currentGeneration) {


        ArrayList<Genotype> offspring;
        Genotype offspring1 = new Genotype(problem, parent1.routes);
        Genotype offspring2 = new Genotype(problem, parent2.routes);

        if (this.crossoverRate <= Math.random() * 100) {
            return new ArrayList<Genotype>(Arrays.asList(offspring1, offspring2));
        }
        if (Math.random() * 100 > 50) {
            offspring = routeCrossover(offspring1, offspring2);
        } else {
            offspring = mergeCrossover(offspring1, offspring2);
        }

        offspring1 = offspring.get(0);
        offspring2 = offspring.get(1);

        return new ArrayList<Genotype>(Arrays.asList(offspring1, offspring2));
    }

    private ArrayList<Genotype> mergeCrossover(Genotype offspring1, Genotype offspring2) {

        int numMerges = (int) (Math.random() * 3);
        for (int i = 0; i < numMerges; i++) {

            ArrayList<Integer> route1 = offspring1.selectRandomInvalidRoute(this.problem);
            ArrayList<Integer> route2 = offspring2.selectRandomInvalidRoute(this.problem);

            if (route1 == null) {
                route1 = offspring1.selectWorstRoute(this.problem);
            }
            if (route2 == null) {
                route2 = offspring2.selectWorstRoute(this.problem);
            }
            for (int customer : route1) {
                offspring2.removeCustomerFromRoutes(customer);
            }
            for (int customer : route2) {
                offspring1.removeCustomerFromRoutes(customer);
            }

            for (int customer : route1) {
                offspring2.addNewCustomerToRoutes(this.problem, customer);
            }
            for (int customer : route2) {
                offspring1.addNewCustomerToRoutes(this.problem, customer);
            }
        }
        return new ArrayList<>(Arrays.asList(offspring1, offspring2));
    }

    private ArrayList<Genotype> routeCrossover(Genotype offspring1, Genotype offspring2) {
        for (int d = 0; d < this.problem.numberOfDepots; d++) {
            for (int r = 0; r < this.problem.maxVehiclesPerDepot; r++) {

                if ((Math.random() * 100) > 50) {
                    offspring2.routes[d][r] = new ArrayList<>(offspring1.routes[d][r]);

                } else {
                    offspring1.routes[d][r] = new ArrayList<>(offspring2.routes[d][r]);
                }

                if (Math.random() * 100 > 70) {

                    int splitPos = (int) (Math.random() * (Integer.max(offspring1.routes[d][r].size(), offspring2.routes[d][r].size()) - 1) + 1);

                    ArrayList<Integer> o1Head, o1Tail, o2Head, o2Tail;
                    if (splitPos < offspring1.routes[d][r].size()) {
                        o1Head = new ArrayList<>(offspring1.routes[d][r].subList(0, splitPos));
                        o1Tail = new ArrayList<>(offspring1.routes[d][r].subList(splitPos, offspring1.routes[d][r].size()));

                    } else {
                        o1Head = new ArrayList<>(offspring1.routes[d][r]);
                        o1Tail = new ArrayList<>();
                    }
                    if (splitPos < offspring2.routes[d][r].size()) {
                        o2Head = new ArrayList<>(offspring2.routes[d][r].subList(0, splitPos));
                        o2Tail = new ArrayList<>(offspring2.routes[d][r].subList(splitPos, offspring2.routes[d][r].size()));
                    } else {
                        o2Head = new ArrayList<>(offspring2.routes[d][r]);
                        o2Tail = new ArrayList<>();
                    }

                    for (Integer customer : o1Tail) {
                        o2Head.removeIf(o2Customer -> o2Customer.equals(customer));
                    }
                    for (Integer customer : o2Tail) {
                        o1Head.removeIf(o1Customer -> o1Customer.equals(customer));
                    }
                    o1Head.addAll(o2Tail);
                    o2Head.addAll(o1Tail);

                    offspring1.routes[d][r] = new ArrayList<>(o1Head);
                    offspring2.routes[d][r] = new ArrayList<>(o2Head);
                }

            }
        }

        int[] offspring1Customers = new int[this.problem.numberOfCustomers];
        int[] offspring2Customers = new int[this.problem.numberOfCustomers];

        // Find duplicates and missing customers
        for (int d = 0; d < this.problem.numberOfDepots; d++) {
            for (int r = 0; r < this.problem.maxVehiclesPerDepot; r++) {
                for (int c = 0; c < Integer.max(offspring1.routes[d][r].size(), offspring2.routes[d][r].size()); c++) {
                    if (c < offspring1.routes[d][r].size()) {
                        offspring1Customers[offspring1.routes[d][r].get(c)] += 1;
                    }
                    if (c < offspring2.routes[d][r].size()) {
                        offspring2Customers[offspring2.routes[d][r].get(c)] += 1;
                    }
                }
            }
        }

        // Clean up offspring
        for (int cNr = 0; cNr < offspring1Customers.length; cNr++) {
            if (offspring1Customers[cNr] == 2) {
                offspring1.removeDuplicateCustomer(this.problem, cNr);
            } else if (offspring1Customers[cNr] == 0) {
                offspring1.addNewCustomerToRoutes(this.problem, cNr);
            }
            if (offspring2Customers[cNr] == 2) {
                offspring2.removeDuplicateCustomer(this.problem, cNr);
            } else if (offspring2Customers[cNr] == 0) {
                offspring2.addNewCustomerToRoutes(this.problem, cNr);
            }
        }
        return new ArrayList<Genotype>(Arrays.asList(offspring1, offspring2));
    }

    private ArrayList<Integer> selectRandomNonEmptyRoute(Genotype genotype, int depot) {
        List<Integer> possibleRoutes = IntStream.rangeClosed(0, this.problem.maxVehiclesPerDepot - 1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = null;

        while (route == null && possibleRoutes.size() > 0) {
            int r = possibleRoutes.get((int) (Math.random() * possibleRoutes.size()));
            possibleRoutes.removeIf(possibleRoute -> possibleRoute == r);
            route = genotype.routes[depot][r];
        }
        return route;
    }

    public void mutate(Genotype individual, int boost) {
        if (this.mutationRate + boost > Math.random() * 100) {
            individual.mutate(this.problem);
        }
    }

    public Genotype getBestIndividual() {
        return bestIndividual;
    }

    public Genotype getBestIndividualInPopulation() {
        return Collections.min(this.population);
    }
}
