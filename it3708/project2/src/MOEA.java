import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MOEA {

    private final Comparator<Individual> weightedSumComparator;
    private final Comparator<Individual> crowdingDistanceComparator;
    private final Comparator<Individual> overallDeviationComparator;
    private final Comparator<Individual> connectivityMeasureComparator;
    ImageParser imageParser;
    SLIC slic;
    Random random;
    Individual[] population;
    private int generations, populationSize,
            overallDeviationWeight, connectivityMeasureWeight,
            minimumSegmentCount, maximumSegmentCount, numberOfTournaments, imagesWritten = 0;
    private double mutationRate, crossoverRate, elitismRate;
    private boolean weightedSum;


    public MOEA(String fileName, int generations, int populationSize,
                double mutationRate, double crossoverRate, double elitismRate,
                int overallDeviationWeight, int connectivityMeasureWeight,
                int minimumSegmentCount, int maximumSegmentCount, int numberOfTournaments, boolean weightedSum) {

        System.out.println("INIT MOEA:");
        this.generations = generations;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismRate = elitismRate;
        this.overallDeviationWeight = overallDeviationWeight;
        this.connectivityMeasureWeight = connectivityMeasureWeight;
        this.minimumSegmentCount = minimumSegmentCount;
        this.maximumSegmentCount = maximumSegmentCount;
        this.numberOfTournaments = numberOfTournaments;
        this.weightedSum = weightedSum;
        this.population = new Individual[populationSize];
        this.random = new Random();

        this.weightedSumComparator = new WeightedSumComparator();
        this.crowdingDistanceComparator = new CrowdingDistanceComparator();
        this.overallDeviationComparator = new OverallDeviationComparator();
        this.connectivityMeasureComparator = new ConnectivityMeasureComparator();
        try {
            this.imageParser = new ImageParser(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.slic = new SLIC(this.imageParser);
        this.slic.run(10000);
    }

    public void run() {
        // Create initial population
        System.out.println("RUN MOEA:");
        Individual[] population = createInitialPopulation();

        // If we have selected to run the weighted sum we use the normal GA approach
        if (this.weightedSum) {
            System.out.println("\tRunning with Weighted Sum comparison");
            for (int i = 0; i < this.generations; i++) {
                //Run crossover to create new offsprings
                final Individual[] offspring = crossover(
                        population, population.length, this.minimumSegmentCount,
                        this.maximumSegmentCount, this.numberOfTournaments);

                // Evaluate the newly created offspring
                evaluate(offspring);

                // Rank the selection using weighted sum
                population = selectWeightedSum(population, offspring, this.populationSize);
            }
        } else {
            System.out.println("\tRunning with Pareto Front comparison");
            for (int i = 0; i < this.generations; i++) {
                System.out.println("Iteration: " + i);
                // Run crossover to create new offsprings
                final Individual[] offspring = crossover(
                        population, population.length, this.minimumSegmentCount,
                        this.maximumSegmentCount, this.numberOfTournaments);

                // Evaluate Solutions
                evaluate(offspring);

                // Select for next generation
                population = nonDominatingSorting(population, offspring, this.populationSize);
            }
        }

        System.out.println("Finished evolving solutions. Saving best images to folder");

        for (int i = 0; i < 5 && i < population.length; i++) {
            writeImage(population[i]);
            if (!weightedSum) {
                System.out.println("Domination rank: " + population[i].dominationRank);
            }
            System.out.println("Segments: " + population[i].segments.length);
            System.out.println("Deviation score: " + population[i].overallDeviation);
            System.out.println("Connectivity score: " + population[i].connectivityMeasure);
            System.out.println("Weighted sum: " + population[i].score);
        }
        this.population = population;
    }


    private Individual[] createInitialPopulation() {
        System.out.println("\tStarting to create initial population");
        Individual[] population = new Individual[this.populationSize];
        // Setup for parallel creation of individuals
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < this.populationSize; i++) {
            final int index = i;
            //executorService.execute(() -> {
            population[index] = new Individual(slic,
                    this.overallDeviationWeight, this.connectivityMeasureWeight,
                    this.minimumSegmentCount, this.maximumSegmentCount);
            population[index].overallDeviationAndConnectivityMeasure();
            //});
        }
        //executorService.shutdown();
        //noinspection StatementWithEmptyBody
        //while (!executorService.isTerminated()) ;

        System.out.println("\tFinished creating initial population");
        return population;
    }


    private Individual[] crossover(Individual[] individuals, int offspringCount, int minimumSegmentCount, int maximumSegmentCount, int numberOfTournaments) {
        final Individual[] offspring = new Individual[offspringCount];

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < offspringCount; i++) {
            final int index = i;

            //executorService.execute(() -> {
            Individual child = null;
            while (child == null) {
                final int splitPoint = random.nextInt(slic.superPixels.size());
                // Select two parents
                Individual p1 = tournamentSelection(individuals, numberOfTournaments);
                Individual p2 = tournamentSelection(individuals, numberOfTournaments);
                child = new Individual(p1, p2, splitPoint, slic);
                if (child.segments.length < minimumSegmentCount || child.segments.length > maximumSegmentCount) {
                    child = null;
                }
            }

            if (random.nextDouble() < this.mutationRate) {
                //mutate(child, minimumSegmentCount, maximumSegmentCount);
            }
            offspring[index] = child;
            //});
        }
        //executorService.shutdown();

        //noinspection StatementWithEmptyBody
        //while (!executorService.isTerminated()) ;

        return offspring;
    }

    private Individual tournamentSelection(@NotNull Individual[] individuals, int numberOfTournaments) {
        int bestIndex = random.nextInt(individuals.length);
        for (int i = 0; i < numberOfTournaments; i++) {
            int contender = random.nextInt(individuals.length);

            if (contender < bestIndex) {
                bestIndex = contender;
            }
        }
        return individuals[bestIndex];
    }

    private void mutate(@NotNull Individual individual, int minimumSegmentCount, int maximumSegmentCount) {
        //TODO: implement mutation using visitedPixels and segments
    }

    private void evaluate(@NotNull Individual[] individuals) {
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < individuals.length; i++) {
            final int index = i;
            //executorService.execute(() -> {
            individuals[index].overallDeviationAndConnectivityMeasure();
            //});
        }
        //executorService.shutdown();

        //noinspection StatementWithEmptyBody
        //while (!executorService.isTerminated()) ;
    }

    // This method ranks the individuals based on the weighted sum
    @NotNull
    private Individual[] selectWeightedSum(Individual[] population, Individual[] offspring, int populationSize) {
        ArrayList<Individual> pq = new ArrayList<>(Arrays.asList(population));
        Collections.addAll(pq, offspring);
        pq.sort(this.weightedSumComparator);

        final ArrayList<Individual> survivors = new ArrayList<>();
        Individual[] survivorArray = new Individual[populationSize];

        int index = 0;

        while (index < populationSize) {
            double p = Math.random();

            int rank = pq.size();

            int rankSum = 0;
            for (int i = pq.size(); i > 0; i--) {
                rankSum += i;
            }
            double cProb = 0.0;
            int lstIndex = 0;

            while (!pq.isEmpty()) {
                cProb += (double) rank / rankSum;

                if (p <= cProb) {
                    survivors.add(pq.remove(lstIndex));
                    index++;
                    break;
                }
                lstIndex++;
                rank--;
            }
        }
        survivors.sort(this.weightedSumComparator);

        return survivors.toArray(survivorArray);

    }

    private Individual[] nonDominatingSorting(@NotNull Individual[] population, @NotNull Individual[] offspring, int populationSize) {
        Individual[] resultIndividuals = new Individual[populationSize];
        int resultIndividualsCount = 0;


        final Individual[] tempPop = new Individual[population.length + offspring.length];
        System.arraycopy(population, 0, tempPop, 0, population.length);
        System.arraycopy(offspring, 0, tempPop, population.length, offspring.length);

        System.out.println("Population length: " + population.length);
        System.out.println("Offspring length: " + offspring.length);
        System.out.println("TempPop length: " + tempPop.length);

        HashMap<Integer, ArrayList<Individual>> dominationMap = new HashMap<>();
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        for (Individual individual : tempPop) {
            //Create domination rank for every solution
            int dr = dominationRank(population, individual);
            individual.dominationRank = dr;

            if (dominationMap.containsKey(dr)) {
                dominationMap.get(dr).add(individual);
            } else {
                ArrayList<Individual> temp = new ArrayList<>();
                temp.add(individual);
                dominationMap.put(dr, temp);
                pq.add(dr);
            }
        }

        Iterator itr = pq.iterator();

        while (itr.hasNext()) {
            int nxtRank = pq.poll();

            ArrayList<Individual> dominationEdge = dominationMap.get(nxtRank);

            if (resultIndividualsCount + dominationEdge.size() <= resultIndividuals.length) {
                for (Individual individual : dominationEdge) {
                    resultIndividuals[resultIndividualsCount] = individual;
                    resultIndividualsCount++;
                }
            } else {
                int neededIndividuals = resultIndividuals.length - resultIndividualsCount;
                Individual[] lastNeededIndividuals = crowdingDistanceSort(dominationEdge, neededIndividuals);

                for (Individual individual : lastNeededIndividuals) {
                    resultIndividuals[resultIndividualsCount] = individual;
                    resultIndividualsCount++;
                }
            }
            if (resultIndividualsCount == resultIndividuals.length) {
                break;
            }
        }
        // TODO: Fix null pointer exception due to lack of resultIndividuals being returned. Some places in the array are null
        //  Look at the crowdingDistanceSort method and the logic above. see if there is anything wrong which produces empty individuals
        return resultIndividuals;
    }

    private Individual[] crowdingDistanceSort(ArrayList<Individual> dominationEdge, int neededIndividuals) {
        Individual[] resIndividuals = new Individual[neededIndividuals];

        ArrayList<Individual> deviationSort = new ArrayList<>(dominationEdge);
        ArrayList<Individual> connectivitySort = new ArrayList<>(dominationEdge);


        deviationSort.sort(overallDeviationComparator);
        connectivitySort.sort(connectivityMeasureComparator);
        PriorityQueue<Individual> pq = new PriorityQueue<>(crowdingDistanceComparator);

        // First we reset the crowding distance for each individual
        for (Individual individual : dominationEdge) {
            individual.crowdingDistance = 0;
        }
        //TODO check logic here, are things sorted as they should be?
        deviationSort.get(0).crowdingDistance = Double.MAX_VALUE;
        deviationSort.get(deviationSort.size() - 1).crowdingDistance = Double.MAX_VALUE;
        pq.add(deviationSort.get(0));
        pq.add(deviationSort.get(deviationSort.size() - 1));


        double deviationMax = deviationSort.get(deviationSort.size() - 1).overallDeviation;
        double deviationMin = deviationSort.get(0).overallDeviation;

        double connectivityMax = connectivitySort.get(connectivitySort.size() - 1).connectivityMeasure;
        double connectivityMin = connectivitySort.get(0).connectivityMeasure;

        for (int i = 1; i < deviationSort.size() - 1; i++) {
            deviationSort.get(i).crowdingDistance = Math.abs(
                    deviationSort.get(i - 1).overallDeviation /
                            deviationSort.get(i + 1).overallDeviation /
                            (deviationMax - deviationMin))
                    + Math.abs(
                    deviationSort.get(i - 1).connectivityMeasure /
                            deviationSort.get(i + 1).connectivityMeasure /
                            (connectivityMax - connectivityMin));
        }

        for (int i = 0; i < resIndividuals.length; i++) {
            resIndividuals[i] = pq.poll();
        }
        return resIndividuals;
    }

    private int dominationRank(@NotNull Individual[] population, Individual individual) {
        int dr = 1;
        for (Individual someIndividual : population) {
            if (individual == someIndividual) {
                continue;
            }
            try {
                if (individual.isDominatedBy(someIndividual)) {
                    dr++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dr;
    }

    void writeImage(Individual individual) {
        final int width = this.imageParser.getWidth();
        final int height = this.imageParser.getHeight();
        final WritableImage writableImageBlack = new WritableImage(width, height);
        final PixelWriter pixelWriterBlack = writableImageBlack.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0 || y == height - 1 || x == 0 || x == width - 1) {
                    pixelWriterBlack.setColor(x, y, Color.BLACK);
                } else if (!segmentContainsAllNeighbours(individual, y, x)) {
                    pixelWriterBlack.setColor(x, y, Color.BLACK);
                } else {
                    pixelWriterBlack.setColor(x, y, Color.WHITE);
                }
            }
        }
        BufferedImage bImage = SwingFXUtils.fromFXImage(writableImageBlack, null);
        this.imagesWritten++;
        File file = new File("./Student Images/" +
                this.imagesWritten + "_segments" +
                individual.segments.length + ".png");
        try {
            ImageIO.write(bImage, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean segmentContainsAllNeighbours(Individual individual, int y, int x) {
        if (slic.neighboursInSameSuperPixel(y, x)) {
            return true;
        }
        int[] startStop = slic.getStartAndStopYX(y, x);
        ArrayList<Integer> neighbouringSuperPixels = new ArrayList<Integer>();
        for (int yy = startStop[0]; yy < startStop[1]; yy++) {
            for (int xx = startStop[2]; xx < startStop[3]; xx++) {
                if (this.slic.label[y][x] != this.slic.label[yy][xx]) {
                    neighbouringSuperPixels.add(this.slic.label[yy][xx]);
                }
            }
        }
        Segment targetSegment = individual.visitedPixels.get(slic.superPixels.get(slic.label[y][x]));
        for (int neighbourId : neighbouringSuperPixels) {
            if (individual.visitedPixels.get(slic.superPixels.get(neighbourId)) != targetSegment) {
                return false;
            }
        }
        return true;
    }
}
