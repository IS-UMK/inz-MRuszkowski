package SongCreatorWindow.Model;

import javafx.stage.Screen;

public class GlobalSettings
{
    /**
     * File extension where projects data is stored
     */
    public final static String projectsExtensions = "mrinz";

    /**
     * window size constants
     */
    public static double Height = 200;
    public static double Width = Screen.getPrimary().getBounds().getWidth();

    /**
     * Stroke settings
     */
    public static int strokeLineWidthForSelection = 10;

    /**
     * Music Key settings
     */
    public static int musicKeyWidth = 100;
    public final static int numberOfPropertySquaresInPath = 3;

    /**
     * Note settings
     */
    public static int noteWidth = 100;
}
