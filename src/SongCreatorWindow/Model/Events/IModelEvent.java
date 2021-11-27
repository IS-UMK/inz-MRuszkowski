package SongCreatorWindow.Model.Events;

public interface IModelEvent  extends IMusicEvent
{
    void onModelLoaded(int latestTimeX);
}
