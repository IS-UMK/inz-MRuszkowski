package SongCreatorWindow.Model;

import SongCreatorWindow.Model.Core.*;
import SongCreatorWindow.Model.Events.IModelEvent;
import SongCreatorWindow.Model.Events.IMusicEvent;
import SongCreatorWindow.Model.Events.INoteEvent;
import SongCreatorWindow.Model.Events.IPathEvent;
import SongCreatorWindow.Model.Exceptions.CannotAddAnotherPathException;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;

import javax.sound.midi.InvalidMidiDataException;
import java.io.*;
import java.util.*;

import static SongCreatorWindow.Model.GlobalSettings.*;

public class ModelManager implements Serializable
{
    private static final long serialVersionUID = 8538391531267863794L;

    String projectName = null;
    public void setProjectName(String name) { projectName = name; }
    public String getProjectName() { return projectName; }

    String projectDestination = null;
    public void setProjectDestination(String name) { projectDestination = name; }
    public String getProjectDestination() { return projectDestination; }

    MusicKeySelection selectedDefaultKey = GlobalSettings.defaultMusicKey;
    public void setDefaultMusicKeySelection(MusicKeySelection musicKey) { selectedDefaultKey = musicKey; }
    public MusicKeySelection getDefaultMusicKeySelection() { return selectedDefaultKey; }
    private int getBasePointSound()
    {
        int base = -1;

        switch (selectedDefaultKey)
        {
            case ViolinKey:
                base = NoteToNumericValue.Get_Octave_5_sound_G(); // 67 - G5
                break;
            case BassKey:
                base = NoteToNumericValue.Get_Octave_4_sound_F(); // 53 - F4
                break;
            case AltoKey:
                base = NoteToNumericValue.Get_Octave_5_sound_C(); // 60 - C5
                break;
        }

        return base;
    }

    //Paths
    List<Path> musicPaths = new LinkedList<Path>();
    public int getTheLatestTimeX()
    {
        int timeX = 0;

        for(Path path : musicPaths)
            for(IPlayable sound: path.getSounds())
                if(sound.getTimeX() > timeX)
                    timeX = sound.getTimeX();

        return timeX;
    }
    Path selectedPath = null;

    /**
     * Get music path from model copied
     * @return Copy of paths
     */
    public List<Path> getPaths() { return new ArrayList<Path>(musicPaths); }

    //events
    transient public List<IPathEvent> pathListeners = new LinkedList<>();
    transient public List<INoteEvent> noteListeners = new LinkedList<>();
    transient public List<IModelEvent> modelListeners = new LinkedList<>();

    public ModelManager() { }

    //region Files - Saving & Loading
    public static void saveProject(ModelManager modelManager) throws IOException
    {
        String fileName = modelManager.getProjectDestination();

        System.out.println(String.format("Saving project destination set to: %s", fileName));

        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(fileName));

        stream.writeObject(modelManager);
        stream.flush();
        stream.close();

        System.out.println(String.format("Project has been saved to file: %s", fileName));
    }

    public static ModelManager loadProject(String pathToProject) throws IOException, ClassNotFoundException
    {
        String fileExtension = getExtensionByString(pathToProject).get();

        if (!fileExtension.equals(GlobalSettings.projectsExtensions))
            throw new IllegalArgumentException("File extension does not match.");

        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(pathToProject));

        ModelManager project = (ModelManager) stream.readObject();
        stream.close();

        System.out.println(String.format("Project %s has been loaded", project.getProjectName()));

        return project;
    }

    public static void exportProjectToMIDI(ModelManager modelManager) throws IOException
    {
        String entireMusic = modelManager.extractEntireMusic();

        String fileName = String.format(modelManager.getProjectDestination(), modelManager.getProjectName());

        //WARNING - Author of JFugue does not close stream here
        MidiFileManager.savePatternToMidi(new Pattern(entireMusic), new File(fileName));

        System.out.println(String.format("Project has been exported to midi file: %s", fileName));
    }

    public static ModelManager importProjectFromMIDI(String pathToProject) throws IOException, InvalidMidiDataException
    {
        Pattern patternFromMidiFile = MidiFileManager.loadPatternFromMidi(new File(pathToProject));

        String musicString = patternFromMidiFile.toString();
        var modelManager = new ModelManager();

        String[] patterns = musicString.split("T[0-9]{1,3}");

        for(int i = 1; i < patterns.length; i++)
            patterns[i] = patterns[i].substring(3).trim();

        byte i = 0;

        int startX;

        NoteSelection saveChoice = GlobalSettings.chosenNote;

        for(String pattern : patterns)
        {
            if(pattern.equals(""))
                continue;

            startX = (int) (numberOfPropertySquaresInPath * Height + musicKeyWidth);

            try {
                modelManager.createPath(String.format("Path %d", i+1));
            } catch (CannotAddAnotherPathException e) {
                System.err.println(e.getMessage());
                return modelManager;
            }

            String[] sounds = pattern.split("I[0-9]{1,3}");

            for(String sound : sounds)
                sound = sound.trim();

            for(String sound : sounds)
            {
                if(sound.equals(""))
                    continue;

                String[] notes = sound.split(" ");

                for(String note: notes)
                {
                    if(note.equals(""))
                        continue;

                    String symbols = note.split("[0-9]")[0];

                    if(symbols.equals("R/") || symbols.equals("@"))
                    {
                        if(symbols.equals("R/")) {
                            symbols = note.split("/")[1];
                        }
                        else symbols =  note.split("@")[1];

                        startX = (int)((Double.parseDouble(symbols) * 2 * GlobalSettings.Height) + GlobalSettings.getStartXofAreaWhereInsertingNotesIsLegal() + GlobalSettings.fixedXPositionOfNotes);
                    }
                    else
                    {
                        String musicSoundLetter = symbols;
                        symbols = note.split(symbols)[1];
                        int octave = Character.getNumericValue(symbols.charAt(0));



                        int noteValue = Note.mapNoteSymbolToNumericalValue(musicSoundLetter, octave);
                        System.out.println(String.format("Note value: %d", noteValue));


                        modelManager.addMusicSymbol(
                                i,
                                startX,
                                (modelManager.getBasePointSound() - noteValue) * 10 + 40
                        );

                        System.out.println(String.format("Note height: %d", ((modelManager.getBasePointSound() - Note.mapNoteSymbolToNumericalValue(musicSoundLetter, octave)) * 10 + 40)));
                    }

                }
            }

            i++;
        }

        GlobalSettings.chosenNote = saveChoice;

        return modelManager;
    }

    public void replaceExistingModel(ModelManager modelManager)
    {
        clearModel();
        fireOnModelLoaded(modelManager.getTheLatestTimeX());

        this.selectedDefaultKey = modelManager.getDefaultMusicKeySelection();

        this.setProjectName(modelManager.getProjectName());
        this.setProjectDestination(modelManager.getProjectDestination());

        for(Path path : modelManager.getPaths())
        {
            try {
                createPath(path.getName(), path.getInstrument(), path.getTempo(), path.getVolume());
            } catch (CannotAddAnotherPathException e) {
                System.err.println(e.getMessage());
                return;
            }

            for(IPlayable sound : path.getSounds())
                addMusicSymbol(path.getVoice(), sound.getTimeX(), sound.getSoundHeight());
        }
    }
    //endregion

    //region Music Symbols
    public void addMusicSymbol(int pathIndex, int insertX, int insertY)
    {
        int base = getBasePointSound();
        int move_sound_by = (insertY - 40) / 10;

        Note note = Note.CreateNote(base - move_sound_by, mapChosenNoteToDuration(GlobalSettings.chosenNote));
        note.setTimeX(insertX);
        note.setSoundHeight(insertY);

        Path path = musicPaths.get(pathIndex);
        path.addSound(note);

        fireOnNoteAdded(path, note);
    }

    private char mapChosenNoteToDuration(NoteSelection noteSelection)
    {
        char duration = 0;

        switch (noteSelection) {
            case WholeNote -> {
                duration = Duration.Whole;
            }
            case HalfNote -> {
                duration = Duration.Half;
            }
            case QuarterNote -> {
                duration = Duration.Quater;
            }
            case EighthNote -> {
                duration = Duration.Eighth;
            }
            case SixteenthNote -> {
                duration = Duration.Sixteenth;
            }
            case ThirtySecondNote -> {
                duration = Duration.Thirty_second;
            }
            case SixtyFourthNote -> {
                duration = Duration.Sixty_fourth;
            }
            case OneHundredTwentyEighthNote -> {
                duration = Duration.One_twenty_eighth;
            }
        }

        return duration;
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
     * Create new path according to user typed values with selected piano as instrument, tempo 120 and volume level 50
     * @param pathName
     */
    public void createPath(String pathName) throws CannotAddAnotherPathException
    {
        createPath(pathName, Instrument.getAllInstruments()[1], 120, (byte)50);
    }

    /**
     * Duplicates selected path
     * @throws CannotAddAnotherPathException
     */
    public void duplicateSelectedPath() throws CannotAddAnotherPathException
    {
        createPath(selectedPath.getName(), selectedPath.getInstrument(), selectedPath.getTempo(), selectedPath.getVolume());

        System.out.println(String.format("Path %s duplicated successfully", selectedPath.getName()));
    }

    /**
     * Create new path according to user typed values with selected instrument
     * @param pathName
     */
    public void createPath(String pathName, String instrument, int tempo, byte volume) throws CannotAddAnotherPathException
    {
        if(musicPaths.size() > 16)
            throw new CannotAddAnotherPathException("Program does not support of handling more than 16 paths at once.");

        var path =
                Path.CreatePath(
                        pathName,
                        (byte)musicPaths.size(),
                        this.selectedDefaultKey,
                        instrument,
                        tempo,
                        volume
                );
        System.out.println("Created Path:" + path.toString());

        musicPaths.add(path);
        fireOnCreatedPathEvent(path);

        System.out.println(String.format("User inserted new path of name - %s", pathName));
    }

    /**
     * Sets the new name of selected path in model
     * @param newName
     */
    public void renameSelectedPathName(String newName)
    {
        selectedPath.setName(newName);

        fireOnPathNameRenamed(selectedPath);

        System.out.println(String.format("Changing path name from %s to %s", selectedPath.getName(), newName));
    }

    /**
     * removes selected path from model
     */
    public void removeSelectedPath()
    {
        musicPaths.remove(selectedPath);

        System.out.println(String.format("Deleting path %s", selectedPath.getName()));

        fireOnPathDeleted(selectedPath);
    }

    public void clearModel()
    {
        //foreach on copied collection due to occurring error - if collection modified during loop, the Exception is thrown
        for(Path path : getPaths())
        {
            this.setSelectedPath(path);
            this.removeSelectedPath();
        }

        clearSelectionOfPath();
    }
    //endregion

    //region Music

    /**
     * Extracts the entire music string out of all paths
     * @return Music String
     */
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

    /**
     * Add listener that react on particular events like IPathEvent, INoteEvent
     * @param listener Inherits after IMusicEvent interface
     */
    public void addListener(IMusicEvent listener)
    {
        if(listener instanceof IPathEvent)
            pathListeners.add((IPathEvent) listener);
        if(listener instanceof INoteEvent)
            noteListeners.add((INoteEvent) listener);
        if(listener instanceof IModelEvent)
            modelListeners.add((IModelEvent) listener);
    }

    public void fireOnModelLoaded(int latestTimeX)
    {
        Iterator iterator = modelListeners.iterator();

        while(iterator.hasNext()) {
            IModelEvent modelEvent = (IModelEvent) iterator.next();
            modelEvent.onModelLoaded(latestTimeX);
        }
    }

    /**
     * Raise event when note is added to particular path
     * @param path
     * @param note
     */
    private void fireOnNoteAdded(Path path, Note note)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            INoteEvent pathEvent = (INoteEvent) iterator.next();
            pathEvent.onMusicSymbolAdded(path, note);
        }
    }

    /**
     * Raise event when new path is created
     * @param path
     */
    private void fireOnCreatedPathEvent(Path path)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            IPathEvent pathEvent = (IPathEvent) iterator.next();
            pathEvent.onPathCreated(path);
        }
    }

    /**
     * Raise event when the selected path has changed name
     * @param path
     */
    private void fireOnPathNameRenamed(Path path)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            IPathEvent pathEvent = (IPathEvent) iterator.next();
            pathEvent.onPathNameRenamed(path);
        }
    }

    /**
     * Raise event when selected path is set to null
     */
    private void fireOnPathClearSelection()
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            IPathEvent pathEvent = (IPathEvent) iterator.next();
            pathEvent.onPathClearSelection();
        }
    }

    /**
     * Raise event when selected path has been deleted
     * @param path
     */
    private void fireOnPathDeleted(Path path)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            IPathEvent pathEvent = (IPathEvent) iterator.next();
            pathEvent.onPathDeleted(path);
        }
    }
    //endregion

    /**
     * Method that checks if loaded file match the required extension used by program
     * @param filename
     * @return Extension of file or empty string packed in container "Optional"
     */
    private static Optional<String> getExtensionByString(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}