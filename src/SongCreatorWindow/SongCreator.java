package SongCreatorWindow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class created only for development purposes
 */
public class SongCreator extends Application
{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("SongCreator.fxml"));

        Scene scene = new Scene(root, 1280, 800);

        stage.setTitle("Song Creator Window");
        stage.setScene(scene);
        stage.show();
    }
}
