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
    /**
     * @return JFugue string
     */
    public int getSoundHeight();
    public void setSoundHeight(int y);
    String ExtractJFugueSoundString();
}