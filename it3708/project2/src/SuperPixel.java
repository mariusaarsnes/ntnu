import java.awt.*;
import java.util.ArrayList;

class SuperPixel {

    ImageParser imageParser;
    CIELab ci = new CIELab();
    int id, x, y;
    int alphaTotal, redTotal, greenTotal, blueTotal;
    boolean cielab;
    ArrayList<Pixel> pixels;
    ArrayList<SuperPixel> neighbours;
    ArrayList<SuperPixelEdge> edges;

    SuperPixel(ImageParser imageParser, int id, int y, int x, int pixelCount) {
        this.imageParser = imageParser;
        this.id = id;
        this.y = y;
        this.x = x;
        this.pixels = new ArrayList<>();
        this.neighbours = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    SuperPixel(ImageParser imageParser, int id, int y, int x, boolean cielab) {
        this.imageParser = imageParser;
        this.id = id;
        this.y = y;
        this.x = x;
        this.cielab = cielab;
        this.pixels = new ArrayList<>();
        this.neighbours = new ArrayList<>();
        this.edges = new ArrayList<>();

    }

    void moveToLowestGradient() {
        int lowestGradient = Integer.MAX_VALUE;
        int bestY = 0, bestX = 0;
        for (int yy = this.y - 1; yy < this.y + 1; yy++) {
            for (int xx = this.x - 1; xx < this.x + 1; xx++) {
                if (yy == 0 || yy == this.imageParser.getHeight() - 1 || xx == 0 || xx == this.imageParser.getWidth() - 1) {
                    continue;
                }
                int tempGradient = getGradient(yy, xx);
                if (tempGradient < lowestGradient) {
                    lowestGradient = tempGradient;
                    bestY = yy;
                    bestX = xx;
                }
            }
        }
        this.y = bestY;
        this.x = bestX;
    }

    private int getGradient(int y, int x) {
        return (int) Math.round(Math.sqrt(this.imageParser.getArgbDistance(y, x + 1, y, x - 1))) +
                (int) Math.round(Math.sqrt(this.imageParser.getArgbDistance(y + 1, x, y - 1, x)));
    }

    public void addPixel(Pixel pixel) {
        if (this.pixels.contains(pixel)) {
            return;
        }
        this.pixels.add(pixel);
    }

    public void addPixels(ArrayList<Pixel> pixels) {
        this.pixels.addAll(pixels);
    }

    public void removePixels(ArrayList<Pixel> pixels) {
        this.pixels.removeAll(pixels);
    }

    public Pixel removePixel(int i) {
        return this.pixels.remove(i);
    }

    public Pixel removePixel(Pixel pixel) {
        for (int i = 0; i < this.pixels.size(); i++) {
            if (this.pixels.get(i) == pixel) {
                return this.pixels.remove(i);
            }
        }
        throw new IllegalArgumentException("Pixel [" + pixel.y + ", " + pixel.x + "] does not exist in superpixel " + this.id);
    }

    public void addNeighbour(SuperPixel neighbour) {
        if (this.neighbours.contains(neighbour)) {
            return;
        }
        this.neighbours.add(neighbour);
        this.edges.add(new SuperPixelEdge(this, neighbour, getDistanceTo(neighbour)));
    }

    public void addEdge(SuperPixel V, double distance) {
        this.edges.add(new SuperPixelEdge(this, V, distance));
    }


    public int getPixelCount() {
        return this.pixels.size();
    }

    public void updateCenter() {
        int[] pos = new int[2];
        for (Pixel p :
                this.pixels) {
            pos[0] += p.y;
            pos[1] += p.x;
        }
        pos[0] /= this.pixels.size();
        pos[1] /= this.pixels.size();
        resetCenter(pos[0], pos[1]);
    }

    public void updateColor() {
        this.redTotal = 0;
        this.greenTotal = 0;
        this.blueTotal = 0;
        this.alphaTotal = 0;
        for (Pixel pixel : this.pixels) {
            this.redTotal += pixel.color.getRed();
            this.greenTotal += pixel.color.getGreen();
            this.blueTotal += pixel.color.getBlue();
            this.alphaTotal += pixel.color.getAlpha();
        }
    }

    public Color getColor() {

        return new Color(
                this.redTotal / this.pixels.size(),
                this.greenTotal / this.pixels.size(),
                this.blueTotal / this.pixels.size(),
                this.alphaTotal / this.pixels.size());

    }

    public float[] getCielab() {
        return ci.fromRGB(new float[]{
                (float) this.redTotal / this.pixels.size(),
                (float) this.greenTotal / this.pixels.size(),
                (float) this.blueTotal / this.pixels.size(),
                (float) this.alphaTotal / this.pixels.size()});
    }

    public void resetCenter(int y, int x) {
        this.y = y;
        this.x = x;

    }

    public double getDistanceTo(SuperPixel sp) {
        if (this.cielab) {
            float[] cielab1 = this.getCielab();
            float[] cielab2 = sp.getCielab();

            double dist = 0;
            for (int i = 0; i < cielab1.length; i++) {
                dist += Math.pow(cielab1[i] - cielab2[i], 2);
            }
            return Math.sqrt(dist);
        } else {
            Color c1 = this.getColor(), c2 = sp.getColor();
            int differenceAlpha = c1.getAlpha() - c2.getAlpha();
            int differenceRed = c1.getRed() - c2.getRed();
            int differenceGreen = c1.getGreen() - c2.getGreen();
            int differenceBlue = c1.getBlue() - c2.getBlue();

            return Math.sqrt(differenceAlpha * differenceAlpha + differenceRed * differenceRed + differenceGreen * differenceGreen + differenceBlue * differenceBlue);
        }
    }
}
