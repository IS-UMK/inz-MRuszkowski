package SongCreatorWindow.Model.Core;

/**
 *
 * Static helper class for JFugue library that provides constant variables that represents duration of notes
 */
public class Duration {
    private Duration(){}
    
    /**
     * Char that represent whole note in JFugue library
     */
    static public char Whole = 'w';
    /**
     * Char that represent half note in JFugue library
     */
    static public char Half = 'h';
    /**
     * Char that represent quater note in JFugue library
     */
    static public char Quarter = 'q';
    /**
     * Char that represent eighth note in JFugue library
     */
    static public char  Eighth = 'i';
    /**
     * Char that represent sixteenth note in JFugue library
     */
    static public char Sixteenth = 's';
    /**
     * Char that represent thirty-second note in JFugue library
     */
    static public char Thirty_second = 't';
    /**
     * Char that represent sixty-fourth note in JFugue library
     */
    static public char Sixty_fourth = 'x';
    /**
     * Char that represent one-twenty-eighth note in JFugue library
     */
    static public char One_twenty_eighth = 'o';

    static public String[] getDurations()
    {
        return new String[]{
                "Whole",
                "Half",
                "Quarter",
                "Eighth",
                "Sixteenth",
                "Thirty_second",
                "Sixty_fourth",
                "One_twenty_eighth"
        };
    }

    static public String getDurationNameByCharacter(char c)
    {
        return switch (c)
        {
            case 'w' -> "Whole";
            case 'h' -> "Half";
            case 'q' -> "Quarter";
            case 'i' -> "Eighth";
            case 's' -> "Sixteenth";
            case 't' -> "Thirty_second";
            case 'x' -> "Sixty_fourth";
            case 'o' -> "One_twenty_eighth";
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    static public char getCharacterByDurationName(String c)
    {
        return switch (c)
                {
                    case "Whole" -> 'w';
                    case "Half" -> 'h';
                    case "Quarter" -> 'q';
                    case "Eighth" -> 'i';
                    case "Sixteenth" -> 's';
                    case "Thirty_second" -> 't';
                    case "Sixty_fourth" -> 'x';
                    case "One_twenty_eighth" -> 'o';
                    default -> throw new IllegalStateException("Unexpected value: " + c);
                };
    }
}
