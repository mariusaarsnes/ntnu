import javafx.scene.paint.Color;

public class Depot extends Point {


    int maxDuration;
    int maxLoad;

    public Depot(int maxDuration, int maxLoad) {
        super(Color.DARKTURQUOISE);
        this.maxDuration = maxDuration;
        this.maxLoad = maxLoad;
        if (this.maxDuration == 0){
            this.maxDuration = Integer.MAX_VALUE;
        }
        if (this.maxLoad == 0){
            this.maxLoad = Integer.MAX_VALUE;
        }
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
