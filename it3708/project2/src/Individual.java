import javafx.util.Pair;

import java.util.*;

public class Individual {


    ImageParser imageParser;
    PixelMatrix pixelMatrix;
    Direction[] genotype;
    double score, edgeValue, overallDeviation, crowdingDistance;
    Random random;


    public Individual(ImageParser image, PixelMatrix pixelMatrix) {
        this.imageParser = image;
        this.pixelMatrix = pixelMatrix;
        this.random = new Random();
        this.genotype = createGenotype();
    }


    private Direction[] createGenotype() {
        Direction[] genotype = new Direction[this.pixelMatrix.getSize()];

        final ArrayList<Pixel> visitedPixels = new ArrayList<Pixel>();
        final ArrayList<PixelEdge> mst = new ArrayList<PixelEdge>();
        final Pixel root = this.pixelMatrix.getPixel(this.random.nextInt(this.pixelMatrix.getHeight()), this.random.nextInt(this.pixelMatrix.getWidth()));
        final PriorityQueue<PixelEdge> pq = new PriorityQueue<>(new rgbDistanceComparator());
        addPixelEdgesToPriorityQueue(pq, root.edges);
        while (!pq.isEmpty()) {
            final PixelEdge pixelEdge = pq.remove();
            boolean addEdgeToMst = false;
            if (!visitedPixels.contains(pixelEdge.U)) {
                addEdgeToMst = true;
                visitedPixels.add(pixelEdge.U);
                addPixelEdgesToPriorityQueue(pq, pixelEdge.U.edges);

            }
            if (!visitedPixels.contains(pixelEdge.V)) {
                addEdgeToMst = true;
                visitedPixels.add(pixelEdge.V);
                addPixelEdgesToPriorityQueue(pq, pixelEdge.V.edges);
            }
            if (addEdgeToMst) {
                mst.add(pixelEdge);
            }

        }
        for (PixelEdge pixelEdge : mst) {
            genotype[pixelEdge.U.index] = pixelEdge.direction;
        }
        return genotype;
    }

    private void addPixelEdgesToPriorityQueue(PriorityQueue<PixelEdge> pq, PixelEdge[] edges) {
        for (PixelEdge edge : edges) {
            if (edge != null) {
                pq.add(edge);
            }
        }
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
