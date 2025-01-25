package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.event.impl.render.RenderLabelEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

@ModuleInfo(aliases = "Name Tags v2", description = "Renderiza el nombre de los jugadores", category = Category.VISUALS)
public final class NameTags2 extends Module {
	
    boolean players = true;
    boolean invis;
    boolean mobs = false;
    boolean animals = false;
    float _x;
    float _y;
    float _z;
    private ArrayList entities;
    
    private final ModeValue mode = new ModeValue("Health Mode", this)
    		.add(new SubMode("Hearts"))
    		.add(new SubMode("Percentage"))
    		.setDefault("Health");
    
    private final NumberValue scale = new NumberValue("Scale", this, 5, 0.1, 10, 0.1);
    private final NumberValue range = new NumberValue("Range", this, 5, 5, 512, 5);
    private final BooleanValue armor = new BooleanValue("Armor", this, true);
    private final BooleanValue durability = new BooleanValue("Durability", this, true);
    private final BooleanValue distance = new BooleanValue("Distance", this, true);
    
    @EventLink
    public final Listener<RenderLabelEvent> onRenderLabel = event -> {
        boolean _0 = event.getTarget().getDisplayName().getFormattedText() != null;
        boolean _1 = !event.getTarget().getDisplayName().getFormattedText().equals("");
        boolean _2 = event.getTarget() instanceof EntityPlayer && PlayerUtil.canTarget(event.getTarget(), true);
        boolean _3 = ((double) mc.player.getDistanceToEntity(event.getTarget()) <= range.getValue().floatValue());
        if ( _0 && _1  && _2 && _3) {
            event.setCancelled(true);
        }
    };
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        ArrayList<EntityLivingBase> playersArr = new ArrayList<>();

        Iterator playerIterator = mc.world.playerEntities.iterator();

        while (playerIterator.hasNext()) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) playerIterator.next();
            if ((double) mc.player.getDistanceToEntity(entityLivingBase) > range.getValue().floatValue()) {
                playersArr.remove(entityLivingBase);
            } else if (entityLivingBase.getName().contains("[NPC]")) {
                playersArr.remove(entityLivingBase);
            } else if (entityLivingBase.isEntityAlive()) {
                if (entityLivingBase.isInvisible()) {
                    playersArr.remove(entityLivingBase);
                } else if (entityLivingBase == mc.player) {
                    playersArr.remove(entityLivingBase);
                } else {
                    if (playersArr.size() > 100) {
                        break;
                    }

                    if (!playersArr.contains(entityLivingBase)) {
                        playersArr.add(entityLivingBase);
                    }
                }
            } else playersArr.remove(entityLivingBase);
        }

        _x = 0.0F;
        _y = 0.0F;
        _z = 0.0F;
        playerIterator = playersArr.iterator();

        while (playerIterator.hasNext()) {
            EntityPlayer player = (EntityPlayer) playerIterator.next();
            if (PlayerUtil.canTarget(player, true)) {
                player.setAlwaysRenderNameTag(false);
                _x = (float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX);
                _y = (float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY);
                _z = (float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ);
                renderNametag(player, _x, _y, _z);
            }
        }
    };

    private String getHealth(EntityPlayer player) {
        DecimalFormat decimalFormat = new DecimalFormat("0.#");
        return mode.is("Percentage") ? decimalFormat.format(player.getHealth() * 5.0F + player.getAbsorptionAmount() * 5.0F) : decimalFormat.format(player.getHealth() / 2.0F + player.getAbsorptionAmount() / 2.0F);
    }

    private void drawNames(EntityPlayer player) {
        float llllIIllllIlllI = (float) getWidth(getPlayerName(player)) / 2.0F + 2.2F;
        float llllIIllllIllIl;
        llllIIllllIlllI = llllIIllllIllIl = (float) ((double) llllIIllllIlllI + (getWidth(" " + getHealth(player)) / 2) + 2.5D);
        float llllIIllllIllII = -llllIIllllIlllI - 2.2F;
        float llllIIllllIlIll = (float) (getWidth(getPlayerName(player)) + 4);
        if (mode.is("Percentage")) {
            RenderUtil.drawBorderedRect(llllIIllllIllII, -3.0F, llllIIllllIlllI, 10.0F, 1.0F, (new Color(20, 20, 20, 180)).getRGB(), (new Color(10, 10, 10, 200)).getRGB());
        } else {
            RenderUtil.drawBorderedRect(llllIIllllIllII + 5.0F, -3.0F, llllIIllllIlllI, 10.0F, 1.0F, (new Color(20, 20, 20, 180)).getRGB(), (new Color(10, 10, 10, 200)).getRGB());
        }

        GlStateManager.disableDepth();
        if (mode.is("Percentage")) {
            llllIIllllIlIll += (float) (getWidth(getHealth(player)) + getWidth(" %") - 1);
        } else {
            llllIIllllIlIll += (float) (getWidth(getHealth(player)) + getWidth(" ") - 1);
        }

        drawString(getPlayerName(player), llllIIllllIllIl - llllIIllllIlIll, 0.0F, 16777215);

        int blendColor;
        if (player.getHealth() > 10.0F) {
            blendColor = ColorUtil.blend(new Color(-16711936), new Color(-256), (1.0F / player.getHealth() / 2.0F * (player.getHealth() - 10.0F))).getRGB();
        } else {
            blendColor = ColorUtil.blend(new Color(-256), new Color(-65536), 0.1F * player.getHealth()).getRGB();
        }

        if (mode.is("Percentage")) {
            drawString(getHealth(player) + "%", llllIIllllIllIl - (float) getWidth(getHealth(player) + " %"), 0.0F, blendColor);
        } else {
            drawString(getHealth(player), llllIIllllIllIl - (float) getWidth(getHealth(player) + " "), 0.0F, blendColor);
        }

        GlStateManager.enableDepth();
    }

    private void drawString(String string, float x, float y, int z) {
        mc.fontRendererObj.drawWithShadow(string, x, y, z);
    }

    private int getWidth(String string) {
        return mc.fontRendererObj.width(string);
    }

    private void startDrawing(float x, float y, float z, EntityPlayer player) {
        float rotateX = mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;
        double scaleRatio = (double) (getSize(player) / 10.0F * scale.getValue().floatValue()) * 1.5D;
        GL11.glPushMatrix();
        RenderUtil.start();
        GL11.glTranslatef(x, y, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(mc.getRenderManager().playerViewX, rotateX, 0.0F, 0.0F);
        GL11.glScaled(-0.01666666753590107D * scaleRatio, -0.01666666753590107D * scaleRatio, 0.01666666753590107D * scaleRatio);
    }

    private void stopDrawing() {
        RenderUtil.stop();
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void renderNametag(EntityPlayer player, float x, float y, float z) {
        y += (float) (1.55D + (player.isSneaking() ? 0.5D : 0.7D));
        startDrawing(x, y, z, player);
        drawNames(player);
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
        if (armor.getValue()) {
            renderArmor(player);
        }

        stopDrawing();
    }

    private void renderArmor(EntityPlayer player) {
        ItemStack[] armor = player.inventory.armorInventory;
        int pos = 0;

        for (ItemStack is : armor) {
            if (is != null) {
                pos -= 8;
            }
        }

        if (player.getHeldItem() != null) {
            pos -= 8;
            ItemStack var10 = player.getHeldItem().copy();
            if (var10.hasEffect() && (var10.getItem() instanceof ItemTool || var10.getItem() instanceof ItemArmor)) {
                var10.stackSize = 1;
            }

            renderItemStack(var10, pos, -20);
            pos += 16;
        }

        armor = player.inventory.armorInventory;

        for (int i = 3; i >= 0; --i) {
            ItemStack var11 = armor[i];
            if (var11 != null) {
                renderItemStack(var11, pos, -20);
                pos += 16;
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private String getPlayerName(EntityPlayer player) {
        boolean isDistanceSettingToggled = distance.getValue();
        return (isDistanceSettingToggled ? (new DecimalFormat("#.##")).format(mc.player.getDistanceToEntity(player)) + "m " : "") + player.getDisplayName().getFormattedText();
    }

    private float getSize(EntityPlayer player) {
        return Math.max(mc.player.getDistanceToEntity(player) / 4.0F, 2.0F);
    }

    private void renderItemStack(ItemStack is, int xPos, int yPos) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0F;
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        mc.getRenderItem().renderItemAndEffectIntoGUI(is, xPos, yPos);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, is, xPos, yPos);
        mc.getRenderItem().zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
        GlStateManager.disableDepth();
        renderEnchantText(is, xPos, yPos);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GlStateManager.popMatrix();
    }

    private void renderEnchantText(ItemStack is, int xPos, int yPos) {
        int newYPos = yPos - 24;
        if (is.getEnchantmentTagList() != null && is.getEnchantmentTagList().tagCount() >= 6) {
            mc.fontRendererObj.drawWithShadow("god", (float) (xPos * 2), (float) newYPos, 16711680);
        } else {
            if (is.getItem() instanceof ItemArmor) {
                int protection = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, is);
                int projectileProtection = EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, is);
                int blastProtectionLvL = EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, is);
                int fireProtection = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, is);
                int thornsLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, is);
                int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is);
                int remainingDurability = is.getMaxDamage() - is.getItemDamage();
                if (durability.getValue()) {
                    mc.fontRendererObj.drawWithShadow(String.valueOf(remainingDurability), (float) (xPos * 2), (float) yPos, 16777215);
                }

                if (protection > 0) {
                    mc.fontRendererObj.drawWithShadow("prot" + protection, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (projectileProtection > 0) {
                    mc.fontRendererObj.drawWithShadow("proj" + projectileProtection, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (blastProtectionLvL > 0) {
                    mc.fontRendererObj.drawWithShadow("bp" + blastProtectionLvL, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (fireProtection > 0) {
                    mc.fontRendererObj.drawWithShadow("frp" + fireProtection, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (thornsLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("th" + thornsLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (unbreakingLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("unb" + unbreakingLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }
            }

            if (is.getItem() instanceof ItemBow) {
                int powerLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, is);
                int punchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, is);
                int flameLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, is);
                int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is);
                if (powerLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("pow" + powerLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (punchLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("pun" + punchLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (flameLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("flame" + flameLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (unbreakingLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("unb" + unbreakingLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }
            }

            if (is.getItem() instanceof ItemSword) {
                int sharpnessLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, is);
                int knockbackLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, is);
                int fireAspectLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, is);
                int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is);
                if (sharpnessLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("sh" + sharpnessLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (knockbackLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("kb" + knockbackLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (fireAspectLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("fire" + fireAspectLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (unbreakingLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("unb" + unbreakingLvl, (float) (xPos * 2), (float) newYPos, -1);
                }
            }

            if (is.getItem() instanceof ItemTool) {
                int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is);
                int efficiencyLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, is);
                int fortuneLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, is);
                int silkTouchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, is);
                if (efficiencyLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("eff" + efficiencyLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (fortuneLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("fo" + fortuneLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (silkTouchLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("silk" + silkTouchLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (unbreakingLvl > 0) {
                    mc.fontRendererObj.drawWithShadow("ub" + unbreakingLvl, (float) (xPos * 2), (float) newYPos, -1);
                }
            }

            if (is.getItem() == Items.golden_apple && is.hasEffect()) {
                mc.fontRendererObj.drawWithShadow("god", (float) (xPos * 2), (float) newYPos, -1);
            }

        }
    }
}