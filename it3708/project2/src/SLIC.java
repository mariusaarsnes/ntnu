import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class SLIC {

    ImageParser imageParser;
    int imageHeight, imageWidth, numPixels;
    ArrayList<SuperPixel> superPixels;
    int[][] label;

    public SLIC(ImageParser imageParser) {

        this.imageParser = imageParser;
        this.imageHeight = imageParser.getHeight();
        this.imageWidth = imageParser.getWidth();
        this.numPixels = imageParser.getNumPixels();
        this.superPixels = new ArrayList<>();
    }

    public void run(int numClusters) {
        System.out.println("Run SLIC");
        int n = this.numPixels;
        int m = 10;
        int s = (int) Math.round(Math.sqrt((double) n / numClusters));
        int i = 0;
        System.out.println("\tGenerating superPixels");
        for (int y = s / 2; y < this.imageHeight; y += s) {
            for (int x = s / 2; x < this.imageWidth; x += s) {
                this.superPixels.add(new SuperPixel(this.imageParser, i, y, x));
            }
        }
        System.out.println("\tFinished generating superPixels");

        // Move centroids to lowest gradient
        System.out.println("\tStarting to move centroids to lowest gradient");
        for (SuperPixel superPixel : this.superPixels) {
            superPixel.moveToLowestGradient();
        }
        System.out.println("\tFinished moving centroids to lowest gradient");

        double error;
        int loop = 0;
        int[][] label = new int[this.imageHeight][this.imageWidth];
        double[][] distance = new double[this.imageHeight][this.imageWidth];
        for (int[] row : label) {
            Arrays.fill(row, -1);
        }
        for (double[] row : distance) {
            Arrays.fill(row, Double.MAX_VALUE);
        }

        do {
            loop++;
            System.out.println("\tLoop " + loop + " started!");
            for (int k = 0; k < this.superPixels.size(); k++) {
                SuperPixel sp = this.superPixels.get(k);
                int yMin = Math.max(0, sp.y - 2 * s);
                int yMax = Math.min(this.imageHeight, sp.y + 2 * s);
                int xMin = Math.max(0, sp.x - 2 * s);
                int xMax = Math.min(this.imageWidth, sp.x + 2 * s);

                for (int y = yMin; y < yMax; y++) {
                    for (int x = xMin; x < xMax; x++) {
                        double dc = Math.sqrt(this.imageParser.getArgbDistance(y, x, sp.y, sp.x));
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

        System.out.println("Number of superPixels before enforcing connectivity: " + this.superPixels.size());
        enforceConnectivity();
        System.out.println("Number of superPixels after  enforcing connectivity: " + this.superPixels.size());
        System.out.println("Total number of pixels in imageParser: " + this.numPixels);
        System.out.println("Starting to update cluster centers");
        updateClusterCenters(this.label);
        System.out.println("Completed updating cluster centers");
        System.out.println("Starting to update neighbouring superpixels");
        updateNeighbouringSuperPixels();
        System.out.println("Completed updating neighbouring superpixels");
        System.out.println("Starting to update superpixel colour");
        updateSuperPixelsColor();
        System.out.println("Completed updating superpixel colour");
        System.out.println("Finished with running SLIC");

    }

    private double updateClusterCenters(int[][] label) {
        double error = 0;
        int[][] newClusterCenter = new int[this.superPixels.size()][2];
        for (int[] val : newClusterCenter) {
            val[0] = 0;
            val[1] = 0;
        }
        int[] newClusterPixelCount = new int[this.superPixels.size()];
        for (int y = 0; y < this.imageHeight; y++) {
            for (int x = 0; x < this.imageWidth; x++) {
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
                    (newX - this.superPixels.get(i).x) * (newX - this.superPixels.get(i).x) +
                            (newY - this.superPixels.get(i).y) * (newY - this.superPixels.get(i).y));
            this.superPixels.get(i).resetCenter(newY, newX);

        }
        return error;


    }

    private void enforceConnectivity() {
        boolean[][] connectedPixels = new boolean[this.imageHeight][this.imageWidth];
        for (SuperPixel cluster : this.superPixels) {
            ArrayList<int[]> visited = getConnectedSection(connectedPixels, cluster.y, cluster.x);
            cluster.pixelCount = visited.size();

        }
        for (int y = 0; y < this.imageHeight; y++) {
            for (int x = 0; x < this.imageWidth; x++) {
                if (connectedPixels[y][x]) {
                    continue;
                }
                ArrayList<int[]> visited = getConnectedSection(connectedPixels, y, x);
                SuperPixel newSp = createNewSuperPixel(visited);
                this.superPixels.add(newSp);
            }
        }
    }

    private ArrayList<int[]> getConnectedSection(@NotNull boolean[][] connectedPixels, int y, int x) {
        ArrayList<int[]> visited = new ArrayList<>();
        ArrayList<int[]> queue = new ArrayList<>();
        boolean[][] queued = new boolean[this.imageHeight][this.imageWidth];
        int[] current = {y, x};
        while (true) {
            visited.add(current);
            connectedPixels[current[0]][current[1]] = true;

            int[] startStop = getStartAndStopYX(current[0], current[1]);
            for (int yy = startStop[0]; yy < startStop[1]; yy++) {
                for (int xx = startStop[2]; xx < startStop[3]; xx++) {
                    if (this.label[yy][xx] != this.label[current[0]][current[1]]) {
                        continue;
                    }
                    if (queued[yy][xx] || connectedPixels[yy][xx]) {
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

    private SuperPixel createNewSuperPixel(@NotNull ArrayList<int[]> visited) {
        int centerY = 0;
        int centerX = 0;
        for (int[] pos : visited) {
            this.label[pos[0]][pos[1]] = this.superPixels.size();
            centerY += pos[0];
            centerX += pos[1];
        }
        centerY /= visited.size();
        centerX /= visited.size();
        return new SuperPixel(this.imageParser, this.superPixels.size(), centerY, centerX);
    }

    private void updateNeighbouringSuperPixels() {
        for (SuperPixel sp : superPixels) {
            boolean[][] visited = new boolean[this.imageHeight][this.imageWidth];
            ArrayList<int[]> queue = new ArrayList<>();
            int[] current = {sp.y, sp.x};
            while (true) {
                visited[current[0]][current[1]] = true;
                int[] startStop = getStartAndStopYX(current[0], current[1]);
                for (int yy = startStop[0]; yy < startStop[1]; yy++) {
                    for (int xx = startStop[2]; xx < startStop[3]; xx++) {
                        if (this.label[yy][xx] != sp.id) {
                            sp.addNeighbour(this.label[yy][xx]);
                            continue;
                        }
                        if (visited[yy][xx]) {
                            continue;
                        }
                        visited[yy][xx] = true;
                        queue.add(new int[]{yy, xx});
                    }
                }
                if (queue.size() == 0) {
                    break;
                }
                current = queue.remove(0);
            }
        }
    }

    private void updateSuperPixelsColor() {
        for (SuperPixel sp : this.superPixels) {
            boolean[][] visited = new boolean[this.imageHeight][this.imageWidth];
            ArrayList<int[]> queue = new ArrayList<>();
            int[] current = {sp.y, sp.x};
            while (true) {
                visited[current[0]][current[1]] = true;
                sp.pixelCount++;
                sp.updateColor(current[0], current[1]);
                int[] startStop = getStartAndStopYX(current[0], current[1]);
                for (int yy = startStop[0]; yy < startStop[1]; yy++) {
                    for (int xx = startStop[2]; xx < startStop[3]; xx++) {
                        if (this.label[yy][xx] != sp.id) {
                            continue;
                        }
                        if (visited[yy][xx]) {
                            continue;
                        }
                        visited[yy][xx] = true;
                        queue.add(new int[]{yy, xx});
                    }
                }
                if (queue.size() == 0) {
                    break;
                }
                current = queue.remove(0);
            }
        }
    }

    private int[] getStartAndStopYX(int y, int x) {
        return new int[]{
                Integer.max(0, y - 1),
                Integer.min(this.imageHeight, y + 2),
                Integer.max(0, x - 1),
                Integer.min(this.imageWidth, x + 2)

        };
    }
}
