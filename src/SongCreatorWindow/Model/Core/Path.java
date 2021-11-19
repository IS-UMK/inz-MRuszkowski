package SongCreatorWindow.Model.Core;

import java.util.LinkedList;

/**
 *
 * Class for JFugue to create music paths. More than one path can be played at onec.
 */
public class Path {
    String _pathName;
    byte _pathId;

    String _selectedInstrument;
    byte _volumeLevel;

    LinkedList<IPlayable> _sounds;
    int _lenght;

    int _tempo;
    int _voice;

    private Path(String pathName, byte pathId, String selectedInstrument, int tempo, byte volumeLevel)
    {
        _pathName = pathName;

        _selectedInstrument = selectedInstrument;
        _volumeLevel = volumeLevel;

        _sounds = new LinkedList<IPlayable>();
        //_lenght = 0;

        _voice = _pathId = pathId;
        _tempo = tempo;
        System.out.println();
    }

    public static Path CreatePath(String pathName, byte pathId, String selectedInstrument, int tempo, byte volumeLevel) { return new Path(pathName, pathId, selectedInstrument, tempo, volumeLevel); }
    public static Path CreatePath(String pathName, byte pathId, String selectedInstrument, int tempo) { return new Path(pathName, pathId, selectedInstrument, tempo, (byte)50); }
    public static Path CreatePath(String pathName, byte pathId, String selectedInstrument) { return new Path(pathName, pathId, selectedInstrument, 120, (byte)50); }

    public String setInstrument()
    {
        return _selectedInstrument;
    }

    public void setInstrument(String selectedInstrument)
    {
        _selectedInstrument = selectedInstrument;
    }

    public int getTempo() { return _tempo; }

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

        return info.toString();
    }

    public String getName()
    {
        return _pathName;
    }

    public void setName(String result)
    {
        _pathName = result;
    }
}
