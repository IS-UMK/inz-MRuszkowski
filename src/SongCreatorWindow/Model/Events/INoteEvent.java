package SongCreatorWindow.Model.Events;

import SongCreatorWindow.Model.Core.IPlayable;
import SongCreatorWindow.Model.Core.Path;

public interface INoteEvent extends IMusicEvent
{
    void onMusicSymbolAdded(Path path, IPlayable musicSound);
}
