import java.util.*;

public class SLIC {
    boolean cielab;
    Random random = new Random();
    ImageParser imageParser;
    PixelMatrix pixelMatrix;
    int imageHeight, imageWidth, numPixels;
    ArrayList<SuperPixel> superPixels;
    int[][] label;

    public SLIC(ImageParser imageParser, boolean cielab) {

        this.imageParser = imageParser;
        this.pixelMatrix = new PixelMatrix(this.imageParser, cielab);
        this.imageHeight = imageParser.getHeight();
        this.imageWidth = imageParser.getWidth();
        this.numPixels = imageParser.getNumPixels();
        this.superPixels = new ArrayList<>();
        this.cielab = cielab;
    }

    public void run(int numClusters) {
        System.out.println("Run SLIC");
        int n = this.numPixels;
        double m;
        if (this.cielab) {
            m = 0.0005;
        } else {
            m = 1;

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
}
