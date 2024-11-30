package cc.unknown.module.impl.visual.api.armor;

import java.util.HashMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class EnchantmentProperty {
	private final String shortName;
	private final int maxLevel;

	public static final HashMap<Integer, EnchantmentProperty> enchantment = new HashMap<Integer, EnchantmentProperty>() {
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

	public static TextFormatting getLevelColor(final int level, final int maxLevel) {
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
