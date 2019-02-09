import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneticAlgorithm {

    // Set Genetic algorithm parameters

    Problem problem;
    List<Genotype> population;
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
        evaluatePopulation();

        for (int currentGeneration = 0; currentGeneration < this.generationNumber; currentGeneration++) {
            // If optimization criteria are met end
            System.out.println("Generation: " + currentGeneration + "\t best distance: " + getBestIndividual().distance);
            if (thresholdIsMet()) {
                break;
            }

            List<Genotype> parents = tournamentSelection(this.populationSize, this.populationSize / 20);
            Collections.sort(parents);
            parents = new ArrayList<>(parents.subList(0, parents.size() / 2));


            ArrayList<Genotype> newGeneration = new ArrayList<Genotype>();
            Collections.sort(this.population);
            for (int i = 0; i < this.populationSize * 5 / 100; i++) {
                newGeneration.add(this.population.remove(0));
            }

            // Crossover of parents chromosomes
            while (newGeneration.size() < this.populationSize) {
                int p1 = (int) (Math.random() * parents.size()), p2 = (int) (Math.random() * parents.size());
                Genotype parent1 = parents.get(p1), parent2 = parents.get(p2);

                newGeneration.addAll(crossover(parent1, parent2));
            }

            for (int i = 0; i < this.population.size(); i++) {
                mutate(newGeneration.get(i));
            }
            this.population = newGeneration;
        }
    }

    // Generate initial random population
    public void initRandomPop() {

        List<Genotype> population = new ArrayList<Genotype>();
        for (int p = 0; p < this.populationSize; p++) {
            population.add(new Genotype(this.problem));
        }
        this.population = population;
    }


    private boolean thresholdIsMet() {
        for (double individualFitness : this.populationFitness) {
            if (individualFitness <= this.problem.solutionThreshold) return true;
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

                if (p2.getFitness() > p1.getFitness()) {
                    p1 = p2;
                }
            }
            winners.add(p1);
        }
        return winners;
    }

    private ArrayList<Genotype> crossover(Genotype parent1, Genotype parent2) {

        ArrayList<Integer>[][] routes1 = new ArrayList[problem.numberOfDepots][problem.maxVehiclesPerDepot];
        ArrayList<Integer>[][] routes2 = new ArrayList[problem.numberOfDepots][problem.maxVehiclesPerDepot];

        for (int i = 0; i < routes1.length; i++) {
            for (int j = 0; j < routes1[i].length; j++) {
                routes1[i][j] = new ArrayList<>(parent1.routes[i][j]);
                routes2[i][j] = new ArrayList<>(parent2.routes[i][j]);
            }
        }

        Genotype offspring1 = new Genotype(problem, routes1);
        Genotype offspring2 = new Genotype(problem, routes2);

        int depot1 = (int) (Math.random() * this.problem.numberOfDepots);
        int depot2 = (int) (Math.random() * this.problem.numberOfDepots);


        ArrayList<Integer> route1 = selectRandomNonEmptyRoute(offspring1, depot1);
        ArrayList<Integer> route2 = selectRandomNonEmptyRoute(offspring2, depot2);

        if (route1 == null || route2 == null) {
            return new ArrayList<Genotype>(Arrays.asList(offspring1, offspring2));
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

        offspring1.updateFitnessVariables(this.problem);
        offspring1.updateFitness(this.problem);

        offspring2.updateFitnessVariables(this.problem);
        offspring2.updateFitness(this.problem);

        return new ArrayList<Genotype>(Arrays.asList(offspring1,offspring2));
    }

    private ArrayList<Integer> selectRandomNonEmptyRoute(Genotype genotype, int depot) {
        List<Integer> possibleRoutes = IntStream.rangeClosed(0, this.problem.numberOfDepots-1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = null;

        while (route == null && possibleRoutes.size() > 0) {
            int r = possibleRoutes.get((int) (Math.random() * possibleRoutes.size()));
            possibleRoutes.removeIf(possibleRoute -> possibleRoute == r);
            route = genotype.routes[depot][r];
        }
        return route;
    }

    private void mutate(Genotype individual) {
        if (this.mutationRate > Math.random() * 100) {
            individual.mutate();
        }
    }

    public Genotype getBestIndividual() {
        return Collections.min(this.population);
    }
}
