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
import cc.unknown.util.player.FriendUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;

@ModuleInfo(aliases = "Anti Cheat", description = "Anticheat incorporado.", category = Category.PLAYER)
public class AntiCheat extends Module {

    public final BooleanValue angle = new BooleanValue("Aim Assist", this, false);
    public final BooleanValue autoBlock = new BooleanValue("Auto Block", this, false);
    public final BooleanValue legitScaffold = new BooleanValue("Legit Scaffold", this, false);
    public final BooleanValue invalidMotion = new BooleanValue("Invalid Motion", this, false);
    public final BooleanValue noFall = new BooleanValue("No Fall", this, false);
    public final BooleanValue noSlow = new BooleanValue("No Slow", this, false);
    public final BooleanValue scaffold = new BooleanValue("Scaffold", this, false);
    public final BooleanValue velocity = new BooleanValue("Velocity", this, false);
    public final BooleanValue omniSprint = new BooleanValue("Omni Sprint", this, false);

    public final BooleanValue selfCheck = new BooleanValue("Self", this, false);
    private final Set<EntityPlayer> hackers = new HashSet<>();
    private final ArrayList<Check> checks = new ArrayList<>();

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        for (EntityPlayer player : mc.world.playerEntities) {
            for (Check check : checks) {
                if ((selfCheck.getValue() || player != mc.player) && !player.isDead && !FriendUtil.isFriend(player)) {
                	if (isCheckEnabled(check.getName())) {
                        check.onPreLiving(player);
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

    public boolean isHacker(EntityPlayer ent) {
        for (EntityPlayer hacker : hackers) {
            if (!ent.getName().equals(hacker.getName())) continue;
            return true;
        }
        return false;
    }
    
    private boolean isCheckEnabled(String name) {
        switch (name) {
            case "Angle": return angle.getValue();
            case "Auto Block": return autoBlock.getValue();
            case "Legit Scaffold": return legitScaffold.getValue();
            case "Invalid Motion": return invalidMotion.getValue();
            case "No Fall": return noFall.getValue();
            case "No Slow": return noSlow.getValue();
            case "Scaffold": return scaffold.getValue();
            case "Velocity": return velocity.getValue();
            case "Omni Sprint": return omniSprint.getValue();
            default: return false;
        }
    }
}