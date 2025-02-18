  package cc.unknown.module.impl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.player.anticheat.Check;
import cc.unknown.module.impl.player.anticheat.impl.*;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;

@ModuleInfo(aliases = "Anti Cheat", description = "Anticheat incorporado.", category = Category.PLAYER)
public class AntiCheat extends Module {

    public final BooleanValue aim = new BooleanValue("Aim", this, false);
    public final BooleanValue eagle = new BooleanValue("Eagle", this, false);

    public final BooleanValue selfCheck = new BooleanValue("Self", this, false);
    private final Set<EntityPlayer> hackers = new HashSet<>();
    private final ArrayList<Check> checks = new ArrayList<>();
    
    public AntiCheat() {
        addChecks(
                new AimCheck(),
                new EagleCheck()
        );
    }

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        for (EntityPlayer player : mc.world.playerEntities) {
            for (Check check : checks) {
                if ((selfCheck.getValue() || player != mc.player) && !player.isDead && !FriendUtil.isFriend(player)) {
                	if (isCheckEnabled(check.getName())) {
                        check.onPlayer(player);
                    }
                }
            }
        }
    };
    
    @EventLink
    public final Listener<PacketReceiveEvent> onReceive = event -> {
        if (event.getPacket() instanceof S14PacketEntity || event.getPacket() instanceof S18PacketEntityTeleport) {
            for (EntityPlayer player : mc.world.playerEntities) {
                for (Check check : checks) {
                    if ((selfCheck.getValue() || player != mc.player) && !player.isDead && !FriendUtil.isFriend(player)) {
                    	if (isCheckEnabled(check.getName())) {
                            check.onReceive(event, player);
                        }
                    }
                }
            }
        }
    };
    
    @Override
    public void onDisable() {
        hackers.clear();
    }
    
    public void addChecks(Check... checks) {
        this.checks.addAll(Arrays.asList(checks));
    }

    public void mark(EntityPlayer ent) {
        hackers.add(ent);
    }
    
    private boolean isCheckEnabled(String name) {
        switch (name) {
            case "Aim": return aim.getValue();
            case "Eagle": return eagle.getValue();
            default: return false;
        }
    }
}