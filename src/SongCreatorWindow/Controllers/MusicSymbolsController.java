package SongCreatorWindow.Controllers;

import SongCreatorWindow.Model.Core.IPlayable;
import SongCreatorWindow.Model.Core.Path;
import SongCreatorWindow.Model.Events.IClickedEvent;
import SongCreatorWindow.Model.Events.IPathEvent;
import SongCreatorWindow.View.ViewManagerModelChangesHandling;
import SongCreatorWindow.View.ViewMusicSymbolsSelectionHandling;

public class MusicSymbolsController implements IClickedEvent, IPathEvent
{
    ViewManagerModelChangesHandling modelView;
    ViewMusicSymbolsSelectionHandling symbolsView;

    public MusicSymbolsController(ViewManagerModelChangesHandling modelView, ViewMusicSymbolsSelectionHandling symbolsView)
    {
        this.symbolsView = symbolsView;
        this.modelView = modelView;
    }

    @Override
    public void onMusicSymbolClicked(Path path, IPlayable musicSound)
    {
        path.setSelectedMusicSound(musicSound);
    }

    @Override
    public void onPathCreated(Path path) {
        path.addListener(this.symbolsView);
        path.addListener(this.modelView);
    }

    @Override
    public void onPathNameRenamed(Path path) {

    }

    @Override
    public void onPathClearSelection() {

    }

    @Override
    public void onPathDeleted(Path path) {

    }

    @Override
    public void onPathClefChanged(Path path, int soundShift) {

    }
}
