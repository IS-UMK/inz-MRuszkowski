package SongCreatorWindow.Controllers;

import SongCreatorWindow.Model.GlobalSettings;
import SongCreatorWindow.Model.ModelManager;
import SongCreatorWindow.View.ViewManagerModelChangesHandling;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.jfugue.player.Player;

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
        {
            TextInputDialog window = new TextInputDialog("Project name");
            window.setHeaderText("Enter name project:");
            window.showAndWait();

            String projectName = window.getResult();
            if(projectName == null)
                return;

            modelManager.setProjectName(projectName);
        }

        try {
            ModelManager.saveProject(modelManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveProjectToFileWithDifferentName(ActionEvent actionEvent)
    {
        TextInputDialog window = new TextInputDialog("Project name");
        window.setHeaderText("Enter name project:");
        window.showAndWait();

        String projectName = window.getResult();
        if(projectName == null)
            return;

        modelManager.setProjectName(projectName);

        try {
            ModelManager.saveProject(modelManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
