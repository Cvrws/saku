package cc.unknown.module.impl.other;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StreamerUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.sound.RadioPlayer;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.StringValue;
import cc.unknown.value.impl.SubMode;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

@ModuleInfo(aliases = "Music Player", description = "Reproductor de música", category = Category.OTHER)
public class MusicPlayer extends Module {

    private final ModeValue mode = new ModeValue("Type", this)
            .add(new SubMode("I Love Radio"))
            .add(new SubMode("NCS"))
            .add(new SubMode("NightCore"))
            .add(new SubMode("90s"))
            .add(new SubMode("Local"))
            .setDefault("Local");

    private final StringValue text = new StringValue("URL:", this, "C:\\Users\\admin\\Music", () -> !mode.is("Local"));
    
    private boolean started = false;
    private String song = "Loading...";

    private List<File> musicFiles;
    private volatile AdvancedPlayer player;
    private final RadioPlayer radio = new RadioPlayer();
    
    @Override
    public void onEnable() {
    	started = true;
    }

    @Override
    public void onDisable() {   
        stopMusic();
        stopLocal();
        started = false;
    }

    @EventLink(value = Priority.EXTREMELY_HIGH)
    public final Listener<TickEvent> onTick = event -> {
        if (started) {
            if (mode.is("Local")) {
                loadMusicFiles(text.getValue());
                if (musicFiles != null && !musicFiles.isEmpty()) {
                    playSong();
                } else {
                	setMessage("No local music files found.");
                }
            } else {
            	setMessage("Playing " + mode.getValue().getName() + " playlist.");
                playOtherMusic(mode.getValue().getName());
            }
            started = false;
        }
    };
    
    private void playLocal(File musicFile) {
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

    private void playOtherMusic(String mode) {
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

    private void loadMusicFiles(String directoryPath) {
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

    private void playSong() {
        if (musicFiles != null && !musicFiles.isEmpty()) {
            File currentFile = musicFiles.remove(new Random().nextInt(musicFiles.size()));
            String fileName = currentFile.getName();
            String songName = fileName.replace(".mp3", "");
            
            songName = songName.substring(0, 1).toUpperCase() + songName.substring(1).toLowerCase();

            setMessage("Playing " + songName);
            playLocal(currentFile);
        } else {
            setMessage("No more songs to play.");
        }
    }
    
    private void stopMusic() {
    	Runnable musicRunnable = () -> {
            if (radio != null) {
                radio.stop();
            }
        };

        new Thread(musicRunnable).start();
    }
    
    private void stopLocal() {
        if (player != null) {
            player.close();
            player = null;
        }
    }
    
    private void setMessage(String message) {
    	PlayerUtil.displayInClient(StreamerUtil.pink + "[S]" + " " + StreamerUtil.red + message);
    }
}