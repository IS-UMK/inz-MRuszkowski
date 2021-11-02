package SongCreatorWindow.Controllers;

import Model.Path;
import com.sun.javafx.geom.Rectangle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Screen;

import java.util.LinkedList;
import java.util.List;

public class MainController
{
    @FXML
    BorderPane workSpace;
    @FXML
    ScrollPane scrollPaneWithPaths;
    @FXML
    AnchorPane anchorPaneWithPaths;

    double Height = workSpace != null ? workSpace.getHeight() / 3 : 200;
    double Width = Screen.getPrimary().getBounds().getWidth();
    List<Canvas> canvasList = new LinkedList<Canvas>();

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
        Canvas canvas = new Canvas(Width, Height);

        //paint new path on new canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //border of path
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(0,0, Width, Height);

        //Display Path Name
        gc.strokeRect(0,0, Height, Height);
        Font font = new Font(Font.getFamilies().toArray()[1].toString(), 24);
        gc.setFont(font);
        //Text should be placed basing on the middle of square, minus half of the final text size on canvas (font size)
        gc.fillText(pathName, Height /2 - pathName.length()/2.0*24*3/4, Height /2, Height);

        //Display selected instrument info
        gc.strokeRect(Height, 0, Height, Height);
        String selected_instrument_note = "Selected Instrument:";
        gc.fillText(
                selected_instrument_note,
                1.4*Height + Height/2 - selected_instrument_note.length()/2.0*24*3/4,
                Height - selected_instrument_note.length()/2.0*24*3/4,
                Height
        );

        Image instrumentImage = new Image(MainController.class.getResource("/Images/piano.png").toString(), Height * .7, Height * .7, false, false);
        gc.drawImage(instrumentImage, Height * 1.15, Height / 10);
        gc.fillText(
            path.getInstrument(),
            1.25*Height + Height/2 - path.getInstrument().length()/2.0*36,
            Height - Height/10,
            Height
        );

        //display speaker and create volume slider
        gc.strokeRect(2 * Height, 0, Height, Height);
        //TODO: Coś nie chce ładować grafiki wektorowej
        Image speakerImage = new Image(MainController.class.getResource("/Images/speaker.png").toString(), 100, 100, false, false);
        gc.drawImage(speakerImage, Height * 2.25, Height / 5);

        Slider volumeSlider = new Slider(0, 100 ,0.5);
        volumeSlider.setMaxWidth(Height * .9);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setLayoutX(Height * 2.15);
        volumeSlider.setLayoutY(Height * .75);

        //five lines for inserting notes
        gc.setLineWidth(2);
        for(int i = 1; i <= 5; i++)
            gc.strokeLine(3* Height + 20,40 + i*20, canvas.getWidth() - 20, 40 + i*20);

        //add (default Violin) music key
        Image violinKeyImage = new Image(MainController.class.getResource("/Images/violin_key.png").toString(), 100, 170.62, false, false);
        gc.drawImage(violinKeyImage, Height * 3, Height / 13);

        //add created canvas and volume slider
        anchorPaneWithPaths.getChildren().add(canvas);
        anchorPaneWithPaths.getChildren().add(volumeSlider);
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
