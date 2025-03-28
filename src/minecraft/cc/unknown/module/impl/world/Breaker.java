package cc.unknown.module.impl.world;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = {"Breaker", "fucker"}, description = "Rompe automaticamente la cama/huevo", category = Category.WORLD)
public class Breaker extends Module {
	
    public final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Through Walls"))
            .add(new SubMode("Surroundings"))
            .setDefault("Through Walls");
    
    private final NumberValue Range = new NumberValue("Range", this, 4, 1, 5, 0.1);

    public final BooleanValue rotate = new BooleanValue("Rotate", this, true);
    public final BooleanValue movementCorrection = new BooleanValue("Movement Correction", this, false);
    public final BooleanValue whitelistFriendlyBed = new BooleanValue("Whitelist Friendly Bed", this, false);

    private final NumberValue fastBreakNormal = new NumberValue("FastBreak", this, 0, 0, 1, 0.1);
    private final NumberValue fastBreakBed = new NumberValue("FastBreak bed", this, 0, 0, 1, 0.1);
    private final NumberValue airMultipalyer = new NumberValue("Air Multiplier", this, 1, 0, 3, 0.1);

    private float hardness;
    private float damagetoblock;

    private int attackTicks;
    private boolean notify = true;
    private int usedItem, delay, rotateTime;
    private Vec3 teleport;

    private BlockPos coordsBed;
    private BlockPos blockToBreak;

    @Getter
    boolean breaking = false;
    
    @Override
    public void onDisable() {
        mc.playerController.curBlockDamageMP = 0;
    }
    
    @Override
    public void onEnable() {
        hardness = 0;
        damagetoblock = 0;
    }
    
    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        notify = true;
    };
    
    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<TeleportEvent> onTeleport = event -> {
        if (mc.player.getDistance(event.getPosX(), event.getPosY(), event.getPosZ()) > 30) {
            teleport = new Vec3(event.getPosX(), event.getPosY(), event.getPosZ());
        }
    };

    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        Module scaffold = getModule(Scaffold.class);
        if (scaffold == null || scaffold.isEnabled()) {
            return;
        }
        EntityPlayerSP player = mc.player;
        attackTicks++;
        delay--;
        if (delay > 0 || (whitelistFriendlyBed.getValue() && teleport != null && player.getDistanceSq(teleport.xCoord, teleport.yCoord, teleport.zCoord) < 1500)) {
            return;
        }

        boolean foundbed = false;
        int bedX = 0;
        int bedY = 0;
        int bedZ = 0;
        for (int x = -Range.getValue().intValue()+1; x <= Range.getValue().intValue()+1; x++) {
            for (int y = -Range.getValue().intValue()+1; y <= Range.getValue().intValue()+1; y++) {
                for (int z = -Range.getValue().intValue()+1; z <= Range.getValue().intValue()+1; z++) {
                    Block block = PlayerUtil.blockRelativeToPlayer(x, y, z);
                    if (block instanceof BlockBed || block instanceof BlockDragonEgg) {
                        foundbed = true;
                        bedX = x;
                        bedY = y;
                        bedZ = z;
                        BlockPos blockPos = new BlockPos(player.posX + x, player.posY + y, player.posZ + z);
                        if (damagetoblock <= 0) {
                            if (coordsBed != null) {
                                if (blockPos.distanceSq(mc.player.getPosition()) < coordsBed.distanceSq(mc.player.getPosition())) {
                                    coordsBed = blockPos;
                                }
                            } else {
                                coordsBed = blockPos;
                            }
                        }
                    }
                }
            }
        }
        ArrayList<BlockPos> nearblocks = new ArrayList<>();

        if (foundbed) {
            breaking = true;
            int airblocks = 0;
            nearblocks.add(new BlockPos(bedX + 1, bedY, bedZ));
            nearblocks.add(new BlockPos(bedX - 1, bedY, bedZ));
            nearblocks.add(new BlockPos(bedX, bedY, bedZ + 1));
            nearblocks.add(new BlockPos(bedX, bedY, bedZ - 1));
            nearblocks.add(new BlockPos(bedX, bedY + 1, bedZ));

            for (int i = 0; i < nearblocks.size(); i++) {
                BlockPos blockPos = nearblocks.get(i);
                Block block = PlayerUtil.blockRelativeToPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (block instanceof BlockBed || block instanceof BlockDragonEgg) {
                    nearblocks.remove(i);
                    nearblocks.add(new BlockPos(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ()));
                    nearblocks.add(new BlockPos(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ()));
                    nearblocks.add(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1));
                    nearblocks.add(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() - 1));
                    nearblocks.add(new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()));
                    break;
                }
            }
            for (int i = 0; i < nearblocks.size(); i++) {
                BlockPos blockPos = nearblocks.get(i);
                Block block = PlayerUtil.blockRelativeToPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (block instanceof BlockAir) {
                    airblocks++;
                }
            }
            if (airblocks > 0 || !this.mode.is("Surroundings")) {
                blockToBreak = coordsBed;
            } else {
                float minHardness = 99999999;

                for (BlockPos blockPos : nearblocks) {
                    Block block = PlayerUtil.blockRelativeToPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    if (block.getBlockHardness() < minHardness && !(block instanceof BlockBed || block instanceof BlockDragonEgg)) {
                        minHardness = block.getBlockHardness();
                        if (damagetoblock <= 0) {
                            blockToBreak = new BlockPos(player.posX + blockPos.getX(), player.posY + blockPos.getY(), player.posZ + blockPos.getZ());
                        }
                    }
                }
                for (int i = 0; i < nearblocks.size(); i++) {
                    BlockPos blockPos = nearblocks.get(i);
                    Block block = PlayerUtil.blockRelativeToPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    if (minHardness == block.getBlockHardness() && blockPos.add(player.getPosition()).distance(player.getPosition()) < blockToBreak.distance(player.getPosition())) {
                        if (damagetoblock <= 0) {
                            blockToBreak = new BlockPos(player.posX + blockPos.getX(), player.posY + blockPos.getY(), player.posZ + blockPos.getZ());
                        }
                    }
                }
            }
            
            if (blockToBreak.distance(player.getPosition()) <= Range.getValue().floatValue()) {
                if (rotate.getValue()) {
                    rotate(blockToBreak);
                }

                int slot = InventoryUtil.findTool(blockToBreak);
                if (slot != -1) PacketUtil.send(new C09PacketHeldItemChange(slot));
                if (slot != -1) hardness = InventoryUtil.getPlayerRelativeBlockHardness(player, mc.world, blockToBreak, slot);
                else hardness = InventoryUtil.getPlayerRelativeBlockHardness(player, mc.world, blockToBreak, mc.player.inventory.currentItem);
                if (!mc.player.onGround) hardness *= airMultipalyer.getValue().floatValue();

                Block currentBlock = PlayerUtil.blockRelativeToPlayer(blockToBreak.getX(), blockToBreak.getY(), blockToBreak.getZ());
                if (currentBlock instanceof BlockDragonEgg) {
                    PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockToBreak, EnumFacing.DOWN));
                    PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockToBreak, EnumFacing.DOWN));
                    mc.player.swingItem();
                    breaking = false;
                    return;
                }

                if (damagetoblock == 0) {
                    PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockToBreak, EnumFacing.DOWN));
                }

                mc.player.swingItem();
                damagetoblock += hardness;
                mc.world.sendBlockBreakProgress(player.getEntityId(), blockToBreak, (int) (damagetoblock * 10 - 1));

                if (damagetoblock >= (currentBlock instanceof BlockBed ? 1 - fastBreakBed.getValue().floatValue() : 1 - fastBreakNormal.getValue().floatValue())) {
                    damagetoblock = 0;

                    PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockToBreak, EnumFacing.DOWN));
                    mc.playerController.onPlayerDestroyBlock(blockToBreak, EnumFacing.UP);
                }

                if (slot != -1) PacketUtil.send(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
            }
        } else {
            if (breaking) {
                damagetoblock = 0;
            }
            breaking = false;
        }

        mc.playerController.curBlockDamageMP = damagetoblock;
    };

    @EventLink
    public final Listener<AttackEvent> onAttackEvent = event -> {
        if(whitelistFriendlyBed.getValue() ){
            attackTicks = 0;
        } else if (attackTicks < 10) {
            attackTicks++;
        }

        if (attackTicks < 10) {
            breaking = true;
        } else {
            breaking = false;
        }
    };

    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> {
        if (breaking) {
            final ScaledResolution scaledResolution = event.getScaledResolution();
            final double y = scaledResolution.getScaledHeight() * 0.80;
            RenderUtil.drawRoundedGradientRectTest((scaledResolution.getScaledWidth() - (mc.playerController.curBlockDamageMP * 100)) / 2, y, mc.playerController.curBlockDamageMP * 100, 10, 4,
                    getTheme().getFirstColor(), getTheme().getSecondColor(), getTheme().getFirstColor());
            RenderUtil.color(Color.WHITE);
        }
    };

    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        if (breaking) {
            try {
                double x1 = blockToBreak.getX() - mc.getRenderManager().renderPosX;
                double y1 = blockToBreak.getY() - mc.getRenderManager().renderPosY;
                double z1 = blockToBreak.getZ() - mc.getRenderManager().renderPosZ;

                double x2 = x1 + 1.0;
                double y2 = y1 + 1.0;
                double z2 = z1 + 1.0;

                renderBoundingBox(x1, y1, z1, x2, y2, z2);

            } catch (NullPointerException e) {

            }
        }
    };
    
    private void renderBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2.0f);
        GL11.glBegin(GL11.GL_LINES);
        if (coordsBed == blockToBreak) {
            GL11.glColor3f(255, 0, 0);
        } else {
            GL11.glColor3f(255, 255, 255);
        }

        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x2, y1, z1);

        GL11.glVertex3d(x2, y1, z1);
        GL11.glVertex3d(x2, y2, z1);

        GL11.glVertex3d(x2, y2, z1);
        GL11.glVertex3d(x1, y2, z1);

        GL11.glVertex3d(x1, y2, z1);
        GL11.glVertex3d(x1, y1, z1);

        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x1, y1, z2);

        GL11.glVertex3d(x2, y1, z1);
        GL11.glVertex3d(x2, y1, z2);

        GL11.glVertex3d(x2, y2, z1);
        GL11.glVertex3d(x2, y2, z2);

        GL11.glVertex3d(x1, y2, z1);
        GL11.glVertex3d(x1, y2, z2);

        GL11.glVertex3d(x1, y1, z2);
        GL11.glVertex3d(x2, y1, z2);

        GL11.glVertex3d(x2, y1, z2);
        GL11.glVertex3d(x2, y2, z2);

        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x1, y2, z2);

        GL11.glVertex3d(x1, y2, z2);
        GL11.glVertex3d(x1, y1, z2);

        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
    }
    
    public void rotate(BlockPos block) {
        if (!this.rotate.getValue()) return;
        final Vector2f rotations = RotationUtil.calculate(block);
        RotationHandler.setRotations(rotations, 10, movementCorrection.getValue() ? MoveFix.SILENT : MoveFix.OFF);
        mc.objectMouseOver.setBlockPos(block);
        mc.objectMouseOver.sideHit = EnumFacing.UP;
        mc.objectMouseOver.hitVec = new Vec3(Math.random(), 1, Math.random());
        mc.objectMouseOver.typeOfHit = MovingObjectPosition.MovingObjectType.BLOCK;
    }
}