package cc.unknown.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.event.impl.render.RenderLabelEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

@ModuleInfo(aliases = "Name Tags", description = "Renderiza el nombre de los jugadores", category = Category.VISUALS)
public final class NameTags extends Module {
	
	private final BoundsNumberValue distance = new BoundsNumberValue("Distance", this, 2, 6, 1, 7, 0.1);
	private final NumberValue scale = new NumberValue("Scale", this, 4.5, 0.1, 10, 0.1);
	private final BooleanValue selfTag = new BooleanValue("Self Tag", this, true);
	private final BooleanValue background = new BooleanValue("Background", this, false);
	private final NumberValue alphaBackground = new NumberValue("Alpha Background", this, 110, 0, 255, 1, () -> !background.getValue());
	private final BooleanValue armor = new BooleanValue("Armor", this, false);
	private final BooleanValue checkInvis = new BooleanValue("Show Invisibles", this, false);

	@EventLink
	public final Listener<RenderLabelEvent> onRenderLabel = event -> {
        if (event.getTarget() instanceof EntityPlayer && ((EntityPlayer)event.getTarget()).deathTime == 0 && (checkInvis.getValue() || !((EntityPlayer)event.getTarget()).isInvisible())) {
            EntityPlayer player = (EntityPlayer) event.getTarget();
            String name = player.getDisplayName().getFormattedText();
            
            if (event.getTarget() == mc.player && !selfTag.getValue()) {
            	return;
            }
            
            event.setCancelled();
            
            renderNewTag(event, player, name);
        }
	};
	
	private void renderNewTag(RenderLabelEvent event, EntityPlayer player, String name) {
	    float nameWidth = mc.fontRendererObj.width(name);
	    double scaleRatio;
	    float scale = 0.02666667F;
	    
	    if (player == mc.player) {
	        scaleRatio = 1.0D;
	    } else {
	        scaleRatio = (double) (getSize(player) / 10.0F * this.scale.getValue().doubleValue()) * 1.5D;
	    }
	    
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) event.getX() + 0.0F, (float) event.getY() + player.height + 0.5F, (float) event.getZ());
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale * scaleRatio, -scale * scaleRatio, scale * scaleRatio);
	        		
	    if (player.isSneaking() || mc.player.isSneaking()) {
	        GlStateManager.translate(0.0F, 9.374999F, 0.0F);
	    }
	    
		GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
		if (armor.getValue()) {
			renderArmor(player);
		}
	    
	    if (background.getValue()) {
	        float backgroundWidth = nameWidth + 12;
	        float backgroundHeight = mc.fontRendererObj.FONT_HEIGHT + 2;
	        RenderUtil.roundedRect(-backgroundWidth / 2 + 1 , -4.0F, backgroundWidth / 2 - 3, backgroundHeight, 6, new Color(0, 0, 0, alphaBackground.getValue().intValue()).getRGB());
	    }

        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        mc.fontRendererObj.draw(name, -nameWidth / 2, 0, -1);
		
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        
		RenderHelper.disableStandardItemLighting();
		mc.entityRenderer.disableLightmap();
	}
	
	private void renderItem(final ItemStack stack, final int x, final int y) {
	    GlStateManager.pushMatrix();

	    try {
	        GlStateManager.enableBlend();
	        GlStateManager.enableAlpha();
	        RenderHelper.enableStandardItemLighting();
	        mc.getRenderItem().zLevel = -150.0F;

	        GlStateManager.disableDepth();
	        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
	        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, y);
	        mc.getRenderItem().zLevel = 0.0F;
	        RenderHelper.disableStandardItemLighting();

	        renderEnchantText(stack, x, y);

	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        GlStateManager.disableBlend();
	        GlStateManager.disableAlpha();
	        GlStateManager.enableDepth();
	        GlStateManager.popMatrix();
	    }
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

			renderItem(var10, pos, -20);
			pos += 16;
		}

		armor = player.inventory.armorInventory;

		for (int i = 3; i >= 0; --i) {
			ItemStack var11 = armor[i];
			if (var11 != null) {
				renderItem(var11, pos, -20);
				pos += 16;
			}
		}

		RenderHelper.disableStandardItemLighting();
		mc.entityRenderer.disableLightmap();
	}
	
	private void renderEnchantText(ItemStack stack, int x, int y) {
		int newY = y - 10;
		int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
		
		if (stack.getEnchantmentTagList() != null && stack.getEnchantmentTagList().tagCount() >= 6) {
			mc.fontRendererObj.drawWithShadow("god", (float) (x * 2), (float) newY, 16711680);
		} else {
			if (stack.getItem() instanceof ItemArmor) {
				int protection = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
				int projectileProtection = EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack);
				int blastProtectionLvL = EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack);
				int fireProtection = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack);
				int thornsLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
				int remainingDurability = stack.getMaxDamage() - stack.getItemDamage();
				
			    String[] enchantments = {"prot" + protection, "proj" + projectileProtection, "bp" + blastProtectionLvL, "frp" + fireProtection, "th" + thornsLvl, "unb" + unbreakingLvl};
			    
			    for (String enchantment : enchantments) {
			        if (enchantment != null && !enchantment.endsWith("0")) {
			            mc.fontRendererObj.drawWithShadow(enchantment, (float) (x * 2) + 2, (float) newY, -1);
			            newY += 8;
			        }
			    }
			}
			
			if (stack.getItem() instanceof ItemBow) {
				int powerLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
				int punchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
				int flameLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);

			    String[] enchantments = {"pow" + powerLvl, "pun" + punchLvl, "flame" + flameLvl, "unb" + unbreakingLvl};
			    
			    for (String enchantment : enchantments) {
			        if (enchantment != null && !enchantment.endsWith("0")) {
			            mc.fontRendererObj.drawWithShadow(enchantment, (float) (x - 33), (float) newY, -1);
			            newY += 8;
			        }
			    }
			}

			if (stack.getItem() instanceof ItemSword) {
			    int sharpnessLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
			    int knockbackLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack);
			    int fireAspectLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);

			    String[] enchantments = {"sh" + sharpnessLvl, "kb" + knockbackLvl, "fire" + fireAspectLvl, "unb" + unbreakingLvl};
			    
			    for (String enchantment : enchantments) {
			        if (enchantment != null && !enchantment.endsWith("0")) {
			            mc.fontRendererObj.drawWithShadow(enchantment, (float) (x - 33), (float) newY, -1);
			            newY += 8;
			        }
			    }
			}
			
			if (stack.getItem() instanceof ItemTool) {
				int efficiencyLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
				int fortuneLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
				int silkTouchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack);

			    String[] enchantments = {"eff" + efficiencyLvl, "fo" + fortuneLvl, "silk" + silkTouchLvl, "ub" + unbreakingLvl};
			    
			    for (String enchantment : enchantments) {
			        if (enchantment != null && !enchantment.endsWith("0")) {
			            mc.fontRendererObj.drawWithShadow(enchantment, (float) (x - 33), (float) newY, -1);
			            newY += 8;
			        }
			    }
			}

			if (stack.getItem() == Items.golden_apple && stack.hasEffect()) {
				mc.fontRendererObj.drawWithShadow("god", (float) (x * 2), (float) newY, -1);
			}
		}
	}
	
	private float getSize(EntityPlayer player) {
		return Math.max(mc.player.getDistanceToEntity(player) / distance.getValue().floatValue(), distance.getSecondValue().floatValue());
	}

}