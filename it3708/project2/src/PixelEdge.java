import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class PixelEdge implements Comparable<PixelEdge> {
    final Pixel U, V;
    final double distance;

    PixelEdge(Pixel U, Pixel V, double distance) {
        this.U = U;
        this.V = V;
        this.distance = distance;
    }

    @Override
    public int compareTo(@NotNull PixelEdge o) {
        return Double.compare(this.distance, o.distance);
    }
}

/**
 * pixelEdgeDistanceComparator:
 * Used to sort pixel edges by distance
 */
class pixelEdgeDistanceComparator implements Comparator<PixelEdge> {

    @Override
    public int compare(PixelEdge o1, PixelEdge o2) {
        return Double.compare(o1.distance, o2.distance);
    }
}


