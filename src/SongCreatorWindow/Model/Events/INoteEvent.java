package SongCreatorWindow.Model.Events;

import SongCreatorWindow.Model.Core.Note;
import SongCreatorWindow.Model.Core.Path;

public interface INoteEvent extends IMusicEvent
{
    void onNoteAdded(Path path, Note note);
}
