/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainWindow;

import SongCreatorWindow.SongCreator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

/**
 *
 * @author Mateusz Ruszkowski
 */
public class MainWindow {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO: Wstawić kod na repozytorium
        //new SongCreator();

        // This is “Twinkle, twinkle, little star”
        Pattern pattern1 = new Pattern("C5q C5q G5q G5q A5q A5q Gh");
        // This is “How I wonder what you are”
        Pattern pattern2 = new Pattern("F5q F5q E5q E5q D5q D5q C5h");
        // This is “Up above the world so high”
        // and “Like a diamond in the sky”
        Pattern pattern3 = new Pattern("G5q G5q F5q F5q E5q E5q D5h");

        Pattern twinkleSong = new Pattern();
        twinkleSong.add(pattern1);
        twinkleSong.add(pattern2);
        twinkleSong.add(pattern3);
        twinkleSong.add(pattern3);
        twinkleSong.add(pattern1);
        twinkleSong.add(pattern2);

        Player player = new Player();
        player.play(twinkleSong);
    }
}
