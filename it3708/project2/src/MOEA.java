import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MOEA {

    ImageParser image;
    PixelMatrix pixelMatrix;
    int generations, populationSize;
    double mutationRate, crossoverRate, elitismRate;

    Individual[] population;

    public MOEA(String fileName, int generations, int populationSize, double mutationRate, double crossoverRate, double elitismRate) {
        System.out.println("INIT MOEA:");
        this.generations = generations;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismRate = elitismRate;
        this.population = new Individual[populationSize];

        try {
            this.image = new ImageParser(fileName);
            this.pixelMatrix = new PixelMatrix(this.image, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // Create initial population
        System.out.println("RUN MOEA:");
        this.population = createInitialPopulation();
    }


    private Individual[] createInitialPopulation() {
        System.out.println("\tStarting to create initial population");
        Individual[] population = new Individual[this.image.getNumPixels()];
        // Setup for parallel creation of individuals
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < this.populationSize; i++) {
            final int index = i;
            executorService.execute(() -> {
                population[index] = new Individual(this.image, this.pixelMatrix);
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // Waiting for all threads to finish
        }
        System.out.println("\tFinished creating initial population");
        return population;
    }
}
