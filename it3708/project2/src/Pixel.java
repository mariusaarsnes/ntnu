import java.awt.*;

public class Pixel {
    final int y, x, argb;
    final Color color;
    final PixelEdge[] edges;
    final Pixel[] neighbours;
    final CIELab ci = new CIELab();
    final float[] cielab;

    Pixel(int y, int x, int argb, Color color) {
        this.y = y;
        this.x = x;
        this.argb = argb;
        this.color = color;
        this.cielab = ci.fromRGB(new float[]{color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()});
        this.edges = new PixelEdge[8];
        this.neighbours = new Pixel[8];
    }


}
