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
    char noteDuration;

    @Override
    public Character getDuration()
    {
        return noteDuration;
    }

    @Override
    public void setDuration(char newDuration) {
        noteDuration = newDuration;
    }

    /**
     * Integer for instrument sound selection.
     */
    int instrument;
    /**
     * In which moment the note occurs
     */

    byte volume;
    int timeX;
    int noteHeight;

    @Override
    public int getTimeX() {
        return timeX;
    }

    @Override
    public void setTimeX(int x) {
        timeX = x;
    }

    @Override
    public int getSoundHeight()
    {
        return noteHeight;
    }

    @Override
    public void setSoundHeight(int y) {
        noteHeight = y;
    }

    @Override
    public String getSoundType() {
        return "Note";
    }

    @Override
    public void setVolume(byte volume){
        this.volume = volume;
    }

    @Override
    public byte getVolume()
    {
        return volume;
    }

    /**
     * Determines octave and note value. In other words place in treble staff
     */
    String noteValue;

    @Override
    public String getValue() {
        return noteValue;
    }
    @Override
    public void setValue(String value) { noteValue = value; }

     TieSelection noteConcatenation;
    @Override
    public TieSelection getSoundConcatenation() { return noteConcatenation; }
    @Override
    public void setSoundConcatenation(TieSelection tie) { noteConcatenation = tie; }

    @Override
    public void setInstrument(int instrumentValue) {
        this.instrument = instrumentValue;
    }

    boolean sharpness = false;
    @Override
    public void setSharpness(boolean isSharp) {
        if(sharpness == isSharp)
            return;

        sharpness = isSharp;

        if(isSharp){
            noteValue = Note.modifySingleSound(noteValue, SoundModification.Sharp);
        }
        else{
            noteValue.replace("#", "");
        }
    }
    @Override
    public boolean isSharp() {
        return sharpness;
    }

    boolean flatness = false;
    @Override
    public void setFlatness(boolean isFlat) {
        if(flatness == isFlat)
            return;

        flatness = isFlat;

        if(isFlat){
            noteValue = Note.modifySingleSound(noteValue, SoundModification.Flat);
        }
        else{
            noteValue.replace("b", "");
        }
    }
    @Override
    public boolean isFlat() {
        return flatness;
    }

    private Note(String noteValue, char noteDuration, int instrument)
    {
        this.noteValue = noteValue;
        this.noteDuration = noteDuration;
        this.instrument = instrument;

        noteConcatenation = TieSelection.None;
    }

    public static Note CreateNote(String noteValue, char noteDuration, int instrument) { return new Note(noteValue, noteDuration, instrument); }
    public static Note CreateNote(String noteValue, char noteDuration) { return new Note(noteValue, noteDuration, 0); }
    public static Note CreateNote(String noteSymbol, int octave, char noteDuration)
    {

        return new Note(noteSymbol + octave, noteDuration, 0);
    }

    @Override
    public int getNumericalNoteValue()
    {
        String symbols = noteValue.split("[0-9]")[0];
        return Note.mapNoteSymbolToNumericalValue(symbols, Integer.parseInt(noteValue.substring(symbols.length())));
    }

    public static int mapNoteSymbolToNumericalValue(String noteSymbol, int octave)
    {
        int baseNoteMIDIValue = switch (noteSymbol) {
            case "C" -> 0;
            case "C#" -> 1;
            case "D" -> 2;
            case "Eb" -> 3;
            case "E" -> 4;
            case "F" -> 5;
            case "F#" -> 6;
            case "G" -> 7;
            case "G#" -> 8;
            case "A" -> 9;
            case "Bb" -> 10;
            case "B" -> 11;
            default -> -1;
        };

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

    static List<Integer> listWithFlatSoundNumericalValues = null;
    public static List<Integer> getNonFlatSoundNumericalValues()
    {
        if(listWithFlatSoundNumericalValues == null) {
            listWithFlatSoundNumericalValues = new LinkedList<>();

            for (int i = 0; i < 128; i += 12) listWithFlatSoundNumericalValues.add(i);
            for (int i = 2; i < 128; i += 12) listWithFlatSoundNumericalValues.add(i);
            for (int i = 4; i < 128; i += 12) listWithFlatSoundNumericalValues.add(i);
            for (int i = 5; i < 128; i += 12) listWithFlatSoundNumericalValues.add(i);
            for (int i = 7; i < 128; i += 12) listWithFlatSoundNumericalValues.add(i);
            for (int i = 9; i < 128; i += 12) listWithFlatSoundNumericalValues.add(i);
            for (int i = 11; i < 128; i += 12) listWithFlatSoundNumericalValues.add(i);

            Collections.sort(listWithFlatSoundNumericalValues);
        }

        return listWithFlatSoundNumericalValues;
    }

    public static String modifySingleSound(String symbols, SoundModification modification)
    {
        char sound = symbols.charAt(0);
        int octave = Integer.parseInt(symbols.substring(symbols.split("[0-9]")[0].length()));

        return modifySingleSound(sound, octave, modification);
    }

    public static String modifySingleSound(char sound, int octave, SoundModification modification)
    {
        StringBuilder modified = new StringBuilder();
        modified.append(sound);

        switch(modification){
            case Sharp -> {
                if (sound != 'E' && sound != 'B') {
                    if(sound != 'A' && sound != 'D')
                        modified.append("#");
                }
                else{
                    modified.append("b");
                }
            }

            case Flat -> {
                if (sound != 'C' && sound != 'F') {
                    if(sound == 'E' || sound == 'B')
                        modified.append("b");
                    else{
                        modified.deleteCharAt(modified.length() - 1);
                        switch (sound){
                            case 'D' -> modified.append("C#");
                            case 'G' -> modified.append("F#");
                            case 'A' -> modified.append("G#");
                        }
                    }
                }
            }
        }

        modified.append(octave);
        return modified.toString();
    }

    @Override
    public String ExtractJFugueSoundString(boolean withInstrument)
    {
        StringBuilder musicString = new StringBuilder();

        if(withInstrument)
            musicString.append(String.format("I%d ", instrument));

        switch (noteConcatenation) {
            case None -> {
                musicString.append(String.format("%s%ca%d", noteValue, noteDuration, volume));
            }
            case Begin -> {
                musicString.append(String.format("%s%c-a%d", noteValue, noteDuration, volume));
            }
            case Continue -> {
                musicString.append(String.format("%s-%c-a%d", noteValue, noteDuration, volume));
            }
            case End -> {
                musicString.append(String.format("%s-%ca%d", noteValue, noteDuration, volume));
            }
        }

        return musicString.toString();
    }

    @Override
    public int getInstrument() {
        return instrument;
    }
}
