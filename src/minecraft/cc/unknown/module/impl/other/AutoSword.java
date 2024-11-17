package cc.unknown.module.impl.other;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.ItemUtil;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

@ModuleInfo(aliases = "Auto Sword", description = "Auto Gun bassically", category = Category.OTHER)
public final class AutoSword extends Module {

	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
        float lastDamage = 0;
        int lastSlot = -1;
        for (int i = 0; i < 9; i++) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == null) continue;
            final float damage = ItemUtil.getCombatDamage(stack);
            if ((stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemPickaxe) &&
                    damage > lastDamage) {
                lastDamage = damage;
                lastSlot = i;
            }
        }
        if (lastSlot != -1 && mc.player.inventory.currentItem != lastSlot)
            mc.player.inventory.currentItem = lastSlot;
	};
}
