package SongCreatorWindow.Model.Stages;

import SongCreatorWindow.Model.Core.Duration;
import SongCreatorWindow.Model.Core.IPlayable;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public class NoteInsertionStage
{
    private IPlayable data;

    void showStage() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox();
        Scene scene = new Scene(root);

        Label[] labels = new Label[]{
            new Label("Type"),
            new Label("Note Duration"),
            new Label("Insertion time"),
            new Label("Instrument")
        };

        ToggleGroup group = new ToggleGroup();

        RadioButton symbolNote = new RadioButton("Single Note");
        RadioButton symbolAccord = new RadioButton("Accord");

        ChoiceBox choiceBox = new ChoiceBox(FXCollections.observableArrayList(Duration.class.getFields()));

        TextField tf = new TextField();
        Button submit = new Button("Submit");

        submit.setOnAction(e -> {
            //data = tf.getText();
            stage.close();
        });

        root.getChildren().addAll(tf, submit);
        stage.setScene(scene);
        stage.showAndWait();
    }

    IPlayable getData() {
        return data;
    }
}
