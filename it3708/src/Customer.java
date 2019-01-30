import javafx.scene.paint.Color;

public class Customer {


    int customerNr;
    int xPos;
    int yPos;
    int serviceDuration;
    int demand;
    Color color;

    public Customer(int i, int x, int y, int d, int q) {
        this.customerNr = i;
        this.xPos = x;
        this.yPos = y;
        this.serviceDuration = d;
        this.demand = q;
        this.color = Color.BLACK;
    }
}
