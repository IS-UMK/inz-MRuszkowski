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
     * In which moment the note occurs
     */
    public int TimeX;
    /**
     * Integer for instrument sound selection.
     */
    public int Instrument;
    /**
     * Determines octave and note value. In other words place in treble staff
     */
    int NoteValue;
    
    @Override
    public String ExtractJFugueSoundString() {
        return String.format("I%d %d%c", Instrument, NoteValue, NoteDuration);
    }
}
