import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

public class Individual {

    SLIC slic;
    int dominationRank, overallDeviationWeight, connectivityMeasureWeight, minimumSegmentCount, maximumSegmentCount;
    double score, edgeValue, overallDeviation, connectivityMeasure, crowdingDistance;
    HashMap<SuperPixel, Segment> visitedPixels;
    Segment[] segments;
    Random random;


    public Individual(SLIC slic, int overallDeviationWeight, int connectivityMeasureWeight, int minimumSegmentCount, int maximumSegmentCount) {
        this.slic = slic;
        this.random = new Random();
        this.overallDeviationWeight = overallDeviationWeight;
        this.connectivityMeasureWeight = connectivityMeasureWeight;
        this.minimumSegmentCount = minimumSegmentCount;
        this.maximumSegmentCount = maximumSegmentCount;

        generateSegments();
    }

    public Individual(@NotNull Individual p1, Individual p2, int splitPoint, @NotNull SLIC slic) {
        this.slic = slic;
        this.minimumSegmentCount = p1.minimumSegmentCount;
        this.maximumSegmentCount = p1.maximumSegmentCount;

        // First we generate the nex hashmap containing all superpixels and which segment they belong to
        HashMap<SuperPixel, Segment> visitedPixels = new HashMap<>();
        Set<Integer> segmentIds = new HashSet<>();
        HashMap<Integer, Integer> segmentIdMap = new HashMap<>();
        ArrayList<Segment> segments = new ArrayList<>();

        for (int i = 0; i < slic.superPixels.size(); i++) {
            SuperPixel sp = slic.superPixels.get(i);
            if (i < splitPoint) {
                Segment s = p1.visitedPixels.get(sp);
                if (!segmentIds.contains(s.id)) {
                    segmentIds.add(s.id);
                    segmentIdMap.put(s.id, segments.size());
                    segments.add(new Segment(segments.size()));
                }
                visitedPixels.put(sp, segments.get(segmentIdMap.get(s.id)));
            } else {
                Segment s = p2.visitedPixels.get(sp);
                if (!segmentIds.contains(s.id)) {
                    segmentIds.add(s.id);
                    segmentIdMap.put(s.id, segments.size());
                    segments.add(new Segment(segments.size()));
                }
                visitedPixels.put(sp, segments.get(segmentIdMap.get(s.id)));
            }
        }
        this.visitedPixels = visitedPixels;
        this.segments = generateSegments(visitedPixels, segments.size());
    }

    private Segment[] generateSegments(HashMap<SuperPixel, Segment> visitedPixels, int numSegments) {
        Segment[] segments = new Segment[numSegments];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = new Segment(i);
        }
        for (Map.Entry<SuperPixel, Segment> entry : visitedPixels.entrySet()) {
            segments[entry.getValue().id].add(entry.getKey());
        }
        return segments;
    }

    private void generateSegments() {

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
                pq.add(new SegmentSuperPixelEdge(segments[i], superPixelEdge));
            }
        }
        while (!pq.isEmpty()) {
            final SegmentSuperPixelEdge segmentSuperPixelEdge = pq.remove();
            final Segment segment = segmentSuperPixelEdge.getSegment();
            final SuperPixelEdge currentSuperPixelEdge = segmentSuperPixelEdge.getSuperPixelEdge();

            if (!visitedPixels.containsKey(currentSuperPixelEdge.V)) {
                segment.add(currentSuperPixelEdge.V);
                visitedPixels.put(currentSuperPixelEdge.V, segment);
                for (SuperPixelEdge superPixelEdge : currentSuperPixelEdge.V.edges) {
                    pq.add(new SegmentSuperPixelEdge(segment, superPixelEdge));
                }
            } else if (!visitedPixels.containsKey(currentSuperPixelEdge.U)) {
                segment.add(currentSuperPixelEdge.U);
                visitedPixels.put(currentSuperPixelEdge.U, segment);
                for (SuperPixelEdge superPixelEdge :
                        currentSuperPixelEdge.U.edges) {
                    pq.add(new SegmentSuperPixelEdge(segment, superPixelEdge));
                }
            }
        }

        this.segments = segments;
        this.visitedPixels = visitedPixels;
    }

    /**
     * Calculate the overall deviation and connectivity measure of an individual
     */
    public void overallDeviationAndConnectivityMeasure() {

        double overallDeviation = 0.0;
        double connectivityMeasure = 0.0;

        for (Segment segment : this.segments) {
            segment.overallDeviation = 0.0;
            segment.connectivityMeasure = 0.0;

            for (SuperPixel sp : segment.pixels) {
                //First calculate overall deviation (Summed argb distance from current superpixel to centroid of segment)
                double od = euclideanDistance(sp, segment);
                overallDeviation += od;
                segment.overallDeviation += od;

                //Calculate the connectivity measure
                double cm = connectivityMeasure(sp, segment);
                connectivityMeasure += cm;
                segment.connectivityMeasure += cm;
            }
        }
        scoreIndividual(overallDeviation, connectivityMeasure, this.overallDeviationWeight, this.connectivityMeasureWeight);
    }


    private double euclideanDistance(SuperPixel sp, Segment segment) {
        Color spColor = sp.getColor();
        Color sColor = segment.getColor();
        int differenceRed = spColor.getRed() - sColor.getRed();
        int differenceGreen = spColor.getGreen() - sColor.getGreen();
        int differenceBlue = spColor.getBlue() - sColor.getBlue();

        return Math.sqrt(Math.pow(differenceRed, 2) + Math.pow(differenceGreen, 2) + Math.pow(differenceBlue, 2));
    }

    private double connectivityMeasure(SuperPixel sp, Segment segment) {
        double cm = 0;

        for (int j = 0; j < sp.edges.size(); j++) {
            if (segment.pixels.contains(sp.edges.get(j).V)) {
                cm += 1 / (j + 1);
            }
        }
        return cm;
    }

    /**
     * Update the overalldeviation, connectivitymeasure and the score of an individual.
     * The score is the weighted overall deviation - the weighted connectivity measure.
     * The higher the score, the better
     *
     * @param overallDeviation
     * @param connectivityMeasure
     * @param overallDeviationWeight
     * @param connectivityMeasureWeight
     */
    void scoreIndividual(double overallDeviation, double connectivityMeasure, int overallDeviationWeight, int connectivityMeasureWeight) {
        this.overallDeviation = overallDeviation;
        this.connectivityMeasure = connectivityMeasure;

        this.score = overallDeviation * overallDeviationWeight - connectivityMeasure * connectivityMeasureWeight;
    }


    public boolean isDominatedBy(@NotNull Individual i) {
        return (this.overallDeviation > i.overallDeviation && this.connectivityMeasure <= i.connectivityMeasure) ||
                (this.overallDeviation >= i.overallDeviation && this.connectivityMeasure < i.connectivityMeasure);
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

class EdgeValueComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual o1, Individual o2) {
        return Double.compare(o2.edgeValue, o1.edgeValue);
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
