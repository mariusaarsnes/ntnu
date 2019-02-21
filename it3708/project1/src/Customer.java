import javafx.scene.paint.Color;

public class Customer extends Point {

    int serviceDuration;
    int demand;

    public Customer(int i, int x, int y, int serviceDuration, int demand) {
        super(i, x, y, Color.BLACK);
        this.serviceDuration = serviceDuration;
        this.demand = demand;
    }
}
