package cc.unknown.util.sound;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SoundUtil {
    private Player player;

    public void playLocalSound() {
        try {
            InputStream inputStream = SoundUtil.class.getResourceAsStream("/assets/minecraft/sakura/sound/knockknock.mp3");
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            player = new Player(bufferedInputStream);
            new Thread(() -> {
                try {
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopLocalSound() {
        if (player != null) {
            player.close();
        }
    }
}