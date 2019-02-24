import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SLIC {

    PixelMatrix pixelMatrix;
    ArrayList<SuperPixel> clusters;
    int[][] label;

    public SLIC(PixelMatrix pixelMatrix) {

        this.pixelMatrix = pixelMatrix;
        this.clusters = new ArrayList<>();
    }

    public void run(int numClusters) {
        /*
        int h = this.pixelMatrix.getHeight(), w = this.pixelMatrix.getWidth();
        int k1 = h / 10, k2 = w / 10;
        int sh = h / k1, sw = w / k2;


        // Step 1: Select Centroids
        ArrayList<Pixel> centroids = new ArrayList<Pixel>();
        for (int y = sh / 2; y < this.pixelMatrix.getHeight(); y += sh) {
            for (int x = sw / 2; x < this.pixelMatrix.getWidth(); x += sw) {
                centroids.add(this.pixelMatrix.getPixel(y, x));
            }
        }

        */
        System.out.println("Run SLIC");
        int n = this.pixelMatrix.getSize();
        int m = 10;
        int s = (int) Math.round(Math.sqrt((float) n / numClusters));
        int i = 0;
        System.out.println("\tGenerating clusters");
        for (int y = s / 2; y < this.pixelMatrix.getHeight(); y += s) {
            for (int x = s / 2; x < this.pixelMatrix.getWidth(); x += s) {
                this.clusters.add(new SuperPixel(i, y, x));
            }
        }
        System.out.println("\tFinished generating clusters");

        // Move centroids to lowest gradient
        System.out.println("\tStarting to move centroids to lowest gradient");
        for (SuperPixel superPixel : this.clusters
        ) {
            superPixel.moveToLowestGradient(this.pixelMatrix);

        }
        System.out.println("\tFinished moving centroids to lowest gradient");

        double error = 0;
        int loop = 0;
        int[][] label = new int[this.pixelMatrix.getHeight()][this.pixelMatrix.getWidth()];
        double[][] distance = new double[this.pixelMatrix.getHeight()][this.pixelMatrix.getWidth()];
        for (int[] row : label) {
            Arrays.fill(row, -1);
        }
        for (double[] row : distance) {
            Arrays.fill(row, Double.MAX_VALUE);
        }

        do {
            loop++;
            System.out.println("\tLoop " + loop + " started!");
            for (int k = 0; k < this.clusters.size(); k++) {
                SuperPixel sp = this.clusters.get(k);
                int yMin = Math.max(0, sp.y - 2 * s);
                int yMax = Math.min(this.pixelMatrix.getHeight(), sp.y + 2 * s);
                int xMin = Math.max(0, sp.x - 2 * s);
                int xMax = Math.min(this.pixelMatrix.getWidth(), sp.x + 2 * s);

                for (int y = yMin; y < yMax; y++) {
                    for (int x = xMin; x < xMax; x++) {
                        //TODO: Figure out what is wrong with cielab values
                        /*
                        float[] lab = this.pixelMatrix.getPixel(y, x).cielab;
                        double dc = Math.sqrt(
                                (lab[0] - sp.centroid.cielab[0]) * (lab[0] - sp.centroid.cielab[0]) +
                                        (lab[1] - sp.centroid.cielab[1]) * (lab[1] - sp.centroid.cielab[1]) +
                                        (lab[2] - sp.centroid.cielab[2]) * (lab[2] - sp.centroid.cielab[2])
                        );
                        */
                        Color color = this.pixelMatrix.getPixel(y, x).color;
                        double dc = Math.sqrt(
                                (color.getAlpha() - sp.centroid.color.getAlpha()) * (color.getAlpha() - sp.centroid.color.getAlpha()) +
                                        (color.getBlue() - sp.centroid.color.getBlue()) * (color.getBlue() - sp.centroid.color.getBlue()) +
                                        (color.getRed() - sp.centroid.color.getRed()) * (color.getRed() - sp.centroid.color.getRed()) +
                                        (color.getGreen() - sp.centroid.color.getGreen()) * (color.getGreen() - sp.centroid.color.getGreen())
                        );
                        double ds = Math.sqrt((x - sp.x) * (x - sp.x) + (y - sp.y) * (y - sp.y));
                        double d = Math.sqrt(dc * dc + ((ds / s) * (ds / s)) * (m * m));
                        if (d < distance[y][x]) {
                            label[y][x] = k;
                            distance[y][x] = d;
                        }
                    }
                }
            }

            error = updateClusterCenters(label);
            System.out.println("\tLoop " + loop + " done! Residual error: " + error);

        } while (error != 0);

        this.label = label;

        //TODO: implement connected components algorithm to avoid disconnected labels
        System.out.println("Number of clusters before enforcing connectivity: " + this.clusters.size());
        enforceConnectivity();
        System.out.println("Number of clusters after  enforcing connectivity: " + this.clusters.size());
        System.out.println("Total number of pixels in image: " + this.pixelMatrix.getSize());
        updateClusterCenters(this.label);
        System.out.println("Finished with running SLIC");

    }

    private double updateClusterCenters(int[][] label) {
        double error = 0;
        int[][] newClusterCenter = new int[this.clusters.size()][2];
        for (int[] val : newClusterCenter) {
            val[0] = 0;
            val[1] = 0;
        }
        int[] newClusterPixelCount = new int[this.clusters.size()];
        for (int y = 0; y < this.pixelMatrix.getHeight(); y++) {
            for (int x = 0; x < this.pixelMatrix.getWidth(); x++) {
                int labelVal = label[y][x];
                if (labelVal != -1) {
                    newClusterCenter[labelVal][0] += y;
                    newClusterCenter[labelVal][1] += x;
                    newClusterPixelCount[labelVal]++;
                }
            }
        }
        for (int i = 0; i < newClusterCenter.length; i++) {
            int newY = Math.round(((float) newClusterCenter[i][0]) / newClusterPixelCount[i]);
            int newX = Math.round(((float) newClusterCenter[i][1]) / newClusterPixelCount[i]);
            error += Math.sqrt(
                    (newX - this.clusters.get(i).x) * (newX - this.clusters.get(i).x) +
                            (newY - this.clusters.get(i).y) * (newY - this.clusters.get(i).y));
            this.clusters.get(i).y = newY;
            this.clusters.get(i).x = newX;

        }
        return error;


    }

    private void enforceConnectivity() {
        boolean[][] connectedPixels = new boolean[this.pixelMatrix.getHeight()][this.pixelMatrix.getWidth()];
        for (SuperPixel cluster : this.clusters) {
            getConnectedSection(connectedPixels, cluster.y, cluster.x);
        }
        for (int y = 0; y < this.pixelMatrix.getHeight(); y++) {
            for (int x = 0; x < this.pixelMatrix.getWidth(); x++) {
                if (connectedPixels[y][x]) {
                    continue;
                }
                ArrayList<int[]> visited = getConnectedSection(connectedPixels, y, x);
                createNewLabeledSection(visited);
            }
        }

    }


    private ArrayList<int[]> getConnectedSection(boolean[][] connectedPixels, int y, int x) {
        ArrayList<int[]> visited = new ArrayList<>();
        ArrayList<int[]> queue = new ArrayList<>();
        boolean[][] queued = new boolean[this.pixelMatrix.getHeight()][this.pixelMatrix.getWidth()];
        int[] current = {y, x};
        while (true) {
            visited.add(current);
            connectedPixels[current[0]][current[1]] = true;
            int yMax = Integer.min(this.pixelMatrix.getHeight(), current[0] + 2);
            int yMin = Integer.max(0, current[0] - 1);
            int xMax = Integer.min(this.pixelMatrix.getWidth(), current[1] + 2);
            int xMin = Integer.max(0, current[1] - 1);
            for (int yy = yMin; yy < yMax; yy++) {
                for (int xx = xMin; xx < xMax; xx++) {
                    if (this.label[yy][xx] != this.label[current[0]][current[1]] || queued[yy][xx] || connectedPixels[yy][xx]) {
                        continue;
                    }
                    queued[yy][xx] = true;
                    queue.add(new int[]{yy, xx});
                }
            }
            if (queue.size() == 0) {
                break;
            }
            current = queue.remove(0);
        }
        return visited;
    }

    private void createNewLabeledSection(ArrayList<int[]> visited) {
        int centerY = 0;
        int centerX = 0;
        for (int[] pos : visited) {
            this.label[pos[0]][pos[1]] = this.clusters.size();
            centerY += pos[0];
            centerX += pos[1];
        }
        centerY /= visited.size();
        centerX /= visited.size();
        this.clusters.add(new SuperPixel(this.clusters.size(), centerY, centerX));
    }
}
