import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

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
        ArrayList<SuperPixel> superPixels = new ArrayList<>();

        System.out.println("\tGenerating superPixels");
        for (int y = s / 2; y < this.imageHeight; y += s) {
            for (int x = s / 2; x < this.imageWidth; x += s) {
                superPixels.add(new SuperPixel(this.imageParser, i, y, x));
                i++;
            }
        }
        System.out.println("\tFinished generating superPixels");

        // Move centroids to lowest gradient
        System.out.println("\tStarting to move centroids to lowest gradient");
        for (SuperPixel superPixel : superPixels) {
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
            for (int k = 0; k < superPixels.size(); k++) {
                SuperPixel sp = superPixels.get(k);
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
            error = updateClusterCenters(label, superPixels);
            System.out.println("\tLoop " + loop + " done! Residual error: " + error);

        } while (error != 0);

        System.out.println("Number of superPixels before enforcing connectivity: " + superPixels.size());
        superPixels = enforceConnectivity(label);
        System.out.println("Number of superPixels after  enforcing connectivity: " + superPixels.size());
        System.out.println("Total number of pixels in imageParser: " + this.numPixels);

        System.out.println("Starting to update cluster centers");
        updateClusterCenters(this.label, superPixels);
        System.out.println("Completed updating cluster centers");

        System.out.println("Starting to update superpixel colour");
        updateSuperPixelsColor(this.label, superPixels);
        System.out.println("Completed updating superpixel colour");

        System.out.println("Starting to update neighbouring superpixels");
        updateNeighbouringSuperPixels(this.label, superPixels);
        System.out.println("Completed updating neighbouring superpixels");


        System.out.println("Finished with running SLIC");
        this.superPixels = superPixels;

    }

    private double updateClusterCenters(int[][] label, ArrayList<SuperPixel> superPixels) {
        double error = 0;
        int[][] newClusterCenter = new int[superPixels.size()][2];
        for (int[] val : newClusterCenter) {
            val[0] = 0;
            val[1] = 0;
        }
        int[] newClusterPixelCount = new int[superPixels.size()];
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
                    (newX - superPixels.get(i).x) * (newX - superPixels.get(i).x) +
                            (newY - superPixels.get(i).y) * (newY - superPixels.get(i).y));
            superPixels.get(i).resetCenter(newY, newX);


        }
        return error;


    }

    private ArrayList<SuperPixel> enforceConnectivity(int[][] label) {
        ArrayList<SuperPixel> newSuperPixels = new ArrayList<>();
        int[][] newLabel = new int[this.imageHeight][this.imageWidth];
        boolean[][] connectedPixels = new boolean[this.imageHeight][this.imageWidth];
        for (int y = 0; y < this.imageHeight; y++) {
            for (int x = 0; x < this.imageWidth; x++) {
                if (connectedPixels[y][x]) {
                    continue;
                }
                ArrayList<int[]> visited = getConnectedSection(connectedPixels, label, y, x);
                newSuperPixels.add(createNewSuperPixel(visited, newLabel, newSuperPixels.size()));
            }
        }
        this.label = newLabel;
        return newSuperPixels;
    }

    private ArrayList<int[]> getConnectedSection(@NotNull boolean[][] connectedPixels, int[][] label, int y, int x) {
        ArrayList<int[]> visited = new ArrayList<>();
        LinkedList<int[]> queue = new LinkedList<>();
        boolean[][] queued = new boolean[this.imageHeight][this.imageWidth];
        int id = label[y][x];
        int[] current = {y, x};
        queued[current[0]][current[1]] = true;
        while (true) {
            visited.add(current);
            connectedPixels[current[0]][current[1]] = true;

            int[] startStop = getStartAndStopYX(current[0], current[1]);
            for (int yy = startStop[0]; yy < startStop[1]; yy++) {
                for (int xx = startStop[2]; xx < startStop[3]; xx++) {
                    if (queued[yy][xx]) {
                        continue;
                    }
                    if (label[yy][xx] != id) {
                        continue;
                    }
                    queued[yy][xx] = true;
                    queue.add(new int[]{yy, xx});
                }
            }
            if ((current = queue.poll()) == null) {
                break;
            }
        }
        return visited;
    }

    private SuperPixel createNewSuperPixel(@NotNull ArrayList<int[]> visited, int[][] label, int id) {
        int centerY = 0;
        int centerX = 0;
        for (int[] pos : visited) {
            label[pos[0]][pos[1]] = id;
            centerY += pos[0];
            centerX += pos[1];
        }
        centerY /= visited.size();
        centerX /= visited.size();
        SuperPixel newSp = new SuperPixel(this.imageParser, id, centerY, centerX, visited.size());
        for (int[] pos :
                visited) {
            newSp.updateColor(pos[0], pos[1]);
        }
        return newSp;
    }

    private void updateNeighbouringSuperPixels(int[][] label, @NotNull ArrayList<SuperPixel> superPixels) {
        boolean[] visited = new boolean[superPixels.size()];
        LinkedList<int[]> queue = new LinkedList<>();

        for (int y = 0; y < this.imageHeight; y++) {
            for (int x = 0; x < this.imageWidth; x++) {

                int id = label[y][x];
                if (visited[id]) {
                    continue;
                }
                int[] current = {y, x};
                boolean[][] queued = new boolean[this.imageHeight][this.imageWidth];
                queued[y][x] = true;

                SuperPixel sp = superPixels.get(id);
                while (true) {
                    int[] startStop = getStartAndStopYX(current[0], current[1]);
                    for (int yy = startStop[0]; yy < startStop[1]; yy++) {
                        for (int xx = startStop[2]; xx < startStop[3]; xx++) {
                            if (queued[yy][xx]) {
                                continue;
                            }
                            if (label[yy][xx] != id) {
                                if (superPixels.get(id).addNeighbour(label[yy][xx])) {
                                    double distance = getArgbDistance(sp, superPixels.get(label[yy][xx]));
                                    sp.addEdge(superPixels.get(label[yy][xx]), distance);
                                }
                                continue;
                            }
                            queued[yy][xx] = true;
                            queue.add(new int[]{yy, xx});
                        }
                    }
                    if ((current = queue.poll()) == null) {
                        visited[id] = true;
                        break;
                    }
                }
            }
        }
    }

    private void updateSuperPixelsColor(int[][] label, ArrayList<SuperPixel> superPixels) {
        boolean[] visited = new boolean[superPixels.size()];
        LinkedList<int[]> queue = new LinkedList<>();

        for (int y = 0; y < this.imageHeight; y++) {
            for (int x = 0; x < this.imageWidth; x++) {
                int id = label[y][x];
                if (visited[id]) {
                    continue;
                }
                int[] current = {y, x};
                boolean[][] queued = new boolean[this.imageHeight][this.imageWidth];
                queued[y][x] = true;

                SuperPixel sp = superPixels.get(id);
                while (true) {
                    sp.pixelCount++;
                    sp.updateColor(current[0], current[1]);
                    int[] startStop = getStartAndStopYX(current[0], current[1]);
                    for (int yy = startStop[0]; yy < startStop[1]; yy++) {
                        for (int xx = startStop[2]; xx < startStop[3]; xx++) {
                            if (queued[yy][xx]) {
                                continue;
                            }
                            if (label[yy][xx] != id) {
                                continue;
                            }
                            queued[yy][xx] = true;
                            queue.add(new int[]{yy, xx});
                        }
                    }
                    if ((current = queue.poll()) == null) {
                        visited[id] = true;
                        break;
                    }
                }
            }
        }
        /*
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
                        if (visited[yy][xx]) {
                            continue;
                        }
                        if (this.label[yy][xx] != sp.id) {
                            continue;
                        }
                        queue.add(new int[]{yy, xx});
                    }
                }
                if (queue.size() == 0) {
                    break;
                }
                current = queue.remove(queue.size() - 1);
            }
        }
        */
    }

    private int[] getStartAndStopYX(int y, int x) {
        return new int[]{
                Integer.max(0, y - 1),
                Integer.min(this.imageHeight, y + 2),
                Integer.max(0, x - 1),
                Integer.min(this.imageWidth, x + 2)

        };
    }


    private double getArgbDistance(SuperPixel p1, SuperPixel p2) {
        Color c1 = p1.getColor(), c2 = p2.getColor();
        int differenceAlpha = c1.getAlpha() - c2.getAlpha();
        int differenceRed = c1.getRed() - c2.getRed();
        int differenceGreen = c1.getGreen() - c2.getGreen();
        int differenceBlue = c1.getBlue() - c2.getBlue();

        return Math.sqrt(differenceAlpha * differenceAlpha + differenceRed * differenceRed + differenceGreen * differenceGreen + differenceBlue * differenceBlue);
    }
}
