package cc.unknown.util.sound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SoundUtil {
	private Clip clip;

	public void playLocalSound() {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(SoundUtil.class.getResource("/assets/minecraft/sakura/sound/knockknock.ogg")));
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
