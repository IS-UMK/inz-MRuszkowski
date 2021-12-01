package SongCreatorWindow.Model.Core;

/**
 *
 * Public class that is part of model. It represents a single note with its properties for JFugue library
 */
public class Note implements IPlayable{

    /**
     * Describes what is the note duration
     */
    public char NoteDuration;

    @Override
    public Character getDuration()
    {
        return NoteDuration;
    }
    /**
     * Integer for instrument sound selection.
     */
    public int Instrument;
    /**
     * In which moment the note occurs
     */
    int TimeX;
    int NoteHeight;

    @Override
    public int getTimeX() {
        return TimeX;
    }

    @Override
    public void setTimeX(int x) {
        TimeX = x;
    }

    @Override
    public int getSoundHeight()
    {
        return NoteHeight;
    }

    @Override
    public void setSoundHeight(int y) {
        NoteHeight = y;
    }

    /**
     * Determines octave and note value. In other words place in treble staff
     */
    public int NoteValue;

    private Note(int noteValue, char noteDuration, int instrument)
    {
        NoteValue = noteValue;
        NoteDuration = noteDuration;
        Instrument = instrument;
    }

    public static Note CreateNote(int noteValue, char noteDuration, int instrument) { return new Note(noteValue, noteDuration, instrument); }
    public static Note CreateNote(int noteValue, char noteDuration) { return new Note(noteValue, noteDuration, 0); }
    public static Note CreateNote(String noteSymbol, int octave, char noteDuration)
    {
        Note note = new Note(mapNoteSymbolToNumericalValue(noteSymbol, octave), noteDuration, 0);

        return note;
    }

    public static int mapNoteSymbolToNumericalValue(String noteSymbol, int octave)
    {
        int baseNoteMIDIValue = -1;

        switch(noteSymbol)
        {
            case "C": baseNoteMIDIValue = 0; break;
            case "C#": baseNoteMIDIValue = 1; break;
            case "D": baseNoteMIDIValue = 2; break;
            case "Eb": baseNoteMIDIValue = 3; break;
            case "E": baseNoteMIDIValue = 4; break;
            case "F": baseNoteMIDIValue = 5; break;
            case "F#": baseNoteMIDIValue = 6; break;
            case "G": baseNoteMIDIValue = 7; break;
            case "G#": baseNoteMIDIValue = 8; break;
            case "A": baseNoteMIDIValue = 9; break;
            case "Bb": baseNoteMIDIValue = 10; break;
            case "B": baseNoteMIDIValue = 11; break;
        }

        return baseNoteMIDIValue + (octave * 12);
    }

    @Override
    public String ExtractJFugueSoundString()
    {
        return String.format("I%d %d%c", Instrument, NoteValue, NoteDuration);
    }
}
