import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

public class Individual {

    SLIC slic;
    Integer[] genotype;
    int dominationRank, minimumSegmentCount, maximumSegmentCount;
    double overallDeviationWeight, connectivityMeasureWeight, score, overallDeviation, connectivityMeasure, crowdingDistance;
    HashMap<SuperPixel, Segment> visitedPixels;
    Segment[] segments;
    Random random;


    public Individual(SLIC slic, double overallDeviationWeight, double connectivityMeasureWeight, int minimumSegmentCount, int maximumSegmentCount) {
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
        this.random = new Random();
        this.minimumSegmentCount = p1.minimumSegmentCount;
        this.maximumSegmentCount = p1.maximumSegmentCount;
        this.overallDeviationWeight = p1.overallDeviationWeight;
        this.connectivityMeasureWeight = p1.connectivityMeasureWeight;


        Integer[] genotype = new Integer[p1.genotype.length];
        System.arraycopy(p1.genotype, 0, genotype, 0, splitPoint);
        System.arraycopy(p2.genotype, splitPoint, genotype, splitPoint, genotype.length - splitPoint);

        this.genotype = genotype;

        /*

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
                    current = genotype[current];
                    continue;
                }
                if (idArray.contains(genotype[current])) {
                    if (visitedPixels.containsKey(current)) {
                        break;
                    }
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
        this.genotype = genotype;
        this.visitedPixels = visitedPixels;
        this.segments = segments.toArray(new Segment[segments.size()]);
        */
        /**
         * V2: Very slow and creates little variation

         int splitPoint1 = Math.min(p1.segments.length, random.nextInt(Math.max(p1.segments.length, p2.segments.length)));
         boolean[] addedSuperPixels = new boolean[slic.superPixels.size()];
         int addedCount = 0;
         HashMap<SuperPixel, Segment> visitedPixels = new HashMap<>();
         ArrayList<Segment> segments = new ArrayList<>();
         int i = 0;
         while (i < splitPoint1) {
         segments.add(new Segment(i));
         Segment s = segments.get(segments.size() - 1);
         for (SuperPixel sp : p1.segments[i].pixels) {
         visitedPixels.put(sp, s);
         s.add(sp);
         addedSuperPixels[sp.id] = true;
         addedCount++;
         }
         i++;
         }
         int j = 0;
         while (i < this.maximumSegmentCount && addedCount < addedSuperPixels.length && j < p2.segments.length) {
         Segment p2Segment = p2.segments[j];
         int spCount = 0;
         Segment s = new Segment(i);
         for (SuperPixel sp : p2Segment.pixels) {
         if (!addedSuperPixels[sp.id]) {
         visitedPixels.put(sp, s);
         s.add(sp);
         addedSuperPixels[sp.id] = true;
         addedCount++;
         spCount++;
         }
         }
         if (spCount > 0) {
         segments.add(s);
         i++;
         }
         j++;


         }

         while (addedCount < addedSuperPixels.length) {
         for (int spId = 0; spId < addedSuperPixels.length; spId++) {
         if (!addedSuperPixels[spId]) {
         SuperPixel sp = slic.superPixels.get(spId);

         SuperPixel closestNeighbour = tryGetNearestConnectedNeighbour(sp, addedSuperPixels);

         if (!addedSuperPixels[closestNeighbour.id]) {
         continue;
         }
         Segment segment = visitedPixels.get(closestNeighbour);
         segment.add(sp);
         visitedPixels.put(sp, segment);
         addedSuperPixels[spId] = true;
         addedCount++;
         }
         }
         }
         this.visitedPixels = visitedPixels;
         this.segments = segments.toArray(new Segment[segments.size()]);
         */

        /**
         * V1: Problem with connectivity
         ArrayList<Segment> segments = new ArrayList<>();
         HashMap<SuperPixel, Segment> visitedPixels = new HashMap<>();
         HashMap<Integer, Integer> segmentIdMapping = new HashMap<>();

         int i = 0;
         for (Map.Entry<SuperPixel, Segment> entry :
         p1.visitedPixels.entrySet()) {
         SuperPixel key = entry.getKey();
         Segment value = entry.getValue();
         if (!segmentIdMapping.containsKey(value.id)) {
         segmentIdMapping.put(value.id, segments.size());
         segments.add(new Segment(segments.size()));
         }
         segments.get(segmentIdMapping.get(value.id)).add(key);
         visitedPixels.put(key, segments.get(segmentIdMapping.get(value.id)));
         i++;
         if (i > splitPoint) {
         break;
         }
         }
         //segmentIdMapping = new HashMap<>();

         for (Map.Entry<SuperPixel, Segment> entry :
         p2.visitedPixels.entrySet()) {
         SuperPixel key = entry.getKey();
         Segment value = entry.getValue();
         if (visitedPixels.containsKey(key)) {
         continue;
         }
         if (!segmentIdMapping.containsKey(value.id)) {
         segmentIdMapping.put(value.id, segments.size());
         segments.add(new Segment(segments.size()));
         }
         segments.get(segmentIdMapping.get(value.id)).add(key);
         visitedPixels.put(key, segments.get(segmentIdMapping.get(value.id)));
         i++;
         if (i >= p2.visitedPixels.size()) {
         break;
         }

         }
         this.visitedPixels = visitedPixels;
         this.segments = segments.toArray(new Segment[segments.size()]);
         */

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
        this.segments = segments.toArray(new Segment[segments.size()]);
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
                pq.add(new SegmentSuperPixelEdge(segments[i], superPixelEdge));
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
                    pq.add(new SegmentSuperPixelEdge(segment, superPixelEdge));
                }
            } else if (!visitedPixels.containsKey(currentSuperPixelEdge.U)) {
                genotype[currentSuperPixelEdge.U.id] = currentSuperPixelEdge.V.id;
                segment.add(currentSuperPixelEdge.U);
                visitedPixels.put(currentSuperPixelEdge.U, segment);
                for (SuperPixelEdge superPixelEdge :
                        currentSuperPixelEdge.U.edges) {
                    pq.add(new SegmentSuperPixelEdge(segment, superPixelEdge));
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
        if (this.slic.cielab) {
            float[] cielab1 = sp.getCielab();
            float[] cielab2 = segment.getCielab();
            double distance = 0;
            for (int i = 0; i < cielab1.length; i++) {
                distance += Math.pow(cielab1[i] - cielab2[i], 2);
            }
            return distance;
        }

        Color spColor = sp.getColor();
        Color sColor = segment.getColor();
        int differenceAlpha = spColor.getAlpha() - sColor.getAlpha();
        int differenceRed = spColor.getRed() - sColor.getRed();
        int differenceGreen = spColor.getGreen() - sColor.getGreen();
        int differenceBlue = spColor.getBlue() - sColor.getBlue();


        return Math.sqrt(Math.pow(differenceRed, 2) + Math.pow(differenceGreen, 2) + Math.pow(differenceBlue, 2) + Math.pow(differenceAlpha, 2));
    }

    private double connectivityMeasure(SuperPixel sp, Segment segment) {
        double cm = 0;

        for (int j = 0; j < sp.edges.size(); j++) {
            if (!segment.pixels.contains(sp.edges.get(j).V)) {
                cm += 1.0 / (sp.edges.size());
            }
        }
        return cm;
    }

    /**
     * Update the overalldeviation, connectivitymeasure and the score of an individual.
     * The score is the weighted overall deviation - the weighted connectivity measure.
     * The lower the score, the better
     *
     * @param overallDeviation
     * @param connectivityMeasure
     * @param overallDeviationWeight
     * @param connectivityMeasureWeight
     */
    void scoreIndividual(double overallDeviation, double connectivityMeasure, double overallDeviationWeight, double connectivityMeasureWeight) {
        this.overallDeviation = overallDeviation;
        this.connectivityMeasure = connectivityMeasure;

        this.score = overallDeviation * overallDeviationWeight + connectivityMeasure * connectivityMeasureWeight;
    }


    public boolean isDominatedBy(@NotNull Individual i) {
        return (this.overallDeviation > i.overallDeviation && this.connectivityMeasure >= i.connectivityMeasure) ||
                (this.overallDeviation >= i.overallDeviation && this.connectivityMeasure > i.connectivityMeasure);
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
