package cc.unknown.util.sound;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cc.unknown.component.impl.render.NotificationComponent;
import cc.unknown.util.Accessor;
import cc.unknown.util.sound.radio.RadioPlayer;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MusicUtil implements Accessor {

    private String song = "Loading...";

    private List<File> musicFiles;
    private volatile AdvancedPlayer player;
    private final RadioPlayer radio = new RadioPlayer();
    
    public void playLocal(File musicFile) {
    	Runnable musicRunnable = () -> {
    		try (FileInputStream fis = new FileInputStream(musicFile)) {
                player = new AdvancedPlayer(fis);

                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        playSong();
                    }
                });
  
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(musicRunnable).start();
    }

    public void playOtherMusic(String mode) {
        Runnable musicRunnable = () -> {
            switch (mode) {
                case "I Love Radio":
                    connectToMusic("https://streams.ilovemusic.de/iloveradio1.mp3");
                    break;
                case "NCS":
                    connectToMusic("http://stream.laut.fm/my-webradio");
                    break;
                case "NightCore":
                    connectToMusic("http://stream.laut.fm/nightcoremusic");
                    break;
                case "90s":
                    connectToMusic("http://stream.laut.fm/eurobeat");
                    break;
            }
        };

        new Thread(musicRunnable).start();
    }
    
    private void connectToMusic(String url) {
        radio.start(url);
    }

    public void loadMusicFiles(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            List<String> validExtensions = Arrays.asList(".mp3");
            musicFiles = new ArrayList<>(Arrays.asList(directory.listFiles((dir, name) -> {
                for (String ext : validExtensions) {
                    if (name.endsWith(ext)) {
                        return true;
                    }
                }
                return false;
            })));
        }
    }

    public void playSong() {
        if (musicFiles != null && !musicFiles.isEmpty()) {
            File currentFile = musicFiles.remove(new Random().nextInt(musicFiles.size()));
            String fileName = currentFile.getName();
            if (fileName.endsWith(".mp3")) {
                song = fileName.replace(".mp3", "");
            }
            
            NotificationComponent.post("Music Player", "Playing " + song.toLowerCase(), 3000);
            playLocal(currentFile);
        } else {
            NotificationComponent.post("Music Player", "No more songs to play.", 1000);
        }
    }
    
    public void stopMusic() {
    	Runnable musicRunnable = () -> {
            if (radio != null) {
                radio.stop();
            }
        };

        new Thread(musicRunnable).start();
    }
    
    public void stopLocal() {
        if (player != null) {
            player.close();
            player = null;
        }
    }

    public List<File> getMusicFiles() {
        return musicFiles;
    }
}