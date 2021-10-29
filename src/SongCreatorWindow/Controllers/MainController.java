package SongCreatorWindow.Controllers;

import Model.Path;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MainController
{
    @FXML
    ScrollPane scrollPaneWithPaths;
    @FXML
    AnchorPane anchorPaneWithPaths;

    //region Project
    public void SaveProjectToFile(ActionEvent actionEvent)
    {

    }

    public void SaveProjectToFileWithDifferentName(ActionEvent actionEvent)
    {

    }

    public void LoadProjectFromFile(ActionEvent actionEvent)
    {

    }

    public void ExportProjectToMIDIFile(ActionEvent actionEvent)
    {

    }

    public void ImportProjectFromMIDIFile(ActionEvent actionEvent)
    {

    }
    //endregion

    //region Paths
    public void InsertNewPath(ActionEvent actionEvent)
    {
        //Ask user for properties of new path
        TextInputDialog window = new TextInputDialog("Path name");
        window.setHeaderText("Enter name of new path:");
        window.showAndWait();

        //get user response
        String pathName = window.getResult();
        System.out.println(String.format("User inserted new path of name - %s", pathName));

        //Create new path according to user typed values
        var path = Path.CreatePath(pathName, "PIANO");

        //create canvas and set its size
        Canvas canvas = new Canvas(scrollPaneWithPaths.getWidth(), 200);
        double height = canvas.getHeight(); //TODO: Zamienić na globalną stałą
        double width = canvas.getWidth();    //TODO: Zamienić na globalną stałą
        anchorPaneWithPaths.setLayoutX(width);
        anchorPaneWithPaths.setLayoutY(height); //TODO: zmienić na wartość stałej globalnej razy ilość canvasów

        //paint new path on new canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //border of path
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(0,0, width, height);

        //Display Path Name
        gc.strokeRect(0,0, height, height);
        gc.setFont(new Font(Font.getFamilies().toArray()[1].toString(), 24));
        //Text should be placed basing on the middle of square, minus half of the final text size on canvas (font size)
        gc.fillText(pathName, height/2 - pathName.length()/2.0*24*3/4, height/2, height);

        //Display selected instrument info
        gc.strokeRect(height, 0, height, height);
        String selected_instrument_note = "Selected Instrument:";
        gc.fillText(
                selected_instrument_note,
                1.4*height + height/2 - selected_instrument_note.length()/2.0*24*3/4,
                height - selected_instrument_note.length()/2.0*24*3/4,
                height
        );
        gc.fillText(
            path.getInstrument(),
            1.4*height + height/2 - path.getInstrument().length()/2.0*36,
            height/2 - path.getInstrument().length()/2.0*24*3/4,
            height
        );

        //display speaker with volume slider
        gc.strokeRect(2 * height, 0, height, height);

        //five lines for inserting notes
        gc.setLineWidth(2);
        for(int i = 1; i <= 5; i++)
            gc.strokeLine(3*height + 20,40 + i*20, canvas.getWidth() - 20, 40 + i*20);

        //add created canvas
        anchorPaneWithPaths.getChildren().add(canvas);
    }

    public void RenameSelected(ActionEvent actionEvent)
    {

    }

    public void DeleteSelected(ActionEvent actionEvent)
    {

    }

    public void RecordPath(ActionEvent actionEvent)
    {

    }
    //endregion

    //region Notes
    public void InsertNote(ActionEvent actionEvent)
    {

    }

    public void EditNote(ActionEvent actionEvent)
    {

    }

    public void DeleteNote(ActionEvent actionEvent)
    {

    }
    //endregion

    //region Song
    public void PlayAllPathsWithoutMutedOnes(ActionEvent actionEvent)
    {

    }

    public void PrintSongToPDFFile(ActionEvent actionEvent)
    {

    }
    //endregion
}
