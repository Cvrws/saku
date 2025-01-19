package cc.unknown.util.sound;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.sound.sampled.FloatControl;

import cc.unknown.Sakura;
import cc.unknown.module.impl.other.MusicPlayer;
import cc.unknown.util.client.StopWatch;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import lombok.Getter;
import lombok.Setter;

public class RadioPlayer {
    private Thread thread;
    private Player player = null;
    @Getter @Setter private String current;
    private FloatControl control = null;
    private final StopWatch timer = new StopWatch();

    public void start(final String url) {
    	MusicPlayer musicPlayer = Sakura.instance.getModuleManager().get(MusicPlayer.class);
    	assert musicPlayer != null;
    	
        if (this.timer.finished(5L)) {
            (this.thread = new Thread(() -> {
                try {
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{new TrustAllCertificates()}, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                    try {
                        this.player = new Player(new URL(url).openStream());
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }

                    //setVolume();
                    this.player.play();
                } catch (JavaLayerException | NoSuchAlgorithmException | KeyManagementException e2) {
                	
                }
            })).start();
            this.timer.reset();
        }
    }

    public void stop() {
    	Runnable musicTask = () -> {
	        if (this.thread != null) {
	            this.thread.interrupt();
	            this.thread = null;
	        }
	        if (this.player != null) {
	            this.player.close();
	            this.player = null;
	        }
        };

        new Thread(musicTask).start();
    }

}