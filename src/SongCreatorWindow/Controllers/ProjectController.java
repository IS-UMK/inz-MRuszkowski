package SongCreatorWindow.Controllers;

import MainWindow.MainWindow;
import SongCreatorWindow.Model.GlobalSettings;
import SongCreatorWindow.Model.ModelManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;

import static SongCreatorWindow.Model.GlobalSettings.midiExtension;
import static SongCreatorWindow.Model.GlobalSettings.projectsExtensions;

public class ProjectController
{
    public static void SaveProjectToFile(ActionEvent actionEvent, ModelManager modelManager)
    {
        if(modelManager.getProjectName() == null)
            if(!projectSavingPreProcess(modelManager,"Choose Project Destination", "Music creator file", projectsExtensions))
                return;

        try {
            ModelManager.saveProject(modelManager);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public static void SaveProjectToFileWithDifferentName(ActionEvent actionEvent, ModelManager modelManager)
    {
        if(!projectSavingPreProcess(modelManager, "Choose Project Destination", "Music creator file", projectsExtensions))
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
    private static boolean projectSavingPreProcess(ModelManager modelManager, String title, String fileExtensionMessage, String fileExtension)
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

    public static void LoadProjectFromFile(ActionEvent actionEvent, ModelManager modelManager)
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

    public static void ExportProjectToMIDIFile(ActionEvent actionEvent, ModelManager modelManager)
    {
        if(!projectSavingPreProcess(modelManager,"Choose Export Destination", "Midi file", midiExtension))
            return;

        try {
            ModelManager.exportProjectToMIDI(modelManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ImportProjectFromMIDIFile(ActionEvent actionEvent, ModelManager modelManager)
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
}
