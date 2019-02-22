import java.util.Comparator;


public class PixelEdge implements Comparable<PixelEdge> {
    final Pixel U, V;
    final double distance;
    final Direction direction;

    PixelEdge(Pixel U, Pixel V, double distance, Direction direction) {
        this.U = U;
        this.V = V;
        this.distance = distance;
        this.direction = direction;
    }

    @Override
    public int compareTo(PixelEdge o) {
        return Double.compare(o.distance, this.distance);
    }
}

class rgbDistanceComparator implements Comparator<PixelEdge> {

    @Override
    public int compare(PixelEdge o1, PixelEdge o2) {
        return Double.compare(o1.distance, o2.distance);
    }
}
