package SongCreatorWindow.Model;

import SongCreatorWindow.Model.Core.MusicClefSelection;
import SongCreatorWindow.Model.Core.Path;
import SongCreatorWindow.Model.Core.SoundTypeSelection;
import SongCreatorWindow.Model.Core.TieSelection;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

public class GlobalSettings
{
    /**
     * File extension where projects data is stored
     */
    public final static String projectsExtensions = "mrinz";
    public static boolean loadingProject = false;
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
    public static double musicClefWidth = 100.0;
    public static boolean duplicatingPath = false;

    public static  double getMusicClefHeight(MusicClefSelection selection)
    {
        return switch (selection)
                {
                    case ViolinClef -> 170.62;
                    case BassClef -> 111.11; // 53 - F4
                    case AltoClef -> 107.95; // 60 - C5
                };
    }
    public static double getMusicClefHeight()
    {
        return getMusicClefHeight(defaultMusicClef);
    }

    /**
     * Path model constant
     */
    public final static int numberOfPropertySquaresInPath = 3;
    public final static int widthOfAreaWhereCanvasExtends = 300;
    public final static int fixedXPositionOfNotes = -40;
    public static int canvasExtension = 600;
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
        return numberOfPropertySquaresInPath * Height + musicClefWidth;
    }

    /**
     * Note Width setting
     */
    public static int noteWidth = 100;
    public static int noteHeight = 100;
    public static char chosenNote = 'w';

    /**
     * Default music key selection for new path setting
     */
    public static MusicClefSelection defaultMusicClef = MusicClefSelection.ViolinClef;

    /**
     * Instrument selection for particular note
     */
    public static int InstrumentForParticularNoteChoice = 1;
    /**
     * Instrument selection for entire path
     */
    public static int InstrumentChoice = 1;
    /**
     * User chooses what type of sound will be inserted
     */
    public static SoundTypeSelection selectedTypeOfSoundToInsertIntoPath = SoundTypeSelection.Note;
    public static TieSelection tieBetweenNotes = TieSelection.None;
    public static boolean skipBindingAfterLoad = false;
    public static String accordSelectionName = "maj";

    /**
     * Bar settings
     */
    public static double constBarFactor_WidthPerTick = (512 / (Path.getSoundTimeX(1)-Path.getSoundTimeX(0)));
}
