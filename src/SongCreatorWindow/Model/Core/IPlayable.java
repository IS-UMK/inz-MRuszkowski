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
    Character getDuration();
    int getSoundHeight();
    void setSoundHeight(int y);
    String getSoundType();
    String getValue();
    /**
     * @return JFugue string
     */
    String ExtractJFugueSoundString();
}
