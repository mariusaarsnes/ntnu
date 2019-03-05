import org.jetbrains.annotations.NotNull;

public class SegmentSuperPixelEdge implements Comparable<SegmentSuperPixelEdge> {

    private final Segment segment;
    private final SuperPixelEdge superPixelEdge;

    public SegmentSuperPixelEdge(Segment segment, SuperPixelEdge superPixelEdge) {
        this.segment = segment;
        this.superPixelEdge = superPixelEdge;
    }

    public Segment getSegment() {
        return this.segment;
    }

    public SuperPixelEdge getSuperPixelEdge() {
        return this.superPixelEdge;
    }

    @Override
    public int compareTo(@NotNull SegmentSuperPixelEdge o) {
        return this.superPixelEdge.compareTo(o.superPixelEdge);
    }
}
