package SongCreatorWindow.Model.Events;

import SongCreatorWindow.Model.Core.IPlayable;
import SongCreatorWindow.Model.Core.Path;

public interface ISoundEvent extends IMusicEvent
{
    void onMusicSymbolAdded(Path path, IPlayable musicSound);
}
