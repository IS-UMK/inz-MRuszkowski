package SongCreatorWindow.Model.Core;

import SongCreatorWindow.Model.Events.IMusicSoundEditionEvent;
import SongCreatorWindow.Model.GlobalSettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Class for JFugue to create music paths. More than one path can be played at onec.
 */
public class Path implements Serializable
{
    String _pathName;
    public String getName()
    {
        return _pathName;
    }
    public void setName(String result)
    {
        _pathName = result;
    }

    MusicClefSelection _selectedKey;

    /**
     * Sets the music clef
     * @param musicClef music clef
     * @return sound shift
     */
    public int setMusicClefSelection(MusicClefSelection musicClef)
    {
        int soundShift = 0;

        switch (_selectedKey) {
            case ViolinClef -> {
                switch (musicClef) {
                    case BassClef -> soundShift = -12;
                    case AltoClef -> soundShift = -6;
                }
            }
            case BassClef -> {
                switch (musicClef) {
                    case ViolinClef -> soundShift = 12;
                    case AltoClef -> soundShift = 6;
                }
            }
            case AltoClef -> {
                switch (musicClef) {
                    case ViolinClef -> soundShift = 6;
                    case BassClef -> soundShift = -6;
                }
            }
        }

        for(var musicSound : _sounds)
            musicSound.setSoundHeight((int) (musicSound.getSoundHeight() + GlobalSettings.getLinesPadding() / 2 * soundShift));

        _selectedKey = musicClef;

        return soundShift;
    }
    public MusicClefSelection getMusicClefSelection() { return _selectedKey; }

    String _selectedInstrument;
    public String getInstrument()
    {
        return _selectedInstrument;
    }
    public void setInstrument(String selectedInstrument)
    {
        _selectedInstrument = selectedInstrument;
    }

    byte _volumeLevel;
    public byte getVolume()
    {
        return _volumeLevel;
    }
    public void setVolume(byte volumeLevel)
    {
        _volumeLevel = volumeLevel;
    }

    byte _voice;
    public byte getVoice()
    {
        return _voice;
    }
    public void setVoice(byte voice)
    {
        _voice = voice;
    }

    int _tempo;
    public int getTempo() { return _tempo; }
    public void setTempo(int tempo) { _tempo = tempo; }

    List<IPlayable> _sounds;
    public List<IPlayable> getSounds() { return new ArrayList<IPlayable>(_sounds); }

    IPlayable selectedMusicSound = null;
    transient List<IMusicSoundEditionEvent> listeners;

    int _lenght;

    private Path(String pathName, byte voice, MusicClefSelection musicKey, String selectedInstrument, int tempo, byte volumeLevel)
    {
        _pathName = pathName;
        _selectedKey = musicKey;

        _selectedInstrument = selectedInstrument;
        _volumeLevel = volumeLevel;

        _sounds = new LinkedList<IPlayable>();
        listeners = new LinkedList<>();
        //_lenght = 0;

        _voice = voice;
        _tempo = tempo;
    }

    public static Path CreatePath(String pathName, byte voice, MusicClefSelection musicKey, String selectedInstrument, int tempo, byte volumeLevel) { return new Path(pathName, voice, musicKey, selectedInstrument, tempo, volumeLevel); }
    public static Path CreatePath(String pathName, byte voice, MusicClefSelection musicKey, String selectedInstrument, int tempo) { return new Path(pathName, voice, musicKey, selectedInstrument, tempo, (byte)50); }
    public static Path CreatePath(String pathName, byte voice, MusicClefSelection musicKey, String selectedInstrument) { return new Path(pathName, voice, musicKey, selectedInstrument, 120, (byte)50); }

    //region MusicSounds
    public void addSound(IPlayable sound)
    {
        boolean added = false;

        for(IPlayable s : _sounds)
            if(s.getTimeX() > sound.getTimeX())
            {
                int index = _sounds.indexOf(s);
                _sounds.add(index, sound);
                added = true;
                break;
            }

        if(!added)
            _sounds.add(sound);

        sound.setSoundConcatenation(GlobalSettings.TieBetweenNotes);
        fireOnMusicSoundTieCheck(sound);
    }

    public void convertToNote(IPlayable musicSound)
    {
        if(_sounds.indexOf(musicSound) == -1)
            return;

        if(musicSound instanceof Accord)
        {
            int indexOfSound = _sounds.indexOf(musicSound);
            _sounds.remove(musicSound);

            var newNote = Note.CreateNote(musicSound.getValue(), musicSound.getDuration(), musicSound.getInstrument());
            newNote.setFlatness(musicSound.isFlat());
            newNote.setSharpness(musicSound.isSharp());
            newNote.setVolume(musicSound.getVolume());
            newNote.setSoundConcatenation(musicSound.getSoundConcatenation());
            newNote.setTimeX(musicSound.getTimeX());
            newNote.setSoundHeight(musicSound.getSoundHeight());

            _sounds.add(indexOfSound, newNote);
            //addSound(newNote);

            fireOnMusicSoundConvertedToNote(this, musicSound, newNote);
        }
    }

    public void convertToAccord(IPlayable musicSound)
    {
        if(_sounds.indexOf(musicSound) == -1)
            return;

        if(musicSound instanceof Note)
        {
            int indexOfSound = _sounds.indexOf(musicSound);
            _sounds.remove(musicSound);

            var newAccord = new Accord((Note)musicSound, GlobalSettings.accordSelectionName);
            newAccord.setFlatness(musicSound.isFlat());
            newAccord.setSharpness(musicSound.isSharp());
            newAccord.setVolume(musicSound.getVolume());
            newAccord.setSoundConcatenation(musicSound.getSoundConcatenation());
            newAccord.setTimeX(musicSound.getTimeX());
            newAccord.setSoundHeight(musicSound.getSoundHeight());

            _sounds.add(indexOfSound, newAccord);
            //addSound(newAccord);

            fireOnMusicSoundConvertedToAccord(this, musicSound, newAccord);
        }
    }

    public void ChangeAccordName(IPlayable musicSound, String accordName)
    {
        if(_sounds.indexOf(musicSound) == -1)
            return;

        ((Accord)musicSound).setAccordName(accordName);

        fireOnAccordNameChanged(this, musicSound);
    }

    public void changeSoundDuration(IPlayable musicSound, char newDuration)
    {
        if(_sounds.indexOf(musicSound) == -1)
            return;

        musicSound.setDuration(newDuration);

        fireOnMusicSoundDurationChange(this, musicSound);
    }

    public void changeSoundHeight(IPlayable musicSound, int soundMove, int octaveMove)
    {
        if(_sounds.indexOf(musicSound) == -1)
            return;

        musicSound.setSoundHeight(
                musicSound.getSoundHeight() - soundMove * 10 - octaveMove * 80
        );

        int noteValue = musicSound.getNumericalNoteValue();

        if(musicSound.isSharp())
            noteValue--;

        if(musicSound.isFlat())
            noteValue++;

        int index = Note.getNonFlatSoundNumericalValues().indexOf(noteValue);
        int newValue = Note.getNonFlatSoundNumericalValues().get(index + soundMove + octaveMove * 7);

        musicSound.setValue(Note.mapNumericalValueOfNoteToSymbols(newValue));

        if(musicSound.isSharp())
            musicSound.setSharpness(false);
        if(musicSound.isFlat())
            musicSound.setFlatness(false);

        fireOnMusicSoundHeightChange(this, musicSound);
    }

    public void changeSoundOccurrence(IPlayable musicSound, double newOccurrenceTime)
    {
        if(_sounds.indexOf(musicSound) == -1)
            return;

        musicSound.setTimeX((int) Path.getSoundTimeX(newOccurrenceTime));

        fireOnMusicSoundOccurrenceTimeChanged(this, musicSound);
    }

    public void changeSoundInstrument(IPlayable musicSound, String instrumentName)
    {
        if(_sounds.indexOf(musicSound) == -1)
            return;

        musicSound.setInstrument(Instrument.getInstrumentValueByChosenName(instrumentName));
    }

    public void setSoundModification(IPlayable musicSound, SoundModification modification)
    {
        if(_sounds.indexOf(musicSound) == -1)
            return;

        switch (modification) {
            case None -> {
                musicSound.setFlatness(false);
                musicSound.setSharpness(false);
            }
            case Sharp -> {
                if(musicSound.isFlat())
                    musicSound.setFlatness(false);

                musicSound.setSharpness(true);
            }
            case Flat -> {
                if(musicSound.isSharp())
                    musicSound.setSharpness(false);

                musicSound.setFlatness(true);
            }
        }

        fireOnMusicSoundModified(this, musicSound);
    }

    /**
     * Get music sound that is selected. Return null if none is chosen
     * @return Music Sound or null
     */
    public IPlayable getSelectedMusicSound()
    {
        return selectedMusicSound;
    }

    /**
     * Set path that will be now selected
     * @param musicSound Chosen Music Sound
     */
    public void setSelectedMusicSound(IPlayable musicSound)
    {
        selectedMusicSound = musicSound;
        fireOnMusicSoundSelected(this, musicSound);
    }

    /**
     * Set selection of Music Sound to None (null)
     */
    public void clearSelectionOfMusicSound()
    {
        fireOnMusicSoundClearSelection();
        selectedMusicSound = null;
    }

    /**
     * Find out what is the index of music sound previously selected
     * @return index of selected path
     */
    public int getIndexOfSelectedMusicSound()
    {
        return _sounds.indexOf(selectedMusicSound);
    }

    public boolean isTiedWithPreviousSound(IPlayable musicSound)
    {
        if(musicSound.getPreviousTiedSound() != null)
            return true;

        return false;
    }
    /*public boolean isTiedWithPreviousSound(IPlayable musicSound)
    {
        int index = _sounds.indexOf(musicSound);

        if(index == 0)
            return false;

        IPlayable previousSound = getPreviousSound(musicSound);

        if(previousSound == null)
            return false;

        switch (previousSound.getSoundConcatenation()) {
            case Begin, Continue -> {
                return true;
            }
        }

        return false;
    }

    public IPlayable getPreviousSound(IPlayable musicSound)
    {
        int index = _sounds.indexOf(musicSound);

        if(index == 0)
            return null;

        return _sounds.get(index - 1);
    }*/

    public void deleteSound(IPlayable musicSound)
    {
        if(_sounds.indexOf(musicSound) == -1)
            return;

        _sounds.remove(musicSound);
        fireOnMusicSoundDeleted(this, musicSound);
    }
    //endregion

    //region Events
    public void addListener(IMusicSoundEditionEvent listener)
    {
        listeners.add(listener);
    }

    private void fireOnAccordNameChanged(Path path, IPlayable musicSound)
    {
        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onAccordNameChanged(path, musicSound);
        }
    }

    private void fireOnMusicSoundSelected(Path path, IPlayable musicSound)
    {
        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onMusicSoundSelectedToEdition(path, musicSound);
        }
    }

    private void fireOnMusicSoundClearSelection()
    {
        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onMusicSoundClearSelection();
        }
    }

    private void fireOnMusicSoundTieCheck(IPlayable musicSound)
    {
        int index = _sounds.indexOf(musicSound);

        TieSelection previousTie = TieSelection.None;

        IPlayable previousSound = null;
        for(int i = index - 1; i >= 0; i--)
        {
            previousSound = _sounds.get(i);
            if(previousSound.getSoundConcatenation() == TieSelection.Begin || previousSound.getSoundConcatenation() == TieSelection.Continue)
            {
                if(previousSound.getNextTiedSound() == null)
                {
                    previousTie = previousSound.getSoundConcatenation();
                    musicSound.setPreviousTiedSound(previousSound);
                    previousSound.setNextTiedSound(musicSound);
                }
                break;
            }
        }

        //TieSelection previousTie = index != 0 && _sounds.size() > 1 ? _sounds.get(index - 1).getSoundConcatenation() : TieSelection.None;

        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onMusicSoundTieCheck(musicSound, previousTie);
        }
    }

    private void fireOnMusicSoundOccurrenceTimeChanged(Path path, IPlayable musicSound)
    {
        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onMusicSoundOccurrenceTimeChanged(path, musicSound);
        }
    }

    private void fireOnMusicSoundHeightChange(Path path, IPlayable musicSound)
    {
        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onMusicSoundHeightChange(path, musicSound);
        }
    }

    private void fireOnMusicSoundDurationChange(Path path, IPlayable musicSound)
    {
        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onMusicSoundDurationChange(path, musicSound);
        }
    }

    private void fireOnMusicSoundModified(Path path, IPlayable musicSound)
    {
        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onMusicSoundModified(path, musicSound);
        }
    }

    private void fireOnMusicSoundConvertedToAccord(Path path, IPlayable musicSound, Accord newAccord)
    {
        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onMusicSoundConvertedToAccord(path, musicSound, newAccord);
        }
    }

    private void fireOnMusicSoundConvertedToNote(Path path, IPlayable musicSound, IPlayable newNote)
    {
        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onMusicSoundConvertedToNote(path, musicSound, newNote);
        }
    }

    private void fireOnMusicSoundDeleted(Path path, IPlayable musicSound)
    {
        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onMusicSoundDeleted(path, musicSound);
        }
    }
    //endregion

    public String getExtractedMusic()
    {
        if(_sounds.size() == 0)
            return "";

        var musicString = new StringBuilder();

        musicString.append(String.format("T%d V%d ", _tempo, _voice));

        int instrumentValue = Instrument.getInstrumentValueByChosenName(getInstrument());

        if(instrumentValue == -1) {
            for (IPlayable s : _sounds) {
                s.setVolume(this.getVolume());
                musicString.append(
                        String.format(
                                "@%s %s ",
                                Path.getSoundTimeOccurrence(s.getTimeX()),
                                s.ExtractJFugueSoundString(true)
                        )
                );
            }
        }
        else{
            musicString.append(String.format("I%d ", instrumentValue));

            for (IPlayable s : _sounds){
                s.setVolume(this.getVolume());
                musicString.append(
                        String.format(
                                "@%s %s ",
                                Path.getSoundTimeOccurrence(s.getTimeX()),
                                s.ExtractJFugueSoundString(false)
                        )
                );
            }
        }

        return musicString.toString();
    }

    public static double getSoundTimeOccurrence(double timeX)
    {
        return (timeX - GlobalSettings.getStartXofAreaWhereInsertingNotesIsLegal() - GlobalSettings.fixedXPositionOfNotes) / (GlobalSettings.Height * 2);
    }

    public static double getSoundTimeX(double occurrenceTime)
    {
        return occurrenceTime * 2 * GlobalSettings.Height + GlobalSettings.getStartXofAreaWhereInsertingNotesIsLegal() + GlobalSettings.fixedXPositionOfNotes;
    }

    @Override
    public String toString() {
        var info = new StringBuilder();

        info.append(String.format("Path name - %s\n", _pathName));
        info.append(String.format("Selected instrument - %s\n", _selectedInstrument));
        info.append(String.format("Volume level - %s\n", _volumeLevel));
        info.append(String.format("Tempo of track - %s\n", _tempo));
        info.append(String.format("Voice - %s\n", _voice));
        info.append(String.format("Music String - %s\n\n", getExtractedMusic()));

        return info.toString();
    }
}
