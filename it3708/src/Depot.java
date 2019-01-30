import javafx.scene.paint.Color;

public class Depot {
    int depotNr;
    int maxDuration;
    int maxLoad;
    int xPos;
    int yPos;
    Color color;

    public Depot(int d, int q) {
        if (d == 0){
            d = Integer.MAX_VALUE;
        }
        if (q == 0){
            q = Integer.MAX_VALUE;
        }
        this.maxDuration = d;
        this.maxLoad = q;
        this.color = Color.DARKTURQUOISE;
    }
    public void setDepotNr(int nr) {
        this.depotNr = nr;
    }
    public void setPos(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }
}
