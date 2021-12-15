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
    void setDuration(char newDuration);

    int getSoundHeight();
    void setSoundHeight(int y);

    String getSoundType();
    String getValue();
    int getNumericalNoteValue();

    void setValue(String value);
    void setVolume(byte volume);
    byte getVolume();

    TieSelection getSoundConcatenation();
    void setSoundConcatenation(TieSelection tie);
    public void setSoundConcatenation(boolean b);
    IPlayable getPreviousTiedSound();
    void setPreviousTiedSound(IPlayable previousTiedSound);
    IPlayable getNextTiedSound();
    void setNextTiedSound(IPlayable nextTiedSound);

    void setInstrument(int instrumentValue);
    int getInstrument();

    void setSharpness(boolean isSharp);
    void setFlatness(boolean isFlat);
    boolean isSharp();
    boolean isFlat();
    SoundModification getModification();
    /**
     * @return JFugue string
     */
    String ExtractJFugueSoundString(boolean withInstrument);

}
