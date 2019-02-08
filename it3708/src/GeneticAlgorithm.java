import java.util.*;

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
            if (thresholdIsMet()) {
                break;
            }

            // Select parents for next generation
            //TODO: Add elitism to parent selection
            List<Genotype> parents = tournamentSelection(this.populationSize/2,3);

            // Crossover of parents chromosomes

            // Mutation of chromosome
        }
    }

    // Generate initial random population
    public void initRandomPop() {

        List<Genotype> population = new ArrayList<Genotype>();
        for (int p =0; p< this.populationSize; p++) {
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
        for (int i = 0; i< this.populationSize; i++){
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

            for (int ts = 1; ts < tournamentSize+1; ts++) {
                Genotype p2 = this.population.get(ts);

                if (p2.getFitness() > p1.getFitness()) {
                    p1 = p2;
                }
            }
            winners.add(p1);
        }
        return winners;
    }

    private List<Genotype> crossover(Genotype parent1, Genotype parent2) {
        return null;
    }

    private Genotype mutate(Genotype individual) {
        if (this.mutationRate > Math.random()*100){
            individual.mutate();
        }
        return null;
    }
}
