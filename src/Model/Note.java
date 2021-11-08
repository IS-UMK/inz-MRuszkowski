package Model;

/**
 *
 * Public class that is part of model. It represents a single note with its properties for JFugue library
 */
public class Note implements IPlayable{

    /**
     * Describes what is the note duration
     */
    public char NoteDuration;
    /**
     * Integer for instrument sound selection.
     */
    public int Instrument;
    /**
     * In which moment the note occurs
     */
    double TimeX;

    @Override
    public double getTimeX() {
        return TimeX;
    }

    @Override
    public void setTimeX(double x) {
        TimeX = x;
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

    @Override
    public String ExtractJFugueSoundString()
    {
        return String.format("I%d %d%c", Instrument, NoteValue, NoteDuration);
    }
}
