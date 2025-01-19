package cc.unknown.util.sound;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SoundUtil {
    private Player player;

    @SneakyThrows
    public void playSound() {
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
    }

    public void stopSound() {
        if (player != null) {
            player.close();
        }
    }
}