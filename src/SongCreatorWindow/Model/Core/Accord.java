package SongCreatorWindow.Model.Core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Public class that is part of model. It represents a Acord (more than one note) for JFugue library
 */
public class Accord implements IPlayable{
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
    public Character getDuration() {
        return rootNote.getDuration();
    }

    @Override
    public void setDuration(char newDuration) {
        rootNote.setDuration(newDuration);
    }

    @Override
    public int getSoundHeight() { return NoteHeight; }

    @Override
    public void setSoundHeight(int y) {
        NoteHeight = y;
    }

    @Override
    public String getSoundType() {
        return "Accord";
    }

    @Override
    public void setVolume(byte volume){ rootNote.setVolume(volume); }

    @Override
    public byte getVolume() {
        return rootNote.getVolume();
    }

    @Override
    public TieSelection getSoundConcatenation() {
        return rootNote.getSoundConcatenation();
    }

    @Override
    public void setSoundConcatenation(TieSelection tie) {
        rootNote.setSoundConcatenation(tie);
    }

    @Override
    public IPlayable getPreviousTiedSound() {
        return rootNote.getPreviousTiedSound();
    }

    @Override
    public void setPreviousTiedSound(IPlayable previousTiedSound) {
        rootNote.setPreviousTiedSound(previousTiedSound);
    }

    @Override
    public IPlayable getNextTiedSound() {
        return rootNote.getNextTiedSound();
    }

    @Override
    public void setNextTiedSound(IPlayable nextTiedSound) {
        rootNote.setNextTiedSound(nextTiedSound);
    }

    @Override
    public void setInstrument(int instrumentValue) {
        rootNote.setInstrument(instrumentValue);
    }

    @Override
    public void setSharpness(boolean isSharp) {
        rootNote.setSharpness(isSharp);
    }

    @Override
    public void setFlatness(boolean isFlat) {
        rootNote.setFlatness(isFlat);
    }

    @Override
    public boolean isSharp() {
        return rootNote.isSharp();
    }

    @Override
    public boolean isFlat() {
        return rootNote.isFlat();
    }

    @Override
    public SoundModification getModification() {
        return rootNote.getModification();
    }

    /**
     * Root Note where accord begins
     */
    Note rootNote;
    @Override
    public String getValue() {
        return rootNote.getValue();
    }
    @Override
    public int getNumericalNoteValue() {
        return rootNote.getNumericalNoteValue();
    }
    @Override
    public void setValue(String value) {
        rootNote.setValue(value);
    }

    String JFugueAccordName;
    public String getAccordName()
    {
        return JFugueAccordName;
    }
    public void setAccordName(String name) { JFugueAccordName = name; }

    
    public Accord(Note rootNote, String AccordName)
    {
        this.rootNote = rootNote;
        JFugueAccordName = AccordName;

        setTimeX(rootNote.getTimeX());
        setSoundHeight(rootNote.getSoundHeight());
    }

    @Override
    public String ExtractJFugueSoundString(boolean withInstrument) {
        
        var musicString = new StringBuilder();

        String[] accordInterval = AccordType.getIntervalsOfAccord(JFugueAccordName);
        List<Integer> nonFlatsSounds = Note.getNonFlatSoundNumericalValues();

        musicString.append(String.format("%s", rootNote.ExtractJFugueSoundString(withInstrument)));
        musicString.append("+");

        for(String interval : accordInterval)
        {
            int indexOfValue = nonFlatsSounds.indexOf(rootNote.getNumericalNoteValue());
            int newValue = nonFlatsSounds.get(indexOfValue + Integer.parseInt(interval.substring(0, 1)) - 1);
            String newSound = Note.mapNumericalValueOfNoteToSymbols(newValue);

            if(interval.length() > 1)
                newSound = Note.modifySingleSound(newSound, SoundModification.Flat);

            musicString.append(newSound);
            musicString.append(rootNote.getDuration());
            musicString.append("a" + rootNote.getVolume());
            musicString.append("+");
        }

        musicString.deleteCharAt(musicString.length() - 1); //Remove the last '+' symbol
        return musicString.toString();
    }

    @Override
    public int getInstrument() {
        return rootNote.getInstrument();
    }

    public class AccordType
    {
        public static String[] getAccordTypeNames()
        {
            List<String> accord_names = new LinkedList<>();

            for(Field field : AccordType.class.getFields())
            {
                accord_names.add(field.getName());
            }

            String[] all_accords_names = new String[accord_names.size()];

            return accord_names.toArray(all_accords_names);
        }

        public static String getAccordValueByName(String providedName)
        {
            for(Field field : AccordType.class.getFields())
            {
                if(field.getName().equals(providedName))
                {
                    try {
                        return (String) field.get(AccordType.class.getName());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            return "";
        }

        public static String[] getIntervalsOfAccord(String providedName)
        {
            String[] intervals = null;

            String methodName = String.format("get%sIntervals", providedName);
            try {
                intervals = (String[])AccordType.class.getMethod(methodName).invoke(AccordType.class);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            return intervals;
        }

        /**
         * Intervals 1, 3, 5
         */
        public static String Major = "maj";
        public static String[] getMajorIntervals(){ return new String[]{ "3", "5" }; }
        /**
         * Intervals 1, 3, 5, 6
         */
        public static String Major6th = "maj6";
        public static String[] getMajor6thIntervals(){ return new String[]{ "3", "5", "6" }; }
        /**
         * Intervals 1, 3, 5, 7
         */
        public static String Major7th = "maj7";
        public static String[] getMajor7thIntervals(){ return new String[]{ "3", "5", "7" }; }
        /**
         * Intervals 1, 3, 5, 7, 9
         */
        public static String Major9th = "maj9";
        public static String[] getMajor9thIntervals(){ return new String[]{ "3", "5", "7", "9" }; }
        /**
         * Intervals 1, 3, 5, 9
         */
        public static String MajorAdded9th = "add9";
        public static String[] getMajorAdded9thIntervals(){ return new String[]{ "3", "5", "9" }; }
        /**
         * Intervals 1, 3, 5, 6, 9
         */
        public static String Major_6by9 = "maj6%9";
        public static String[] getMajor_6by9Intervals(){ return new String[]{ "3", "5", "6", "9" }; }
        /**
         * Intervals 1, 3, 5, 6, 7
         */
        public static String Major_7by6 = "maj7%6";
        public static String[] getMajor_7by6Intervals(){ return new String[]{ "3", "5", "6", "7" }; }
        /**
         * Intervals 1, 3, 5, 7, 9, 13
         */
        public static String Major13 = "maj13";
        public static String[] getMajor13Intervals(){ return new String[]{ "3", "5", "7", "9", "13" }; }
        /**
         * Intervals 1, b3, 5
         */
        public static String Minor = "min";
        public static String[] getMinorIntervals(){ return new String[]{ "3b", "5" }; }
        /**
         * Intervals 1, b3, 5, 6
         */
        public static String Minor6th = "min6";
        public static String[] getMinor6thIntervals(){ return new String[]{ "3b", "5", "6" }; }
        /**
         * Intervals 1, b3, 5, b7
         */
        public static String Minor7th = "min7";
        public static String[] getMinor7thIntervals(){ return new String[]{ "3b", "5", "7b" }; }
        /**
         * Intervals 1, b3, 5, b7, 9
         */
        public static String Minor9th = "min9";
        public static String[] getMinor9thIntervals(){ return new String[]{ "3b", "5", "7b", "9" }; }
        /**
         * Intervals 1, b3, 5, b7, 9, 11
         */
        public static String Minor11th = "min11";
        public static String[] getMinor11thIntervals(){ return new String[]{ "3b", "5", "7b", "9", "11" }; }
        /**
         * Intervals 1, b3, 5, b7, 11
         */
        public static String Minor_7by11 = "min7%11";
        public static String[] getMinor_7by11Intervals(){ return new String[]{ "3b", "5", "7b", "11" }; }
        /**
         * Intervals 1, b3, 5, 9
         */
        public static String MinorAdded9th = "minadd9";
        public static String[] getMinorAdded9thIntervals(){ return new String[]{ "3b", "5", "9" }; }
        /**
         * Intervals 1, b3, 5, 6
         */
        public static String Minor_6by9 = "min6%9";
        public static String[] getMinor_6by9Intervals(){ return new String[]{ "3b", "5", "6" }; }
        /**
         * Intervals 1, b3, 5, 7
         */
        public static String MinorMajor7th = "minmaj7";
        public static String[] getMinorMajor7thIntervals(){ return new String[]{ "3b", "5", "7" }; }
        /**
         * Intervals 1, b3, 5, 7 ,9
         */
        public static String MinorMajor9th = "minmaj9";
        public static String[] getMinorMajor9thIntervals(){ return new String[]{ "3b", "5", "7", "9" }; }
        /**
         * Intervals 1, 3, 5, b7
         */
        public static String Dominant7th = "dom7";
        public static String[] getDominant7thIntervals(){ return new String[]{ "3", "5", "7b" }; }
        /**
         * Intervals 1, 3, 5, 6, b7
         */
        public static String Dominant_7by6 = "dom7%6";
        public static String[] getDominant_7by6Intervals(){ return new String[]{ "3", "5", "6", "7b" }; }
        /**
         * Intervals 1, 3, 5, b7, 11
         */
        public static String Dominant_7by11 = "dom7%11";
        public static String[] getDominant_7by11Intervals(){ return new String[]{ "3", "5", "7b", "11" }; }
        /**
         * Intervals 1, 4, 5, b7
         */
        public static String Dominant7th_Sus = "dom7sus";
        public static String[] getDominant7th_SusIntervals(){ return new String[]{ "4", "5", "7b" }; }
        /**
         * Intervals 1, 4, 5, 6, b7
         */
        public static String Dominant_7by6_Sus = "dom7%6sus";
        public static String[] getDominant_7by6_SusIntervals(){ return new String[]{ "4", "5", "6", "7b" }; }
        /**
         * Intervals 1, 3, 5, b7, 9
         */
        public static String Dominant9th = "dom9";
        public static String[] getDominant9thIntervals(){ return new String[]{ "3", "5", "7b", "9" }; }
        /**
         * Intervals 1, 3, 5, b7, 9, 11
         */
        public static String Dominant11th = "dom11";
        public static String[] getDominant11thIntervals(){ return new String[]{ "3", "5", "7b", "9", "11" }; }
        /**
         * Intervals 1, 3, 5, b7, 9, 13
         */
        public static String Dominant13th = "dom13";
        public static String[] getDominant13thIntervals(){ return new String[]{ "3", "5", "7b", "9", "13" }; }
        /**
         * Intervals 1, 3, 5, b7, 11, 13
         */
        public static String Dominant13th_Sus = "dom13sus";
        public static String[] getDominant13th_SusIntervals(){ return new String[]{ "3", "5", "7b", "11", "13" }; }
        /**
         * Intervals 1, 3, 5, b7, 9, 11, 13
         */
        public static String Dominant7th_6by11 = "dom7%6%11";
        public static String[] getDominant7th_6by11Intervals(){ return new String[]{ "3", "5", "7b", "9", "11", "13" }; }
        /**
         * Intervals 1, 3, #5
         */
        public static String Augmented = "aug";
        public static String[] getAugmentedIntervals(){ return new String[]{ "3", "5#" }; }
        /**
         * Intervals 1, 3, #5, b7
         */
        public static String Augmented7th = "aug7";
        public static String[] getAugmented7thIntervals(){ return new String[]{ "3", "5#", "b7" }; }
        /**
         * Intervals 1, b3, b5
         */
        public static String Diminished = "dim";
        public static String[] getDiminishedIntervals(){ return new String[]{ "3b", "5" }; }
        /**
         * Intervals 1, b3, b5, 6
         */
        public static String Diminished7th = "dim7";
        public static String[] getDiminished7thIntervals(){ return new String[]{ "3b", "5b", "6" }; }
        /**
         * Intervals 1, 2, 5
         */
        public static String Suspended2nd = "sus2";
        public static String[] getSuspended2ndIntervals(){ return new String[]{ "2", "5" }; }
        /**
         * Intervals 1, 4, 5
         */
        public static String Suspended4th = "sus4";
        public static String[] getSuspended4thIntervals(){ return new String[]{ "4", "5" }; }
    }
}
