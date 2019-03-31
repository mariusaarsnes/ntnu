import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUI extends BorderPane {

    private Stage stage;
    private final Color[] colors = new Color[]{
            Color.RED, Color.BLUE, Color.GREEN,
            Color.YELLOW, Color.PURPLE, Color.ORANGE,
            Color.DARKBLUE, Color.PINK, Color.LIGHTGRAY,
            Color.DARKCYAN};

    public GUI(Stage stage){
        super();
        this.stage = stage;

        Scene scene = new Scene(this,2040,1400);
        stage.setScene(scene);
        stage.setTitle("JSSP - Job Shop Scheduling Problem");
        stage.show();
    }

    public void drawGantt(Solution solution, int optimalMakespan) {
        int makespan = solution.getMakespan();
        int[][][] schedule = solution.getSchedule();

        int width = 2000;
        double widthTranslate = (double) width / makespan;
        int height = schedule.length * 50;

        Pane pane = new Pane();

        pane.setMaxSize(width,height);
        pane.setMinSize(width,height);

        int xLines = 10;
        int xInterval = makespan/xLines;
        for(int i = 0; i< xLines; i++) {
            double x = widthTranslate * xInterval * i;
            Line line = new Line(x,0,x,height+5);
            Text text = new Text(String.valueOf(xInterval*i));
            text.setTranslateY(height + 15);
            text.setTranslateX(x+15);
            pane.getChildren().addAll(line,text);
        }
        Line lineLast = new Line(width, 0, width, height+5);
        Text textLast = new Text(String.valueOf(makespan));
        textLast.setTranslateY(height+15);
        textLast.setTranslateX(width+15);
        pane.getChildren().addAll(lineLast,textLast);

        for (int i = 0; i < schedule.length; i++) {
            int y = i*50;
            for(int j = 0; j < schedule[i].length; j++) {
                String name = String.valueOf(j);
                Task task = new Task(name, schedule[i][j][1] * widthTranslate, colors[j % colors.length]);
                task.setTranslateX(schedule[i][j][0] * widthTranslate);
                task.setTranslateY(y);
                pane.getChildren().add(task);
            }
        }

        setCenter(pane);

        pane.setStyle("fx-border-color: gray");
        BorderPane.setAlignment(pane, Pos.CENTER);

        String rating = String.format("%.2f%%", (double) optimalMakespan / solution.getMakespan() * 100);
        final Text text = new Text("Current makespan: " + solution.getMakespan() + ", optimal: " + optimalMakespan + ", rating: " + rating);
        setBottom(text);
        BorderPane.setAlignment(text,Pos.CENTER);

    }

    private class Task extends StackPane {
        private Task (String text, double width, Color color) {
            super();
            final Rectangle rectangle = new Rectangle(width,50);
            rectangle.setFill(color);
            getChildren().addAll(rectangle, new Text(text));
        }
    }
}
