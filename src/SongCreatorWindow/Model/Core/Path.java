package SongCreatorWindow.Model.Core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static SongCreatorWindow.Model.GlobalSettings.defaultMusicKey;

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

    int _lenght;

    private Path(String pathName, byte voice, MusicKeySelection musicKey, String selectedInstrument, int tempo, byte volumeLevel)
    {
        _pathName = pathName;
        _selectedKey = musicKey;

        _selectedInstrument = selectedInstrument;
        _volumeLevel = volumeLevel;

        _sounds = new LinkedList<IPlayable>();
        //_lenght = 0;

        _voice = voice;
        _tempo = tempo;
        System.out.println();
    }

    public static Path CreatePath(String pathName, byte voice, MusicKeySelection musicKey, String selectedInstrument, int tempo, byte volumeLevel) { return new Path(pathName, voice, musicKey, selectedInstrument, tempo, volumeLevel); }
    public static Path CreatePath(String pathName, byte voice, MusicKeySelection musicKey, String selectedInstrument, int tempo) { return new Path(pathName, voice, musicKey, selectedInstrument, tempo, (byte)50); }
    public static Path CreatePath(String pathName, byte voice, MusicKeySelection musicKey, String selectedInstrument) { return new Path(pathName, voice, musicKey, selectedInstrument, 120, (byte)50); }

    public void addSound(IPlayable sound)
    {
        for(IPlayable s : _sounds)
            if(s.getTimeX() > sound.getTimeX())
            {
                int index = _sounds.indexOf(s);
                _sounds.add(index, sound);
                return;
            }

        _sounds.add(sound);
    }

    public String getExtractedMusic()
    {
        var musicString = new StringBuilder();

        musicString.append(String.format("T%d V%d ", _tempo, _voice));

        for(IPlayable s : _sounds)
            musicString.append(String.format("%s ", s.ExtractJFugueSoundString()));

        return musicString.toString();
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
