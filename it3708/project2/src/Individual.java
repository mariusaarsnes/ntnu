import java.util.*;

public class Individual {


    ImageParser imageParser;
    SLIC slic;
    int[] genotype;
    int minimumSegmentCount, maximumSegmentCount;
    double score, edgeValue, overallDeviation, crowdingDistance;
    HashMap<SuperPixel,Segment> visitedPixels;
    Segment[] segments;
    Random random;


    public Individual(ImageParser image, SLIC slic, int minimumSegmentCount, int maximumSegmentCount ) {
        this.imageParser = image;
        this.slic = slic;
        this.random = new Random();
        this.minimumSegmentCount = minimumSegmentCount;
        this.maximumSegmentCount = maximumSegmentCount;

        this.genotype = createGenotype();
    }

    private int[] createGenotype() {
        final int[] genotype = new int[this.slic.superPixels.size()];
        //TODO: Fix byll
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

            segments[i] = new Segment(rootPixel);
            visitedPixels.put(rootPixel, segments[i]);
            for (SuperPixelEdge superPixelEdge : rootPixel.edges) {
                pq.add(new SegmentSuperPixelEdge(segments[i], superPixelEdge));
            }
        }
        while (!pq.isEmpty()) {
            final SegmentSuperPixelEdge segmentSuperPixelEdge = pq.remove();
            final Segment segment = segmentSuperPixelEdge.getSegment();
            final SuperPixelEdge currentSuperPixelEdge = segmentSuperPixelEdge.getSuperPixelEdge();

            if (!visitedPixels.containsKey(currentSuperPixelEdge.V)){
                segment.add(currentSuperPixelEdge.V);
                visitedPixels.put(currentSuperPixelEdge.V,segment);
                for (SuperPixelEdge superPixelEdge:currentSuperPixelEdge.V.edges) {
                    pq.add(new SegmentSuperPixelEdge(segment,superPixelEdge));
                }
            } else if (!visitedPixels.containsKey(currentSuperPixelEdge.U)) {
                segment.add(currentSuperPixelEdge.U);
                visitedPixels.put(currentSuperPixelEdge.U,segment);
                for (SuperPixelEdge superPixelEdge :
                        currentSuperPixelEdge.U.edges) {
                    pq.add(new SegmentSuperPixelEdge(segment, superPixelEdge));
                }
            }
        }
        int count = 0;
        for (Segment segment :
                segments) {
            count += segment.pixels.size();
        }
        this.segments = segments;
        this.visitedPixels = visitedPixels;
        return genotype;
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
