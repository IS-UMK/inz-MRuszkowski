package SongCreatorWindow.Model;

import SongCreatorWindow.Model.Core.*;
import SongCreatorWindow.Model.Events.IModelEvent;
import SongCreatorWindow.Model.Events.IMusicEvent;
import SongCreatorWindow.Model.Events.IPathEvent;
import SongCreatorWindow.Model.Events.ISoundEvent;
import SongCreatorWindow.Model.Exceptions.CannotAddAnotherPathException;
import org.jfugue.devices.MusicTransmitterToSequence;
import org.jfugue.devtools.MidiDevicePrompt;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import java.io.*;
import java.util.*;

import static SongCreatorWindow.Model.GlobalSettings.*;

public class ModelManager implements Serializable
{
    String projectName = null;
    public void setProjectName(String name) { projectName = name; }
    public String getProjectName() { return projectName; }

    String projectDestination = null;
    public void setProjectDestination(String name) { projectDestination = name; }
    public String getProjectDestination() { return projectDestination; }

    MusicClefSelection selectedDefaultKey = GlobalSettings.defaultMusicClef;
    public void setDefaultMusicKeySelection(MusicClefSelection musicKey) { selectedDefaultKey = musicKey; }
    public MusicClefSelection getDefaultMusicKeySelection() { return selectedDefaultKey; }
    private int getBasePointSound(MusicClefSelection musicClefSelection)
    {
        return switch (musicClefSelection) {
            case ViolinClef -> 67;// G5
            case BassClef -> 53;// F4
            case AltoClef -> 60; // C5
        };
    }
    private String getCalculatedSoundValue(int insertY, MusicClefSelection musicClefSelection)
    {
        int base = getBasePointSound(musicClefSelection);
        int move_sound_by = switch(musicClefSelection){
            case ViolinClef -> (insertY - 40) / 10;
            case BassClef -> insertY / 10;
            case AltoClef -> (insertY - 20) / 10;
        };

        List<Integer> numericalNoNFlatSounds = Note.getNonFlatSoundNumericalValues();

        int numericalValueOfNote = numericalNoNFlatSounds.get(numericalNoNFlatSounds.indexOf(base) - move_sound_by);

        return Note.mapNumericalValueOfNoteToSymbols(numericalValueOfNote);
    }

    //Paths
    List<Path> musicPaths = new LinkedList<>();
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
    public List<Path> getPaths() { return new ArrayList<>(musicPaths); }

    //events
    transient public List<IPathEvent> pathListeners = new LinkedList<>();
    transient public List<ISoundEvent> noteListeners = new LinkedList<>();
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

    /**
     * Method that interprets MIDI file as a .mrinz music project. Method will NEVER return the same model due to errors while reading MIDI files.
     * Model will be only similar and the original is not possible to restore. Time when notes and accords occurs are often confused and sometimes even their order.
     * Music Clef selection is unknown so user has to choose clef manually.
     * @param pathToProject Destination of MIDI file
     * @return ModelManager that represents similar music written in MIDI file
     * @throws IOException When file is missing
     * @throws InvalidMidiDataException Midi exception
     */
    public static ModelManager importProjectFromMIDI(String pathToProject) throws IOException, InvalidMidiDataException
    {
        Pattern patternFromMidiFile = MidiFileManager.loadPatternFromMidi(new File(pathToProject));

        String musicString = patternFromMidiFile.toString();
        var modelManager = new ModelManager();

        List<String> patterns = new LinkedList<>();
        String tmpPattern;
        for(String pattern : musicString.split("V[0-9]{1,2}")) {
            tmpPattern = pattern.replaceAll("T[0-9]{1,3}", "").strip();
            if (!tmpPattern.equals(""))
                patterns.add(tmpPattern);
        }

        byte i = 0;

        int startX, octave, noteValue, index, baseIndex, insertY;
        double parsedValue;
        IPlayable lastSound;
        SoundModification modification = SoundModification.None;

        char saveChoice = GlobalSettings.chosenNote;

        List<Integer> nonFlatSounds = Note.getNonFlatSoundNumericalValues();

        for(String pattern : patterns)
        {
            parsedValue = 0;

            startX = (int) (numberOfPropertySquaresInPath * Height + musicClefWidth);

            String[] sounds = pattern.split("I[0-9]{1,3}");
            sounds[0] = pattern.replace(sounds[1], "");

            GlobalSettings.InstrumentChoice = Integer.parseInt(sounds[0].substring(1));

            try {
                modelManager.createPath(String.format("Path %d", i+1));
            } catch (CannotAddAnotherPathException e) {
                System.err.println(e.getMessage());
                return modelManager;
            }

            for(String sound : sounds)
            {
                if(sound.charAt(0) == 'I')
                    continue;

                String[] notes = sound.split(" ");

                for(String note: notes)
                {
                    if(note.equals(""))
                        continue;

                    String symbols = note.split("[0-9]")[0];

                    if(symbols.charAt(0) == 'R' || symbols.charAt(0) == '@')
                    {
                        if(symbols.charAt(0) == 'R') {
                            if(symbols.charAt(1) != '/'){
                                symbols = symbols.substring(1);

                                int j;
                                for(j = 0; j < symbols.length(); j++)
                                {
                                    switch (symbols.charAt(j)) {
                                        case 'w' -> parsedValue += 1;
                                        case 'h' -> parsedValue += 0.5;
                                        case 'q' -> parsedValue += 0.25;
                                        case 'i' -> parsedValue += 0.125;
                                        case 's' -> parsedValue += 0.0625;
                                        case 't' -> parsedValue += 0.03125;
                                        case 'x' -> parsedValue += 0.015625;
                                        case 'o' -> parsedValue += 0.0078125;
                                        case '.' -> parsedValue *= 1.5;
                                    }
                                }
                            }
                            else {
                                parsedValue += Double.parseDouble(note.split("/")[1]);
                            }
                        }
                        else {
                            parsedValue += Double.parseDouble(note.split("@")[1]);
                        }

                        if(symbols.charAt(0) == '@')
                            startX = (int)Path.getSoundTimeX(parsedValue);
                        else {
                            startX += Path.getSoundTimeX(parsedValue) - GlobalSettings.getStartXofAreaWhereInsertingNotesIsLegal();
                        }
                    }
                    else
                    {
                        parsedValue = 0;

                        String musicSoundLetter = symbols;
                        symbols = note.split(symbols)[1];
                        octave = Character.getNumericValue(symbols.charAt(0));

                        GlobalSettings.chosenNote = symbols.charAt(1);
                        if( GlobalSettings.chosenNote == '/'){
                            float tmp = Float.parseFloat(symbols.substring(2, symbols.length() - 3));

                            if(tmp > 0.75) GlobalSettings.chosenNote = 'w';
                            else if(tmp > 0.375) GlobalSettings.chosenNote = 'h';
                            else if(tmp > 0.1875) GlobalSettings.chosenNote = 'q';
                            else if(tmp > 0.09375) GlobalSettings.chosenNote = 'i';
                            else if(tmp > 0.046875) GlobalSettings.chosenNote = 's';
                            else if(tmp > 0.0234375) GlobalSettings.chosenNote = 't';
                            else GlobalSettings.chosenNote = 'x';
                        }

                        noteValue = Note.mapNoteSymbolToNumericalValue(musicSoundLetter, octave);

                        index = nonFlatSounds.indexOf(noteValue);
                        modification = SoundModification.None;
                        if(index < 0)
                        {
                            if(musicSoundLetter.charAt(1) == 'b')
                            {
                                index = nonFlatSounds.indexOf(noteValue + 1);
                                modification = SoundModification.Flat;
                            }
                            else
                            {
                                index = nonFlatSounds.indexOf(noteValue - 1);
                                modification = SoundModification.Sharp;
                            }
                        }

                        baseIndex = nonFlatSounds.indexOf(modelManager.getBasePointSound(modelManager.getPathByIndex(i).getMusicClefSelection()));
                        insertY = (int)(GlobalSettings.getLinesStartHeight()) - (index - baseIndex) * 10;

                        lastSound = modelManager.addMusicSymbol(
                                i,
                                startX,
                                insertY
                        );

                        modelManager.getPathByIndex(i).setSoundModification(lastSound, modification);
                    }

                }
            }

            i++;
        }

        GlobalSettings.chosenNote = saveChoice;
        GlobalSettings.InstrumentChoice = 1;

        return modelManager;
    }

    public void replaceExistingModel(ModelManager modelManager)
    {
        clearModel();
        fireOnModelLoaded(modelManager.getTheLatestTimeX());

        this.selectedDefaultKey = modelManager.getDefaultMusicKeySelection();

        this.setProjectName(modelManager.getProjectName());
        this.setProjectDestination(modelManager.getProjectDestination());

        var userPrefTie = GlobalSettings.tieBetweenNotes;

        Path loadedPath;
        SoundModification modifier;
        for(Path path : modelManager.getPaths())
        {
            try {
                createPath(path.getName(), path.getInstrument(), path.getTempo(), path.getVolume(), path.getMusicClefSelection());
            } catch (CannotAddAnotherPathException e) {
                System.err.println(e.getMessage());
                return;
            }

            loadedPath = this.getPathByIndex(musicPaths.size() - 1);

            SoundTypeSelection save = GlobalSettings.selectedTypeOfSoundToInsertIntoPath;

            for(IPlayable sound : path.getSounds()) {
                if(sound.getSoundConcatenation() != TieSelection.None)
                    GlobalSettings.tieBetweenNotes = TieSelection.Include;
                else GlobalSettings.tieBetweenNotes = TieSelection.None;

                modifier = SoundModification.None;
                if(sound.isSharp())
                    modifier = SoundModification.Sharp;
                else if(sound.isFlat())
                    modifier = SoundModification.Flat;

                if(sound.getSoundType().equals("Accord"))
                    GlobalSettings.selectedTypeOfSoundToInsertIntoPath = SoundTypeSelection.Accord;
                else GlobalSettings.selectedTypeOfSoundToInsertIntoPath = SoundTypeSelection.Note;

                loadedPath.setSoundModification(
                        addMusicSymbol(path.getVoice(), sound.getTimeX(), sound.getSoundHeight(), sound.getDuration()),
                        modifier
                );
            }

            GlobalSettings.selectedTypeOfSoundToInsertIntoPath = save;
        }

        GlobalSettings.tieBetweenNotes = userPrefTie;
    }
    //endregion

    //region Music Symbols
    public IPlayable addMusicSymbol(int pathIndex, int insertX, int insertY)
    {
        return addMusicSymbol(pathIndex, insertX, insertY, GlobalSettings.chosenNote);
    }

    public IPlayable addMusicSymbol(int pathIndex, int insertX, int insertY, char duration)
    {
        Path path;
        try {
            path = musicPaths.get(pathIndex);
        }
        catch(RuntimeException e)
        {
            return null;
        }

        IPlayable sound = null;

        int instrument = GlobalSettings.InstrumentChoice;

        Note note;
        if(instrument > 0)
            note = Note.CreateNote(getCalculatedSoundValue(insertY, path.getMusicClefSelection()), duration, GlobalSettings.InstrumentForParticularNoteChoice);
        else note = Note.CreateNote(getCalculatedSoundValue(insertY, path.getMusicClefSelection()), duration);
        note.setTimeX(insertX);
        note.setSoundHeight(insertY);

        switch (GlobalSettings.selectedTypeOfSoundToInsertIntoPath) {
            case Note -> sound = note;
            case Accord -> sound = new Accord(note, GlobalSettings.accordSelectionName);
        }
        path.addSound(sound);

        fireOnNoteAdded(path, sound);

        return sound;
    }
    //endregion

    //region Paths Methods
    public void recordPath() throws CannotAddAnotherPathException
    {
        if(musicPaths.size() > 16)
            throw new CannotAddAnotherPathException("Program does not support of handling more than 16 paths at once.");

        /*try {
            MidiDevice device = MidiDevicePrompt.askForMidiDevice();
            MusicTransmitterToSequence transmitter = new MusicTransmitterToSequence(device);
            DemoPrint.step("Press [ENTER] when you're ready to start playing...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            transmitter.startListening();
            DemoPrint.step("Press [ENTER] when you're ready to stop playing...");
            scanner.nextLine();
            scanner.close();
            transmitter.stopListening();
        }
        catch (MidiUnavailableException e) { e.printStackTrace(); }
        catch (InvalidMidiDataException e) { e.printStackTrace(); }*/

        try {
            MidiDevice device = MidiDevicePrompt.askForMidiDevice();
            MusicTransmitterToSequence transmitter = new MusicTransmitterToSequence(device);
            transmitter.listenForMillis(10000);
            Sequence music = transmitter.getSequence();

            System.out.println(music);
        }
        catch (MidiUnavailableException e) { e.printStackTrace(); }
        catch (InvalidMidiDataException e) { e.printStackTrace(); }
        catch (InterruptedException ex) { }
    }
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

    public int getIndexOfPath(Path path) {
        return musicPaths.indexOf(path);
    }
    public Path getPathByIndex(int index)
    {
        try{
            return musicPaths.get(index);
        }
        catch (RuntimeException e)
        {
            return null;
        }
    }

    public Path getLastPath()
    {
        return musicPaths.get(musicPaths.size() - 1);
    }

    /**
     * Create new path according to user typed values with selected piano as instrument, tempo 120 and volume level 50 and selected Key
     * @param pathName Name of path
     */
    public void createPath(String pathName) throws CannotAddAnotherPathException
    {
        createPath(pathName, Instrument.getAllInstruments()[GlobalSettings.InstrumentChoice], 120, (byte)50, getDefaultMusicKeySelection());
    }

    /**
     * Create new path according to user typed values with selected instrument
     * @param pathName Name of path
     */
    public void createPath(String pathName, String instrument, int tempo, byte volume, MusicClefSelection musicKey) throws CannotAddAnotherPathException
    {
        if(musicPaths.size() > 16)
            throw new CannotAddAnotherPathException("Program does not support of handling more than 16 paths at once.");

        var path =
                Path.CreatePath(
                        pathName,
                        (byte)musicPaths.size(),
                        musicKey,
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
     * Duplicates selected path
     * @throws CannotAddAnotherPathException Thrown when user surpass acceptable amount of paths
     */
    public void duplicateSelectedPath() throws CannotAddAnotherPathException
    {
        createPath(selectedPath.getName(), selectedPath.getInstrument(), selectedPath.getTempo(), selectedPath.getVolume(), selectedPath.getMusicClefSelection());

        Path newPath = musicPaths.get(musicPaths.size() - 1);
        int pathIndex = musicPaths.indexOf(newPath);

        IPlayable duplicatedSound;
        for(IPlayable sound : selectedPath.getSounds())
        {
            GlobalSettings.selectedTypeOfSoundToInsertIntoPath = sound.getSoundType().equals("Note") ? SoundTypeSelection.Note : SoundTypeSelection.Accord;

            if(sound.isTiedWithPreviousSound() || sound.isTiedWithAnotherSound())
                GlobalSettings.tieBetweenNotes = TieSelection.Include;
            else GlobalSettings.tieBetweenNotes = TieSelection.None;

            addMusicSymbol(pathIndex, sound.getTimeX(), sound.getSoundHeight(), sound.getDuration());

            duplicatedSound = newPath.getLatestSound();

            if(sound.isSharp())
                newPath.setSoundModification(duplicatedSound, SoundModification.Sharp);
            else if(sound.isFlat())
                newPath.setSoundModification(duplicatedSound, SoundModification.Flat);
        }

        System.out.println(String.format("Path %s duplicated successfully", selectedPath.getName()));
    }

    public void changeMusicClefOfSelectedPath(MusicClefSelection musicClef)
    {
        int soundShift = selectedPath.setMusicClefSelection(musicClef);

        System.out.println(String.format("Music clef of path \"%s\" changed to %s", selectedPath.getName(), musicClef));

        fireOnPathClefChanged(selectedPath, soundShift);
    }

    /**
     * Sets the new name of selected path in model
     * @param newName New name that will be assing to existing path
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
        if(listener instanceof ISoundEvent)
            noteListeners.add((ISoundEvent) listener);
        if(listener instanceof IModelEvent)
            modelListeners.add((IModelEvent) listener);
    }

    public void fireOnModelLoaded(int latestTimeX)
    {
        Iterator<IModelEvent> iterator = modelListeners.iterator();

        while(iterator.hasNext()) {
            try{
                IModelEvent modelEvent = iterator.next();
                modelEvent.onModelLoaded(latestTimeX);
            }
            catch (Exception e){}
        }
    }

    /**
     * Raise event when note is added to particular path
     * @param path path where note has been added
     * @param note added note
     */
    private void fireOnNoteAdded(Path path, IPlayable note)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            try {
                ISoundEvent pathEvent = (ISoundEvent) iterator.next();
                pathEvent.onMusicSymbolAdded(path, note);
            }
            catch (Exception e){}
        }
    }

    /**
     * Raise event when new path is created
     * @param path created path
     */
    private void fireOnCreatedPathEvent(Path path)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            try{
                IPathEvent pathEvent = (IPathEvent) iterator.next();
                pathEvent.onPathCreated(path);
            }
            catch (Exception e){}
        }
    }

    /**
     * Raise event when path's clef has been changed
     * @param path path where clef has changed
     * @param soundShift Threshold shift
     */
    private void fireOnPathClefChanged(Path path, int soundShift)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            try{
                IPathEvent pathEvent = (IPathEvent) iterator.next();
                pathEvent.onPathClefChanged(path, soundShift);
            }
            catch (Exception e){}
        }
    }

    /**
     * Raise event when the selected path has changed name
     * @param path renamed path
     */
    private void fireOnPathNameRenamed(Path path)
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            try{
                IPathEvent pathEvent = (IPathEvent) iterator.next();
                pathEvent.onPathNameRenamed(path);
            }
            catch (Exception e){}
        }
    }

    /**
     * Raise event when selected path is set to null
     */
    private void fireOnPathClearSelection()
    {
        Iterator iterator = pathListeners.iterator();

        while(iterator.hasNext()) {
            try {
                IPathEvent pathEvent = (IPathEvent) iterator.next();
                pathEvent.onPathClearSelection();
            }
            catch (Exception ignored){}
        }
    }

    /**
     * Raise event when selected path has been deleted
     * @param path deleted path
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