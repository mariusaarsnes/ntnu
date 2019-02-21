public class PixelMatrix {

    private final ImageParser imageParser;
    private final Pixel[][] pixels;

    public PixelMatrix(ImageParser imageParser) {
        this.imageParser = imageParser;
        pixels = createPixels();

    }

    private Pixel[][] createPixels() {
        final Pixel[][] pixels = new Pixel[imageParser.getHeight()][imageParser.getWidth()];

        // Create Pixel matrix
        for (int y = 0; y < imageParser.getHeight(); y++) {
            for (int x = 0; x < imageParser.getWidth(); x++) {
                pixels[y][x] = new Pixel(y, x, imageParser.getPixels()[y][x]);
            }
        }

        // Find neighbours and distances
        for (int y = 0; y < imageParser.getHeight(); y++) {
            for (int x = 0; x < imageParser.getWidth(); x++) {
                final Pixel currentPixel = pixels[y][x];

                // Has neighbour NORTH
                if (y > 0) {
                    // Has neighbour NORTH WEST
                    if (x > 0) {
                        currentPixel.neighbours[6] = pixels[y - 1][x - 1];
                        currentPixel.edges[6] = currentPixel.neighbours[6].edges[5];
                    }
                    // Has neighbour NORTH EAST
                    if (x < imageParser.getWidth() - 1) {
                        currentPixel.neighbours[4] = pixels[y - 1][x + 1];
                        currentPixel.edges[4] = currentPixel.neighbours[4].edges[7];
                    }
                    currentPixel.neighbours[2] = pixels[y - 1][x];
                    currentPixel.edges[2] = currentPixel.neighbours[2].edges[3];
                }

                // Has neighbour WEST
                if (x > 0) {
                    // Has neighbour SOUTH WEST
                    if (y < imageParser.getHeight() - 1) {
                        currentPixel.neighbours[7] = pixels[y + 1][x - 1];
                        currentPixel.edges[7] = new PixelEdge(currentPixel, currentPixel.neighbours[7], euclideanDistance(currentPixel, currentPixel.neighbours[7]));
                    }
                    currentPixel.neighbours[1] = pixels[y][x - 1];
                    currentPixel.edges[1] = currentPixel.neighbours[1].edges[0];
                }
                // Has neighbour SOUTH
                if (y < imageParser.getHeight() - 1) {
                    // Has neighbour SOUTH EAST
                    if (x < imageParser.getWidth() - 1) {
                        currentPixel.neighbours[5] = pixels[y + 1][x + 1];
                        currentPixel.edges[5] = new PixelEdge(currentPixel, currentPixel.neighbours[5], euclideanDistance(currentPixel, currentPixel.neighbours[5]));
                    }
                    currentPixel.neighbours[3] = pixels[y + 1][x];
                    currentPixel.edges[3] = new PixelEdge(currentPixel, currentPixel.neighbours[3], euclideanDistance(currentPixel, currentPixel.neighbours[3]));
                }
                // Has neighbour EAST
                if (x < imageParser.getWidth() - 1) {
                    currentPixel.neighbours[0] = pixels[y][x + 1];
                    currentPixel.edges[0] = new PixelEdge(currentPixel, currentPixel.neighbours[0], euclideanDistance(currentPixel, currentPixel.neighbours[0]));
                }
            }
        }
        return pixels;
    }


    private double euclideanDistance(Pixel p1, Pixel p2) {
        int differenceRed = p1.color.getRed() - p2.color.getRed();
        int differenceGreen = p1.color.getGreen() - p2.color.getGreen();
        int differenceBlue = p1.color.getBlue() - p2.color.getBlue();

        return Math.sqrt(Math.pow(differenceRed, 2) + Math.pow(differenceGreen, 2) + Math.pow(differenceBlue, 2));
    }


    public Pixel[][] getPixels() {
        return this.pixels;
    }

    public Pixel getPixel(int y, int x) {
        return this.pixels[y][x];
    }
}
