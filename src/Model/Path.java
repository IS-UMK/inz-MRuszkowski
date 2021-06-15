package Model;

import java.util.LinkedList;

/**
 *
 * Class for JFugue to create music paths. More than one path can be played at onec.
 */
public class Path {
    String PathName;
    int PathId;
    LinkedList<IPlayable> Sound;
    int Lenght;
}
