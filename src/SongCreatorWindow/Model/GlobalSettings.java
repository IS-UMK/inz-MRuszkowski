package SongCreatorWindow.Model;

import SongCreatorWindow.Model.Core.MusicKeySelection;
import SongCreatorWindow.Model.Core.NoteSelection;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

public class GlobalSettings
{
    /**
     * File extension where projects data is stored
     */
    public final static String projectsExtensions = "mrinz";
    /**
     * File extension where model of HashMap "Project name --->  Project Destination Path" is stored
     */
    public final static String fileNameWithProjectList = "projectsList.dat";
    /**
     * File extension where ListView items are stored in order. This file can be deleted before starting the program, projects on list will be sorted then.
     */
    public final static String fileNameWithProjectListView = "projectsListView.dat";
    /**
     * Extension of MIDI files
     */
    public final static String midiExtension = "mid";

    /**
     * Height of Canvas
     */
    public static double Height = 200;
    /**
     * Window size constants for canvas Width
     */
    public static double Width = Screen.getPrimary().getBounds().getWidth();

    /**
     * Stroke thickness setting
     */
    public static int strokeLineWidthForSelection = 10;
    public final static int strokeLineBorderWidth = 2;
    public static Color selectionColor = Color.BLUE;

    /**
     * Music Key Width setting
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

    /**
     * Path model constant
     */
    public final static int numberOfPropertySquaresInPath = 3;
    public final static int widthOfAreaWhereCanvasExtends = 300;
    public final static int fixedXPositionOfNotes = -40;
    public static int canvasExtension = 300;
    public static double getLinesStartHeight() { return Height / 5; }
    /**
     * Left and right margin of five lines
     */
    public static double getLinesMargins() { return Height / 10; }
    /**
     * Padding between another lines
     */
    public static double getLinesPadding() { return Height / 10; }
    public static double getStartXofAreaWhereInsertingNotesIsLegal()
    {
        return numberOfPropertySquaresInPath * Height + musicKeyWidth;
    }

    /**
     * Note Width setting
     */
    public static int noteWidth = 100;
    public static int noteHeight = 100;
    public static NoteSelection chosenNote = NoteSelection.WholeNote;

    /**
     * Default music key selection for new path setting
     */
    public static MusicKeySelection defaultMusicKey = MusicKeySelection.ViolinKey;
}
