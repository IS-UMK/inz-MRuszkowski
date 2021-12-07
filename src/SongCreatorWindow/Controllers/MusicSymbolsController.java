package SongCreatorWindow.Controllers;

import SongCreatorWindow.Model.Core.IPlayable;
import SongCreatorWindow.Model.Core.Path;
import SongCreatorWindow.Model.Events.IClickedEvent;
import SongCreatorWindow.Model.Events.IPathEvent;
import SongCreatorWindow.View.ViewMusicSymbolsSelectionHandling;

public class MusicSymbolsController implements IClickedEvent, IPathEvent
{
    ViewMusicSymbolsSelectionHandling view;

    public MusicSymbolsController(ViewMusicSymbolsSelectionHandling view)
    {
        this.view = view;
    }

    @Override
    public void onMusicSymbolClicked(Path path, IPlayable musicSound)
    {
        path.setSelectedMusicSound(musicSound);
    }

    @Override
    public void onPathCreated(Path path) {
        path.addListener(this.view);
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
}
