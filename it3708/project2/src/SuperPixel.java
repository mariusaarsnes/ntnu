import java.awt.*;
import java.util.ArrayList;

public class SuperPixel {

    int id, x, y;
    Pixel centroid;
    ArrayList<Pixel> pixels;


    public SuperPixel(int id, int y, int x) {
        this.id = id;
        this.y = y;
        this.x = x;
        this.pixels = new ArrayList<>();
    }

    public void moveToLowestGradient(PixelMatrix pixelMatrix) {
        int lowestGradient = Integer.MAX_VALUE;
        int bestY = 0, bestX = 0;
        for (int yy = this.y - 1; yy < this.y + 1; yy++) {
            for (int xx = this.x - 1; xx < this.x + 1; xx++) {
                if (yy == 0 || yy == pixelMatrix.getHeight() || xx == 0 || xx == pixelMatrix.getWidth()) {
                    continue;
                }
                int tempGradient = getGradient(pixelMatrix, yy, xx);
                if (tempGradient < lowestGradient) {
                    lowestGradient = tempGradient;
                    bestY = yy;
                    bestX = xx;
                }
            }
        }
        this.y = bestY;
        this.x = bestX;
        this.centroid = pixelMatrix.getPixel(bestY, bestX);
    }

    private int getGradient(PixelMatrix pixelMatrix, int y, int x) {
        /*
        float[] lab1 = pixelMatrix.getPixel(y, x + 1).cielab;
        float[] lab2 = pixelMatrix.getPixel(y, x - 1).cielab;
        float[] lab3 = pixelMatrix.getPixel(y + 1, x).cielab;
        float[] lab4 = pixelMatrix.getPixel(y - 1, x).cielab;

        float sub1 = 0, sub2 = 0;

        for (int i = 0; i < 4; i++) {
            sub1 += (lab1[i] - lab2[i]) * (lab1[i] - lab2[i]);
            sub2 += (lab3[i] - lab4[i]) * (lab3[i] - lab4[i]);
        }
        */
        Color argb1 = pixelMatrix.getPixel(y, x + 1).color;
        Color argb2 = pixelMatrix.getPixel(y, x - 1).color;
        Color argb3 = pixelMatrix.getPixel(y + 1, x).color;
        Color argb4 = pixelMatrix.getPixel(y - 1, x).color;

        float sub1 = 0, sub2 = 0;


        sub1 = (argb1.getAlpha() - argb2.getAlpha()) * (argb1.getAlpha() - argb2.getAlpha()) +
                (argb1.getBlue() - argb2.getBlue()) * (argb1.getBlue() - argb2.getBlue()) +
                (argb1.getRed() - argb2.getRed()) * (argb1.getRed() - argb2.getRed()) +
                (argb1.getGreen() - argb2.getGreen()) * (argb1.getGreen() - argb2.getGreen());
        sub2 += (argb3.getAlpha() - argb4.getAlpha()) * (argb3.getAlpha() - argb4.getAlpha()) +
                (argb3.getBlue() - argb4.getBlue()) * (argb3.getBlue() - argb4.getBlue()) +
                (argb3.getRed() - argb4.getRed()) * (argb3.getRed() - argb4.getRed()) +
                (argb3.getGreen() - argb4.getGreen()) * (argb3.getGreen() - argb4.getGreen());
        return (int) Math.round(Math.sqrt(sub1)) + (int) Math.round(Math.sqrt(sub2));
    }
}
