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

    MusicKeySelection _selectedKey;
    public void setMusicKeySelection(MusicKeySelection musicKey) { _selectedKey = musicKey; }
    public MusicKeySelection getMusicKeySelection() { return _selectedKey; }

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
    List<IMusicSoundEditionEvent> listeners;

    int _lenght;

    private Path(String pathName, byte voice, MusicKeySelection musicKey, String selectedInstrument, int tempo, byte volumeLevel)
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

    public static Path CreatePath(String pathName, byte voice, MusicKeySelection musicKey, String selectedInstrument, int tempo, byte volumeLevel) { return new Path(pathName, voice, musicKey, selectedInstrument, tempo, volumeLevel); }
    public static Path CreatePath(String pathName, byte voice, MusicKeySelection musicKey, String selectedInstrument, int tempo) { return new Path(pathName, voice, musicKey, selectedInstrument, tempo, (byte)50); }
    public static Path CreatePath(String pathName, byte voice, MusicKeySelection musicKey, String selectedInstrument) { return new Path(pathName, voice, musicKey, selectedInstrument, 120, (byte)50); }

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

        fireOnMusicSoundTieCheck(sound);
        sound.setSoundConcatenation(GlobalSettings.TieBetweenNotes);
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
    //endregion

    //region Events
    public void addListener(IMusicSoundEditionEvent listener)
    {
        listeners.add(listener);
    }

    public void fireOnMusicSoundSelected(Path path, IPlayable musicSound)
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
        TieSelection previousTie = index != 0 && _sounds.size() > 1 ? _sounds.get(index - 1).getSoundConcatenation() : TieSelection.None;

        Iterator iterator = listeners.iterator();

        while(iterator.hasNext()) {
            IMusicSoundEditionEvent modelEvent = (IMusicSoundEditionEvent) iterator.next();
            modelEvent.onMusicSoundTieCheck(musicSound, previousTie);
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
