import java.awt.*;
import java.util.HashSet;

public class Segment {

    final HashSet<SuperPixel> pixels = new HashSet<>();
    private final CIELab ci = new CIELab();
    public double overallDeviation;
    public double connectivityMeasure;
    int id, alphaTotal, redTotal, greenTotal, blueTotal, pixelCount;

    public Segment(int id) {
        this.id = id;
        this.alphaTotal = 0;
        this.redTotal = 0;
        this.greenTotal = 0;
        this.blueTotal = 0;
        this.pixelCount = 0;
    }

    public Segment(SuperPixel root, int id) {
        this.id = id;
        this.pixels.add(root);
        this.alphaTotal = root.alphaTotal;
        this.redTotal = root.redTotal;
        this.greenTotal = root.greenTotal;
        this.blueTotal = root.blueTotal;
        this.pixelCount = root.getPixelCount();
    }

    public void add(SuperPixel pixel) {
        this.pixels.add(pixel);
        this.alphaTotal += pixel.alphaTotal;
        this.redTotal += pixel.redTotal;
        this.greenTotal += pixel.greenTotal;
        this.blueTotal += pixel.blueTotal;
        this.pixelCount += pixel.getPixelCount();
    }

    public void remove(SuperPixel pixel) {
        this.pixels.remove(pixel);
        this.alphaTotal -= pixel.alphaTotal;
        this.redTotal -= pixel.redTotal;
        this.greenTotal -= pixel.greenTotal;
        this.blueTotal -= pixel.blueTotal;
        this.pixelCount -= pixel.getPixelCount();
    }

    int getArgb() {
        return new Color(
                this.redTotal / this.pixelCount,
                this.greenTotal / this.pixelCount,
                this.blueTotal / this.pixelCount,
                this.alphaTotal / this.pixelCount).getRGB();
    }

    Color getColor() {
        if (this.pixelCount == 0) {
            System.out.println("something is wrong");
        }
        return new Color(
                this.redTotal / this.pixelCount,
                this.greenTotal / this.pixelCount,
                this.blueTotal / this.pixelCount,
                this.alphaTotal / this.pixelCount);
    }

    float[] getCielab() {
        return ci.fromRGB(new float[]{
                (float) this.redTotal / this.pixelCount,
                (float) this.greenTotal / this.pixelCount,
                (float) this.blueTotal / this.pixelCount,
                (float) this.alphaTotal / this.pixelCount});
    }
}