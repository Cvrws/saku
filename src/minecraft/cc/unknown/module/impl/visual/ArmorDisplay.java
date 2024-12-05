package cc.unknown.module.impl.visual;

import java.util.Map;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.api.armor.EnchantmentProperty;
import cc.unknown.module.impl.visual.api.armor.TextFormatting;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

@ModuleInfo(aliases = "Armor Display", description = "Muestra tu armadura", category = Category.VISUALS)
public final class ArmorDisplay extends Module {

	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		if (mc.player == null || mc.player.isSpectator()) {
			return;
		}

		int yOffset = 56;
		if (mc.player.isInsideOfMaterial(Material.water) && mc.player.getAir() > 0) {
			yOffset += 10;
		} else if (mc.player.isRiding()) {
			final Entity entity = mc.player.ridingEntity;
			if (entity instanceof EntityLivingBase) {
				final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
				final float maxHealth = entityLivingBase.getMaxHealth();
				if (maxHealth > 40.0) {
					yOffset += 20;
				} else if (maxHealth > 20.0) {
					yOffset += 10;
				}
			} else {
				yOffset -= 10;
			}
		}

		final ScaledResolution scaledResolution = mc.scaledResolution;
		final int xPosition = scaledResolution.getScaledWidth() / 2 + 10 + 16 * 4;
		final int yPosition = scaledResolution.getScaledHeight() - yOffset;
		for (int i = 0; i <= 4; ++i) {
			ItemStack item;
			if (i == 0) {
				item = mc.player.getHeldItem();
			} else {
				item = mc.player.inventory.armorInventory[i - 1];
			}
			if (item != null) {
				RenderHelper.enableGUIStandardItemLighting();
				final RenderItem itemRenderer = mc.getRenderItem();
				itemRenderer.renderItemAndEffectIntoGUI(item, xPosition - (i * 16), yPosition);
				itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, item, xPosition - (i * 16), yPosition, null);
				
				RenderHelper.disableStandardItemLighting();

				GlStateManager.disableDepth();
				GlStateManager.pushMatrix();
				GlStateManager.scale(0.5F, 0.5F, 0.0F);

				int j = 0;
				for (final Map.Entry<Integer, Integer> entry : EnchantmentHelper.getEnchantments(item).entrySet()) {
					final EnchantmentProperty enchantmentProperty = EnchantmentProperty.enchantment.get(entry.getKey());
					if (enchantmentProperty == null) {
						continue;
					}
					final int level = entry.getValue();
					final TextFormatting levelColor = EnchantmentProperty.getLevelColor(level, enchantmentProperty.getMaxLevel());
					final String text = TextFormatting.translate(String.format("&r%s%s%d&r", enchantmentProperty.getShortName(), levelColor, level));
					RenderUtil.drawShadedString(text, (xPosition - (i * 16)) * 2, (yPosition + (j * 4)) * 2, -1);
					++j;
				}

				GlStateManager.popMatrix();
				GlStateManager.enableDepth();
			}
		}
	};
}