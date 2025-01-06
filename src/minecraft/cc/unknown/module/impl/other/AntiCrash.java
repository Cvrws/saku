package cc.unknown.module.impl.other;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.util.IChatComponent;

@ModuleInfo(aliases = "Anti Exploit", description = "Evita cualquier ataque dirigido al cliente.", category = Category.OTHER)
public class AntiCrash extends Module {

	private final BooleanValue 
	demoCheck = new BooleanValue("Demo Check", this, true),
	crashCheck = new BooleanValue("Crash Check", this, true),
	explosionCheck = new BooleanValue("Explosion Check", this, true),
	log4jCheck = new BooleanValue("Log4J Check", this, true),
	particlesCheck = new BooleanValue("Particles Check", this, true),
	illegalVelocityCheck = new BooleanValue("Illegal Velocity Check", this, true),
	resourceCheck = new BooleanValue("Resource RCE Check", this, true),
	teleportCheck = new BooleanValue("Teleport Check", this, true),
	fakeDeathCheck = new BooleanValue("Fake Death Check", this, true),
	destroySelfCheck = new BooleanValue("Destroy Self Check", this, true),
	bookCheck = new BooleanValue("Book Check", this, true);
	
	private final Pattern PATTERN = Pattern.compile(".*\\$\\{[^}]*}.*");
	private int particles;
	
    @EventLink
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
    	Packet<?> packet = event.getPacket();
    	
        if (demoCheck.getValue() && packet instanceof S2BPacketChangeGameState) {
            final S2BPacketChangeGameState wrapper = ((S2BPacketChangeGameState) packet);
            if (wrapper.getGameState() == 5 && wrapper.func_149137_d() == 0) {
            	event.setCancelled();
            }
        }
        
        if (crashCheck.getValue() && packet instanceof S2BPacketChangeGameState) {
            S2BPacketChangeGameState wrapper = (S2BPacketChangeGameState) event.getPacket();
            if ((wrapper.getGameState() == 7 || wrapper.getGameState() == 8) && wrapper.func_149137_d() >= 100) {
                event.setCancelled();
            }
        }
        
        if (destroySelfCheck.getValue() && event.getPacket() instanceof S13PacketDestroyEntities) {
            if (!isInGame()) return;
            S13PacketDestroyEntities wrapper = (S13PacketDestroyEntities) event.getPacket();
            if (Arrays.stream(wrapper.getEntityIDs()).anyMatch(i -> i == mc.player.getEntityId())) {
                event.setCancelled(true);
            }
        }
        
        if (explosionCheck.getValue() && packet instanceof S27PacketExplosion) {
            final S27PacketExplosion wrapper = ((S27PacketExplosion) packet);
            if (wrapper.func_149149_c() >= Byte.MAX_VALUE || wrapper.func_149144_d() >= Byte.MAX_VALUE || wrapper.func_149147_e() >= Byte.MAX_VALUE) {
            	event.setCancelled();
            } else if (wrapper.getStrength() == 0) {
            	event.setCancelled();
            }
        }
    	
        if (fakeDeathCheck.getValue() && event.getPacket() instanceof S19PacketEntityStatus) {
            S19PacketEntityStatus wrapper = (S19PacketEntityStatus) event.getPacket();
            if (wrapper.getOpCode() == 3 && wrapper.getEntity(mc.theWorld) == mc.player) {
                event.setCancelled(true);
            }
        }
    	
        if (particlesCheck.getValue() && packet instanceof S2APacketParticles) {
            final S2APacketParticles wrapper = ((S2APacketParticles) packet);
            particles += wrapper.getParticleCount();
            particles -= 6;
            particles = Math.min(particles, 150);
            if (particles > 100 || wrapper.getParticleCount() < 1 || Math.abs(wrapper.getParticleCount()) > 20 || wrapper.getParticleSpeed() < 0 || wrapper.getParticleSpeed() > 1000) {
            	event.setCancelled();
            }
        }
        
        if (illegalVelocityCheck.getValue() && packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) event.getPacket();
            if ((wrapper.getMotionX() > 31200 || wrapper.getMotionY() > 31200 || wrapper.getMotionZ() > 31200)) {
            	event.setCancelled();
            }
        }
    	
        if (resourceCheck.getValue() && packet instanceof S48PacketResourcePackSend) {
            final S48PacketResourcePackSend wrapper = ((S48PacketResourcePackSend) packet);
            final String url = wrapper.getURL();
            final String hash = wrapper.getHash();
            if (url.toLowerCase().startsWith("level://")) {
                check(url, hash);
                event.setCancelled();
            }
        }
    	
    	if (teleportCheck.getValue() && packet instanceof S08PacketPlayerPosLook) {
            final S08PacketPlayerPosLook wrapper = ((S08PacketPlayerPosLook) packet);
            if (Math.abs(wrapper.x) > 1E+9 || Math.abs(wrapper.y) > 1E+9 || Math.abs(wrapper.z) > 1E+9) {
            	event.setCancelled();
            }
        }
    	
    	if (bookCheck.getValue() && packet instanceof S3FPacketCustomPayload) {
    		S3FPacketCustomPayload wrapper = ((S3FPacketCustomPayload) packet);
    		if (wrapper.getChannelName() == "MC|BOpen") {
    			event.setCancelled();
    		}
    	}
    	
        if (log4jCheck.getValue()) {
            if (packet instanceof S29PacketSoundEffect) {
                final S29PacketSoundEffect wrapper = (S29PacketSoundEffect) packet;
                final String name = wrapper.getSoundName();
                if (PATTERN.matcher(name).matches()) {
                	event.setCancelled();
                }
            } else if (packet instanceof S02PacketChat) {
                final S02PacketChat wrapper = ((S02PacketChat) packet);
                final IChatComponent component = wrapper.getChatComponent();
                if (PATTERN.matcher(component.getUnformattedText()).matches() || PATTERN.matcher(component.getFormattedText()).matches()) {
                	event.setCancelled();
                }
            }
        }
    };
    
    private boolean check(String url, final String hash) {
        try {
            final URI uri = new URI(url);
            final String scheme = uri.getScheme();
            final boolean isLevelProtocol = "level".equals(scheme);
            if (!("http".equals(scheme) || "https".equals(scheme) || isLevelProtocol)) {
                throw new URISyntaxException(url, "Wrong protocol");
            }
            url = URLDecoder.decode(url.substring("level://".length()), StandardCharsets.UTF_8.toString());
            if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip"))) {
                throw new URISyntaxException(url, "Invalid levelstorage resource pack path");
            }

            return false;
        } catch (final Exception e) {
            PacketUtil.sendNoEvent(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            return true;
        }
    }
}