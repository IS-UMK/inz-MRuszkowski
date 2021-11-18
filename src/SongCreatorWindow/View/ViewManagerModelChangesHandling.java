package SongCreatorWindow.View;

import SongCreatorWindow.Model.Core.Path;
import SongCreatorWindow.Model.Events.NoteEvent;
import SongCreatorWindow.Model.Events.PathEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ViewManagerModelChangesHandling implements PathEvent, NoteEvent
{
    //Data structures for GUI Components
    List<Canvas> canvasList;
    HashMap<Path, Canvas> canvasMap;
    HashMap<Canvas, Slider> volumeSliderMap;
    HashMap<Canvas, ChoiceBox> choiceBoxMap;
    HashMap<Canvas, TextField> tempoMap;
    HashMap<Canvas, MenuItem> selectionMenuItemToCanvas;

    public ViewManagerModelChangesHandling()
    {
        canvasList = new LinkedList<Canvas>();
        canvasMap = new HashMap<>();
        volumeSliderMap = new HashMap<Canvas, Slider>();
        choiceBoxMap = new HashMap<>();
        tempoMap = new HashMap<>();
        selectionMenuItemToCanvas = new HashMap<>();
    }

    @Override
    public void onNoteAdded() {

    }

    @Override
    public void onPathCreated() {

    }
}
