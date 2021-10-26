package MainWindow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.ManagedPlayer;
import org.jfugue.player.Player;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Controller
{
    //list with project
    @FXML
    private ListView<String> listViewWithSongProjects;
    private HashMap<String, String> listModelWithSongProjects = new HashMap<String, String>();

    //Logs in main window
    @FXML
    private Label logLabel;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

    //When music is played from MainWindow
    @FXML
    Button playSongDirectlyFromFileButton;
    Player player = new Player();
    ManagedPlayer managedPlayer = player.getManagedPlayer();

    private void appendTextToLogLabel(String message)
    {
        logLabel.setText(String.format("%s\n%s - %s", logLabel.getText(), message, dtf.format(LocalDateTime.now())));
    }

    //Events when button click occurs below

    public void PlaySongDirectlyFromFile(javafx.event.ActionEvent actionEvent)
    {
        if(!managedPlayer.isPlaying()) {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose Midi File");
                File file = fileChooser.showOpenDialog(MainWindow.StageToDeleteLater);

                Pattern patternFromFile = MidiFileManager.loadPatternFromMidi(file);

                new Thread(() -> {
                    //TODO: Jak wysłać wiadomość z innego wątku?
                    //appendTextToLogLabel(String.format("Playing song %s has started", file.getName()));

                    if (!managedPlayer.isPlaying()) {
                        player.play(patternFromFile);
                    } else {
                        //appendTextToLogLabel(String.format("Cannot play song because there is already different one playing", file.getName()));
                        return;
                    }

                    //appendTextToLogLabel(String.format("Playing song %s has been finished", file.getName()));
                }).start();

                playSongDirectlyFromFileButton.setText("Stop Playing");
            } catch (RuntimeException e) {
                appendTextToLogLabel("Loading song aborted");
                return;
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
                appendTextToLogLabel(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                appendTextToLogLabel(e.getMessage());
            }
        }

        else{
            playSongDirectlyFromFileButton.setText("Play Song from MIDI File");
            managedPlayer.reset();
        }
    }

    public void CreateNewSongProject(javafx.event.ActionEvent actionEvent)
    {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../SongCreatorWindow/SongCreator.fxml"));

            Scene scene = new Scene((Parent)root, 600, 800);

            Stage stage = new Stage();
            stage.setTitle("Song Creator Window");
            stage.setScene(scene);
            stage.show();

            appendTextToLogLabel("New song project has been created");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void EditSongProjectFromList(javafx.event.ActionEvent actionEvent)
    {
        //TODO: przekazać referencję do pliku na podstawie zaznaczonego elementu na liście
        try {
            String selectedItem = listViewWithSongProjects.getSelectionModel().getSelectedItem();
            File file = new File(listModelWithSongProjects.get(selectedItem));

            //TODO: Jak przekazać referencje pliku to nowego okna?
            //plik jest już czytany w nowych oknie

            appendTextToLogLabel(String.format("Project %s has been opened", file.getName()));
        }
        catch (RuntimeException e)
        {
            appendTextToLogLabel("None item is selected to be edited");
        }
    }

    public void OpenSongProjectFromFile(javafx.event.ActionEvent actionEvent)
    {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Project File");
            File file = fileChooser.showOpenDialog(MainWindow.StageToDeleteLater);

            String fileName = file.getName();
            String filePath = file.getPath();

            var res = listModelWithSongProjects.get(fileName);
            if(res != null && res.equals(filePath))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "File is already on list!");
                alert.showAndWait();
                return;
            }

            listModelWithSongProjects.put(fileName, filePath);
            listViewWithSongProjects.getItems().add(fileName);

            //TODO: Jak przekazać referencje pliku to nowego okna?
            //plik jest już czytany w nowych oknie

            appendTextToLogLabel(String.format("Project %s has been opened", fileName));
        }
        catch (RuntimeException e)
        {
            appendTextToLogLabel("Loading project aborted");
            return;
        }
    }

    public void RemoveSongProjectFromList(javafx.event.ActionEvent actionEvent)
    {
        removeItemFromListAndOptionallyFromDisk(false);
    }

    public void DeleteSongProjectFromDisk(javafx.event.ActionEvent actionEvent)
    {
        removeItemFromListAndOptionallyFromDisk(true);

    }

    private void removeItemFromListAndOptionallyFromDisk(boolean removeAlsoFromDisk)
    {
        String userMessage;
        if(removeAlsoFromDisk) userMessage = "Are You sure to delete file?";
        else userMessage = "Are You sure to remove project from list?";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,userMessage);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.NO)
            return;

        try {
            String selectedItem = listViewWithSongProjects.getSelectionModel().getSelectedItem();
            listViewWithSongProjects.getItems().remove(selectedItem);
            appendTextToLogLabel(String.format("Project %s has been removed from list", selectedItem));

            if(removeAlsoFromDisk)
            {
                String path = listModelWithSongProjects.get(selectedItem);

                File file = new File(path);
                if(file.delete())
                {
                    appendTextToLogLabel(String.format("File %s deleted successfully", file.getName()));
                }
                else{
                    appendTextToLogLabel(String.format("Could not delete file %s", file.getName()));
                }
            }

            listModelWithSongProjects.remove(selectedItem);
        }
        catch (RuntimeException e)
        {
            appendTextToLogLabel("None item is selected to be removed");
        }
    }

    public void MoveSongProjectUpOnList(javafx.event.ActionEvent actionEvent)
    {
        movementOnListView(-1);
    }

    public void MoveSongProjectDownOnList(javafx.event.ActionEvent actionEvent)
    {
        movementOnListView(1);
    }

    private void movementOnListView(int movement)
    {
        String selectedItem = listViewWithSongProjects.getSelectionModel().getSelectedItem();
        var index = listViewWithSongProjects.getItems().indexOf(selectedItem);

        if(index <= 0 && movement < 0)
            return;

        if(index >= listViewWithSongProjects.getItems().stream().count() - 1 && movement > 0)
            return;

        listViewWithSongProjects.getItems().remove(selectedItem);
        listViewWithSongProjects.getItems().add(index + movement,selectedItem);
        appendTextToLogLabel(String.format("Project %s moved %s on list", selectedItem, (movement < 0) ? "up" : "down"));
    }

    public void ExitFromApp(javafx.event.ActionEvent actionEvent)
    {
        System.exit(0);
    }
}
