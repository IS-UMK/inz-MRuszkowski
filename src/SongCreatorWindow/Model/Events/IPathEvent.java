package SongCreatorWindow.Model.Events;

import SongCreatorWindow.Model.Core.Path;

public interface IPathEvent extends IMusicEvent
{
    void onPathCreated(Path path);
    void onPathNameRenamed(Path path);
    void onPathClearSelection();
    void onPathDeleted(Path path);
    void onPathClefChanged(Path path);
}
