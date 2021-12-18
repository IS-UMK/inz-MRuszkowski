package SongCreatorWindow.Model.Events;

import SongCreatorWindow.Model.Core.Accord;
import SongCreatorWindow.Model.Core.IPlayable;
import SongCreatorWindow.Model.Core.Path;
import SongCreatorWindow.Model.Core.TieSelection;

public interface IMusicSoundEditionEvent extends IMusicEvent
{

    void onMusicSoundSelectedToEdition(Path path, IPlayable musicSound);

    void onMusicSoundClearSelection();

    void onMusicSoundTieCheck(Path path, IPlayable musicSound);

    void onMusicSoundOccurrenceTimeChanged(Path path, IPlayable musicSound);

    void onMusicSoundHeightChange(Path path, IPlayable musicSound);

    void onMusicSoundDurationChange(Path path, IPlayable musicSound);

    void onMusicSoundModified(Path path, IPlayable musicSound);

    void onAccordNameChanged(Path path, IPlayable musicSound);

    void onMusicSoundConvertedToAccord(Path path, IPlayable musicSound, Accord newAccord);

    void onMusicSoundConvertedToNote(Path path, IPlayable musicSound, IPlayable newNote);

    void onMusicSoundDeleted(Path path, IPlayable musicSound);

    /**
     * Method is created due to error that occurs when parameter of music sound named "timeX" changes.
     * Tree map that gives access to MenuItem of the relevant music sound, after change of this parameter,
     * returns null in case when item is obligated to move up or down on list.
     * @param path Path that given music Sound belongs to
     * @param musicSound Particular music Sound of the given path
     */
    void onMusicSoundOccurrenceTimeChangedPreprocess(Path path, IPlayable musicSound);
}
