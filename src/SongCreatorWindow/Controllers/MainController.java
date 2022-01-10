package SongCreatorWindow.Controllers;

import MainWindow.MainWindow;
import SongCreatorWindow.Model.Core.GlobalLoaderDTO;
import SongCreatorWindow.Model.Core.MusicClefSelection;
import SongCreatorWindow.Model.Exceptions.CannotAddAnotherPathException;
import SongCreatorWindow.Model.GlobalSettings;
import SongCreatorWindow.Model.ModelManager;
import SongCreatorWindow.Views.ViewManagerModelChangesHandling;
import SongCreatorWindow.Views.ViewMusicSymbolsSelectionHandling;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.jfugue.player.ManagedPlayer;
import org.jfugue.player.ManagedPlayerListener;
import org.jfugue.player.Player;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

import static SongCreatorWindow.Model.GlobalSettings.*;

public class MainController
{
    @FXML
    ScrollPane scrollPaneWithPaths;
    @FXML
    AnchorPane anchorPaneWithPaths;
    @FXML
    AnchorPane anchorPaneWithNotesAndAccordsSelection;
    @FXML
    VBox vBoxWithNotesAndAccordsProperties;
    @FXML
    VBox vBoxPaneWithCurrentlySelectedNoteOrAccordProperties;

    //Data structures for program logic (Model)
    @FXML
    Menu selectPathMenuItem;
    @FXML
    Menu selectSoundMenuItem;

    //For playing music
    @FXML
    MenuItem playMenuItem;

    ModelManager modelManager;
    ViewManagerModelChangesHandling viewManager;
    ViewMusicSymbolsSelectionHandling musicSymbolsViewManager;
    MusicSymbolsController musicSymbolsController;

    GlobalLoaderDTO loader = GlobalLoaderDTO.getInstance();

    public void initLoader(String filePath)
    {
        loader.setLoadingData(filePath);
    }

    public MainController()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                scrollPaneWithPaths.setPannable(true);
                scrollPaneWithPaths.setFocusTraversable(true);

                musicSymbolsViewManager =
                        new ViewMusicSymbolsSelectionHandling(
                                anchorPaneWithNotesAndAccordsSelection,
                                vBoxWithNotesAndAccordsProperties,
                                vBoxPaneWithCurrentlySelectedNoteOrAccordProperties
                        );

                modelManager = new ModelManager();

                viewManager = new ViewManagerModelChangesHandling(modelManager, anchorPaneWithPaths, selectPathMenuItem, selectSoundMenuItem, playMenuItem);
                modelManager.addListener(viewManager);

                musicSymbolsController = new MusicSymbolsController(viewManager, musicSymbolsViewManager);
                modelManager.addListener(musicSymbolsController);
                viewManager.addListener(musicSymbolsController);


                if(loader.isHereProjectToLoad()) {
                    try {
                        modelManager.replaceExistingModel(
                                ModelManager.loadProject(
                                        loader.getProjectDestinationOnce()
                                )
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //region Project
    public void SaveProjectToFile(ActionEvent actionEvent)
    {
        if(modelManager.getProjectName() == null)
            if(!projectSavingPreProcess("Choose Project Destination", "Music creator file", projectsExtensions))
                return;

        try {
            ModelManager.saveProject(modelManager);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public void SaveProjectToFileWithDifferentName(ActionEvent actionEvent)
    {
        if(!projectSavingPreProcess("Choose Project Destination", "Music creator file", projectsExtensions))
            return;

        try {
            ModelManager.saveProject(modelManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asks user for project name and destination
     * @return True if preprocess has finished successfully
     */
    private boolean projectSavingPreProcess(String title, String fileExtensionMessage, String fileExtension)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                String.format("%s (*.%s)", fileExtensionMessage, fileExtension),
                String.format("*.%s", fileExtension)
        );
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(MainWindow.StageToDeleteLater);
        if(file == null)
            return false;

        String projectName = file.getName();
        if(projectName == null)
            return false;

        modelManager.setProjectName(projectName);
        modelManager.setProjectDestination(file.getPath());

        return true;
    }

    public void LoadProjectFromFile(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Project File");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                String.format("Music creator file (*.%s)", GlobalSettings.projectsExtensions, GlobalSettings.projectsExtensions),
                String.format("*.%s", GlobalSettings.projectsExtensions)
        );
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(MainWindow.StageToDeleteLater);

        if(file == null)
            return;

        ModelManager loadedProject = null;

        try {
            loadedProject = ModelManager.loadProject(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(loadedProject == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are You sure to load project? Current work will be lost if is not saved.");

        if(alert.showAndWait().get() != ButtonType.OK)
            return;

        modelManager.replaceExistingModel(loadedProject);
    }

    public void ExportProjectToMIDIFile(ActionEvent actionEvent)
    {
        if(!projectSavingPreProcess("Choose Export Destination", "Midi file", midiExtension))
            return;

        try {
            ModelManager.exportProjectToMIDI(modelManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ImportProjectFromMIDIFile(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Midi File");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                String.format("Midi file (*.%s)", GlobalSettings.midiExtension),
                String.format("*.%s", GlobalSettings.midiExtension)
        );
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(MainWindow.StageToDeleteLater);

        if(file == null)
            return;

        ModelManager loadedProject = null;

        try {
            loadedProject = ModelManager.importProjectFromMIDI(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

        if(loadedProject == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are You sure to load project? Current work will be lost if is not saved.");

        if(alert.showAndWait().get() != ButtonType.OK)
            return;

        modelManager.replaceExistingModel(loadedProject);
    }
    //endregion

    //region Paths
    public void InsertNewPath(ActionEvent actionEvent)
    {
        //Ask user for properties of new path
        TextInputDialog window = new TextInputDialog("Path name");
        window.setHeaderText("Enter name of new path:");
        window.showAndWait();

        //get user response and create path
        String pathName = window.getResult();
        if(pathName == null)
            return;

        try {
            modelManager.createPath(pathName);
        } catch (CannotAddAnotherPathException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, e.getMessage());
            alert.showAndWait();
        }
    }

    public void DuplicateSelectedPath(ActionEvent actionEvent)
    {
        if(!checkPathSelection())
            return;

        try {
            modelManager.duplicateSelectedPath();
        } catch (CannotAddAnotherPathException e) {
            e.printStackTrace();
        }
    }

    public void RenameSelected(ActionEvent actionEvent)
    {
        if(!checkPathSelection())
            return;

        //ask user for new name
        TextInputDialog window = new TextInputDialog("Changing path name");
        window.setHeaderText("Enter name of existing path:");
        window.showAndWait();

        //get user choice
        String newName = window.getResult();
        modelManager.renameSelectedPathName(newName);
    }

    public void UnselectPath(ActionEvent actionEvent)
    {
        if(!checkPathSelection())
            return;

        modelManager.clearSelectionOfPath();
    }

    public void DeleteSelected(ActionEvent actionEvent)
    {
        if(!checkPathSelection())
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are You sure to delete path?");

        if(alert.showAndWait().get() != ButtonType.OK)
            return;

        modelManager.removeSelectedPath();
    }

    public void DeleteAllPaths(ActionEvent actionEvent)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are You sure to delete ALL path?");

        if(alert.showAndWait().get() != ButtonType.OK)
            return;

        modelManager.clearModel();
    }

    public void RecordPath(ActionEvent actionEvent)
    {
        try
        {
            modelManager.recordPath();
        }
        catch (CannotAddAnotherPathException e)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, e.getMessage());
            alert.showAndWait();
        }
        catch(NoSuchElementException e)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "There is no MIDI device plugged in");
            alert.showAndWait();
        }
    }
    //endregion

    //region Music Symbols
    public void ChangePathClefToViolin(ActionEvent actionEvent)
    {
        if(!checkPathSelection())
            return;

        modelManager.changeMusicClefOfSelectedPath(MusicClefSelection.ViolinClef);
    }

    public void ChangePathClefToBass(ActionEvent actionEvent)
    {
        if(!checkPathSelection())
            return;

        modelManager.changeMusicClefOfSelectedPath(MusicClefSelection.BassClef);
    }

    public void ChangePathClefToAlto(ActionEvent actionEvent)
    {
        if(!checkPathSelection())
            return;

        modelManager.changeMusicClefOfSelectedPath(MusicClefSelection.AltoClef);
    }

    private boolean checkPathSelection()
    {
        if(modelManager.getSelectedPath() == null)
        {
            var alert = new Alert(Alert.AlertType.ERROR, "None path is selected");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    public void selectMusicSymbol(MouseEvent mouseEvent)
    {
        musicSymbolsViewManager.drawSelectionOfNote(mouseEvent.getX(), mouseEvent.getY());
    }
    //endregion

    //region Song
    Player player = new Player();
    ManagedPlayer managedPlayer = player.getManagedPlayer();
    ManagedPlayerListener playerListener = new ManagedPlayerListener() {
        @Override
        public void onStarted(Sequence sequence) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    viewManager.changeTextOfPlayMenuItem("Pause");
                    new Thread(() ->
                    {
                        viewManager.initializeVisualBar();
                        while(!managedPlayer.isFinished())
                        {
                            if(managedPlayer.isPlaying())
                            {
                                viewManager.setVisualBarPosition(GlobalSettings.getStartXofAreaWhereInsertingNotesIsLegal() + managedPlayer.getTickPosition() / GlobalSettings.constBarFactor_WidthPerTick);
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
            });
        }

        @Override
        public void onFinished() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    viewManager.changeTextOfPlayMenuItem("Play");
                    viewManager.removeVisualBar();
                }
            });
        }

        @Override
        public void onPaused() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    viewManager.changeTextOfPlayMenuItem("Resume");
                }
            });
        }

        @Override
        public void onResumed() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    viewManager.changeTextOfPlayMenuItem("Pause");
                }
            });
        }

        @Override
        public void onSeek(long l) {

        }

        @Override
        public void onReset() {

        }
    };

    public void PlayAllPathsWithoutMutedOnes(ActionEvent actionEvent)
    {
        new Thread(() -> {
            if(!managedPlayer.isPlaying()){
                if(!managedPlayer.isPaused())
                {
                    player = new Player();
                    managedPlayer = player.getManagedPlayer();
                    managedPlayer.addManagedPlayerListener(playerListener);
                    player.play(modelManager.extractEntireMusic());
                }
                else
                    managedPlayer.resume();
            }
            else{
                managedPlayer.pause();
            }
        }).start();
    }

    public void StopMusic(ActionEvent actionEvent)
    {
        if(managedPlayer.isPlaying())
            managedPlayer.finish();

        if(managedPlayer.isPaused())
        {
            managedPlayer.seek(managedPlayer.getTickLength());
            managedPlayer.resume();
        }
    }

    public void PrintSongToPNGFile(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG File", "*.png"));
        fileChooser.setTitle("Save song to png file");

        File file = fileChooser.showSaveDialog(MainWindow.StageToDeleteLater);

        if(file == null)
            return;

        String path = file.getAbsolutePath();

        try
        {
            viewManager.printSongToPNGFile(path);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region MouseEvents
    public void InsertNoteOnMouseClicked(MouseEvent mouseEvent)
    {
        if(mouseEvent.getButton() == MouseButton.PRIMARY) {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();

            System.out.println(String.format("User Clicked left mouse button at position: X - %f, Y - %f", x, y));

            if (x > GlobalSettings.getStartXofAreaWhereInsertingNotesIsLegal()) {
                int pathIndex = (int) y / (int) Height;
                System.out.println(String.format("The selected path where to insert note: %d", pathIndex));
                int insertX = ((int) (x + GlobalSettings.fixedXPositionOfNotes) / 10) * 10;
                int insertY = ((int) ((y - 80 - Height * pathIndex) / 10)) * 10;
                insertY = insertY > (Height - Height / 20) ?  insertY - (int)Height / 20 : insertY;

                modelManager.addMusicSymbol(pathIndex, insertX, insertY);
            }
        }
    }
    //endregion
}
