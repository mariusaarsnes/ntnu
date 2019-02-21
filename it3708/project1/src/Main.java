import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends Application {

    Stage window;
    Scene scene;
    ChoiceBox<String> problemChoiceBox;
    Canvas canvas;
    Problem problem;
    GeneticAlgorithm ga;

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        window.setTitle("Multiple Depot Vehicles Routing Problem");

        BorderPane border = new BorderPane();
        HBox header = generateHeader();
        border.setTop(header);
        Canvas canvas = generateCanvas();
        border.setCenter(canvas);
        window.setScene(new Scene(border, 3000, 2500));

        window.show();
    }

    private HBox generateHeader() {
        /**
         * Helper function to generate the header for configuration of the run
         */
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);

        //Problem choice
        Label problemChoiceLabel = generateHeaderLabel("Selected Problem: ");
        ChoiceBox<String> problemChoiceBox = generateProblemChoice();

        // Population size
        Label popSizeLabel = generateHeaderLabel("Population size: ");
        TextField popSizeText = generateTextField("100", 24);

        // Generation number
        Label genNumLabel = generateHeaderLabel("Generation number: ");
        TextField genNumText = generateTextField("1000", 24);

        // Crossover Rate
        Label crossRateLabel = generateHeaderLabel("Crossover rate: ");
        TextField crossRateText = generateTextField("20", 24);

        Label mutRateLabel = generateHeaderLabel("Mutation rate: ");
        TextField mutRateText = generateTextField("20", 24);

        Label elitRateLabel = generateHeaderLabel("Elitism rate: ");
        TextField elitRateText = generateTextField("10", 24);

        Button runButton = generateButton("Run!", 24);
        runButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //TODO: Run algorithm with given parameters
                popSizeText.getText();
                initCanvas();
                runGA(
                        Integer.parseInt(popSizeText.getText()),
                        Integer.parseInt(genNumText.getText()),
                        Integer.parseInt(crossRateText.getText()),
                        Integer.parseInt(mutRateText.getText()),
                        Integer.parseInt(elitRateText.getText())
                );

            }
        });
        hbox.getChildren().addAll(
                problemChoiceLabel,
                problemChoiceBox,
                popSizeLabel,
                popSizeText,
                genNumLabel,
                genNumText,
                crossRateLabel,
                crossRateText,
                mutRateLabel,
                mutRateText,
                elitRateLabel,
                elitRateText,
                runButton);
        return hbox;
    }


    private void runGA(int popSize, int genNum, int crossRate, int mutRate, int elitRate) {
        this.ga = new GeneticAlgorithm(this.problem, popSize, genNum, crossRate, mutRate, elitRate);
        this.ga.run();
        Genotype bestIndividual = this.ga.getBestIndividual();

        System.out.println("\n" + bestIndividual.distance);
        for (int d = 0; d < bestIndividual.routes.length; d++) {
            for (int r = 0; r < bestIndividual.routes[d].length; r++) {
                if (bestIndividual.routes[d][r].size() > 0) {
                    System.out.println(d + "\t" + r + "\t" +
                            (int) bestIndividual.getDistanceOfRoute(bestIndividual.routes[d][r], this.problem.depots[d].getNr(), this.problem) + "\t" +
                            bestIndividual.getDemandOfRoute(bestIndividual.routes[d][r], problem) + "\t" +
                            this.problem.closestDepotToCustomers[bestIndividual.routes[d][r].get(bestIndividual.routes[d][r].size() - 1)] + "\t" +
                            bestIndividual.routes[d][r]);
                }
            }
        }

        System.out.println("DONE");
        drawPaths(bestIndividual.routes);
    }

    private void drawPaths(ArrayList<Integer>[][] paths) {
        Color[] colors = new Color[]{Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE, Color.ORANGE, Color.PINK, Color.INDIGO, Color.TEAL, Color.SANDYBROWN, Color.OLDLACE};
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        gc.setLineWidth(0.3);

        for (int depot = 0; depot < paths.length; depot++) {
            gc.setStroke(colors[depot]);
            for (int vehicle = 0; vehicle < paths[depot].length; vehicle++) {

                Color pointColor = colors[depot];
                int[] startPos = this.problem.depots[depot].getPosition();

                int c;
                for (c = 0; c < paths[depot][vehicle].size(); c++) {


                    int[] destPos = this.problem.customers[paths[depot][vehicle].get(c)].getPosition();

                    gc.strokeLine(startPos[0], startPos[1], destPos[0], destPos[1]);
                    drawPointOnCanvas(gc, pointColor, destPos[0], destPos[1]);
                    startPos = destPos;
                }
                if (c > 0) {
                    int[] destPos = this.problem.depots[this.problem.closestDepotToCustomers[this.problem.customers[paths[depot][vehicle].get(c - 1)].getNr()]].getPosition();


                    gc.strokeLine(startPos[0], startPos[1], destPos[0], destPos[1]);
                    drawPointOnCanvas(gc, Color.DARKTURQUOISE, destPos[0], destPos[1]);
                }
            }
        }
    }

    private Canvas generateCanvas() {

        Canvas canvas = new Canvas(1600, 1600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.fill();


        gc.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        gc.scale(4.5, -4.5);


        this.canvas = canvas;
        return canvas;
    }

    private void initCanvas() {
        /**
         * Draws all the depots and customers for the selected problem
         */
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(-this.canvas.getHeight(), -this.canvas.getWidth(), 2 * this.canvas.getHeight(), 2 * this.canvas.getWidth());
        for (Depot depot : this.problem.depots
        ) {
            drawPointOnCanvas(gc, depot.color, depot.x, depot.y);

        }
        for (Customer customer : this.problem.customers
        ) {
            drawPointOnCanvas(gc, customer.color, customer.x, customer.y);
        }
    }

    private void drawPointOnCanvas(GraphicsContext gc, Color color, int x, int y) {
        /**
         * Draw a point on the canvas
         * input:
         * - canvas: canvas to be drawn on
         * - color: color of the point
         * - x: x coordinate
         * - y: y coordinate
         *
         */
        gc.setFill(color);
        gc.fillOval(x - 1, y - 1, 2, 2);
    }

    private Label generateHeaderLabel(String text) {
        Label temp = new Label(text);
        temp.setFont(new Font("Arial", 24));
        return temp;
    }

    private TextField generateTextField(String defaultContent, int fontSize) {
        TextField temp = new TextField(defaultContent);
        temp.setFont(new Font("Arial", fontSize));
        return temp;
    }

    private Button generateButton(String defaultContent, int fontSize) {
        Button temp = new Button(defaultContent);
        temp.setFont(new Font("Arial", fontSize));
        return temp;
    }

    private ChoiceBox<String> generateProblemChoice() {
        String[] data_files = null;
        try {
            data_files = getFilesInFolder("data_files/");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (data_files == null) {
                data_files = new String[]{"no problems"};

            }
        }
        ChoiceBox<String> problemChoiceBox = new ChoiceBox<>();
        problemChoiceBox.getItems().addAll(data_files);
        problemChoiceBox.setTooltip(new Tooltip("Select a problem"));
        problemChoiceBox.setStyle("-fx-font: 24 arial;");

        problemChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setProblem(problemChoiceBox.getItems().get((Integer) t1));
                initCanvas();
            }
        });
        return problemChoiceBox;
    }

    private void setProblem(String fileName) {
        this.problem = new Problem(fileName);

    }


    private String[] getFilesInFolder(String folderName) throws IOException {
        /**
         * Returns a sorted list of files for a given folder
         */
        File folder = new File(folderName);
        File[] files = folder.listFiles();
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String fileTemp = files[i].toString();
            fileTemp = fileTemp.substring(fileTemp.lastIndexOf("/") + 1);
            fileNames[i] = fileTemp;
        }
        Arrays.sort(fileNames);
        return fileNames;
    }
}
