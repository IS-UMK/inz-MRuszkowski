package SongCreatorWindow.Model.Core;

import java.io.Serializable;

/**
 *
 * Inteface to extracting song as JFugue string
 */
public interface IPlayable extends Serializable {
    /**
     * Get the moment when note occurs
     */
    int getTimeX();

    /**
     * set the moment when note occurs
     * @param x
     */
    void setTimeX(int x);
    public NoteSelection getDuration();
    public int getSoundHeight();
    public void setSoundHeight(int y);
    /**
     * @return JFugue string
     */
    String ExtractJFugueSoundString();
}
