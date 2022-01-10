package MainWindow;

import SongCreatorWindow.Controllers.MainController;
import SongCreatorWindow.Model.GlobalSettings;
import javafx.application.Platform;
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
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Controller
{
    //list with project
    @FXML
    private ListView<String> listViewWithSongProjects;
    private HashMap<String, String> listModelWithSongProjects = new HashMap<String, String>();

    //When music is played from MainWindow
    @FXML
    Button playSongDirectlyFromFileButton;
    String nameOfFile;
    Player player = new Player();
    ManagedPlayer managedPlayer = player.getManagedPlayer();

    public Controller()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                LoadListWithProjects();
            }
        });

        managedPlayer.addManagedPlayerListener(new ManagedPlayerListener() {
            @Override
            public void onStarted(Sequence sequence) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        playSongDirectlyFromFileButton.setText("Stop Playing");
                        Logger.appendTextToLogLabel(logLabel, String.format("Playing song from file %s", nameOfFile));
                    }
                });
            }

            @Override
            public void onFinished() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        playSongDirectlyFromFileButton.setText("Play Song from MIDI File");
                        Logger.appendTextToLogLabel(logLabel, String.format("Playing song from file %s has been finished", nameOfFile));
                    }
                });
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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        playSongDirectlyFromFileButton.setText("Play Song from MIDI File");
                    }
                });
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
                nameOfFile = file.getName();

                Pattern patternFromFile = MidiFileManager.loadPatternFromMidi(file);
                new Thread(() -> {
                    if (!managedPlayer.isPlaying()) {
                        player.play(patternFromFile);

                        while (!this.managedPlayer.isFinished()) {
                            try {
                                Thread.sleep(20L);
                            } catch (InterruptedException var3) {
                            }
                        }
                    }
                }).start();

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

        Scene scene = new Scene(root, 1280, 800);

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
                    String.format("Music creator file (*.%s)", GlobalSettings.projectsExtensions),
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

            SaveListWithProjects();
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

    public void SaveListWithProjects()
    {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(GlobalSettings.fileNameWithProjectList));

            stream.writeObject(listModelWithSongProjects);
            stream.flush();
            stream.close();

            var items = listViewWithSongProjects.getItems();
            List<String> toFile = new LinkedList<>();//String[items.size()];
            int i = 0;
            for(var item : items)
                toFile.add(item);

            stream = new ObjectOutputStream(new FileOutputStream(GlobalSettings.fileNameWithProjectListView));
            stream.writeObject(toFile);
            stream.flush();
            stream.close();

            System.out.println("Current values of list with projects has been saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LoadListWithProjects()
    {
        boolean modelLoaded = false;

        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(GlobalSettings.fileNameWithProjectList));

            listModelWithSongProjects = (HashMap<String, String>)stream.readObject();
            stream.close();
            modelLoaded = true;

            stream = new ObjectInputStream(new FileInputStream(GlobalSettings.fileNameWithProjectListView));

            var items = (List<String>)stream.readObject();
            stream.close();

            for(String item : items)
                listViewWithSongProjects.getItems().add(item);
        } catch (IOException e) {
            if(modelLoaded)
            {
                for(String key : listModelWithSongProjects.keySet())
                    listViewWithSongProjects.getItems().add(key);
            }
        } catch (ClassNotFoundException e) {
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
            SaveListWithProjects();
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

        SaveListWithProjects();

        Logger.appendTextToLogLabel(logLabel,String.format("Project %s moved %s on list", selectedItem, (movement < 0) ? "up" : "down"));
    }

    public void ExitFromApp(javafx.event.ActionEvent actionEvent)
    {
        System.exit(0);
    }
}
