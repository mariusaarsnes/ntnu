import java.awt.*;

public class Pixel {
    final int y, x, argb;
    final Color color;
    final PixelEdge[] edges;
    final Pixel[] neighbours;
    final CIELab ci = new CIELab();
    final float[] cielab;

    Pixel(int y, int x, int argb, Color color, float[] cielab) {
        this.y = y;
        this.x = x;
        this.argb = argb;
        this.color = color;
        this.cielab = cielab;
        this.edges = new PixelEdge[8];
        this.neighbours = new Pixel[8];
    }

    int getEdgeIndex(PixelEdge pixelEdge) {
        for (int i = 0; i < 8; i++) {
            if (edges[i] == pixelEdge) {
                return i;
            }
        }
        return -1;
    }
}
