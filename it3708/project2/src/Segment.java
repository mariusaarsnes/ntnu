import java.awt.*;
import java.util.HashSet;

public class Segment {

    final HashSet<SuperPixel> pixels = new HashSet<>();
    private final CIELab ci = new CIELab();
    public double overallDeviation;
    public double connectivityMeasure;
    public double edgeValue;
    public int id, alphaTotal, redTotal, greenTotal, blueTotal;
    Color color;

    public Segment(int id) {
        this.id = id;
        this.alphaTotal = 0;
        this.redTotal = 0;
        this.greenTotal = 0;
        this.blueTotal = 0;

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
        this.updateColor();

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
        this.updateColor();

    }

    public void remove(SuperPixel pixel) {
        this.pixels.remove(pixel);
        this.alphaTotal -= pixel.color.getAlpha();
        this.redTotal -= pixel.color.getRed();
        this.greenTotal -= pixel.color.getGreen();
        this.blueTotal -= pixel.color.getBlue();
        this.updateColor();
    }

    private void updateColor() {
        this.color = new Color(
                this.redTotal / this.pixels.size(),
                this.greenTotal / this.pixels.size(),
                this.blueTotal / this.pixels.size(),
                this.alphaTotal / this.pixels.size());
    }

    int getArgb() {
        return this.color.getRGB();
    }

    Color getColor() {
        return this.color;
    }

    float[] getCielab() {
        return ci.fromRGB(new float[]{
                this.color.getRed(),
                this.color.getGreen(),
                this.color.getBlue(),
                this.color.getAlpha()});
    }
}