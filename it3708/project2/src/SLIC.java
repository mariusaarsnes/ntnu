import java.util.*;

public class SLIC {
    boolean cielab;
    Random random = new Random();
    ImageParser imageParser;
    PixelMatrix pixelMatrix;
    int imageHeight, imageWidth, numPixels, minSPSize;
    ArrayList<SuperPixel> superPixels;
    int[][] label;

    public SLIC(ImageParser imageParser, boolean cielab, int minSPSize) {

        this.imageParser = imageParser;
        this.pixelMatrix = new PixelMatrix(this.imageParser, cielab);
        this.imageHeight = imageParser.getHeight();
        this.imageWidth = imageParser.getWidth();
        this.numPixels = imageParser.getNumPixels();
        this.superPixels = new ArrayList<>();
        this.cielab = cielab;
        this.minSPSize = minSPSize;
    }

    public void run(int numClusters) {
        System.out.println("Run SLIC");
        int n = this.numPixels;
        double m;
        if (cielab) {
            m = 0.01;
        } else {
            m = 10;

        }
        int s = (int) Math.round(Math.sqrt((double) n / numClusters));
        int i = 0;
        ArrayList<SuperPixel> superPixels = new ArrayList<>();

        System.out.println("\tGenerating superPixels");
        for (int y = s / 2; y < this.imageHeight; y += s) {
            for (int x = s / 2; x < this.imageWidth; x += s) {
                superPixels.add(new SuperPixel(this.imageParser, i, y, x, cielab));
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
                        double dc = 0;
                        if (cielab) {
                            dc = this.imageParser.getCIElabDistance(y, x, sp.y, sp.x);
                        } else {
                            dc = this.imageParser.getArgbDistance(y, x, sp.y, sp.x);
                        }
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

        // Add all pixels to corresponding superpixel
        for (int y = 0; y < label.length; y++) {
            for (int x = 0; x < label[y].length; x++) {
                int spId = label[y][x];
                SuperPixel sp = superPixels.get(spId);
                sp.addPixel(this.pixelMatrix.getPixel(y, x));
            }
        }
        System.out.println("Starting to enforce connectivity");
        enforceConnectivity(superPixels, label);
        System.out.println("Completed enforcing connectivity");

        System.out.println("Starting to update superpixel centera and colours");
        for (SuperPixel sp : superPixels) {
            sp.updateCenter();
            sp.updateColor();
        }

        System.out.println("Completed updating superpixel centers and colours");
        updateNeighbours(superPixels, label);
        System.out.println("Starting to update superpixel neighbours");

        this.superPixels = superPixels;
        this.label = label;


        /**
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
         */
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

    private void enforceConnectivity(ArrayList<SuperPixel> superPixels, int[][] label) {
        // Loop through each superpixel
        for (SuperPixel sp :
                superPixels) {
            //Find all connected subsections in the superpixel
            ArrayList<ArrayList<Pixel>> connectedSections = new ArrayList<>();
            boolean[] visited = new boolean[sp.pixels.size()];
            for (int i = 0; i < sp.pixels.size(); i++) {
                if (visited[i]) {
                    continue;
                }
                visited[i] = true;
                ArrayList<Pixel> connectedPixels = new ArrayList<>();
                connectedPixels.add(sp.pixels.get(i));
                PriorityQueue<PixelEdge> edges = new PriorityQueue<>();
                for (PixelEdge edge :
                        sp.pixels.get(i).edges) {
                    if (edge != null && sp.pixels.contains(edge.V)) {
                        edges.add(edge);
                    }
                }
                while (!edges.isEmpty()) {
                    PixelEdge shortestEdge = edges.poll();
                    if (connectedPixels.contains(shortestEdge.V)) {
                        continue;
                    }
                    connectedPixels.add(shortestEdge.V);
                    visited[sp.pixels.indexOf(shortestEdge.V)] = true;
                    for (PixelEdge edge : shortestEdge.V.edges) {
                        if (edge != null && sp.pixels.contains(edge.V)) {
                            edges.add(edge);
                        }
                    }

                    if (connectedPixels.contains(shortestEdge.U)) {
                        continue;
                    }
                    visited[sp.pixels.indexOf(shortestEdge.U)] = true;
                    for (PixelEdge edge : shortestEdge.U.edges) {
                        if (edge != null && sp.pixels.contains(edge.U)) {
                            edges.add(edge);
                        }
                    }

                }
                if (connectedPixels.size() > 0) {
                    connectedSections.add(connectedPixels);
                }
            }
            //Sort the subsections based on length
            connectedSections.sort(Comparator.comparing(ArrayList::size));
            // Go through subsections until there is only one section left
            while (connectedSections.size() > 1) {
                ArrayList<Pixel> shortestSection = connectedSections.get(0);
                // Until there are no pixels left in the subsection, move pixels over to other  neighbouring superpixels
                while (shortestSection.size() > 0) {
                    for (Iterator<Pixel> it = shortestSection.iterator(); it.hasNext(); ) {
                        Pixel pixel = it.next();

                        PixelEdge shortestEdge = null;
                        while (shortestEdge == null) {
                            shortestEdge = pixel.edges[this.random.nextInt(pixel.edges.length)];
                        }
                        double minDistance = Double.MAX_VALUE;
                        for (PixelEdge edge : pixel.edges) {
                            if (edge == null) {
                                continue;
                            }
                            if (edge.distance < minDistance && !sp.pixels.contains(edge.V)) {
                                minDistance = edge.distance;
                                shortestEdge = edge;
                            }
                        }
                        // If we found a neighbouring pixel in another superpixel
                        if (minDistance != Double.MAX_VALUE) {
                            it.remove();
                            sp.removePixel(pixel);
                            SuperPixel newSp = superPixels.get(label[shortestEdge.V.y][shortestEdge.V.x]);
                            newSp.addPixel(pixel);
                            label[pixel.y][pixel.x] = newSp.id;
                        }
                    }
                }
                connectedSections.remove(shortestSection);
            }
        }
    }

    private void updateNeighbours(ArrayList<SuperPixel> superPixels, int[][] label) {
        for (SuperPixel sp : superPixels) {
            for (Pixel pixel : sp.pixels) {
                for (PixelEdge edge : pixel.edges) {
                    if (edge == null) {
                        continue;
                    }
                    if (!neighboursInSameSuperPixel(edge, label)) {
                        sp.addNeighbour(superPixels.get(label[edge.V.y][edge.V.x]));
                    }
                }
            }
        }
    }

    public boolean neighboursInSameSuperPixel(PixelEdge edge, int[][] label) {
        return label[edge.U.y][edge.U.x] == label[edge.V.y][edge.V.x];
    }

    public boolean neighboursInSameSuperPixel(int y, int x) {
        int[] startStop = getStartAndStopYX(y, x);
        for (int yy = startStop[0]; yy < startStop[1]; yy++) {
            for (int xx = startStop[2]; xx < startStop[3]; xx++) {
                if (this.label[yy][xx] != this.label[y][x]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[] getStartAndStopYX(int y, int x) {
        return new int[]{
                Integer.max(0, y - 1),
                Integer.min(this.imageHeight, y + 2),
                Integer.max(0, x - 1),
                Integer.min(this.imageWidth, x + 2)

        };
    }

/**
 * private ArrayList<SuperPixel> enforceConnectivity(int[][] label) {
 * ArrayList<SuperPixel> newSuperPixels = new ArrayList<>();
 * int[][] newLabel = new int[this.imageHeight][this.imageWidth];
 * boolean[][] connectedPixels = new boolean[this.imageHeight][this.imageWidth];
 * for (int y = 0; y < this.imageHeight; y++) {
 * for (int x = 0; x < this.imageWidth; x++) {
 * if (connectedPixels[y][x]) {
 * continue;
 * }
 * ArrayList<int[]> visited = getConnectedSection(connectedPixels, label, y, x);
 * newSuperPixels.add(createNewSuperPixel(visited, newLabel, newSuperPixels.size()));
 * }
 * }
 * this.label = newLabel;
 * return newSuperPixels;
 * }
 * <p>
 * private ArrayList<int[]> getConnectedSection(@NotNull boolean[][] connectedPixels, int[][] label, int y, int x) {
 * ArrayList<int[]> visited = new ArrayList<>();
 * LinkedList<int[]> queue = new LinkedList<>();
 * boolean[][] queued = new boolean[this.imageHeight][this.imageWidth];
 * int id = label[y][x];
 * int[] current = {y, x};
 * queued[current[0]][current[1]] = true;
 * while (true) {
 * visited.add(current);
 * connectedPixels[current[0]][current[1]] = true;
 * <p>
 * int[] startStop = getStartAndStopYX(current[0], current[1]);
 * for (int yy = startStop[0]; yy < startStop[1]; yy++) {
 * for (int xx = startStop[2]; xx < startStop[3]; xx++) {
 * if (queued[yy][xx]) {
 * continue;
 * }
 * if (label[yy][xx] != id) {
 * continue;
 * }
 * queued[yy][xx] = true;
 * queue.add(new int[]{yy, xx});
 * }
 * }
 * if ((current = queue.poll()) == null) {
 * break;
 * }
 * }
 * return visited;
 * }
 * <p>
 * private SuperPixel createNewSuperPixel(@NotNull ArrayList<int[]> visited, int[][] label, int id) {
 * int centerY = 0;
 * int centerX = 0;
 * for (int[] pos : visited) {
 * label[pos[0]][pos[1]] = id;
 * centerY += pos[0];
 * centerX += pos[1];
 * }
 * centerY /= visited.size();
 * centerX /= visited.size();
 * SuperPixel newSp = new SuperPixel(this.imageParser, id, centerY, centerX, visited.size());
 * for (int[] pos :
 * visited) {
 * newSp.updateColor(pos[0], pos[1]);
 * }
 * return newSp;
 * }
 * <p>
 * private void updateNeighbouringSuperPixels(int[][] label, @NotNull ArrayList<SuperPixel> superPixels) {
 * boolean[] visited = new boolean[superPixels.size()];
 * LinkedList<int[]> queue = new LinkedList<>();
 * <p>
 * for (int y = 0; y < this.imageHeight; y++) {
 * for (int x = 0; x < this.imageWidth; x++) {
 * <p>
 * int id = label[y][x];
 * if (visited[id]) {
 * continue;
 * }
 * int[] current = {y, x};
 * boolean[][] queued = new boolean[this.imageHeight][this.imageWidth];
 * queued[y][x] = true;
 * <p>
 * SuperPixel sp = superPixels.get(id);
 * while (true) {
 * int[] startStop = getStartAndStopYX(current[0], current[1]);
 * for (int yy = startStop[0]; yy < startStop[1]; yy++) {
 * for (int xx = startStop[2]; xx < startStop[3]; xx++) {
 * if (queued[yy][xx]) {
 * continue;
 * }
 * if (label[yy][xx] != id) {
 * if (sp.addNeighbour(label[yy][xx])) {
 * double distance = 0;
 * if (cielab) {
 * distance = getCielabDistance(sp, superPixels.get(label[yy][xx]));
 * } else {
 * distance = getArgbDistance(sp, superPixels.get(label[yy][xx]));
 * }
 * sp.addEdge(superPixels.get(label[yy][xx]), distance);
 * }
 * continue;
 * }
 * queued[yy][xx] = true;
 * queue.add(new int[]{yy, xx});
 * }
 * }
 * if ((current = queue.poll()) == null) {
 * visited[id] = true;
 * break;
 * }
 * }
 * }
 * }
 * }
 * <p>
 * private void updateSuperPixelsColor(int[][] label, ArrayList<SuperPixel> superPixels) {
 * boolean[] visited = new boolean[superPixels.size()];
 * LinkedList<int[]> queue = new LinkedList<>();
 * <p>
 * for (int y = 0; y < this.imageHeight; y++) {
 * for (int x = 0; x < this.imageWidth; x++) {
 * int id = label[y][x];
 * if (visited[id]) {
 * continue;
 * }
 * int[] current = {y, x};
 * boolean[][] queued = new boolean[this.imageHeight][this.imageWidth];
 * queued[y][x] = true;
 * <p>
 * SuperPixel sp = superPixels.get(id);
 * while (true) {
 * sp.pixelCount++;
 * sp.updateColor(current[0], current[1]);
 * int[] startStop = getStartAndStopYX(current[0], current[1]);
 * for (int yy = startStop[0]; yy < startStop[1]; yy++) {
 * for (int xx = startStop[2]; xx < startStop[3]; xx++) {
 * if (queued[yy][xx]) {
 * continue;
 * }
 * if (label[yy][xx] != id) {
 * continue;
 * }
 * queued[yy][xx] = true;
 * queue.add(new int[]{yy, xx});
 * }
 * }
 * if ((current = queue.poll()) == null) {
 * visited[id] = true;
 * break;
 * }
 * }
 * }
 * }
 * }
 * <p>
 * public int[] getStartAndStopYX(int y, int x) {
 * return new int[]{
 * Integer.max(0, y - 1),
 * Integer.min(this.imageHeight, y + 2),
 * Integer.max(0, x - 1),
 * Integer.min(this.imageWidth, x + 2)
 * <p>
 * };
 * }
 * <p>
 * private double getArgbDistance(SuperPixel p1, SuperPixel p2) {
 * Color c1 = p1.getColor(), c2 = p2.getColor();
 * int differenceAlpha = c1.getAlpha() - c2.getAlpha();
 * int differenceRed = c1.getRed() - c2.getRed();
 * int differenceGreen = c1.getGreen() - c2.getGreen();
 * int differenceBlue = c1.getBlue() - c2.getBlue();
 * <p>
 * return Math.sqrt(differenceAlpha * differenceAlpha + differenceRed * differenceRed + differenceGreen * differenceGreen + differenceBlue * differenceBlue);
 * }
 * <p>
 * private double getCielabDistance(SuperPixel p1, SuperPixel p2) {
 * float[] cielab1 = p1.getCielab();
 * float[] cielab2 = p2.getCielab();
 * <p>
 * double dist = 0;
 * for (int i = 0; i < cielab1.length; i++) {
 * dist += Math.pow(cielab1[i] - cielab2[i], 2);
 * }
 * return Math.sqrt(dist);
 * }
 * <p>
 * public boolean neighboursInSameSuperPixel(int y, int x) {
 * int[] startStop = getStartAndStopYX(y, x);
 * for (int yy = startStop[0]; yy < startStop[1]; yy++) {
 * for (int xx = startStop[2]; xx < startStop[3]; xx++) {
 * if (this.label[yy][xx] != this.label[y][x]) {
 * return false;
 * }
 * }
 * }
 * return true;
 * }
 * <p>
 * public SuperPixel closestNeighbour(SuperPixel sp) {
 * return Collections.min(sp.edges).V;
 * }
 * <p>
 * public void joinLessThanK(int[][] label, ArrayList<SuperPixel> superPixels, int k) {
 * for (int y = 0; y < this.imageHeight; y++) {
 * for (int x = 0; x < this.imageWidth; x++) {
 * int currentId = label[y][x];
 * SuperPixel smallSp = superPixels.get(currentId);
 * while (smallSp.pixelCount > 0) {
 * <p>
 * }
 * }
 * }
 * }
 */
}
