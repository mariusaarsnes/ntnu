public class SLIC {

    PixelMatrix pixelMatrix;
    int n;

    public SLIC(PixelMatrix pixelMatrix) {
        this.pixelMatrix = pixelMatrix;
    }

    public void run() {
        int h = this.pixelMatrix.getHeight(), w = this.pixelMatrix.getWidth();
        int k1 = h / 10, k2 = w / 10;
        int sh = h / k1, sw = w / k2;


        // Step 1: Select Centroids
        Pixel[] centroids = new Pixel[k1 * k2];
        int i = 0;
        for (int y = sh; y < this.pixelMatrix.getHeight(); y += sh) {
            for (int x = sw; x < this.pixelMatrix.getWidth(); x += sw) {
                centroids[i] = this.pixelMatrix.getPixel(y, x);
                i++;
            }
        }

        // Loop thoru
    }

}
