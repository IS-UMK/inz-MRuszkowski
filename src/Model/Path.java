package Model;

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

    LinkedList<IPlayable> _sound;
    int _lenght;

    private Path(String pathName, String selectedInstrument, byte volumeLevel)
    {
        _pathName = pathName;
        //_pathId = 0;

        _selectedInstrument = selectedInstrument;
        _volumeLevel = volumeLevel;

        _sound = new LinkedList<IPlayable>();
        //_lenght = 0;
    }

    public static Path CreatePath(String pathName, String selectedInstrument, byte volumeLevel) { return new Path(pathName, selectedInstrument, volumeLevel); }
    public static Path CreatePath(String pathName, String selectedInstrument) { return new Path(pathName, selectedInstrument, (byte)50); }

    public String getInstrument()
    {
        return _selectedInstrument;
    }

    public void setInstrument(String selectedInstrument)
    {
        _selectedInstrument = selectedInstrument;
    }
}
