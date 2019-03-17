import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

public class Individual {

    SLIC slic;
    Integer[] genotype;
    int dominationRank, minimumSegmentCount, maximumSegmentCount;
    double overallDeviationWeight, connectivityMeasureWeight, edgeValueWeight,
            score, overallDeviation, connectivityMeasure, edgeValue, crowdingDistance;
    HashMap<SuperPixel, Segment> visitedPixels;
    Segment[] segments;
    Random random;


    public Individual(SLIC slic, double overallDeviationWeight, double connectivityMeasureWeight, double edgeValueWeight, int minimumSegmentCount, int maximumSegmentCount) {
        this.slic = slic;
        this.random = new Random();
        this.overallDeviationWeight = overallDeviationWeight;
        this.connectivityMeasureWeight = connectivityMeasureWeight;
        this.edgeValueWeight = edgeValueWeight;
        this.minimumSegmentCount = minimumSegmentCount;
        this.maximumSegmentCount = maximumSegmentCount;

        generateSegments();
    }

    public Individual(@NotNull Individual p1, Individual p2, int splitPoint, @NotNull SLIC slic) {
        this.slic = slic;
        this.random = new Random();
        this.minimumSegmentCount = p1.minimumSegmentCount;
        this.maximumSegmentCount = p1.maximumSegmentCount;
        this.overallDeviationWeight = p1.overallDeviationWeight;
        this.connectivityMeasureWeight = p1.connectivityMeasureWeight;
        this.edgeValueWeight = p1.edgeValueWeight;


        Integer[] genotype = new Integer[p1.genotype.length];

        if (this.random.nextDouble() < 0.5) {

            System.arraycopy(p1.genotype, 0, genotype, 0, splitPoint);
            System.arraycopy(p2.genotype, splitPoint, genotype, splitPoint, genotype.length - splitPoint);
        } else {
            for (int i = 0; i < genotype.length; i++) {
                if (this.random.nextDouble() < 0.5) {
                    genotype[i] = p1.genotype[i];
                } else {
                    genotype[i] = p2.genotype[i];
                }
            }
        }
        boolean[] added = new boolean[genotype.length];
        for (int pos = 0; pos < genotype.length; pos++) {
            if (added[pos]) {
                continue;
            }
            int current = pos;
            ArrayList<Integer> idArray = new ArrayList<>();
            while (genotype[current] != null) {
                if (added[current]) {
                    break;
                }
                if (idArray.contains(current)) {
                    genotype[current] = null;
                    break;
                }
                idArray.add(current);
                current = genotype[current];
            }
        }
        this.genotype = genotype;
    }

    public void setVisitedAndSegments() {
        HashMap<SuperPixel, Segment> visitedPixels = new HashMap<>();
        ArrayList<Segment> segments = new ArrayList<>();

        boolean[] added = new boolean[genotype.length];
        for (int pos = 0; pos < genotype.length; pos++) {
            if (added[pos]) {
                continue;
            }
            int current = pos;
            ArrayList<Integer> idArray = new ArrayList<>();
            while (genotype[current] != null) {
                if (added[current]) {
                    break;
                }

                if (idArray.contains(genotype[current])) {
                    genotype[current] = null;
                    break;
                }

                idArray.add(current);
                current = genotype[current];
            }

            SuperPixel root = slic.superPixels.get(current);

            if (visitedPixels.containsKey(root)) {
                Segment segment = visitedPixels.get(root);
                for (int id : idArray) {
                    SuperPixel sp = slic.superPixels.get(id);
                    segment.add(sp);
                    visitedPixels.put(sp, segment);
                    added[id] = true;
                }
            } else {
                Segment segment = new Segment(segments.size());
                idArray.add(current);
                for (int id : idArray) {
                    SuperPixel sp = slic.superPixels.get(id);
                    segment.add(sp);
                    visitedPixels.put(sp, segment);
                    added[id] = true;
                }
                segments.add(segment);
            }
        }
        this.visitedPixels = visitedPixels;
        this.segments = segments.toArray(new Segment[0]);
    }

    private void generateSegments() {

        final Integer[] genotype = new Integer[this.slic.superPixels.size()];
        final HashMap<SuperPixel, Segment> visitedPixels = new HashMap<>();
        final int segmentCount = this.minimumSegmentCount + this.random.nextInt(this.maximumSegmentCount - this.minimumSegmentCount + 1);
        final Segment[] segments = new Segment[segmentCount];

        final PriorityQueue<SegmentSuperPixelEdge> pq = new PriorityQueue<>();

        for (int i = 0; i < segmentCount; i++) {
            SuperPixel rootPixel;
            do {
                int pos = random.nextInt(this.slic.superPixels.size());
                rootPixel = this.slic.superPixels.get(pos);
            }
            while (visitedPixels.containsKey(rootPixel));

            segments[i] = new Segment(rootPixel, i);
            visitedPixels.put(rootPixel, segments[i]);
            for (SuperPixelEdge superPixelEdge : rootPixel.edges) {
                if (!visitedPixels.containsKey(superPixelEdge.V)) {
                    pq.add(new SegmentSuperPixelEdge(segments[i], superPixelEdge));
                }

            }
        }
        while (!pq.isEmpty()) {
            final SegmentSuperPixelEdge segmentSuperPixelEdge = pq.remove();
            final Segment segment = segmentSuperPixelEdge.getSegment();
            final SuperPixelEdge currentSuperPixelEdge = segmentSuperPixelEdge.getSuperPixelEdge();

            if (!visitedPixels.containsKey(currentSuperPixelEdge.V)) {
                genotype[currentSuperPixelEdge.V.id] = currentSuperPixelEdge.U.id;
                segment.add(currentSuperPixelEdge.V);
                visitedPixels.put(currentSuperPixelEdge.V, segment);
                for (SuperPixelEdge superPixelEdge : currentSuperPixelEdge.V.edges) {
                    if (!visitedPixels.containsKey(superPixelEdge.V)) {
                        pq.add(new SegmentSuperPixelEdge(segment, superPixelEdge));
                    }

                }
            }
        }
        this.genotype = genotype;
        this.segments = segments;
        this.visitedPixels = visitedPixels;
    }

    /**
     * Calculate the overall deviation and connectivity measure of an individual
     */
    public void overallDeviationAndConnectivityMeasureAndEdgeValue() {

        double overallDeviation = 0.0;
        double connectivityMeasure = 0.0;
        double edgeValue = 0.0;

        for (Segment segment : this.segments) {
            segment.overallDeviation = 0.0;
            segment.connectivityMeasure = 0.0;
            segment.edgeValue = 0.0;

            for (SuperPixel sp : segment.pixels) {
                //First calculate overall deviation (Summed argb distance from current superpixel to centroid of segment)
                double od = euclideanDistance(sp, segment);
                overallDeviation += od;
                segment.overallDeviation += od;

                //Calculate the connectivity measure
                double cm = connectivityMeasure(sp, segment);
                connectivityMeasure += cm;
                segment.connectivityMeasure += cm;

                for (SuperPixelEdge superPixelEdge : sp.edges) {
                    if (!segment.pixels.contains(superPixelEdge.V)) {
                        edgeValue += superPixelEdge.distance;
                        segment.edgeValue += superPixelEdge.distance;
                    }
                }
            }
        }
        scoreIndividual(overallDeviation, connectivityMeasure, edgeValue, this.overallDeviationWeight, this.connectivityMeasureWeight, this.edgeValueWeight);
    }


    private double euclideanDistance(SuperPixel sp, Segment segment) {
        if (this.slic.cielab) {
            float[] cielab1 = sp.getCielab();
            float[] cielab2 = segment.getCielab();

            double distance = 0.0;
            for (int i = 0; i < cielab1.length; i++) {
                distance += Math.pow(cielab1[i] - cielab2[i], 2);
            }
            return Math.sqrt(distance);
        } else {
            Color spColor = sp.getColor();
            Color sColor = segment.getColor();
            int differenceRed = spColor.getRed() - sColor.getRed();
            int differenceGreen = spColor.getGreen() - sColor.getGreen();
            int differenceBlue = spColor.getBlue() - sColor.getBlue();
            int differenceAlpha = spColor.getAlpha() - sColor.getAlpha();
            return Math.sqrt(Math.pow(differenceRed, 2) + Math.pow(differenceGreen, 2) + Math.pow(differenceBlue, 2) + Math.pow(differenceAlpha, 2));
        }

    }

    private double connectivityMeasure(SuperPixel sp, Segment segment) {
        double cm = 0;
        for (SuperPixel neighbour : sp.neighbours) {
            if (!segment.pixels.contains(neighbour)) {
                cm += 1.0 / sp.neighbours.size();
            }
        }
        return cm;
    }

    /**
     * @param overallDeviation
     * @param connectivityMeasure
     * @param overallDeviationWeight
     * @param connectivityMeasureWeight
     */
    void scoreIndividual(double overallDeviation, double connectivityMeasure, double edgeValue,
                         double overallDeviationWeight, double connectivityMeasureWeight, double edgeValueWeight) {
        this.overallDeviation = overallDeviation;
        this.connectivityMeasure = connectivityMeasure;
        this.edgeValue = edgeValue;

        this.score = (overallDeviation * overallDeviationWeight) +
                (connectivityMeasure * connectivityMeasureWeight) -
                (edgeValue * edgeValueWeight);
    }


    public boolean isDominatedBy(@NotNull Individual i) {
        return (this.overallDeviation > i.overallDeviation && this.connectivityMeasure >= i.connectivityMeasure && this.edgeValue <= i.edgeValue) ||
                (this.overallDeviation >= i.overallDeviation && this.connectivityMeasure > i.connectivityMeasure && this.edgeValue <= i.edgeValue) ||
                (this.overallDeviation >= i.overallDeviation && this.connectivityMeasure >= i.connectivityMeasure && this.edgeValue < i.edgeValue);
    }
}


class WeightedSumComparator implements Comparator<Individual> {
    @Override
    public int compare(Individual o1, Individual o2) {
        return Double.compare(o1.score, o2.score);
    }
}


class CrowdingDistanceComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual o1, Individual o2) {
        return Double.compare(o2.crowdingDistance, o1.crowdingDistance);
    }
}

class OverallDeviationComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual o1, Individual o2) {
        return Double.compare(o1.overallDeviation, o2.overallDeviation);
    }
}

class ConnectivityMeasureComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual o1, Individual o2) {
        return Double.compare(o1.connectivityMeasure, o2.connectivityMeasure);
    }

}

class EdgeValueComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual o1, Individual o2) {
        return Double.compare(o1.edgeValue, o2.edgeValue);
    }
}
