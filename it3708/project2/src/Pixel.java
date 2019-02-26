import java.awt.*;

public class Pixel {
    final int row, column, index, argb;
    final Color color;
    final PixelEdge[] edges;
    final Pixel[] neighbours;


    public Pixel(int row, int column, int index, int argb) {
        this.row = row;
        this.column = column;
        this.index = index;
        this.argb = argb;
        this.color = new Color(argb);
        this.edges = new PixelEdge[8];
        this.neighbours = new Pixel[8];
    }
}
