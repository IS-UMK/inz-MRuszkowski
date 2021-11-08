package Model;

/**
 *
 * Inteface to extracting song as JFugue string
 */
public interface IPlayable {
    /**
     * Get the moment when note occurs
     */
    double getTimeX();

    /**
     * set the moment when note occurs
     * @param x
     */
    void setTimeX(double x);
    /**
     * @return JFugue string
     */
    String ExtractJFugueSoundString();
}
