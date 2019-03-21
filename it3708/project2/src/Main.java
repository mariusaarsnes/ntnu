import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        deleteExistingSegmentations();
        MOEA moea = new MOEA("./Test Image/test image_1.jpg",
                100, 50,
                0.3, 0.5, 0.1,
                0.0, 0.6, 0.0,
                3, 50, 5, 500, 0.01, true, true);
        moea.run();
        drawImage(moea.slic, moea);
        System.out.println("Done");
        Plotter p = new Plotter();

        p.plotFront(moea.getParetoFront());
        primaryStage.close();
    }

    private void deleteExistingSegmentations() {
        try {
            Files.walk(Paths.get("Segmentation Evaluation/Student_Segmentation_Files"))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void drawImage(SLIC slic, MOEA moea) {

        final WritableImage image = new WritableImage(slic.imageWidth, slic.imageHeight);
        final PixelWriter pixelWriter = image.getPixelWriter();

        for (SuperPixel sp : slic.superPixels) {
            for (Pixel pixel : sp.pixels) {
                pixelWriter.setArgb(pixel.x, pixel.y, sp.getColor().getRGB());
            }

        }
        /*
        for (int y = 0; y < slic.imageHeight; y++) {
            for (int x = 0; x < slic.imageWidth; x++) {
                pixelWriter.setArgb(x, y, slic.superPixels.get(slic.label[y][x]).getColor().getRGB());
            }
        }
        */
        File testFile = new File("test.png");

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", testFile);
        } catch (Exception e) {
            e.printStackTrace();
        }


        final WritableImage image2 = new WritableImage(slic.imageWidth, slic.imageHeight);
        final PixelWriter pixelWriter2 = image2.getPixelWriter();

        for (int y = 0; y < slic.imageHeight; y++) {
            for (int x = 0; x < slic.imageWidth; x++) {
                try {
                    pixelWriter2.setArgb(x, y, moea.population[moea.population.length - 1].visitedPixels.get(slic.superPixels.get(slic.label[y][x])).getArgb());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        File testFile2 = new File("test2.png");

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image2, null), "png", testFile2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
