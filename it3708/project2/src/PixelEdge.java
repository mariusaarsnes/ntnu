import java.util.Comparator;


/**
 *     Directions W, NW, N, NE means V is starting node
 *     Directions E, SE, S, SW means U is starting node
 */
public class PixelEdge implements Comparable<PixelEdge> {
    final Pixel U, V;
    final double distance;

    PixelEdge(Pixel U, Pixel V, double distance) {
        this.U = U;
        this.V = V;
        this.distance = distance;
    }

    @Override
    public int compareTo(PixelEdge o) {
        return Double.compare(o.distance, this.distance);
    }
}

class rgbDistanceComparator implements Comparator<PixelEdge> {

    @Override
    public int compare(PixelEdge o1, PixelEdge o2) {
        return Double.compare(o2.distance, o1.distance);
    }
}
