package Model;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Static helper class for JFugue library that provides constant variables that represents numerical value of instrument
 */
public class Instrument {
    /**
     * Get All Instruments available in JFugue Library as String array
     * @return String array with instruments names
     */
    public static String[] getAllInstruments() {
        List<String> instrument_names = new LinkedList<>();

        for(Class subClass : Instrument.class.getClasses())
        {
            for(Field field : subClass.getFields()) {
                instrument_names.add(
                        String.format("%s - %s", subClass.getName().split("\\$")[1], field.getName().replace('_', ' '))
                );
            }
        }

        String[] all_instrument_names = new String[instrument_names.size()];

        return instrument_names.toArray(all_instrument_names);
    }

    /**
     * Static method that returns the instrument value based on given string from list. Method utilizes the Reflection System
     * @param providedChoice is a String from list where user chose Instrument. Must be Porivded in format "Instrument - Particular_Sound"
     *                       for example "Guitar - GUITAR HARMONICS"
     * @return Numeric value of Instrument, -1 means that instrument has not been found
     */
    public static int GetInstrumentValueByChosenName(String providedChoice)
    {
        String[] choice = providedChoice.split("-");
        choice[0] = choice[0].strip();
        choice[1] = choice[1].strip();

        for(Class subClass : Instrument.class.getClasses())
            for(Field field : subClass.getFields()) {
                if(subClass.getName().split("\\$")[1].equals(choice[0]) && field.getName().replace('_', ' ').equals(choice[1]))
                {
                    System.err.println("Entered");
                    try {
                        return field.getInt(Class.forName(subClass.getName()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }

        return -1;
    }

    public abstract class Sound_Effects
    {
        public static int GUITAR_FRET_NOISE = 120;
        public static int BREATH_NOISE = 121;
        public static int SEASHORE = 122;
        public static int BIRD_TWEET = 123;
        public static int TELEPHONE_RING = 124;
        public static int HELICOPTER = 125;
        public static int APPLAUSE = 126;
        public static int GUNSHOT = 127;
    }
    public abstract class Percussive
    {
        public static int TINKLE_BELL = 112;
        public static int AGOGO = 113;
        public static int STEEL_DRUMS = 114;
        public static int WOODBLOCK = 115;
        public static int TAIKO_DRUM = 116;
        public static int MELODIC_TOM = 117;
        public static int SYNTH_DRUM = 118;
        public static int REVERSE_CYMBAL = 119;
    }
    public abstract class Ethnic
    {
        public static int SITAR = 104;
        public static int BANJO = 105;
        public static int SHAMISEN = 106;
        public static int KOTO = 107;
        public static int KALIMBA = 108;
        public static int BAGPIPE = 109;
        public static int FIDDLE = 110;
        public static int SHANAI = 111;
    }
    public abstract class Synth_Effects
    {
        public static int FX_RAIN_or_RAIN = 96;
        public static int FX_SOUNDTRACK_or_SOUNDTRACK = 97;
        public static int FX_CRYSTAL_or_CRYSTAL = 98;
        public static int FX_ATMOSPHERE = 99;
        public static int FX_BRIGHTSNESS_or_BRIGHTNESS = 100;
        public static int FX_GOBLINS_or_GOBLINS = 101;
        public static int FX_ECHOES_or_ECHOES = 102;
        public static int FX_SCIFI_or_SCIFI = 103;
    }
    public abstract class Synth_Pad
    {
        public static int PAD_NEW_AGE_or_NEW_AGE = 88;
        public static int PAD_WARM_or_WARM = 89;
        public static int PAD_POLYSYNTH_or_POLYSYNTH = 90;
        public static int PAD_CHOIR_or_CHOIR = 91;
        public static int PAD_BOWED_or_BOWED = 92;
        public static int PAD_METALLIC_or_METALLIC = 93;
        public static int PAD_HALO_or_HALO = 94;
        public static int PAD_SWEEP_or_SWEEP = 95;
    }
    public abstract class Synth_Lead
    {
        public static int LEAD_SQUARE_or_SQUARE = 80;
        public static int LEAD_SAWTOOTH_or_SAWTOOTH = 81;
        public static int LEAD_CALLIOPE_or_CALLIOPE = 82;
        public static int LEAD_CHIFF_or_CHIFF = 83;
        public static int LEAD_CHARANG_or_CHARANG = 84;
        public static int LEAD_VOICE_or_VOICE = 85;
        public static int LEAD_FIFTHS_or_FIFTHS = 86;
        public static int LEAD_BASSLEAD_or_BASSLEAD = 87;
    }
    public abstract class Pipe
    {
        public static int PICCOLO = 72;
        public static int FLUTE = 73;
        public static int RECORDER = 74;
        public static int PAN_FLUTE = 75;
        public static int BLOWN_BOTTLE = 76;
        public static int SKAKUHACHI = 77;
        public static int WHISTLE = 78;
        public static int OCARINA = 79;
    }
    public abstract class Reed
    {
        public static int SOPRANO_SAX = 64;
        public static int ALTO_SAX = 65;
        public static int TENOR_SAX = 66;
        public static int BARITONE_SAX = 67;
        public static int OBOE = 68;
        public static int ENGLISH_HORN = 69;
        public static int BASSOON = 70;
        public static int CLARINET = 71;
    }
    public abstract class Brass
    {
        public static int TRUMPET = 56;
        public static int TROMBONE = 57;
        public static int TUBA = 58;
        public static int MUTED_TRUMPET = 59;
        public static int FRENCH_HORN = 60;
        public static int BRASS_SECTION = 61;
        public static int SYNTH_BRASS_1 = 62;
        public static int SYNTH_BRASS_2 = 63;
    }
    public abstract class Ensemble
    {
        public static int STRING_ENSEMBLE_1 = 48;
        public static int STRING_ENSEMBLE_2 = 49;
        public static int SYNTH_STRINGS_1 = 50;
        public static int SYNTH_STRINGS_2 = 51;
        public static int CHOIR_AAHS = 52;
        public static int VOICE_OOHS = 53;
        public static int SYNTH_VOICE = 54;
        public static int ORCHESTRA_HIT = 55;
    }
    public abstract class Strings
    {
        public static int VIOLIN = 40;
        public static int VIOLA = 41;
        public static int CELLO = 42;
        public static int CONTRABASS = 43;
        public static int TREMOLO_STRING = 44;
        public static int PIZZICATO_STRINGS = 45;
        public static int ORCHESTRAL_STRINGS = 46;
        public static int TIMPANI = 47;
    }
    public abstract class Bass
    {
        public static int ACOUSTIC_BASS = 32;
        public static int ELECTRIC_BASS_FINGER = 33;
        public static int ELECTRIC_BASS_PICK = 34;
        public static int FRETLESS_BASS = 35;
        public static int SLAP_BASS_1 = 36;
        public static int SLAP_BASS_2 = 37;
        public static int SYNTH_BASS_1 = 38;
        public static int SYNTH_BASS_2 = 39;
    }
    public abstract class Guitar
    {
        public static int GUITAR_or_NYLON_STRING_GUITAR = 24;
        public static int STEEL_STRING_GUITAR = 25;
        public static int ELECTRIC_JAZZ_GUITAR = 26;
        public static int ELECTRIC_CLEAN_GUITAR = 27;
        public static int ELECTRIC_MUTED_GUITAR = 28;
        public static int OVERDRIVEN_GUITAR = 29;
        public static int DISTORTION_GUITAR = 30;
        public static int GUITAR_HARMONICS = 31;
    }
    public abstract class Organ
    {
        public static int DRAWBAR_ORGAN = 16;
        public static int PERCUSSIVE_ORGAN = 17;
        public static int ROCK_ORGAN = 18;
        public static int CHURCH_ORGAN = 19;
        public static int REED_ORGAN = 20;
        public static int ACCORDION = 21;
        public static int HARMONICA = 22;
        public static int TANGO_ACCORDION = 23;
    }
    public abstract class Chromatic_Percussion
    {
        public static int CELESTA = 8;
        public static int GLOCKENSPIEL = 9;
        public static int MUSIC_BOX = 10;
        public static int VIBRAPHONE = 11;
        public static int MARIMBA = 12;
        public static int XYLOPHONE = 13;
        public static int TUBULAR_BELLS = 14;
        public static int DULCIMER = 15;
    }
    public abstract class Piano
    {
        public static int PIANO_or_ACOUSTIC_GRAND = 0;
        public static int BRIGHT_ACOUSTIC = 1;
        public static int ELECTRIC_GRAND = 2;
        public static int HONKEY_TONK = 3;
        public static int ELECTRIC_PIANO_1 = 4;
        public static int ELECTRIC_PIANO_2 = 5;
        public static int HARPISCHORD = 6;
        public static int CLAVINET = 7;
    }
}
