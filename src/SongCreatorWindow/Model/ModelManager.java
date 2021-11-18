package SongCreatorWindow.Model;

import SongCreatorWindow.Model.Core.IPlayable;
import SongCreatorWindow.Model.Core.Note;
import SongCreatorWindow.Model.Core.NoteToNumericValue;
import SongCreatorWindow.Model.Core.Path;
import SongCreatorWindow.Model.Events.IMusicEvent;
import SongCreatorWindow.Model.Events.INoteEvent;
import SongCreatorWindow.Model.Events.IPathEvent;

import java.io.File;
import java.util.*;

public class ModelManager
{
    //Paths
    List<Path> musicPaths = new LinkedList<Path>();
    Path selectedPath = null;

    //events
    public List<IPathEvent> pathListeners = new LinkedList<>();
    public List<INoteEvent> noteListeners = new LinkedList<>();

    public ModelManager(String pathToProject) throws IllegalArgumentException, NoSuchElementException
    {
        /*if(pathToProject != null) {
            String fileExtension = getExtensionByString(pathToProject).get();

            if (fileExtension != GlobalSettings.projectsExtensions)
                throw new IllegalArgumentException("File extension does not match.");

            File file = new File(pathToProject);
        }*/
    }

    //region Notes
    public void addNote(int pathIndex, int insertX, int insertY)
    {
        int base = NoteToNumericValue.Get_Octave_5_sound_G();
        int move_sound_by = (insertY - 40) / 10;

        Note note = Note.CreateNote(base - move_sound_by, 'q');
        note.setTimeX(insertX);
        note.setNoteHeight(insertY);

        Path path = musicPaths.get(pathIndex);
        path.addSound(note);

        fireOnNoteAdded(path, note);
    }
    //endregion

    //region Paths Methods

    /**
     * Get path that is selected. Return null if none is chosen
     * @return Path or null
     */
    public Path getSelectedPath()
    {
        return selectedPath;
    }

    /**
     * Set path that will be now selected
     * @param path Chosen Path
     */
    public void setSelectedPath(Path path)
    {
        selectedPath = path;
    }

    /**
     * Set selection of Path to None (null)
     */
    public void clearSelectionOfPath()
    {
        selectedPath = null;
        fireOnPathClearSelection();
    }

    /**
     * Find out what is the index of path previously selected
     * @return index of selected path
     */
    public int getIndexOfSelectedPath()
    {
        return musicPaths.indexOf(selectedPath);
    }

    /**
     * Create new path according to user typed values
     * @param pathName
     */
    public void createPath(String pathName)
    {
        //
        var path = Path.CreatePath(pathName, (byte)musicPaths.size(), "PIANO");
        System.out.println("Created Path:" + path.toString());

        musicPaths.add(path);
        fireOnCreatedPathEvent(path);

        System.out.println(String.format("User inserted new path of name - %s", pathName));
    }

    public void renameSelectedPathName(String newName)
    {
        selectedPath.setName(newName);

        fireOnPathNameRenamed(selectedPath);

        System.out.println(String.format("Changing path name from %s to %s", selectedPath.getName(), newName));
    }

    public void removeSelectedPath()
    {
        musicPaths.remove(selectedPath);

        System.out.println(String.format("Deleting path %s", selectedPath.getName()));

        fireOnPathDeleted(selectedPath);
    }
    //endregion

    //region Music
    public String extractEntireMusic()
    {
        var musicString = new StringBuilder();

        for(Path s : musicPaths)
            musicString.append(s.getExtractedMusic());

        System.out.println(String.format("Built music string - %s", musicString));

        return musicString.toString();
    }
    //endregion

    //region Events/Listeners Methods
    public void addListener(IMusicEvent listener)
    {
        if(listener instanceof IPathEvent)
            pathListeners.add((IPathEvent) listener);
        if(listener instanceof INoteEvent)
            noteListeners.add((INoteEvent) listener);
    }

    private void fireOnNoteAdded(Path path, Note note)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            INoteEvent pathEvent = (INoteEvent) iterator.next();
            pathEvent.onNoteAdded(path, note);
        }
    }

    private void fireOnCreatedPathEvent(Path path)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            IPathEvent pathEvent = (IPathEvent) iterator.next();
            pathEvent.onPathCreated(path);
        }
    }

    private void fireOnPathNameRenamed(Path path)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            IPathEvent pathEvent = (IPathEvent) iterator.next();
            pathEvent.onPathNameRenamed(path);
        }
    }

    private void fireOnPathClearSelection()
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            IPathEvent pathEvent = (IPathEvent) iterator.next();
            pathEvent.onPathClearSelection();
        }
    }

    private void fireOnPathDeleted(Path path)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            IPathEvent pathEvent = (IPathEvent) iterator.next();
            pathEvent.onPathDeleted(path);
        }
    }
    //endregion

    private Optional<String> getExtensionByString(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}