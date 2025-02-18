package cc.unknown.module.impl.visual;

import java.util.HashMap;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
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

		final int xPosition = event.getScaledResolution().getScaledWidth() / 2 + 10 + 16 * 4;
		final int yPosition = event.getScaledResolution().getScaledHeight() - yOffset;
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
			}
		}
	};
	
	@RequiredArgsConstructor
	@Getter
	public final class EnchantmentProperty {
		private final String shortName;
		private final int maxLevel;

		public final HashMap<Integer, EnchantmentProperty> enchantment = new HashMap<Integer, EnchantmentProperty>() {
			{
				put(0, new EnchantmentProperty("Pr", 4));
				put(1, new EnchantmentProperty("Fp", 4));
				put(2, new EnchantmentProperty("Ff", 4));
				put(3, new EnchantmentProperty("Bp", 4));
				put(4, new EnchantmentProperty("Pp", 4));
				put(5, new EnchantmentProperty("Re", 3));
				put(6, new EnchantmentProperty("Aq", 1));
				put(7, new EnchantmentProperty("Th", 3));
				put(8, new EnchantmentProperty("Ds", 3));
				put(16, new EnchantmentProperty("Sh", 5));
				put(17, new EnchantmentProperty("Sm", 5));
				put(18, new EnchantmentProperty("BoA", 5));
				put(19, new EnchantmentProperty("Kb", 2));
				put(20, new EnchantmentProperty("Fa", 2));
				put(21, new EnchantmentProperty("Lo", 3));
				put(32, new EnchantmentProperty("Ef", 5));
				put(33, new EnchantmentProperty("St", 1));
				put(34, new EnchantmentProperty("Ub", 3));
				put(35, new EnchantmentProperty("Fo", 3));
				put(48, new EnchantmentProperty("Po", 5));
				put(49, new EnchantmentProperty("Pu", 2));
				put(50, new EnchantmentProperty("Fl", 1));
				put(51, new EnchantmentProperty("Inf", 1));
				put(61, new EnchantmentProperty("LoS", 3));
				put(62, new EnchantmentProperty("Lu", 3));
			}
		};

		public TextFormatting getLevelColor(final int level, final int maxLevel) {
			if (level > maxLevel) {
				return TextFormatting.LIGHT_PURPLE;
			}
			if (level == maxLevel) {
				return TextFormatting.RED;
			}
			switch (level) {
			case 1:
				return TextFormatting.AQUA;
			case 2:
				return TextFormatting.GREEN;
			case 3:
				return TextFormatting.YELLOW;
			case 4:
				return TextFormatting.GOLD;
			}
			return TextFormatting.GRAY;
		}
	}
	
	public enum TextFormatting {
		BLACK('0', -16777216), DARK_BLUE('1', -16777046), DARK_GREEN('2', -16733696), DARK_AQUA('3', -16733526),
		DARK_RED('4', -5636096), DARK_PURPLE('5', -5635926), GOLD('6', -22016), GRAY('7', -5592406),
		DARK_GRAY('8', -11184811), BLUE('9', -11184641), GREEN('a', -11141291), AQUA('b', -11141121), RED('c', -43691),
		LIGHT_PURPLE('d', -43521), YELLOW('e', -171), WHITE('f', -1), MAGIC('k', 0), BOLD('l', 0), STRIKETHROUGH('m', 0),
		UNDERLINE('n', 0), ITALIC('o', 0), RESET('r', 0);

		private final String toString;
		private final int rgb;

		public static final char COLOR_CHAR = '\u00A7';

		TextFormatting(final char code, final int rgb) {
			this.rgb = rgb;
			this.toString = new String(new char[] { COLOR_CHAR, code });
		}

		@Override
		public String toString() {
			return this.toString;
		}

		public int getRGB() {
			return this.rgb;
		}

		public static String translate(final String text) {
			char[] b = text.toCharArray();
			for (int i = 0; i < b.length - 1; ++i) {
				if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
					b[i] = TextFormatting.COLOR_CHAR;
					b[i + 1] = Character.toLowerCase(b[i + 1]);
				}
			}
			return new String(b);
		}
	}
}