package SongCreatorWindow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SongCreator extends Application
{
    public static void main(String[] args) {
        launch(args);
    }
    //TODO: Usunąć klasę
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("SongCreator.fxml"));

        Scene scene = new Scene(root, 600, 800);

        stage.setTitle("Song Creator Window");
        stage.setScene(scene);
        stage.show();
    }
}
