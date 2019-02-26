import org.jetbrains.annotations.NotNull;

public class PixelMatrix {

    private final Pixel[][] pixels;
    private final int height, width;

    public PixelMatrix(@NotNull ImageParser imageParser) {

        this.height = imageParser.getHeight();
        this.width = imageParser.getWidth();
        System.out.println("\tStarting to build new imageParser matrix");
        pixels = createPixels(imageParser);
        System.out.println("\tFinished building new imageParser matrix");

    }

    private Pixel[][] createPixels(@NotNull ImageParser imageParser) {
        final Pixel[][] pixels = new Pixel[imageParser.getHeight()][imageParser.getWidth()];

        // Create Pixel matrix
        int height = imageParser.getHeight(), width = imageParser.getWidth();
        int[][] imagePixels = imageParser.getPixelArgb();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = new Pixel(y, x, y * width + x, imagePixels[y][x]);
            }
        }
        /*
        // Find neighbours and distances
        for (int y = 0; y < imageParser.getHeight(); y++) {
            for (int x = 0; x < imageParser.getWidth(); x++) {
                final Pixel currentPixel = pixels[y][x];

                // Has neighbour NORTH
                if (y > 0) {
                    // Has neighbour NORTH WEST
                    if (x > 0) {
                        currentPixel.neighbours[6] = pixels[y - 1][x - 1];
                        currentPixel.edges[6] = new PixelEdge(
                                currentPixel,
                                currentPixel.neighbours[6],
                                euclideanDistance(currentPixel, currentPixel.neighbours[6]),
                                Direction.NW);
                    }
                    // Has neighbour NORTH EAST
                    if (x < imageParser.getWidth() - 1) {
                        currentPixel.neighbours[4] = pixels[y - 1][x + 1];
                        currentPixel.edges[4] = new PixelEdge(
                                currentPixel,
                                currentPixel.neighbours[4],
                                euclideanDistance(currentPixel, currentPixel.neighbours[4]),
                                Direction.NE);
                    }
                    currentPixel.neighbours[2] = pixels[y - 1][x];
                    currentPixel.edges[2] = new PixelEdge(
                            currentPixel,
                            currentPixel.neighbours[2],
                            euclideanDistance(currentPixel, currentPixel.neighbours[2]),
                            Direction.N);
                }

                // Has neighbour WEST
                if (x > 0) {
                    // Has neighbour SOUTH WEST
                    if (y < imageParser.getHeight() - 1) {
                        currentPixel.neighbours[7] = pixels[y + 1][x - 1];
                        currentPixel.edges[7] = new PixelEdge(
                                currentPixel,
                                currentPixel.neighbours[7],
                                euclideanDistance(currentPixel, currentPixel.neighbours[7]),
                                Direction.SW);
                    }
                    currentPixel.neighbours[1] = pixels[y][x - 1];
                    currentPixel.edges[1] = new PixelEdge(
                            currentPixel,
                            currentPixel.neighbours[1],
                            euclideanDistance(currentPixel, currentPixel.neighbours[1]),
                            Direction.W);
                }
                // Has neighbour SOUTH
                if (y < imageParser.getHeight() - 1) {
                    // Has neighbour SOUTH EAST
                    if (x < imageParser.getWidth() - 1) {
                        currentPixel.neighbours[5] = pixels[y + 1][x + 1];
                        currentPixel.edges[5] = new PixelEdge(
                                currentPixel,
                                currentPixel.neighbours[5],
                                euclideanDistance(currentPixel, currentPixel.neighbours[5]),
                                Direction.SE);
                    }
                    currentPixel.neighbours[3] = pixels[y + 1][x];
                    currentPixel.edges[3] = new PixelEdge(
                            currentPixel,
                            currentPixel.neighbours[3],
                            euclideanDistance(currentPixel, currentPixel.neighbours[3]),
                            Direction.S);
                }
                // Has neighbour EAST
                if (x < imageParser.getWidth() - 1) {
                    currentPixel.neighbours[0] = pixels[y][x + 1];
                    currentPixel.edges[0] = new PixelEdge(
                            currentPixel,
                            currentPixel.neighbours[0],
                            euclideanDistance(currentPixel, currentPixel.neighbours[0]),
                            Direction.E);
                }
            }
        }
         */
        return pixels;
    }


    private double euclideanDistance(Pixel p1, Pixel p2) {
        int differenceRed = p1.color.getRed() - p2.color.getRed();
        int differenceGreen = p1.color.getGreen() - p2.color.getGreen();
        int differenceBlue = p1.color.getBlue() - p2.color.getBlue();

        return Math.sqrt(Math.pow(differenceRed, 2) + Math.pow(differenceGreen, 2) + Math.pow(differenceBlue, 2));
    }


    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getSize() {
        return this.height * this.width;
    }

    public Pixel[][] getPixels() {
        return this.pixels;
    }

    public Pixel getPixel(int y, int x) {
        return this.pixels[y][x];
    }
}
