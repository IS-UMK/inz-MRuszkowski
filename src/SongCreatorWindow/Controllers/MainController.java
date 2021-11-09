package SongCreatorWindow.Controllers;

import Images.ImageManager;
import Model.Instrument;
import Model.Note;
import Model.NoteToNumericValue;
import Model.Path;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import org.jfugue.player.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainController
{
    //TODO: Wyeliminować wzorzec "kula błota"

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
    HashMap<Path, Canvas> canvasMap = new HashMap<>();
    HashMap<Canvas, Slider> volumeSliderMap = new HashMap<Canvas, Slider>();
    HashMap<Canvas, ChoiceBox> choiceBoxMap = new HashMap<>();
    HashMap<Canvas, TextField> textFieldMap = new HashMap<>();
    HashMap<Canvas, TextField> tempoMap = new HashMap<>();
    HashMap<Canvas, MenuItem> selectionMenuItemToCanvas = new HashMap<>();

    //Data structures for program logic (Model)
    @FXML
    Menu selectPathMenuItem;
    Path selectedPath = null;

    Canvas interactionCanvas;
    int strokeLineWidthForSelection = 10;

    List<Path> musicPaths = new LinkedList<Path>();

    //For playing music
    @FXML
    MenuItem playMenuItem;

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

        var tempoTextField = new TextField();
        tempoTextField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(
            ObservableValue<? extends String> observable,
            String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    tempoTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        tempoTextField.setMaxWidth(Height / 5);
        tempoTextField.setLayoutX(Height * 2.5);
        tempoTextField.setLayoutY(Height * canvasList.size() + Height / 10);
        tempoTextField.setText(String.valueOf(path.getTempo()));

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
        canvasMap.put(path, canvas);
        volumeSliderMap.put(canvas, volumeSlider);
        choiceBoxMap.put(canvas, choiceBox);
        textFieldMap.put(canvas, tempoTextField);
        tempoMap.put(canvas, tempoTextField);

        //add created canvas and volume slider
        anchorPaneWithPaths.getChildren().add(canvas);
        anchorPaneWithPaths.getChildren().add(choiceBox);
        anchorPaneWithPaths.getChildren().add(tempoTextField);
        anchorPaneWithPaths.getChildren().add(volumeSlider);

        var pathToSelect = new MenuItem(pathName);
        pathToSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(interactionCanvas == null)
                {
                    interactionCanvas = new Canvas(Width, Height);
                    anchorPaneWithPaths.getChildren().add(interactionCanvas);
                }

                selectedPath = path;

                GraphicsContext gc = interactionCanvas.getGraphicsContext2D();
                gc.clearRect(0, 0, interactionCanvas.getWidth(), interactionCanvas.getHeight());

                int index = musicPaths.indexOf(selectedPath);
                interactionCanvas.setHeight(Height);
                interactionCanvas.setLayoutY(Height * index);

                gc.setStroke(Color.BLUE);
                gc.setLineWidth(strokeLineWidthForSelection);
                gc.strokeRect(strokeLineWidthForSelection / 2, strokeLineWidthForSelection / 2,
                        Width - strokeLineWidthForSelection, Height - strokeLineWidthForSelection);

                System.out.println(String.format("Path number %d has been selected", index));
            }
        });
        selectPathMenuItem.getItems().add(pathToSelect);
        selectionMenuItemToCanvas.put(canvas, pathToSelect);
    }

    public void UnselectPath(ActionEvent actionEvent)
    {
        interactionCanvas.getGraphicsContext2D().clearRect(0, 0, interactionCanvas.getWidth(), interactionCanvas.getHeight());
        interactionCanvas.setHeight(0);
        selectedPath = null;
    }

    public void RenameSelected(ActionEvent actionEvent)
    {
        if(selectedPath == null)
            return;

        //ask user for new name
        TextInputDialog window = new TextInputDialog("Changing path name");
        window.setHeaderText("Enter name of existing path:");
        window.showAndWait();

        //get user choice
        String result = window.getResult();
        System.out.println(String.format("Changing path name from %s to %s", selectedPath.GetName(), result));
        selectedPath.setName(result);

        //change name on canvas
        Canvas canvas = canvasMap.get(selectedPath);
        var gc = canvas.getGraphicsContext2D();

        gc.clearRect(0,0, Height, Height);
        gc.strokeRect(0,0, Height, Height);
        Font font = new Font(Font.getFamilies().toArray()[1].toString(), 24);
        gc.setFont(font);
        //Text should be placed basing on the middle of square, minus half of the final text size on canvas (font size)
        gc.fillText(result, Height /2 - result.length()/2.0*24*3/4, Height /2, Height);

        //change name in menu item
        MenuItem pathSelectionMenuItem = selectionMenuItemToCanvas.get(canvas);
        pathSelectionMenuItem.setText(result);

        UnselectPath(null);
    }

    public void DeleteSelected(ActionEvent actionEvent)
    {
        if(selectedPath == null)
        {
            var alert = new Alert(Alert.AlertType.ERROR, "None path is selected");
            alert.showAndWait();
            return;
        }

        //get canvas with components
        Canvas canvas = canvasMap.get(selectedPath);
        musicPaths.remove(selectedPath);

        //remove it from GUI
        anchorPaneWithPaths.getChildren().remove(canvas);
        selectedPath = null;

        //move up canvases below selected to delete
        int index = canvasList.indexOf(canvas);
        System.out.println(String.format("Deleting path %d", index));

        for(int i = index + 1; i < canvasList.size(); i++)
        {
            System.out.println(String.format("Moving components of path %d up", i));

            var canvas_to_move_up = canvasList.get(i);
            canvas_to_move_up.setLayoutY(canvas_to_move_up.getLayoutY() - Height);

            var volumeSlider_to_move_up = volumeSliderMap.get(canvas_to_move_up);
            volumeSlider_to_move_up.setLayoutY(volumeSlider_to_move_up.getLayoutY() - Height);

            var choiceBox_to_move_up = choiceBoxMap.get(canvas_to_move_up);
            choiceBox_to_move_up.setLayoutY(choiceBox_to_move_up.getLayoutY() - Height);

            var textField_to_move_up = textFieldMap.get(canvas_to_move_up);
            textField_to_move_up.setLayoutY(textField_to_move_up.getLayoutY() - Height);

            var tempoField_to_move_up= tempoMap.get(canvas_to_move_up);
            tempoField_to_move_up.setLayoutY(tempoField_to_move_up.getLayoutY() - Height);
        }

        //remove this canvas from data structures
        canvasMap.remove(selectedPath);
        canvasList.remove(canvas);

        //remove components associated with that canvas
        Slider volumeSlider = volumeSliderMap.get(canvas);
        volumeSliderMap.remove(volumeSlider);
        anchorPaneWithPaths.getChildren().remove(volumeSlider);

        ChoiceBox choiceBox = choiceBoxMap.get(canvas);
        choiceBoxMap.remove(choiceBox);
        anchorPaneWithPaths.getChildren().remove(choiceBox);

        TextField textField = textFieldMap.get(canvas);
        textFieldMap.remove(textField);
        anchorPaneWithPaths.getChildren().remove(textField);

        TextField tempoField = tempoMap.get(canvas);
        tempoMap.remove(tempoField);
        anchorPaneWithPaths.getChildren().remove(tempoField);

        //remove selection option
        MenuItem pathSelectionMenuItem = selectionMenuItemToCanvas.get(canvas);
        selectPathMenuItem.getItems().remove(pathSelectionMenuItem);
        selectionMenuItemToCanvas.remove(pathSelectionMenuItem);

        //clear selection
        UnselectPath(null);
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
        new Thread(() -> {
            Player player = new Player();
            var musicString = new StringBuilder();

            for(Path s : musicPaths)
                musicString.append(s.getExtractedMusic());

            System.out.println(String.format("Built music string - %s", musicString.toString()));

            player.play(musicString.toString());
        }).start();
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
            //model code
            int index = (int)y / (int)Height;
            System.out.println(String.format("The selected path where to insert note: %d", index));

            Note note = Note.CreateNote(NoteToNumericValue.Get_Octave_5_sound_C(), 'q');
            note.setTimeX(x);
            musicPaths.get(index).addSound(note);

            //view code
            Image noteImage = ImageManager.getInstance().setDimensions(100, 100).getQuarterNote();

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
