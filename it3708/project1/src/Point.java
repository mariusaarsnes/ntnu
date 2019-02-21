import javafx.scene.paint.Color;

public class Point {


    int nr;
    int x;
    int y;
    Color color;

    public Point(Color color) {
        this.color = color;
    }

    public Point(int nr, int x, int y, Color color) {
        this.nr = nr;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getNr() {
        return this.nr;
    }

    public int[] getPosition() {
        return new int[]{x, y};
    }

    public Color getColor() {
        return this.color;
    }

    public double getDistanceTo(Point destPoint) {
        int[] destPos = destPoint.getPosition();
        return Math.hypot(this.x - destPos[0], this.y - destPos[1]);
    }

    public double getDistanceFrom(Point startPoint) {
        int[] startPos = startPoint.getPosition();
        return Math.hypot(startPos[0] - this.x, startPos[1] - this.y);
    }

}
