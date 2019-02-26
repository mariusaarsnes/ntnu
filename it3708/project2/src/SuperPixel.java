import java.awt.*;
import java.util.HashMap;

class SuperPixel {

    ImageParser imageParser;
    int id, x, y;
    HashMap<SuperPixel, Double> neighbours;

    SuperPixel(ImageParser imageParser, int id, int y, int x) {
        this.imageParser = imageParser;
        this.id = id;
        this.y = y;
        this.x = x;
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

    Color getColor() {
        return this.imageParser.getPixelColor(this.y, this.x);
    }

    int getArgb() {
        return this.imageParser.getPixelArgb(this.y, this.x);
    }

    public void addNeighbour() {

    }
}
