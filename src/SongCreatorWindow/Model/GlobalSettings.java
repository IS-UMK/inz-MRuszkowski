package SongCreatorWindow.Model;

import SongCreatorWindow.Model.Core.MusicKeySelection;
import SongCreatorWindow.Model.Core.NoteToNumericValue;
import javafx.stage.Screen;

public class GlobalSettings
{
    /**
     * File extension where projects data is stored
     */
    public final static String projectsExtensions = "mrinz";
    public final static String fileNameWithProjectList = "projectsList.dat";
    public final static String fileNameWithProjectListView = "projectsListView.dat";
    public final static String midiExtension = "mid";

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
    public static double musicKeyWidth = 100.0;
    public static double getMusicKeyHeight()
    {
        double height = 0;

        switch (defaultMusicKey)
        {
            case ViolinKey:
                height = 170.62;
                break;
            case BassKey:
                height = 111.11; // 53 - F4
                break;
            case AltoKey:
                height = 107.95; // 60 - C5
                break;
        }

        return height;
    }
    public final static int numberOfPropertySquaresInPath = 3;

    /**
     * Note settings
     */
    public static int noteWidth = 100;

    /**
     * Default music key selection for new path
     */
    public static MusicKeySelection defaultMusicKey = MusicKeySelection.ViolinKey;
}
