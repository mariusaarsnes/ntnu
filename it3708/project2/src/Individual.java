import javax.swing.text.Segment;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

public class Individual {


    ImageParser imageParser;
    SLIC slic;
    int[] genotype;
    double score, edgeValue, overallDeviation, crowdingDistance;
    Random random;


    public Individual(ImageParser image, SLIC slic) {
        this.imageParser = image;
        this.slic = slic;
        this.random = new Random();
        this.genotype = createGenotype();
    }

    private int[] createGenotype() {
        final int[] genotype = new int[this.slic.superPixels.size()];

        final HashMap<SuperPixel, Segment> visitedPixels = new HashMap<>();
        final int segmentCount = 2;//this.minimumSegmentCount + this.random.nextInt(this.maximumSegmentCount - this.minimumSegmentCount + 1);
        final Segment[] segments = new Segment[segmentCount];

        final PriorityQueue<SegmentSuperPixelEdge> pq = new PriorityQueue<>();

        for (int i = 0; i < segmentCount; i++) {
            int pos;
            SuperPixel rootPixel;
            do {
                pos = random.nextInt(this.slic.superPixels.size());
                rootPixel = this.slic.superPixels.get(pos);
            }
            while (visitedPixels.containsKey(rootPixel));
            /*
            segments[i] = new Segment(rootPixel);
            visitedPixels.put(rootPixel, segments[i]);
            for (SuperPixelEdge pixelEdge : rootPixel.edges) {
                pq.add(new SegmentSuperPixelEdge(segments[i], pixelEdge));
            }
            */
        }
        return new int[]{9, 2};
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
