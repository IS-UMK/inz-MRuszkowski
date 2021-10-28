package MainWindow;

import javafx.scene.control.Label;
import org.jfugue.player.ManagedPlayer;
import org.jfugue.player.Player;
import org.jfugue.pattern.Pattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PlaySongThread implements Runnable
{
    Label _logLabel;
    Method _log;
    Player _player;
    ManagedPlayer _managedPlayer;
    Pattern _pattern;

    public PlaySongThread(Label logLabel, Method log, Player player, ManagedPlayer managedPlayer, Pattern pattern)
    {
        _logLabel = logLabel;
        _log = log;
        _player = player;
        _managedPlayer = managedPlayer;
        _pattern = pattern;
    }
    @Override
    public void run() {
        try {
            if (!_managedPlayer.isPlaying()) {
                _log.invoke(_logLabel, String.format("Playing song %s has started", _pattern));
                _player.play(_pattern);

                while (!this._managedPlayer.isFinished()) {
                    try {
                        Thread.sleep(20L);
                    } catch (InterruptedException var3) {
                    }
                }

                _log.invoke(_logLabel, String.format("Playing song %s has been finished", _pattern));
            } else {
                _log.invoke(_logLabel, String.format("Cannot play song because there is already different one playing", _pattern));
                return;
            }
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {

        }
    }
}
