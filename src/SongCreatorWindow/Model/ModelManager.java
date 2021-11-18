package SongCreatorWindow.Model;

import SongCreatorWindow.Model.Events.NoteEvent;
import SongCreatorWindow.Model.Events.PathEvent;

import java.io.File;
import java.util.*;

public class ModelManager
{
    //events
    public List<PathEvent> pathListeners = new LinkedList<>();
    public List<NoteEvent> noteListeners = new LinkedList<>();

    private ModelManager(String pathToProject) throws IllegalArgumentException, NoSuchElementException
    {
        if(pathToProject != null) {
            String fileExtension = getExtensionByString(pathToProject).get();

            if (fileExtension != GlobalSettings.projectsExtensions)
                throw new IllegalArgumentException("File extension does not match.");

            File file = new File(pathToProject);
        }
    }

    private Optional<String> getExtensionByString(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}