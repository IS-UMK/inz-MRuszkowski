package SongCreatorWindow.Model.Events;

import SongCreatorWindow.Model.Core.IPlayable;
import SongCreatorWindow.Model.Core.Path;

public interface IClickedEvent {
    void onMusicSymbolClicked(Path path, IPlayable musicSound);
}
