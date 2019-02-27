import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Random;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        MOEA moea = new MOEA("./Segmentering/kuseknuser.png", 100, 2, 0.5, 0.5, 0.1);
        drawImage(moea.slic);
        //moea.run();
        System.out.println("Done");
        //Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        //primaryStage.setTitle("Hello World");
        //primaryStage.setScene(new Scene(root, 2000, 2000));
        //primaryStage.show();
    }


    private void drawImage(SLIC slic) {
        Random rand = new Random();


        final WritableImage image = new WritableImage(slic.imageWidth, slic.imageHeight);
        final PixelWriter pixelWriter = image.getPixelWriter();

        for (int y = 0; y < slic.imageHeight; y++) {
            for (int x = 0; x < slic.imageWidth; x++) {
                pixelWriter.setArgb(x, y, slic.superPixels.get(slic.label[y][x]).getColor().getRGB());
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
