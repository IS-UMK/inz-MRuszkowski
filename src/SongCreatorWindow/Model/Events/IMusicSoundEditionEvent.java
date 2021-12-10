package SongCreatorWindow.Model.Events;

import SongCreatorWindow.Model.Core.IPlayable;
import SongCreatorWindow.Model.Core.Path;
import SongCreatorWindow.Model.Core.TieSelection;

public interface IMusicSoundEditionEvent extends IMusicEvent
{

    void onMusicSoundSelectedToEdition(Path path, IPlayable musicSound);

    void onMusicSoundClearSelection();

    void onMusicSoundTieCheck(IPlayable musicSound, TieSelection previousTie);

    void onMusicSoundOccurrenceTimeChanged(Path path, IPlayable musicSound);

    void onMusicSoundHeightChange(Path path, IPlayable musicSound);

    void onMusicSoundDurationChange(Path path, IPlayable musicSound);
}
