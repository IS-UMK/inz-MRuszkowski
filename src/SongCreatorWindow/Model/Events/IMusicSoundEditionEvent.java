package SongCreatorWindow.Model.Events;

import SongCreatorWindow.Model.Core.IPlayable;
import SongCreatorWindow.Model.Core.Path;

public interface IMusicSoundEditionEvent extends IMusicEvent
{

    void onMusicSoundSelectedToEdition(Path path, IPlayable musicSound);

    void onMusicSoundClearSelection(Path path, IPlayable musicSound);
}
