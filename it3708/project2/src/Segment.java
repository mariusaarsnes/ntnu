import java.awt.*;
import java.util.HashSet;

public class Segment {

    final HashSet<SuperPixel> pixels = new HashSet<>();
    private final CIELab ci = new CIELab();
    public double overallDeviation;
    public double connectivityMeasure;
    public double edgeValue;
    public int id, alphaTotal, redTotal, greenTotal, blueTotal, pixelCount;

    public Segment(int id) {
        this.id = id;
        this.alphaTotal = 0;
        this.redTotal = 0;
        this.greenTotal = 0;
        this.blueTotal = 0;
        this.pixelCount = 0;

        this.overallDeviation = 0;
        this.connectivityMeasure = 0;
        this.edgeValue = 0;

    }

    public Segment(SuperPixel root, int id) {
        this.id = id;
        this.pixels.add(root);
        this.alphaTotal = root.color.getAlpha();
        this.redTotal = root.color.getRed();
        this.greenTotal = root.color.getGreen();
        this.blueTotal = root.color.getBlue();
        this.pixelCount = root.getPixelCount();

        this.overallDeviation = 0;
        this.connectivityMeasure = 0;
        this.edgeValue = 0;
    }

    public void add(SuperPixel pixel) {
        this.pixels.add(pixel);
        this.alphaTotal += pixel.color.getAlpha();
        this.redTotal += pixel.color.getRed();
        this.greenTotal += pixel.color.getGreen();
        this.blueTotal += pixel.color.getBlue();
        this.pixelCount += pixel.getPixelCount();
    }

    public void remove(SuperPixel pixel) {
        this.pixels.remove(pixel);
        this.alphaTotal -= pixel.color.getAlpha();
        this.redTotal -= pixel.color.getRed();
        this.greenTotal -= pixel.color.getGreen();
        this.blueTotal -= pixel.color.getBlue();
        this.pixelCount -= pixel.getPixelCount();
    }

    int getArgb() {
        Color color = getColor();
        return color.getRGB();
    }

    Color getColor() {
        return new Color(
                this.redTotal / this.pixels.size(),
                this.greenTotal / this.pixels.size(),
                this.blueTotal / this.pixels.size(),
                this.alphaTotal / this.pixels.size());
    }

    float[] getCielab() {
        Color color = getColor();
        return ci.fromRGB(new float[]{
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                color.getAlpha()});
    }
}