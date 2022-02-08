package SongCreatorWindow.Views;

import Images.ImageManager;
import SongCreatorWindow.Model.Core.*;
import SongCreatorWindow.Model.Events.*;
import SongCreatorWindow.Model.GlobalSettings;
import SongCreatorWindow.Model.ModelManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import org.jfugue.player.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static SongCreatorWindow.Model.GlobalSettings.*;

public class ViewManagerModelChangesHandling implements IPathEvent, ISoundEvent, IModelEvent, IMusicSoundEditionEvent
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
    HashMap<Path, Menu> selectionMenuForSounds;
    HashMap<Menu, TreeMap<IPlayable, MenuItem>> selectionMenuItemOfSound;

    HashMap<IPlayable, List<ImageView>> musicSymbols;
    HashMap<IPlayable, ImageView> modificationSymbols;
    HashMap<IPlayable, javafx.scene.shape.Path> bindingSymbols;
    List<IClickedEvent> listeners;

    //GUI components
    AnchorPane anchorPaneWithPaths;
    Menu selectPathMenuItem;
    Menu selectSoundMenuItem;
    MenuItem playMenuItem;

    Canvas interactionCanvas;
    double canvasCurrentWidth = Width;

    Canvas soundEditionCanvas;

    Canvas visualBarCanvas;

    Player player;

    public ViewManagerModelChangesHandling(ModelManager modelManager, AnchorPane anchorPaneWithPaths, Menu selectPathMenuItem, Menu selectSoundMenuItem, MenuItem playMenuItem)
    {
        this.modelManager = modelManager;

        canvasList = new LinkedList<Canvas>();
        canvasMap = new HashMap<>();
        volumeSliderMap = new HashMap<Canvas, Slider>();
        choiceBoxMap = new HashMap<>();
        tempoMap = new HashMap<>();
        selectionMenuItemToCanvas = new HashMap<>();

        selectionMenuForSounds = new HashMap<>();
        selectionMenuItemOfSound = new HashMap<>();

        musicSymbols = new HashMap<>();
        modificationSymbols = new HashMap<>();
        bindingSymbols = new HashMap<>();
        listeners = new LinkedList<>();

        this.anchorPaneWithPaths = anchorPaneWithPaths;
        this.selectPathMenuItem = selectPathMenuItem;
        this.selectSoundMenuItem = selectSoundMenuItem;
        this.playMenuItem = playMenuItem;

        visualBarCanvas = new Canvas(strokeLineBorderWidth * 5, 0);
        anchorPaneWithPaths.getChildren().add(visualBarCanvas);
        visualBarCanvas.getGraphicsContext2D().setFill(Color.RED);

        player = new Player();
    }

    @Override
    public void onModelLoaded(int latestTimeX)
    {
        canvasCurrentWidth = latestTimeX > canvasCurrentWidth ? latestTimeX + GlobalSettings.canvasExtension : GlobalSettings.Width;
    }

    @Override
    public void onMusicSymbolAdded(Path path, IPlayable musicSound)
    {
        if(!GlobalSettings.loadingProject)
            new Thread(() -> {
                    player.play(musicSound.ExtractJFugueSoundString(true));
            }).start();

        Image musicSymbolImage = ImageManager.getInstance().setDimensions(GlobalSettings.noteWidth, GlobalSettings.noteHeight).getNote(musicSound.getDuration());

        List<ImageView> views = new LinkedList<>();
        var canvas = canvasMap.get(path);

        if(musicSound.getTimeX() >= canvas.getWidth() - GlobalSettings.widthOfAreaWhereCanvasExtends)
            addSpaceToCanvas();

        ImageView view = createImageViewOfSound(musicSymbolImage, path, musicSound, musicSound.getTimeX(), musicSound.getSoundHeight());
        view.setPickOnBounds(false);
        views.add(view);
        anchorPaneWithPaths.getChildren().add(view);

        if(musicSound instanceof Note)
        {
            System.out.println(String.format("Note inserted at: X - %d, Y - %d", musicSound.getTimeX(), musicSound.getSoundHeight()));
        }
        else if(musicSound instanceof Accord)
        {
            drawAnotherAccordParts(views, musicSymbolImage, path, musicSound);
        }

        musicSymbols.put(musicSound, views);


        //Add different edition selection
        Menu menu = selectionMenuForSounds.get(path);

        MenuItem soundItemMenu = new MenuItem(String.format("%s %s %.3f", musicSound.getSoundType(), musicSound.getValue(), Path.getSoundTimeOccurrence(musicSound.getTimeX())));
        soundItemMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fireOnMusicSymbolClicked(path, musicSound);
            }
        });

        var menuItems = selectionMenuItemOfSound.get(menu);
        menuItems.put(musicSound, soundItemMenu);
        menu.getItems().add(menuItems.headMap(musicSound).size(), soundItemMenu);
    }

    private ImageView createImageViewOfSound(Image musicSymbolImage, Path path, IPlayable musicSound, int insertX, int insertY)
    {
        ImageView view = new ImageView(musicSymbolImage);
        view.setFitWidth(musicSymbolImage.getWidth());
        view.setPickOnBounds(true);

        view.setLayoutX(insertX);
        view.setLayoutY(insertY + modelManager.getIndexOfPath(path) * GlobalSettings.Height);
        view.setFitHeight(musicSymbolImage.getHeight());

        view.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseButton.SECONDARY) {
                    System.out.println(String.format("Music Symbol Clicked at: %d %d", insertX, insertY));
                    fireOnMusicSymbolClicked(path, musicSound);
                }
            }
        });

        return view;
    }

    private void drawAnotherAccordParts(List<ImageView> views, Image musicSymbolImage, Path path, IPlayable musicSound)
    {
        String[] accordIntervals = Accord.AccordType.getIntervalsOfAccord(((Accord)musicSound).getAccordName());//GlobalSettings.accordSelectionName);

        int soundHeight = musicSound.getSoundHeight();

        System.out.println(String.format("Accord part inserted at: X - %d, Y - %d", musicSound.getTimeX(), soundHeight));

        int drawAboveBy;
        ImageView anotherView;
        for(String interval : accordIntervals)
        {
            try
            {
                drawAboveBy = Integer.parseInt(interval);
            }
            catch (Exception e)
            {
                drawAboveBy = interval.charAt(0)-48;
            }
            drawAboveBy--;

            anotherView = createImageViewOfSound(musicSymbolImage, path, musicSound, musicSound.getTimeX(), (int)(GlobalSettings.getLinesPadding() / -2) * drawAboveBy + soundHeight);

            views.add(anotherView);
            anchorPaneWithPaths.getChildren().add(anotherView);

            System.out.println(String.format("Accord part inserted at: X - %d, Y - %d", musicSound.getTimeX(), soundHeight));
        }
    }

    public void addListener(IClickedEvent listener)
    {
        listeners.add(listener);
    }

    private void fireOnMusicSymbolClicked(Path path, IPlayable musicSound)
    {
        Iterator iterator = listeners.iterator();

        while(iterator.hasNext())
        {
            IClickedEvent clickedEvent = (IClickedEvent) iterator.next();
            clickedEvent.onMusicSymbolClicked(path, musicSound);
        }
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

        if(modelManager.getSelectedPath() != null)
        {
            GraphicsContext gc = interactionCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, interactionCanvas.getWidth(), interactionCanvas.getHeight());

            int index = modelManager.getIndexOfSelectedPath();
            interactionCanvas.setWidth(canvasCurrentWidth);
            interactionCanvas.setLayoutY(Height * index);

            gc.setStroke(GlobalSettings.selectionColor);
            gc.setLineWidth(strokeLineWidthForSelection);
            gc.strokeRect(strokeLineWidthForSelection / 2, strokeLineWidthForSelection / 2,
                    canvasCurrentWidth - strokeLineWidthForSelection, Height - strokeLineWidthForSelection);
        }
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
        gc.setFill(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(0,0, canvasCurrentWidth, Height);

        //set up font to use
        Font font = new Font(Font.getFamilies().toArray()[1].toString(), 24);
        var savedFont = gc.getFont();

        displayNameOfNewPath(path, gc, font);

        displaySelectedInstrumentInfoOfNewPath(path, gc);

        ChoiceBox instrumentChoiceBox = displayInstrumentChoiceBoxOfNewPath(path, gc);

        //display speaker, tempo selection and create volume slider
        gc.strokeRect(2 * Height, 0, Height, Height);

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
        TextField tempoTextField = displayTempoOfNewPath(path);

        Slider volumeSlider = displayVolumeSliderOfNewPath(path);

        drawFiveLinesOfNewPath(canvas, gc, numberOfPropertySquaresInPath * Height + GlobalSettings.getLinesMargins(), 0);

        //add (default Violin) music key
        drawPathClef(path, gc, 0,0);

        //Save created components
        canvasList.add(canvas);
        canvasMap.put(path, canvas);
        volumeSliderMap.put(canvas, volumeSlider);
        choiceBoxMap.put(canvas, instrumentChoiceBox);
        tempoMap.put(canvas, tempoTextField);

        //add created canvas and volume slider
        anchorPaneWithPaths.getChildren().addAll(canvas, instrumentChoiceBox, tempoTextField, volumeSlider);

        initializeMenuItems(path, canvas);
    }

    //region New Path Creation
    private void initializeMenuItems(Path path, Canvas canvas) {
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
                interactionCanvas.setWidth(canvasCurrentWidth);
                interactionCanvas.setLayoutY(Height * index);

                gc.setStroke(GlobalSettings.selectionColor);
                gc.setLineWidth(strokeLineWidthForSelection);
                gc.strokeRect(strokeLineWidthForSelection / 2, strokeLineWidthForSelection / 2,
                        canvasCurrentWidth - strokeLineWidthForSelection, Height - strokeLineWidthForSelection);

                System.out.println(String.format("Path number %d has been selected", index));
            }
        });

        selectPathMenuItem.getItems().add(pathToSelect);
        selectionMenuItemToCanvas.put(canvas, pathToSelect);


        var pathMenuToSelectSound = new Menu(path.getName());

        selectionMenuForSounds.put(path, pathMenuToSelectSound);
        selectionMenuItemOfSound.put(
            pathMenuToSelectSound,
                new TreeMap<>(new Comparator<IPlayable>() {
                    @Override
                    public int compare(IPlayable o1, IPlayable o2) {
                        if(o1.getTimeX() > o2.getTimeX())
                            return 1;
                        else if(o1.getTimeX() < o2.getTimeX())
                            return -1;

                        return 0;
                    }
                }
            )
        );

        selectSoundMenuItem.getItems().add(pathMenuToSelectSound);
    }

    private void drawFiveLinesOfNewPath(Canvas canvas, GraphicsContext gc, double startX, int multipliedHeight) {
        //five lines for inserting notes
        gc.setLineWidth(GlobalSettings.strokeLineBorderWidth);
        for(int i = 1; i <= 5; i++)
            gc.strokeLine(
                    startX,
                    GlobalSettings.getLinesStartHeight() + i * GlobalSettings.getLinesPadding() + multipliedHeight * GlobalSettings.Height,
                    canvas.getWidth() - GlobalSettings.getLinesMargins(),
                    GlobalSettings.getLinesStartHeight() + i * GlobalSettings.getLinesPadding() + multipliedHeight * GlobalSettings.Height
            );
    }

    private Slider displayVolumeSliderOfNewPath(Path path)
    {
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
                path.setVolume((byte)(t1.byteValue() * 1.27));
                System.out.println(String.format("Volume of path %s has been changed to %d", path.getName() , path.getVolume()));
            }
        });
        volumeSlider.setValue(path.getVolume());
        return volumeSlider;
    }

    private TextField displayTempoOfNewPath(Path path)
    {
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
        return tempoTextField;
    }

    private ChoiceBox displayInstrumentChoiceBoxOfNewPath(Path path, GraphicsContext gc)
    {
        //Instrument Selection
        var instruments = Instrument.getAllInstruments();

        //Observable list where I can choose Instrument - create list with given instrument list
        ChoiceBox instrumentChoiceBox = new ChoiceBox(FXCollections.observableArrayList(instruments));
        instrumentChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
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
        instrumentChoiceBox.setLayoutX(1.1 * Height);
        instrumentChoiceBox.setLayoutY(Height * canvasList.size() + Height - Height/5);
        instrumentChoiceBox.setMinWidth(Height * .8);
        instrumentChoiceBox.setMaxWidth(Height * .8);
        instrumentChoiceBox.setValue(path.getInstrument());
        return instrumentChoiceBox;
    }

    private void displaySelectedInstrumentInfoOfNewPath(Path path, GraphicsContext gc)
    {
        //Display selected instrument info
        gc.strokeRect(Height, 0, Height, Height);
        String selected_instrument_note = "Selected Instrument:";
        gc.fillText(
                selected_instrument_note,
                1.4*Height + Height/2 - selected_instrument_note.length()/2.0*24*3/4,
                Height - selected_instrument_note.length()/2.0*24*3/4,
                Height
        );

        Image instrumentImage = ImageManager.getInstance().setDimensions(Height * .7, Height * .7).getInstrumentByName(path.getInstrument());
        gc.drawImage(instrumentImage, Height * 1.15, Height / 10);
    }

    private void displayNameOfNewPath(Path path, GraphicsContext gc, Font font)
    {
        //Display Path Name
        gc.strokeRect(0,0, Height, Height);
        gc.setFont(font);
        //Text should be placed basing on the middle of square, minus half of the final text size on canvas (font size)
        //gc.fillText(path.getName(), Height /2 - path.getName().length()/2.0*24*3/4, Height /2, Height);
        //gc.fillText(path.getName(), Height /2 - path.getName().length()/2.0*24*3/4, Height /2, Height);
        gc.fillText(path.getName(), Height / 2 - path.getName().length() * 7, Height / 2);
    }
    //endregion

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
        gc.fillText(path.getName(), Height / 2 - path.getName().length() * 7, Height / 2);

        //change name in menu item
        MenuItem pathSelectionMenuItem = selectionMenuItemToCanvas.get(canvas);
        pathSelectionMenuItem.setText(path.getName());

        MenuItem pathWhereSoundsAreSelected = selectionMenuForSounds.get(path);
        pathWhereSoundsAreSelected.setText(path.getName());

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


        List<ImageView> views;
        ImageView modifier;
        javafx.scene.shape.Path bindings;
        for(IPlayable sound : path.getSounds())
        {
            views = musicSymbols.get(sound);
            for(var view : views)
                anchorPaneWithPaths.getChildren().remove(view);

            modifier = modificationSymbols.get(sound);
            if(modifier != null)
            {
                anchorPaneWithPaths.getChildren().remove(modifier);
                modificationSymbols.remove(modifier);
            }

            bindings = bindingSymbols.get(sound);
            if(bindings != null)
            {
                anchorPaneWithPaths.getChildren().remove(bindings);
                bindingSymbols.remove(bindings);
            }
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

        //remove selection options
        MenuItem pathSelectionMenuItem = selectionMenuItemToCanvas.get(canvas);
        selectPathMenuItem.getItems().remove(pathSelectionMenuItem);
        selectionMenuItemToCanvas.remove(pathSelectionMenuItem);

        Menu soundsOfPath = selectionMenuForSounds.get(path);

        TreeMap<IPlayable, MenuItem> items = selectionMenuItemOfSound.get(soundsOfPath);

        for(var item : items.keySet().toArray())
        {
            soundsOfPath.getItems().remove(item);
            items.remove(item);
        }

        selectSoundMenuItem.getItems().remove(soundsOfPath);
        selectionMenuForSounds.remove(soundsOfPath);

        Path currentPath;
        for(int i = index; i < canvasList.size(); i++)
        {
            currentPath = modelManager.getPathByIndex(i);
            for (IPlayable sound : currentPath.getSounds()) {
                onMusicSoundHeightChange(currentPath, sound);
                onMusicSoundTieCheck(currentPath, sound);
                onMusicSoundModified(currentPath, sound);
            }
        }

        onPathClearSelection();
        onMusicSoundClearSelection();
    }

    @Override
    public void onPathClefChanged(Path path, int soundShift)
    {
        var canvas = canvasMap.get(path);
        var gc = canvas.getGraphicsContext2D();

        gc.clearRect(
                numberOfPropertySquaresInPath * Height + GlobalSettings.strokeLineBorderWidth * 2,
                GlobalSettings.strokeLineBorderWidth * 2,
                GlobalSettings.musicClefWidth - strokeLineBorderWidth * 2,
                GlobalSettings.Height - strokeLineBorderWidth * 4
        );

        for(int i = 1; i <= 5; i++)
            gc.strokeLine(
                    numberOfPropertySquaresInPath * Height + GlobalSettings.getLinesMargins(),
                    GlobalSettings.getLinesStartHeight() + i * GlobalSettings.getLinesPadding(),
                    numberOfPropertySquaresInPath * Height + GlobalSettings.musicClefWidth + GlobalSettings.strokeLineBorderWidth,
                    GlobalSettings.getLinesStartHeight() + i * GlobalSettings.getLinesPadding()
            );

        drawPathClef(path, gc, 0, 0);

        for(IPlayable sound : path.getSounds())
        {
            onMusicSoundHeightChange(path, sound);
            onMusicSoundTieCheck(path, sound);
        }

        onMusicSoundClearSelection();
    }

    private void drawPathClef(Path path, GraphicsContext gc, double moveX, int multiplyHeight) {
        Image newClef;

        switch (path.getMusicClefSelection()) {
            case ViolinClef -> {
                newClef = ImageManager.getInstance().setDimensions(GlobalSettings.musicClefWidth, GlobalSettings.getMusicClefHeight(path.getMusicClefSelection())).getMusicClef(MusicClefSelection.ViolinClef);
                gc.drawImage(newClef, Height * 3 + moveX, Height / 13 + multiplyHeight * GlobalSettings.Height);
            }
            case BassClef -> {
                newClef = ImageManager.getInstance().setDimensions(GlobalSettings.musicClefWidth * .6, GlobalSettings.getMusicClefHeight(path.getMusicClefSelection()) * .6).getMusicClef(MusicClefSelection.BassClef);
                gc.drawImage(newClef, Height * 3 + getLinesPadding() * 1.2 + moveX, Height / 3.3+ multiplyHeight * GlobalSettings.Height);
            }
            case AltoClef -> {
                newClef = ImageManager.getInstance().setDimensions(GlobalSettings.musicClefWidth * .81, GlobalSettings.getMusicClefHeight(path.getMusicClefSelection()) * .81).getMusicClef(MusicClefSelection.AltoClef);
                gc.drawImage(newClef, Height * 3 + getLinesPadding() * .5 + moveX, Height / 3.5+ multiplyHeight * GlobalSettings.Height);
            }
        }
    }

    @Override
    public void onPathClearSelection()
    {
        if(interactionCanvas == null)
        {
            interactionCanvas = new Canvas(GlobalSettings.Width, GlobalSettings.Height);
            anchorPaneWithPaths.getChildren().add(interactionCanvas);
        }

        interactionCanvas.getGraphicsContext2D().clearRect(0, 0, interactionCanvas.getWidth(), interactionCanvas.getHeight());
        interactionCanvas.setHeight(0);
    }

    @Override
    public void onMusicSoundSelectedToEdition(Path path, IPlayable musicSound)
    {
        if(soundEditionCanvas == null)
        {
            soundEditionCanvas = new Canvas(0, 0);
            anchorPaneWithPaths.getChildren().add(soundEditionCanvas);
        }

        soundEditionCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("Unselecting sound");
                path.clearSelectionOfMusicSound();
            }
        });

        onMusicSoundClearSelection();

        var gc = soundEditionCanvas.getGraphicsContext2D();

        int index = modelManager.getIndexOfPath(path);

        soundEditionCanvas.setWidth(GlobalSettings.noteWidth);
        soundEditionCanvas.setHeight(GlobalSettings.noteHeight);
        soundEditionCanvas.setLayoutX(musicSound.getTimeX());
        soundEditionCanvas.setLayoutY(musicSound.getSoundHeight() + GlobalSettings.Height * index);

        gc.setLineWidth(GlobalSettings.strokeLineWidthForSelection);
        gc.setStroke(GlobalSettings.selectionColor);

        gc.strokeRect(strokeLineWidthForSelection / 2, strokeLineWidthForSelection / 2,
                soundEditionCanvas.getWidth() - strokeLineWidthForSelection, soundEditionCanvas.getHeight() - strokeLineWidthForSelection);
    }

    @Override
    public void onMusicSoundClearSelection() {
        if(soundEditionCanvas == null)
        {
            soundEditionCanvas = new Canvas(0, 0);
            anchorPaneWithPaths.getChildren().add(soundEditionCanvas);
        }

        soundEditionCanvas.getGraphicsContext2D().clearRect(0, 0, soundEditionCanvas.getWidth(), soundEditionCanvas.getHeight());
        soundEditionCanvas.setHeight(0);
    }

    @Override
    public void onMusicSoundTieCheck(Path path, IPlayable musicSound)
    {
        if(musicSound.isTiedWithPreviousSound())
        {
            redrawSoundBinding(path, musicSound);
        }

        if(musicSound.isTiedWithAnotherSound())
        {
            redrawSoundBinding(path, musicSound.getNextTiedSound());
        }
    }

    private void redrawSoundBinding(Path path, IPlayable musicSound)
    {
        javafx.scene.shape.Path tiePath = bindingSymbols.get(musicSound);

        if (tiePath != null)
        {
            anchorPaneWithPaths.getChildren().remove(tiePath);
            bindingSymbols.remove(musicSound);
        }

        tiePath = createBindingSymbolImageView(path, musicSound);
        bindingSymbols.put(musicSound, tiePath);
        anchorPaneWithPaths.getChildren().add(tiePath);
    }

    private javafx.scene.shape.Path createBindingSymbolImageView(Path path, IPlayable musicSound)
    {
        IPlayable previousSound = musicSound.getPreviousTiedSound();

        var tiePath = new javafx.scene.shape.Path();
        tiePath.setStrokeWidth(strokeLineBorderWidth * 5);
        tiePath.setStrokeLineCap(StrokeLineCap.ROUND);

        //Moving to the starting point
        var moveTo = new javafx.scene.shape.MoveTo();

        double firstX = previousSound.getTimeX() + noteWidth *.8;
        double firstY = previousSound.getSoundHeight() + noteHeight * .8 + modelManager.getIndexOfPath(path) * GlobalSettings.Height;
        double secondX = musicSound.getTimeX() + noteHeight * .2;
        double secondY = musicSound.getSoundHeight() + noteHeight * .8 + modelManager.getIndexOfPath(path) * GlobalSettings.Height;

        moveTo.setX(firstX);
        moveTo.setY(firstY);

        double R = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(firstY - secondY, 2));

        double y = secondY - firstY;

        double angle1 = Math.asin(y/R);
        double angle2 = Math.asin(-y/R);

        double r = R/3;
        double rotation = 45;
        if(secondY - firstY > 0)
            rotation += 180;

        double newX1 = r * Math.cos(angle1 + rotation);
        double newY1 = r * Math.sin(angle1 + rotation);

        double newX2 = r * Math.cos(angle2 - rotation);
        double newY2 = r * Math.sin(angle2 - rotation);
        //Instantiating the class CubicCurve
        CubicCurveTo cubicCurveTo = new CubicCurveTo();

        //Setting properties of the class CubicCurve
        cubicCurveTo.setControlX1(firstX + newX1);
        cubicCurveTo.setControlY1(firstY - newY1);
        cubicCurveTo.setControlX2(secondX - newX2);
        cubicCurveTo.setControlY2(secondY + newY2);
        cubicCurveTo.setX(secondX);
        cubicCurveTo.setY(secondY);

        //Adding the path elements to Observable list of the Path class
        tiePath.getElements().add(moveTo);
        tiePath.getElements().add(cubicCurveTo);

        return tiePath;
    }

    @Override
    public void onMusicSoundOccurrenceTimeChanged(Path path, IPlayable musicSound) {
        var views = musicSymbols.get(musicSound);

        for(ImageView view : views) {
            view.setLayoutX(musicSound.getTimeX());
        }

        onMusicSoundModified(path, musicSound);
        onMusicSoundSelectedToEdition(path, musicSound);

        AddSoundMenuItem(path, musicSound);
    }

    @Override
    public void onMusicSoundOccurrenceTimeChangedPreprocess(Path path, IPlayable musicSound)
    {
        DeleteSoundMenuItem(path, musicSound);
    }

    @Override
    public void onMusicSoundHeightChange(Path path, IPlayable musicSound) {
        var views = musicSymbols.get(musicSound);

        int savedLocation = (int) views.get(0).getLayoutY();
        views.get(0).setLayoutY(musicSound.getSoundHeight() + GlobalSettings.Height * modelManager.getIndexOfPath(path));

        if(views.size() > 1)
        {
            int movedLocation = (int) (views.get(0).getLayoutY() - savedLocation);//(int) (views.get(1).getLayoutY() - views.get(0).getLayoutY());

            for (int i = 1; i < views.size(); i++)
            {
                views.get(i).setLayoutY(views.get(i).getLayoutY() + movedLocation);//views.get(i - 1).getLayoutY() + i * movedLocation + GlobalSettings.Height * modelManager.getIndexOfPath(path));
            }
        }

        onMusicSoundModified(path, musicSound);
        onMusicSoundSelectedToEdition(path, musicSound);

        DeleteSoundMenuItem(path, musicSound);
        AddSoundMenuItem(path, musicSound);
    }

    private void AddSoundMenuItem(Path path, IPlayable musicSound) {
        Menu menu = selectionMenuForSounds.get(path);
        TreeMap<IPlayable, MenuItem> soundItems = selectionMenuItemOfSound.get(menu);

        MenuItem soundItemMenu = new MenuItem();
        soundItemMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fireOnMusicSymbolClicked(path, musicSound);
            }
        });

        soundItemMenu.setText(String.format("%s %s %.3f", musicSound.getSoundType(), musicSound.getValue(), Path.getSoundTimeOccurrence(musicSound.getTimeX())));

        soundItems.put(musicSound, soundItemMenu);
        menu.getItems().add(soundItems.headMap(musicSound).size(), soundItemMenu);
    }

    private void DeleteSoundMenuItem(Path path, IPlayable musicSound)
    {
        Menu menu = selectionMenuForSounds.get(path);
        TreeMap<IPlayable, MenuItem> soundItems = selectionMenuItemOfSound.get(menu);

        MenuItem soundItemMenu = soundItems.get(musicSound);

        menu.getItems().remove(soundItemMenu);
        soundItems.remove(musicSound);
    }

    @Override
    public void onMusicSoundDurationChange(Path path, IPlayable musicSound)
    {
        var views = musicSymbols.get(musicSound);

        for(ImageView view : views)
            view.setImage(ImageManager.getInstance().getNote(musicSound.getDuration()));
    }

    @Override
    public void onMusicSoundModified(Path path, IPlayable musicSound)
    {
        int symbolDim = GlobalSettings.noteHeight / 3;

        Image modificationSymbol = switch (musicSound.getModification())
        {
            case None -> ImageManager.getInstance().setDimensions(symbolDim, symbolDim).getModificationSymbol(SoundModification.None);
            case Sharp -> ImageManager.getInstance().setDimensions(symbolDim, symbolDim).getModificationSymbol(SoundModification.Sharp);
            case Flat -> ImageManager.getInstance().setDimensions(symbolDim, symbolDim).getModificationSymbol(SoundModification.Flat);
        };

        var previousView = modificationSymbols.get(musicSound);
        if(previousView != null) {
            previousView.setImage(null);
            modificationSymbols.remove(previousView);
        }

        if(modificationSymbol != null) {
            var view = new ImageView(modificationSymbol);

            int insertX = musicSound.getTimeX() - symbolDim / 2;
            int insertY = (int) (musicSound.getSoundHeight() + GlobalSettings.noteHeight - (symbolDim * 1.2) + modelManager.getIndexOfPath(path) * GlobalSettings.Height);

            view.setFitWidth(symbolDim);
            view.setFitHeight(symbolDim);
            view.setLayoutX(insertX);
            view.setLayoutY(insertY);

            System.out.println(String.format("Modification symbol inserted at: %d %d", insertX, insertY));

            modificationSymbols.put(musicSound, view);
            anchorPaneWithPaths.getChildren().add(view);
        }
    }

    @Override
    public void onAccordNameChanged(Path path, IPlayable musicSound)
    {
        var views = musicSymbols.get(musicSound);

        removeAdditionalSymbols(views);
        addAdditionalSymbols(views, path, musicSound);
    }

    private void removeAdditionalSymbols(List<ImageView> views)
    {
        ImageView view;
        int size = views.size() - 1;
        for(int i = size; i >= 1; i--)
        {
            view = views.get(i);
            anchorPaneWithPaths.getChildren().remove(view);
            views.remove(view);
        }
    }

    private void addAdditionalSymbols(List<ImageView> views, Path path, IPlayable musicSound)
    {
        var imageOfSoundSymbol = ImageManager.getInstance().setDimensions(GlobalSettings.noteWidth, GlobalSettings.noteHeight).getNote(musicSound.getDuration());

        drawAnotherAccordParts(views, imageOfSoundSymbol, path, musicSound);
    }

    @Override
    public void onMusicSoundConvertedToAccord(Path path, IPlayable musicSound, Accord newAccord)
    {
        onMusicSymbolAdded(path, newAccord);

        onMusicSoundTieCheck(path, newAccord);
        onMusicSoundModified(path, newAccord);
        onMusicSoundSelectedToEdition(path, newAccord);
    }

    @Override
    public void onMusicSoundConvertedToNote(Path path, IPlayable musicSound, IPlayable newNote)
    {
        onMusicSymbolAdded(path, newNote);

        onMusicSoundTieCheck(path, newNote);
        onMusicSoundModified(path, newNote);
        onMusicSoundSelectedToEdition(path, newNote);
    }

    private void refreshSoundBindings(Path path, IPlayable musicSound, IPlayable newSound)
    {
        if(musicSound.isTiedWithPreviousSound())
        {
            var tiePath = bindingSymbols.get(musicSound);
            anchorPaneWithPaths.getChildren().remove(tiePath);
            bindingSymbols.remove(musicSound);

            redrawSoundBinding(path, newSound);
        }

        if(musicSound.isTiedWithAnotherSound())
        {
            IPlayable nextSound = musicSound.getNextTiedSound();

            var tiePath = bindingSymbols.get(nextSound);
            anchorPaneWithPaths.getChildren().remove(tiePath);
            bindingSymbols.remove(nextSound);

            redrawSoundBinding(path, nextSound);
        }
    }

    @Override
    public void onMusicSoundDeleted(Path path, IPlayable musicSound)
    {
        ImageView modifier = modificationSymbols.get(musicSound);
        List<ImageView> views = musicSymbols.get(musicSound);

        if(modifier != null)
        {
            modificationSymbols.remove(modifier);
            anchorPaneWithPaths.getChildren().remove(modifier);
        }

        musicSymbols.remove(views);
        for(ImageView view : views)
            anchorPaneWithPaths.getChildren().remove(view);

        DeleteSoundMenuItem(path, musicSound);

        onMusicSoundClearSelection();

        var tiePath = bindingSymbols.get(musicSound);
        if(tiePath != null)
        {
            bindingSymbols.remove(tiePath);
            anchorPaneWithPaths.getChildren().remove(tiePath);
        }

        if(musicSound.isTiedWithAnotherSound())
        {
            tiePath = bindingSymbols.get(musicSound.getNextTiedSound());
            bindingSymbols.remove(tiePath);
            anchorPaneWithPaths.getChildren().remove(tiePath);
        }
    }

    public void changeTextOfPlayMenuItem(String newText)
    {
        playMenuItem.setText(newText);
    }

    //region visual bar
    public void setVisualBarPosition(double newX)
    {
        visualBarCanvas.setLayoutX(newX);
    }

    public void initializeVisualBar()
    {
        visualBarCanvas.setHeight(anchorPaneWithPaths.getHeight());
        visualBarCanvas.setLayoutX(GlobalSettings.getStartXofAreaWhereInsertingNotesIsLegal());
        visualBarCanvas.setLayoutY(0);
        visualBarCanvas.getGraphicsContext2D().fillRect(0, 0, visualBarCanvas.getWidth(), visualBarCanvas.getHeight());
        visualBarCanvas.setHeight(anchorPaneWithPaths.getHeight());
    }
    public void removeVisualBar()
    {
        visualBarCanvas.setHeight(0);
        visualBarCanvas.setLayoutX(GlobalSettings.getStartXofAreaWhereInsertingNotesIsLegal());
    }
    //endregion

    public void printSongToPNGFile(String destinationPath) throws FileNotFoundException
    {
        Canvas canvasToPrint = new Canvas(canvasCurrentWidth, GlobalSettings.Height * canvasMap.size());
        Canvas currentCanvas;

        var gc = canvasToPrint.getGraphicsContext2D();
        int i = 0;
        double moveX = GlobalSettings.Height * canvasMap.size() * -1;

        List<ImageView> soundViews;
        ImageView modifiersView;
        javafx.scene.shape.Path bindingViewPath;

        for(Path path : modelManager.getPaths()) {
            currentCanvas = canvasMap.get(path);

            drawFiveLinesOfNewPath(currentCanvas, gc, 0, i);
            drawPathClef(path, gc, moveX, i);

            for(IPlayable sound : path.getSounds())
            {
                soundViews = musicSymbols.get(sound);

                for(ImageView view : soundViews)
                    gc.drawImage(view.getImage(), view.getLayoutX() + moveX, view.getLayoutY());

                modifiersView = modificationSymbols.get(sound);
                if(modifiersView != null)
                    gc.drawImage(modifiersView.getImage(), modifiersView.getLayoutX() + moveX, modifiersView.getLayoutY());

                bindingViewPath = bindingSymbols.get(sound);
                if(bindingViewPath != null)
                {
                    var elementsOfPath = bindingViewPath.getElements();

                    MoveTo moveToPoint = (MoveTo)elementsOfPath.get(0);
                    CubicCurveTo curve = (CubicCurveTo) elementsOfPath.get(1);

                    gc.setLineWidth(5 * GlobalSettings.strokeLineBorderWidth);
                    gc.beginPath();
                    gc.moveTo(moveToPoint.getX() + moveX, moveToPoint.getY());
                    gc.bezierCurveTo(
                            curve.getControlX1() + moveX,
                            curve.getControlY1(),
                            curve.getControlX2() + moveX,
                            curve.getControlY2(),
                            curve.getX() + moveX,
                            curve.getY()
                    );

                    gc.stroke();
                    gc.closePath();
                    gc.setLineWidth(GlobalSettings.strokeLineBorderWidth);
                }
            }

            i++;
        }

        WritableImage image = canvasToPrint.snapshot(null, null);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", new File(destinationPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("Song printed to %s", destinationPath));
    }
}
