import java.awt.*;
import java.util.HashSet;

public class Segment {

    final HashSet<SuperPixel> pixels = new HashSet<>();
    public double overallDeviation;

    private int alphaTotal, redTotal, greenTotal, blueTotal, pixelCount;

    public Segment(SuperPixel root) {
        pixels.add(root);
        this.alphaTotal = root.alphaTotal;
        this.redTotal = root.redTotal;
        this.greenTotal = root.greenTotal;
        this.blueTotal = root.blueTotal;
        this.pixelCount = root.pixelCount;
    }

    public void add(SuperPixel pixel) {
        this.pixels.add(pixel);
        this.alphaTotal += pixel.alphaTotal;
        this.redTotal += pixel.redTotal;
        this.greenTotal += pixel.greenTotal;
        this.blueTotal += pixel.blueTotal;
        pixelCount += pixel.pixelCount;
    }

    int getArgb() {
        return new Color(
                this.redTotal / this.pixelCount,
                this.greenTotal / this.pixelCount,
                this.blueTotal / this.pixelCount,
                this.alphaTotal / this.pixelCount).getRGB();
    }

    Color getColor() {
        return new Color(
                this.redTotal / this.pixelCount,
                this.greenTotal / this.pixelCount,
                this.blueTotal / this.pixelCount,
                this.alphaTotal / this.pixelCount);
    }
}