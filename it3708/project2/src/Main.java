import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;

import java.awt.*;

import javafx.stage.Stage;

import java.io.File;
import java.util.Random;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        MOEA moea = new MOEA("./147091/Test image.jpg", 100, 2, 0.5, 0.5, 0.1);
        SLIC slic = new SLIC(moea.pixelMatrix);
        slic.run(256);
        drawImage(moea.pixelMatrix, slic);
        //moea.run();
        System.out.println("Done");
        //Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        //primaryStage.setTitle("Hello World");
        //primaryStage.setScene(new Scene(root, 2000, 2000));
        //primaryStage.show();
    }


    private void drawImage(PixelMatrix pixelMatrix, SLIC slic) {
        Random rand = new Random();


        final WritableImage image = new WritableImage(pixelMatrix.getWidth(), pixelMatrix.getHeight());
        final PixelWriter pixelWriter = image.getPixelWriter();
        /*
        Color[] colors = new Color[slic.clusters.size()];
        for (int i = 0; i < colors.length; i++) {
            int r = rand.nextInt(256);
            int g = rand.nextInt(256);
            int b = rand.nextInt(256);
            colors[i] = new Color(r, g, b);
        }
        */
        for (int y = 0; y < pixelMatrix.getHeight(); y++) {
            for (int x = 0; x < pixelMatrix.getWidth(); x++) {
                pixelWriter.setArgb(x, y, pixelMatrix.getPixel(slic.clusters.get(slic.label[y][x]).y, slic.clusters.get(slic.label[y][x]).x).argb);
            }
        }

        File testFile = new File("test.png");

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", testFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
