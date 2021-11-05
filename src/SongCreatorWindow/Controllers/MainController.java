package SongCreatorWindow.Controllers;

import Images.ImageManager;
import Model.Instrument;
import Model.Path;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

import java.lang.reflect.Field;
import java.util.HashMap;
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

    //Constants
    double Height = workSpace != null ? workSpace.getHeight() / 3 : 200;
    double Width = Screen.getPrimary().getBounds().getWidth();

    //Data structures for GUI Components
    List<Canvas> canvasList = new LinkedList<Canvas>();
    HashMap<Canvas, Slider> sliderMap = new HashMap<Canvas, Slider>();

    //Data structures for program logic (Model)
    List<Path> musicPaths = new LinkedList<Path>();

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
        var path = Path.CreatePath(pathName, (byte)musicPaths.size(), "PIANO");
        System.out.println("Created Path:" + path.toString());

        //create canvas and set its size
        Canvas canvas = new Canvas(Width, Height);
        canvas.setLayoutY(Height * canvasList.size());

        //paint new path on new canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //border of path
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(0,0, Width, Height);

        //set up font to use
        Font font = new Font(Font.getFamilies().toArray()[1].toString(), 24);
        var savedFont = gc.getFont();

        //Display Path Name
        gc.strokeRect(0,0, Height, Height);
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

        Image instrumentImage = ImageManager.getInstance().setDimensions(Height * .7, Height * .7).getPiano();
        gc.drawImage(instrumentImage, Height * 1.15, Height / 10);

        //Instrument Selection
        var instruments = Instrument.getAllInstruments();

        //Observable list where I can choose Instrument - create list with given instrument list
        ChoiceBox choiceBox = new ChoiceBox(FXCollections.observableArrayList(instruments));

        //set layout of list created above
        choiceBox.setLayoutX(1.1 * Height);
        choiceBox.setLayoutY(Height * canvasList.size() + Height - Height/5);
        choiceBox.setMinWidth(Height * .8);
        choiceBox.setMaxWidth(Height * .8);
        choiceBox.setValue(instruments[0]);

        //display speaker, tempo selection and create volume slider
        gc.strokeRect(2 * Height, 0, Height, Height);
        //TODO: Coś nie chce ładować grafiki wektorowej
        Image speakerImage = ImageManager.getInstance().setDimensions(100, 100).getSpeaker();
        gc.drawImage(speakerImage, Height * 2.25, Height / 5);

        //tempo selection
        gc.setFont(savedFont);
        gc.fillText(
                "Tempo",
                Height * 2.25,
                Height / 5.5,
                Height
        );
        /*var tempoLabel = new Label("Tempo");
        tempoLabel.setMinWidth(Height / 2);
        tempoLabel.setLayoutX(Height * 2.25);
        tempoLabel.setLayoutY(Height * canvasList.size() + Height / 8.5);*/

        var textField = new TextField();
        textField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(
            ObservableValue<? extends String> observable,
            String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        textField.setMaxWidth(Height / 5);
        textField.setLayoutX(Height * 2.5);
        textField.setLayoutY(Height * canvasList.size() + Height / 10);
        textField.setText(String.valueOf(path.GetTempo()));

        //Volume selection
        Slider volumeSlider = new Slider(0, 100 ,0.5);
        volumeSlider.setMaxWidth(Height * .9);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setLayoutX(Height * 2.15);
        volumeSlider.setLayoutY(Height * canvasList.size() + Height * .75);

        //five lines for inserting notes
        gc.setLineWidth(2);
        for(int i = 1; i <= 5; i++)
            gc.strokeLine(3* Height + 20,40 + i*20, canvas.getWidth() - 20, 40 + i*20);

        //add (default Violin) music key
        Image violinKeyImage = ImageManager.getInstance().setDimensions(100, 170.62).getViolinKey();
        gc.drawImage(violinKeyImage, Height * 3, Height / 13);

        //Save created components
        musicPaths.add(path);
        canvasList.add(canvas);
        sliderMap.put(canvas, volumeSlider);

        //add created canvas and volume slider
        anchorPaneWithPaths.getChildren().add(canvas);
        anchorPaneWithPaths.getChildren().add(choiceBox);
        anchorPaneWithPaths.getChildren().add(textField);
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


    //region MouseEvents
    public void InsertNoteOnMouseClicked(MouseEvent mouseEvent)
    {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        System.out.println(String.format("User Clicked left mouse button at position: X - %f, Y - %f", x, y));

        if(x > 3 * Height + 100) // 100 - rozmiar klucza wiolinowego, może lepiej by było to sparametryzowane
        {
            Image noteImage = ImageManager.getInstance().setDimensions(100, 100).getQuarterNote();

            int index = (int)y / (int)Height;
            System.out.println(String.format("The selected path where to insert note: %d", index));

            var canvas = canvasList.get(index);

            var gc = canvas.getGraphicsContext2D();

            int insertX = ((int)(x - 45) / 10) * 10;
            int insertY = ((int)((y - 80 - Height * index) / 10)) * 10;
            System.out.println(String.format("Note inserted at: X - %d, Y - %d", insertX, insertY));
            gc.drawImage(noteImage, insertX, insertY);
        }
    }
    //endregion
}
