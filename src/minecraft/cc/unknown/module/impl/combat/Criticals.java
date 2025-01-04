package cc.unknown.module.impl.combat;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.ghost.AimAssist;
import cc.unknown.module.impl.movement.Stuck;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Criticals", description = "Consigue un golpe crítico cada vez que atacas", category = Category.COMBAT)
public final class Criticals extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Packet"))
            .add(new SubMode("Verus"))
            .add(new SubMode("Balance"))
            .add(new SubMode("Freeze"))
            .add(new SubMode("Legit"))
            .setDefault("Balance");
    
    private final NumberValue delay = new NumberValue("Delay", this, 500, 0, 1000, 1, () -> mode.is("balance"));
    private final NumberValue packets = new NumberValue("Packets", this, 45, 1, 100, 1, () -> !mode.is("Packet"));
    
    private final NumberValue timer = new NumberValue("Timer", this, 0.5, 0, 1, 0.1, () -> !mode.is("balance"));
    private final NumberValue timerTime = new NumberValue("Balance Delay", this, 2000, 100, 3000, 100, () -> !mode.is("balance"));
    private final NumberValue chance = new NumberValue("Chance", this, 90, 0, 100, 1, () -> !mode.is("balance"));
    
    private long startTimer;
    private boolean delayed = false;

    private final double[] offsets = new double[]{0.0625, 0}, VALUES = new double[]{0.0005D, 0.0001D};
    private final StopWatch stopwatch = new StopWatch();
    
    private boolean attacked;
    private int ticks;
    public static boolean stuckEnabled;
    
    @Override
    public void onEnable() {
        stuckEnabled = false;
    }
    
    @Override
    public void onDisable() {
        if (startTimer != -1) {
            mc.timer.timerSpeed = 1.0f;
        }
        startTimer = -1;
    }

    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
        String type = mode.getValue().getName().toLowerCase();
        
        if (chance.getValue().doubleValue() != 100.0 && Math.random() >= chance.getValue().doubleValue() / 100.0) {
            return;
        }
        
        switch (type) {
            case "packet":
                if (stopwatch.finished(delay.getValue().longValue()) && mc.player.onGroundTicks > 2) {
                    for (double offset : offsets) {
                        for (int i = 0; i < packets.getValue().intValue(); i++) {
	                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY + offset, mc.player.posZ, true));
	                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY, mc.player.posZ, false));
    					}
                    }
                    mc.player.onCriticalHit(event.getTarget());
                    stopwatch.reset();
                }
                break;
            
            case "verus":
                if (stopwatch.finished(delay.getValue().longValue()) && mc.player.onGroundTicks > 2 && mc.player.hurtTime != 0) {
                    for (double offset : offsets) {
                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY + offset, mc.player.posZ, false));
                    }
                    mc.player.onCriticalHit(event.getTarget());
                    stopwatch.reset();
                }
                break;
            
            case "balance":
            	attacked = true;
                break;
                
            case "Legit":
        		if(mc.theWorld.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 1, mc.player.posZ)).getBlock().isCollidable()) {
        			if(mc.player.onGround && mc.player.jumpMovementFactor == 0.125)
        				mc.player.jump();
        		}
        		break;
            case "Freeze":
                if ((getModule(KillAura.class).target != null || getModule(AimAssist.class).target != null) && mc.player.onGround) {
                    mc.player.jump();
                }
                if (mc.player.fallDistance > 0) {
                    getModule(Stuck.class).setEnabled(true);
                    stuckEnabled = true;
                }
                if ((getModule(KillAura.class).target == null || getModule(AimAssist.class).target == null) && stuckEnabled) {
                    getModule(Stuck.class).setEnabled(false);
                    stuckEnabled = false;
                }
            	break;
        }
    };

    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {        
        if (mode.is("balance")) {
            if (mc.player == null) return;

            if (startTimer != -1) {
                if (mc.player.onGround || delayed || System.currentTimeMillis() - startTimer > timerTime.getValue().longValue()) {
                    mc.timer.timerSpeed = 1.0f;
                    startTimer = -1;
                    attacked = false;
                }
            } else if (mc.player.motionY < 0 && !mc.player.onGround && !delayed && attacked) {
                if (mc.timer.timerSpeed != timer.getValue().floatValue() && chance.getValue().doubleValue() != 100 && Math.random() * 100 > chance.getValue().doubleValue()) {
                    delayed = true;
                    return;
                }

                if (isTargetNearby(getModule(KillAura.class).isEnabled() ? getModule(KillAura.class).range.getValue().intValue() : 3)) {
                    startTimer = System.currentTimeMillis();
                    mc.timer.timerSpeed = timer.getValue().floatValue();
                }
            } else if (mc.player.onGround) {
                delayed = false;
            }
        }
    };

    private boolean isTargetNearby(double dist) {
        return mc.theWorld.playerEntities.stream().filter(target -> target != mc.player).anyMatch(target -> new Vec3(target).distanceTo(mc.player) < dist);
    }
}
