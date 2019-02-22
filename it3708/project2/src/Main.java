import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        MOEA moea = new MOEA("./86016/Test image.jpg", 100, 2, 0.5, 0.5, 0.1);
        SLIC slic = new SLIC(moea.pixelMatrix);
        slic.run();
        //moea.run();
        System.out.println("Done");
        //Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        //primaryStage.setTitle("Hello World");
        //primaryStage.setScene(new Scene(root, 2000, 2000));
        //primaryStage.show();
    }
}
