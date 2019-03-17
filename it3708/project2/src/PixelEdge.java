import org.jetbrains.annotations.NotNull;

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


