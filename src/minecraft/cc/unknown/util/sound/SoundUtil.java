package cc.unknown.util.sound;

import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SoundUtil implements Accessor {
    private final HashMap<String, AudioInputStream> sounds = new HashMap<String, AudioInputStream>();
    private Clip clip;
	
    public void playSound(final String sound) {
        playSound(sound, 1, 1);
    }

    public void playSound(final String sound, final float volume, final float pitch) {
        mc.world.playSound(mc.player.posX, mc.player.posY, mc.player.posZ, sound, volume, pitch, false);
    }

    public void playLocalSound() {
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(SoundUtil.class.getResource("/assets/minecraft/sakura/sound/sayonara.wav")));
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stopLocalSound() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}