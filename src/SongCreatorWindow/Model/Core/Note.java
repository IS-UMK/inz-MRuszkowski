package SongCreatorWindow.Model.Core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

    @Override
    public String getSoundType() {
        return "Note";
    }

    /**
     * Determines octave and note value. In other words place in treble staff
     */
    public String NoteValue;

    @Override
    public String getValue() {
        return NoteValue;
    }

    private Note(String noteValue, char noteDuration, int instrument)
    {
        NoteValue = noteValue;
        NoteDuration = noteDuration;
        Instrument = instrument;
    }

    public static Note CreateNote(String noteValue, char noteDuration, int instrument) { return new Note(noteValue, noteDuration, instrument); }
    public static Note CreateNote(String noteValue, char noteDuration) { return new Note(noteValue, noteDuration, 0); }
    public static Note CreateNote(String noteSymbol, int octave, char noteDuration)
    {
        Note note = new Note(noteSymbol + octave, noteDuration, 0);

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

    public static String mapNumericalValueOfNoteToSymbols(int noteValue)
    {
        int sound = noteValue % 12;
        int octave = noteValue / 12;

        StringBuilder symbols = new StringBuilder();

        switch (sound)
        {
            case 0 -> { symbols.append("C"); }
            case 1 -> { symbols.append("C#"); }
            case 2 -> { symbols.append("D"); }
            case 3 -> { symbols.append("Eb"); }
            case 4 -> { symbols.append("E"); }
            case 5 -> { symbols.append("F"); }
            case 6 -> { symbols.append("F#"); }
            case 7 -> { symbols.append("G"); }
            case 8 -> { symbols.append("G#"); }
            case 9 -> { symbols.append("A"); }
            case 10 -> { symbols.append("Bb"); }
            case 11 -> { symbols.append("B"); }
        }

        switch (octave)
        {
            case 1 -> { symbols.append("1"); }
            case 2 -> { symbols.append("2"); }
            case 3 -> { symbols.append("3"); }
            case 4 -> { symbols.append("4"); }
            case 5 -> { symbols.append("5"); }
            case 6 -> { symbols.append("6"); }
            case 7 -> { symbols.append("7"); }
            case 8 -> { symbols.append("8"); }
            case 9 -> { symbols.append("9"); }
            case 10 -> { symbols.append("10"); }
        }

        return symbols.toString();
    }

    public static List<Integer> getNonFlatSoundNumericalValues()
    {
        List list = new LinkedList<Integer>();

        for(int i = 0; i < 128; i += 12) list.add(i);
        for(int i = 2; i < 128; i += 12) list.add(i);
        for(int i = 4; i < 128; i += 12) list.add(i);
        for(int i = 5; i < 128; i += 12) list.add(i);
        for(int i = 7; i < 128; i += 12) list.add(i);
        for(int i = 9; i < 128; i += 12) list.add(i);
        for(int i = 11; i < 128; i += 12) list.add(i);

        Collections.sort(list);

        return list;
    }

    @Override
    public String ExtractJFugueSoundString()
    {
        return String.format("I%d %s%c", Instrument, NoteValue, NoteDuration);
    }
}
