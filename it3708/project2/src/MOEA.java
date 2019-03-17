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
    private final Comparator<Individual> edgeValueComparator;
    ImageParser imageParser;
    SLIC slic;
    Random random;
    Individual[] population;
    private int generations, populationSize,
            minimumSegmentCount, maximumSegmentCount, numberOfTournaments, imagesWritten = 0;
    private double overallDeviationWeight, connectivityMeasureWeight, edgeValueWeight, mutationRate, crossoverRate, elitismRate, m;
    private boolean weightedSum, cielab;


    public MOEA(String fileName, int generations, int populationSize,
                double mutationRate, double crossoverRate, double elitismRate,
                double overallDeviationWeight, double connectivityMeasureWeight, double edgeValueWeight,
                int minimumSegmentCount, int maximumSegmentCount, int numberOfTournaments, int numSlicClusters, double m,
                boolean weightedSum, boolean cielab) {

        System.out.println("INIT MOEA:");
        this.generations = generations;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismRate = elitismRate;
        this.overallDeviationWeight = overallDeviationWeight;
        this.connectivityMeasureWeight = connectivityMeasureWeight;
        this.edgeValueWeight = edgeValueWeight;
        this.minimumSegmentCount = minimumSegmentCount;
        this.maximumSegmentCount = maximumSegmentCount;
        this.numberOfTournaments = numberOfTournaments;
        this.weightedSum = weightedSum;
        this.cielab = cielab;
        this.m = m;
        this.population = new Individual[populationSize];
        this.random = new Random();

        this.weightedSumComparator = new WeightedSumComparator();
        this.crowdingDistanceComparator = new CrowdingDistanceComparator();
        this.overallDeviationComparator = new OverallDeviationComparator();
        this.connectivityMeasureComparator = new ConnectivityMeasureComparator();
        this.edgeValueComparator = new EdgeValueComparator();
        try {
            this.imageParser = new ImageParser(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.slic = new SLIC(this.imageParser, cielab);
        this.slic.run(numSlicClusters, m);
    }

    public void run() {
        // Create initial population
        System.out.println("RUN MOEA:");
        //Instant initStart = Instant.now();
        Individual[] population = createInitialPopulation();

        //Instant initEnd = Instant.now();
        //System.out.println("Time to create initial population: " + Duration.between(initStart, initEnd));
        /*
        for (int i = 0; i < 10 && i < population.length; i++) {
            writeImage(population[i]);
            if (!weightedSum) {
                System.out.println("Domination rank: " + population[i].dominationRank);
            }
            System.out.println("Segments: " + population[i].segments.length);
            System.out.println("Deviation score: " + population[i].overallDeviation);
            System.out.println("Connectivity score: " + population[i].connectivityMeasure);
            System.out.println("EdgeValue score: " + population[i].edgeValue);
            System.out.println("Weighted sum: " + population[i].score);
        }
        */
        /*
        for (Individual individual :
                population) {
            writeImage(individual);
        }
        */
        /*
        rankPopulation(population);
        for (Individual individual : getParetoFront(population, 1)) {
            writeImage(individual);
        }
        this.population = population;
        */
        // If we have selected to run the weighted sum we use the normal GA approach
        if (this.weightedSum) {
            System.out.println("\tRunning with Weighted Sum comparison");
            for (int i = 0; i < this.generations; i++) {
                System.out.println("Iteration: " + i);
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
                //Instant crossoverStart = Instant.now();
                final Individual[] offspring = crossover(
                        population, population.length, this.minimumSegmentCount,
                        this.maximumSegmentCount, this.numberOfTournaments);
                //Instant crossoverEnd = Instant.now();
                //System.out.println("Time to do crossover: " + Duration.between(crossoverStart, crossoverEnd));
                // Evaluate Solutions
                //Instant evaluateStart = Instant.now();
                evaluate(offspring);
                //Instant evaluateEnd = Instant.now();
                //System.out.println("Time to evaluate: " + Duration.between(evaluateStart, evaluateEnd));
                /*
                // Select for next generation
                for (Individual individual :
                        population) {
                    writeImage(individual, i, "pop_before");
                }
                for (Individual o : offspring) {
                    writeImage(o, i, "offspring");
                }
                */
                //Instant nsgaStart = Instant.now();
                population = nonDominatingSorting(population, offspring, this.populationSize);
                //Instant nsgaEnd = Instant.now();
                //System.out.println("Time to run nsga: " + Duration.between(nsgaStart, nsgaEnd));
                /*
                for (Individual individual :
                        population) {
                    writeImage(individual, i, "pop_after");
                }
                */

            }
        }

        System.out.println("Finished evolving solutions. Saving best images to folder");
        ArrayList<Individual> paretoFront = getParetoFront(population, 1);
        for (Individual individual : paretoFront) {
            writeImage(individual);
            if (!weightedSum) {
                System.out.println("Domination rank: " + individual.dominationRank);
            }
            System.out.println("Segments: " + individual.segments.length);
            System.out.println("Deviation score: " + individual.overallDeviation);
            System.out.println("Connectivity score: " + individual.connectivityMeasure);
            System.out.println("EdgeValue score: " + individual.edgeValue);
            System.out.println("Weighted sum: " + individual.score);
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
            executorService.execute(() -> {

                population[index] = new Individual(slic,
                        this.overallDeviationWeight, this.connectivityMeasureWeight, this.edgeValueWeight,
                        this.minimumSegmentCount, this.maximumSegmentCount);

                population[index].overallDeviationAndConnectivityMeasureAndEdgeValue();
            });
        }
        executorService.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) ;

        System.out.println("\tFinished creating initial population");
        return population;
    }


    private Individual[] crossover(Individual[] individuals, int offspringCount, int minimumSegmentCount, int maximumSegmentCount, int numberOfTournaments) {
        final Individual[] offspring = new Individual[offspringCount];

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < offspringCount; i++) {
            final int index = i;

            executorService.execute(() -> {
                Individual child = null;
                while (child == null) {
                    final int splitPoint = random.nextInt(slic.superPixels.size());
                    // Select two parents
                    Individual p1 = tournamentSelection(individuals, numberOfTournaments);
                    Individual p2 = p1;
                    while (p2 == p1) {
                        p2 = tournamentSelection(individuals, numberOfTournaments);
                    }

                    child = new Individual(p1, p2, splitPoint, slic);
                    if (random.nextDouble() < this.mutationRate) {
                        mutate(child);
                    }
                    int numNull = 0;
                    for (int pos = 0; pos < child.genotype.length; pos++) {
                        if (child.genotype[pos] == null) {
                            numNull++;
                        }
                    }
                    if (numNull < minimumSegmentCount || numNull > maximumSegmentCount) {
                        child = null;
                    }
                }

                child.setVisitedAndSegments();
                offspring[index] = child;
            });
        }
        executorService.shutdown();

        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) ;

        return offspring;
    }

    private Individual tournamentSelection(@NotNull Individual[] individuals, int numberOfTournaments) {
        Individual best = individuals[this.random.nextInt(individuals.length)];
        for (int i = 0; i < numberOfTournaments; i++) {
            Individual contender = individuals[this.random.nextInt(individuals.length)];

            if (best.isDominatedBy(contender)) {
                best = contender;
            } else if (contender.dominationRank < best.dominationRank) {
                best = contender;
            } else if (contender.score < best.score) {
                best = contender;
            }
        }
        return best;
    }

    private void mutate(@NotNull Individual individual) {


        int randomPos = individual.random.nextInt(individual.genotype.length);
        double prob = this.random.nextDouble();
        if (prob > 0.6) {
            individual.genotype[randomPos] = null;
        } else if (prob > 0.3) {
            SuperPixel sp = this.slic.superPixels.get(randomPos);
            int roof = sp.neighbours.size();
            int randomNeighbourId = sp.neighbours.get(this.random.nextInt(roof)).id;
            if (individual.genotype[randomNeighbourId] == null || individual.genotype[randomNeighbourId] != randomPos) {
                individual.genotype[randomPos] = randomNeighbourId;
            }
        } else {
            SuperPixelEdge spEdge = this.slic.superPixels.get(0).edges.get(0);
            for (SuperPixel sp : this.slic.superPixels) {
                for (SuperPixelEdge newSpEdge : sp.edges) {
                    try {
                        if (spEdge.distance < newSpEdge.distance &&
                                individual.genotype[newSpEdge.U.id] != null &&
                                individual.genotype[newSpEdge.V.id] != null &&
                                individual.genotype[newSpEdge.U.id] != newSpEdge.V.id &&
                                individual.genotype[newSpEdge.V.id] != newSpEdge.U.id) {
                            spEdge = newSpEdge;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            individual.genotype[spEdge.U.id] = spEdge.V.id;
        }


    }


    private void evaluate(@NotNull Individual[] individuals) {
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < individuals.length; i++) {
            final int index = i;
            executorService.execute(() -> {
                individuals[index].overallDeviationAndConnectivityMeasureAndEdgeValue();
            });
        }
        executorService.shutdown();

        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) ;
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


        HashMap<Integer, ArrayList<Individual>> dominationMap = new HashMap<>();
        PriorityQueue<Integer> pq = new PriorityQueue<Integer>();

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
        return resultIndividuals;
    }

    private Individual[] crowdingDistanceSort(ArrayList<Individual> dominationEdge, int neededIndividuals) {
        Individual[] resIndividuals = new Individual[neededIndividuals];

        ArrayList<Individual> deviationSort = new ArrayList<>(dominationEdge);
        ArrayList<Individual> connectivitySort = new ArrayList<>(dominationEdge);
        ArrayList<Individual> edgeValueSort = new ArrayList<>(dominationEdge);


        deviationSort.sort(overallDeviationComparator);
        connectivitySort.sort(connectivityMeasureComparator);
        edgeValueSort.sort(edgeValueComparator);
        PriorityQueue<Individual> pq = new PriorityQueue<>(crowdingDistanceComparator);

        // First we reset the crowding distance for each individual
        for (Individual individual : dominationEdge) {
            individual.crowdingDistance = 0;
        }
        deviationSort.get(0).crowdingDistance = Double.MAX_VALUE;
        deviationSort.get(deviationSort.size() - 1).crowdingDistance = Double.MAX_VALUE;
        pq.add(deviationSort.get(0));
        pq.add(deviationSort.get(deviationSort.size() - 1));


        double deviationMax = deviationSort.get(deviationSort.size() - 1).overallDeviation;
        double deviationMin = deviationSort.get(0).overallDeviation;

        double connectivityMax = connectivitySort.get(connectivitySort.size() - 1).connectivityMeasure;
        double connectivityMin = connectivitySort.get(0).connectivityMeasure;

        double edgeValueMax = edgeValueSort.get(edgeValueSort.size() - 1).edgeValue;
        double edgeValueMin = edgeValueSort.get(0).edgeValue;

        for (int i = 1; i < deviationSort.size() - 1; i++) {
            deviationSort.get(i).crowdingDistance = Math.abs(
                    (deviationSort.get(i - 1).overallDeviation -
                            deviationSort.get(i + 1).overallDeviation) /
                            (deviationMax - deviationMin))
                    + Math.abs(
                    (deviationSort.get(i - 1).connectivityMeasure -
                            deviationSort.get(i + 1).connectivityMeasure) /
                            (connectivityMax - connectivityMin))
                    + Math.abs(
                    (deviationSort.get(i - 1).edgeValue -
                            deviationSort.get(i + 1).edgeValue) /
                            (edgeValueMax - edgeValueMin));
            pq.add(deviationSort.get(i));
        }

        for (int i = 0; i < resIndividuals.length; i++) {
            resIndividuals[i] = pq.poll();
        }
        return resIndividuals;
    }

    private void rankPopulation(Individual[] individuals) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < individuals.length; i++) {
            final int index = i;
            executorService.execute(() -> {
                individuals[index].dominationRank = dominationRank(individuals, individuals[index]);
            });
        }
        executorService.shutdown();

        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) ;
    }

    private int dominationRank(@NotNull Individual[] population, Individual individual) {
        int dr = 1;
        for (Individual someIndividual : population) {
            if (individual == someIndividual) {
                continue;
            }
            if (individual.isDominatedBy(someIndividual)) {
                dr++;
            }
        }
        return dr;
    }

    void writeImage(Individual individual) {
        final int width = this.imageParser.getWidth();
        final int height = this.imageParser.getHeight();
        final WritableImage writableImageBlack = new WritableImage(width, height);
        final WritableImage writableImageLimeGreen = new WritableImage(width, height);
        final PixelWriter pixelWriterBlack = writableImageBlack.getPixelWriter();
        final PixelWriter pixelWriterLimeGreen = writableImageLimeGreen.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0 || y == height - 1 || x == 0 || x == width - 1) {
                    pixelWriterBlack.setColor(x, y, Color.BLACK);
                    pixelWriterLimeGreen.setColor(x, y, Color.LIMEGREEN);
                } else if (!segmentContainsAllNeighbours(individual, y, x)) {
                    pixelWriterBlack.setColor(x, y, Color.BLACK);
                    pixelWriterLimeGreen.setColor(x, y, Color.LIMEGREEN);
                } else {
                    pixelWriterBlack.setColor(x, y, Color.WHITE);
                    pixelWriterLimeGreen.setArgb(x, y, this.slic.pixelMatrix.getPixel(y, x).argb);
                }
            }
        }
        BufferedImage bImage = SwingFXUtils.fromFXImage(writableImageBlack, null);
        BufferedImage lmImage = SwingFXUtils.fromFXImage(writableImageLimeGreen, null);
        this.imagesWritten++;
        File bFile = new File("./Segmentation Evaluation/Student_Segmentation_Files/BlackAndWhite/" +
                this.imagesWritten + "_segments" +
                individual.segments.length + ".png");
        File lmFile = new File("./Segmentation Evaluation/Student_Segmentation_Files/LimeGreen/" +
                this.imagesWritten + "_segments" +
                individual.segments.length + ".png");
        try {
            ImageIO.write(bImage, "png", bFile);
            ImageIO.write(lmImage, "png", lmFile);
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

    private ArrayList<Individual> getParetoFront(Individual[] individuals, int rank) {
        ArrayList<Individual> front = new ArrayList<>();
        for (Individual individual : individuals) {
            if (individual.dominationRank <= rank) {
                front.add(individual);
            }
        }
        return front;
    }

    public ArrayList<Individual> getParetoFront() {
        ArrayList<Individual> front = new ArrayList<>();
        for (Individual individual : this.population) {
            if (individual.dominationRank == 1) {
                front.add(individual);
            }
        }
        return front;
    }
}
