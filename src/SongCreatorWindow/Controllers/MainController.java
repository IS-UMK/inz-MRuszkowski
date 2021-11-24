package SongCreatorWindow.Controllers;

import MainWindow.MainWindow;
import SongCreatorWindow.Model.ModelManager;
import SongCreatorWindow.View.ViewManagerModelChangesHandling;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.jfugue.player.Player;

import java.io.File;
import java.io.IOException;

import static SongCreatorWindow.Model.GlobalSettings.*;

public class MainController
{
    //TODO: Wyeliminować wzorzec "kula błota"

    @FXML
    BorderPane workSpace;
    @FXML
    AnchorPane anchorPaneWithPaths;

    //Data structures for program logic (Model)
    @FXML
    Menu selectPathMenuItem;


    //For playing music
    @FXML
    MenuItem playMenuItem;

    ModelManager modelManager;
    ViewManagerModelChangesHandling viewManager;

    public MainController()
    {
        //TODO: Można to zrobić bardziej elegancko?
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    modelManager = new ModelManager();
                    viewManager = new ViewManagerModelChangesHandling(modelManager, anchorPaneWithPaths, selectPathMenuItem, playMenuItem);
                    modelManager.addListener(viewManager);
                }
            },
            1000
        );
    }

    //region Project
    public void SaveProjectToFile(ActionEvent actionEvent)
    {
        if(modelManager.getProjectName() == null)
            if(!projectSavingPreProcess())
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
        if(!projectSavingPreProcess())
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
    private boolean projectSavingPreProcess()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Project Destination");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                String.format("Music creator file (*.%s)", projectsExtensions),
                String.format("*.%s", projectsExtensions)
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
                String.format("Music creator file (*.%s)", projectsExtensions),
                String.format("*.%s", projectsExtensions)
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
        alert.showAndWait();

        modelManager.replaceExistingModel(loadedProject);
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

        //get user response and create path
        String pathName = window.getResult();
        if(pathName == null)
            return;

        modelManager.createPath(pathName);
    }

    public void RenameSelected(ActionEvent actionEvent)
    {
        if(modelManager.getSelectedPath() == null)
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
        if(modelManager.getSelectedPath() == null)
            return;

        modelManager.clearSelectionOfPath();
    }

    public void DeleteSelected(ActionEvent actionEvent)
    {
        if(modelManager.getSelectedPath() == null)
        {
            var alert = new Alert(Alert.AlertType.ERROR, "None path is selected");
            alert.showAndWait();
            return;
        }

        modelManager.removeSelectedPath();
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
            player.play(modelManager.extractEntireMusic());
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

        if(x > numberOfPropertySquaresInPath * Height + musicKeyWidth)
        {
            int pathIndex = (int)y / (int)Height;
            System.out.println(String.format("The selected path where to insert note: %d", pathIndex));
            int insertX = ((int)(x - 45) / 10) * 10;
            int insertY = ((int)((y - 80 - Height * pathIndex) / 10)) * 10;

            modelManager.addNote(pathIndex, insertX, insertY);
        }
    }
    //endregion
}
