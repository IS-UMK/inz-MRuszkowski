package SongCreatorWindow.View;

import Images.ImageManager;
import SongCreatorWindow.Model.Core.Instrument;
import SongCreatorWindow.Model.Core.Note;
import SongCreatorWindow.Model.Core.Path;
import SongCreatorWindow.Model.Events.IModelEvent;
import SongCreatorWindow.Model.Events.INoteEvent;
import SongCreatorWindow.Model.Events.IPathEvent;
import SongCreatorWindow.Model.GlobalSettings;
import SongCreatorWindow.Model.ModelManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static SongCreatorWindow.Model.GlobalSettings.*;
import static SongCreatorWindow.Model.GlobalSettings.strokeLineWidthForSelection;

public class ViewManagerModelChangesHandling implements IPathEvent, INoteEvent, IModelEvent
{
    //Model
    ModelManager modelManager;

    //Data structures for GUI Components
    List<Canvas> canvasList;
    HashMap<Path, Canvas> canvasMap;
    HashMap<Canvas, Slider> volumeSliderMap;
    HashMap<Canvas, ChoiceBox> choiceBoxMap;
    HashMap<Canvas, TextField> tempoMap;
    HashMap<Canvas, MenuItem> selectionMenuItemToCanvas;

    //GUI components
    AnchorPane anchorPaneWithPaths;
    Menu selectPathMenuItem;
    MenuItem playMenuItem;

    Canvas interactionCanvas;
    double canvasCurrentWidth = Width;

    public ViewManagerModelChangesHandling(ModelManager modelManager, AnchorPane anchorPaneWithPaths, Menu selectPathMenuItem, MenuItem playMenuItem)
    {
        this.modelManager = modelManager;

        canvasList = new LinkedList<Canvas>();
        canvasMap = new HashMap<>();
        volumeSliderMap = new HashMap<Canvas, Slider>();
        choiceBoxMap = new HashMap<>();
        tempoMap = new HashMap<>();
        selectionMenuItemToCanvas = new HashMap<>();

        this.anchorPaneWithPaths = anchorPaneWithPaths;
        this.selectPathMenuItem = selectPathMenuItem;
        this.playMenuItem = playMenuItem;
    }

    @Override
    public void onModelLoaded(int latestTimeX)
    {
        canvasCurrentWidth = latestTimeX > canvasCurrentWidth ? latestTimeX + GlobalSettings.canvasExtension : GlobalSettings.Width;
    }

    @Override
    public void onNoteAdded(Path path, Note note)
    {
        Image noteImage = ImageManager.getInstance().setDimensions(noteWidth, 100).getQuarterNote();

        var canvas = canvasMap.get(path);

        var gc = canvas.getGraphicsContext2D();

        if(note.getTimeX() >= canvas.getWidth() - GlobalSettings.widthOfAreaWhereCanvasExtends)
            addSpaceToCanvas();

        gc.drawImage(noteImage, note.getTimeX(), note.getSoundHeight());
        System.out.println(String.format("Note inserted at: X - %d, Y - %d", note.getTimeX(), note.getSoundHeight()));
    }

    private void addSpaceToCanvas()
    {
        double oldWidth = 0;

        for(Canvas canvas : canvasList)
        {
            oldWidth = canvas.getWidth();
            canvas.setWidth(oldWidth + GlobalSettings.canvasExtension);
            var gc = canvas.getGraphicsContext2D();

            gc.clearRect(
                    oldWidth - GlobalSettings.getLinesMargins(),
                    0,
                    canvas.getWidth(),
                    canvas.getHeight()
            );

            gc.strokeRect(
                    numberOfPropertySquaresInPath * Height,
                    0,
                    canvas.getWidth(),
                    canvas.getHeight()
            );

            for(int i = 1; i <= 5; i++)
                gc.strokeLine(
                        oldWidth - GlobalSettings.getLinesMargins(),
                        GlobalSettings.getLinesStartHeight() + i * GlobalSettings.getLinesPadding(),
                        canvas.getWidth() - GlobalSettings.getLinesMargins(),
                        GlobalSettings.getLinesStartHeight() + i * GlobalSettings.getLinesPadding()
                );
        }

        canvasCurrentWidth = oldWidth + GlobalSettings.canvasExtension;
    }

    @Override
    public void onPathCreated(Path path)
    {
        //create canvas and set its size
        Canvas canvas = new Canvas(canvasCurrentWidth, Height);
        canvas.setLayoutY(Height * canvasList.size());

        //paint new path on new canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //border of path
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(0,0, canvasCurrentWidth, Height);

        //set up font to use
        Font font = new Font(Font.getFamilies().toArray()[1].toString(), 24);
        var savedFont = gc.getFont();

        //Display Path Name
        gc.strokeRect(0,0, Height, Height);
        gc.setFont(font);
        //Text should be placed basing on the middle of square, minus half of the final text size on canvas (font size)
        gc.fillText(path.getName(), Height /2 - path.getName().length()/2.0*24*3/4, Height /2, Height);

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
        choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                String instrumentName = (String) observableValue.getValue();
                path.setInstrument(instrumentName);

                gc.clearRect(
                        Height * 1.15,
                        Height / 10,
                        Height * .7,
                        Height * .7
                );

                String instrument = instrumentName.split(" ")[0];
                Image instrumentImage = ImageManager.getInstance().setDimensions(Height * .7, Height * .7).getInstrumentByName(instrument);
                gc.drawImage(instrumentImage, Height * 1.15, Height / 10);

                System.out.println(String.format("Instrument set to %s for path %s", instrumentName, path.getName()));
            }
        });

        //set layout of list created above
        choiceBox.setLayoutX(1.1 * Height);
        choiceBox.setLayoutY(Height * canvasList.size() + Height - Height/5);
        choiceBox.setMinWidth(Height * .8);
        choiceBox.setMaxWidth(Height * .8);
        choiceBox.setValue(instruments[1]);

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
                Height / 7.5,
                Height
        );

        var tempoTextField = new TextField();
        tempoTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                if (!newValue.matches("[^\\d]"))
                {
                    if(newValue.isEmpty())
                        return;

                    path.setTempo(Integer.parseInt(newValue));
                    System.out.println(String.format("Tempo of path %s has been changed to %d", path.getName() , path.getTempo()));
                }
            }
        });
        tempoTextField.setMaxWidth(Height / 5);
        tempoTextField.setLayoutX(Height * 2.5);
        tempoTextField.setLayoutY(Height * canvasList.size() + Height / 20);
        tempoTextField.setText(String.valueOf(path.getTempo()));

        //Volume selection
        Slider volumeSlider = new Slider(0, 100 ,0.5);
        volumeSlider.setMaxWidth(Height * .9);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setLayoutX(Height * 2.15);
        volumeSlider.setLayoutY(Height * canvasList.size() + Height * .75);
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                path.setVolume(t1.byteValue());
                System.out.println(String.format("Volume of path %s has been changed to %d", path.getName() , path.getVolume()));
            }
        });
        volumeSlider.setValue(path.getVolume());

        //five lines for inserting notes
        gc.setLineWidth(GlobalSettings.strokeLineBorderWidth);
        for(int i = 1; i <= 5; i++)
            gc.strokeLine(
                    numberOfPropertySquaresInPath * Height + GlobalSettings.getLinesMargins(),
                    GlobalSettings.getLinesStartHeight() + i * GlobalSettings.getLinesPadding(),
                    canvas.getWidth() - GlobalSettings.getLinesMargins(),
                    GlobalSettings.getLinesStartHeight() + i * GlobalSettings.getLinesPadding()
            );

        //add (default Violin) music key
        Image musicKeyImage = ImageManager.getInstance().setDimensions(GlobalSettings.musicKeyWidth, GlobalSettings.getMusicKeyHeight()).getMusicKey(path.getMusicKeySelection());
        gc.drawImage(musicKeyImage, Height * 3, Height / 13);

        //Save created components
        canvasList.add(canvas);
        canvasMap.put(path, canvas);
        volumeSliderMap.put(canvas, volumeSlider);
        choiceBoxMap.put(canvas, choiceBox);
        tempoMap.put(canvas, tempoTextField);

        //add created canvas and volume slider
        anchorPaneWithPaths.getChildren().add(canvas);
        anchorPaneWithPaths.getChildren().add(choiceBox);
        anchorPaneWithPaths.getChildren().add(tempoTextField);
        anchorPaneWithPaths.getChildren().add(volumeSlider);

        var pathToSelect = new MenuItem(path.getName());
        pathToSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(interactionCanvas == null)
                {
                    interactionCanvas = new Canvas(Width, Height);
                    anchorPaneWithPaths.getChildren().add(interactionCanvas);
                }

                modelManager.setSelectedPath(path);

                GraphicsContext gc = interactionCanvas.getGraphicsContext2D();
                gc.clearRect(0, 0, interactionCanvas.getWidth(), interactionCanvas.getHeight());

                int index = modelManager.getIndexOfSelectedPath();
                interactionCanvas.setHeight(Height);
                interactionCanvas.setLayoutY(Height * index);

                gc.setStroke(Color.BLUE);
                gc.setLineWidth(strokeLineWidthForSelection);
                gc.strokeRect(strokeLineWidthForSelection / 2, strokeLineWidthForSelection / 2,
                        canvasCurrentWidth - strokeLineWidthForSelection, Height - strokeLineWidthForSelection);

                System.out.println(String.format("Path number %d has been selected", index));
            }
        });

        selectPathMenuItem.getItems().add(pathToSelect);
        selectionMenuItemToCanvas.put(canvas, pathToSelect);
    }

    @Override
    public void onPathNameRenamed(Path path) {
        //change name on canvas
        Canvas canvas = canvasMap.get(path);
        var gc = canvas.getGraphicsContext2D();

        gc.clearRect(0,0, Height, Height);
        gc.strokeRect(0,0, Height, Height);
        Font font = new Font(Font.getFamilies().toArray()[1].toString(), 24);
        gc.setFont(font);
        //Text should be placed basing on the middle of square, minus half of the final text size on canvas (font size)
        gc.fillText(path.getName(), Height /2 - path.getName().length()/2.0*24*3/4, Height /2, Height);

        //change name in menu item
        MenuItem pathSelectionMenuItem = selectionMenuItemToCanvas.get(canvas);
        pathSelectionMenuItem.setText(path.getName());

        onPathClearSelection();
    }

    @Override
    public void onPathDeleted(Path path) {
        //get canvas with components
        Canvas canvas = canvasMap.get(path);

        //remove it from GUI
        anchorPaneWithPaths.getChildren().remove(canvas);

        //move up canvases below selected to delete
        int index = canvasList.indexOf(canvas);

        for(int i = index + 1; i < canvasList.size(); i++)
        {
            System.out.println(String.format("Moving components of path %d up", i));

            var canvas_to_move_up = canvasList.get(i);
            canvas_to_move_up.setLayoutY(canvas_to_move_up.getLayoutY() - Height);

            var volumeSlider_to_move_up = volumeSliderMap.get(canvas_to_move_up);
            volumeSlider_to_move_up.setLayoutY(volumeSlider_to_move_up.getLayoutY() - Height);

            var choiceBox_to_move_up = choiceBoxMap.get(canvas_to_move_up);
            choiceBox_to_move_up.setLayoutY(choiceBox_to_move_up.getLayoutY() - Height);

            var tempoMap_to_move_up= tempoMap.get(canvas_to_move_up);
            tempoMap_to_move_up.setLayoutY(tempoMap_to_move_up.getLayoutY() - Height);
        }

        //remove this canvas from data structures
        canvasMap.remove(path);
        canvasList.remove(canvas);

        //remove components associated with that canvas
        Slider volumeSlider = volumeSliderMap.get(canvas);
        volumeSliderMap.remove(volumeSlider);
        anchorPaneWithPaths.getChildren().remove(volumeSlider);

        ChoiceBox choiceBox = choiceBoxMap.get(canvas);
        choiceBoxMap.remove(choiceBox);
        anchorPaneWithPaths.getChildren().remove(choiceBox);

        TextField tempoField = tempoMap.get(canvas);
        tempoMap.remove(tempoField);
        anchorPaneWithPaths.getChildren().remove(tempoField);

        //remove selection option
        MenuItem pathSelectionMenuItem = selectionMenuItemToCanvas.get(canvas);
        selectPathMenuItem.getItems().remove(pathSelectionMenuItem);
        selectionMenuItemToCanvas.remove(pathSelectionMenuItem);

        onPathClearSelection();
    }

    @Override
    public void onPathClearSelection()
    {
        if(interactionCanvas == null)
        {
            interactionCanvas = new Canvas(Width, Height);
            anchorPaneWithPaths.getChildren().add(interactionCanvas);
        }

        interactionCanvas.getGraphicsContext2D().clearRect(0, 0, interactionCanvas.getWidth(), interactionCanvas.getHeight());
        interactionCanvas.setHeight(0);
    }
}
