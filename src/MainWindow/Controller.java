package MainWindow;

import SongCreatorWindow.Controllers.MainController;
import SongCreatorWindow.Model.Core.GlobalLoaderDTO;
import SongCreatorWindow.Model.GlobalSettings;
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
import org.jfugue.player.ManagedPlayerListener;
import org.jfugue.player.Player;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static SongCreatorWindow.Model.GlobalSettings.midiExtension;

public class Controller
{
    //list with project
    @FXML
    private ListView<String> listViewWithSongProjects;
    private HashMap<String, String> listModelWithSongProjects = new HashMap<String, String>();

    //When music is played from MainWindow
    @FXML
    Button playSongDirectlyFromFileButton;
    Player player = new Player();
    ManagedPlayer managedPlayer = player.getManagedPlayer();

    public Controller()
    {
        managedPlayer.addManagedPlayerListener(new ManagedPlayerListener() {
            @Override
            public void onStarted(Sequence sequence) {
                playSongDirectlyFromFileButton.setText("Stop Playing");
            }

            @Override
            public void onFinished() {
                playSongDirectlyFromFileButton.setText("Play Song from MIDI File");
            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onResumed() {

            }

            @Override
            public void onSeek(long l) {

            }

            @Override
            public void onReset() {
                playSongDirectlyFromFileButton.setText("Play Song from MIDI File");
            }
        });
    }

    //Logs in main window
    @FXML
    Label logLabel;

    private class Logger {
        static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

        public static void appendTextToLogLabel(Label logLabel, String message) {
            logLabel.setText(String.format("%s\n%s - %s", logLabel.getText(), message, dtf.format(LocalDateTime.now())));
        }
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

                var playing = new PlaySongThread(
                        player,
                        managedPlayer,
                        patternFromFile
                );
                Thread music = new Thread(playing);
                music.start();

                /*new Thread(() -> {
                    //TODO: Jak wysłać wiadomość z innego wątku? Czy można uruchamiać funkcje z innego wątku?
                    //appendTextToLogLabel(String.format("Playing song %s has started", file.getName()));

                    if (!managedPlayer.isPlaying()) {
                        player.play(patternFromFile);

                        while(!this.managedPlayer.isFinished()) {
                            try {
                                Thread.sleep(20L);
                            } catch (InterruptedException var3) {
                            }
                        }

                        //playSongDirectlyFromFileButton.setText("Play Song from MIDI File");
                    } else {
                        //appendTextToLogLabel(String.format("Cannot play song because there is already different one playing", file.getName()));
                        return;
                    }

                    //appendTextToLogLabel(String.format("Playing song %s has been finished", file.getName()));
                }).start();*/

                //playSongDirectlyFromFileButton.setText("Stop Playing");
            } catch (RuntimeException e) {
                Logger.appendTextToLogLabel(logLabel,"Loading song aborted");
                return;
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
                Logger.appendTextToLogLabel(logLabel, e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Logger.appendTextToLogLabel(logLabel, e.getMessage());
            }
        }

        else{
            playSongDirectlyFromFileButton.setText("Play Song from MIDI File");
            managedPlayer.reset();
        }
    }

    private void OpenSongCreatorWindow() throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("../SongCreatorWindow/SongCreator.fxml"));

        Scene scene = new Scene(root, 800, 600);

        Stage stage = new Stage();
        stage.setTitle("Song Creator Window");
        stage.setScene(scene);
        stage.show();
    }

    private void OpenSongCreatorWindow(String pathToProject) throws IOException
    {
        FXMLLoader root = new FXMLLoader(getClass().getResource("../SongCreatorWindow/SongCreator.fxml"));

        Scene scene = new Scene(root.load(), 800, 600);

        MainController controller = root.getController();
        controller.initLoader(pathToProject);

        Stage stage = new Stage();
        stage.setTitle("Song Creator Window");
        stage.setScene(scene);
        stage.show();
    }

    public void CreateNewSongProject(javafx.event.ActionEvent actionEvent)
    {
        try {
            OpenSongCreatorWindow();

            Logger.appendTextToLogLabel(logLabel,"New song project has been created");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void EditSongProjectFromList(javafx.event.ActionEvent actionEvent)
    {
        try {
            String selectedItem = listViewWithSongProjects.getSelectionModel().getSelectedItem();
            //File file = new File(listModelWithSongProjects.get(selectedItem));
            String destinationPath = listModelWithSongProjects.get(selectedItem);

            OpenSongCreatorWindow(destinationPath);

            Logger.appendTextToLogLabel(logLabel,String.format("Project %s has been opened", selectedItem));
        }
        catch (RuntimeException e)
        {
            Logger.appendTextToLogLabel(logLabel,"None item is selected to be edited");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void OpenSongProjectFromFile(javafx.event.ActionEvent actionEvent)
    {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Project File");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    String.format("Midi file (*.%s)", GlobalSettings.projectsExtensions),
                    String.format("*.%s", GlobalSettings.projectsExtensions)
            );
            fileChooser.getExtensionFilters().add(extFilter);

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

            OpenSongCreatorWindow(filePath);

            Logger.appendTextToLogLabel(logLabel,String.format("Project %s has been opened", fileName));
        }
        catch (RuntimeException e)
        {
            Logger.appendTextToLogLabel(logLabel,"Loading project aborted");
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
            Logger.appendTextToLogLabel(logLabel,String.format("Project %s has been removed from list", selectedItem));

            if(removeAlsoFromDisk)
            {
                String path = listModelWithSongProjects.get(selectedItem);

                File file = new File(path);
                if(file.delete())
                {
                    Logger.appendTextToLogLabel(logLabel,String.format("File %s deleted successfully", file.getName()));
                }
                else{
                    Logger.appendTextToLogLabel(logLabel,String.format("Could not delete file %s", file.getName()));
                }
            }

            listModelWithSongProjects.remove(selectedItem);
        }
        catch (RuntimeException e)
        {
            Logger.appendTextToLogLabel(logLabel,"None item is selected to be removed");
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
        Logger.appendTextToLogLabel(logLabel,String.format("Project %s moved %s on list", selectedItem, (movement < 0) ? "up" : "down"));
    }

    public void ExitFromApp(javafx.event.ActionEvent actionEvent)
    {
        System.exit(0);
    }
}
