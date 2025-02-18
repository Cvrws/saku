package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.RenderLabelEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.font.Font;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;

@ModuleInfo(aliases = "Name Tags", description = "Renderiza el nombre de los jugadores", category = Category.VISUALS)
public final class NameTags extends Module {
	
	private final NumberValue distance = new NumberValue("Distance", this, 2.4, 1, 7, 0.1);
	private final NumberValue scale = new NumberValue("Scale", this, 2.4, 0.1, 10, 0.1);
	private final BooleanValue selfTag = new BooleanValue("Self Tag", this, true);
	private final BooleanValue dropShadow = new BooleanValue("Drop shadow", this, true);
	private final BooleanValue showDistance = new BooleanValue("Show Distance", this, false);
	private final BooleanValue showHealth = new BooleanValue("Show Health", this, false);
	private final BooleanValue showHitsToKill = new BooleanValue("Show Hits to Kill", this, false);
	private final BooleanValue onlyRenderName = new BooleanValue("Only Render Name", this, false);
	private final BooleanValue checkInvis = new BooleanValue("Show Invisibles", this, false);
	private final DescValue armorSettings = new DescValue("Armor Settings", this);
	private final BooleanValue showArmor = new BooleanValue("Show Armor", this, true);
	private final BooleanValue showEnchants = new BooleanValue("Show enchant", this, true, () -> !showArmor.getValue());
	private final BooleanValue showDurability = new BooleanValue("Show Durability", this, true, () -> !showArmor.getValue());
	private final BooleanValue showStackSize = new BooleanValue("Show StackSize", this, true, () -> !showArmor.getValue());
    private Font font = Fonts.MAISON.get(16, Weight.NONE);

	@EventLink
	public final Listener<RenderLabelEvent> onRenderLabel = event -> {
        if (event.getTarget() instanceof EntityPlayer && ((EntityPlayer)event.getTarget()).deathTime == 0) {
        	EntityPlayer player = (EntityPlayer) event.getTarget();
        	
        	event.setCancelled();
            String name;
            
            if (!checkInvis.getValue() && player.isInvisible()) {
                return;
            }
            
            if (onlyRenderName.getValue()) {
            	name = player.getName();
            } else {
            	name = player.getDisplayName().getFormattedText();
            }
            
            if (showHealth.getValue()) {
                name = name + " " + PlayerUtil.getHealthStr(player) + " HP";
            }
            
            if (showHitsToKill.getValue()) {
                name = name + " " + PlayerUtil.getHitsToKill(player, mc.player.getCurrentEquippedItem());
            }
            
            if (showDistance.getValue()) {
                int distance = Math.round(mc.player.getDistanceToEntity(player));
                String color = "§";
                if (distance < 8) {
                    color += "c";
                }
                else if (distance < 30) {
                    color += "6";
                }
                else if (distance < 60) {
                    color += "e";
                }
                else if (distance < 90) {
                    color += "a";
                }
                else {
                    color += "2";
                }
                name = color + distance + "m§r " + name;
            }

            event.setCancelled();
            
            renderNewTag(event, player, name);
        }
	};
	
	private void renderNewTag(RenderLabelEvent event, EntityPlayer player, String name) {
        if (PlayerUtil.unusedNames(player) && player.getName().contains("AstralMC-CTM")) {
        	return;
        }
        
		float nameWidth = font.width(name);
        float compactWidth = nameWidth + 12;
        float compactHeight = font.height() + 2;
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

		if (showArmor.getValue()) {
			renderArmor(player);
		}
		
	    if (player.isSneaking()) {
	        GlStateManager.translate(0.0F, 9.374999F, 0.0F);
	    }
	    
	    GlStateManager.disableLighting();
	    GlStateManager.depthMask(false);
	    GlStateManager.disableDepth();
	    GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        mc.fontRendererObj.draw(name, -nameWidth / 2, 0, -1, dropShadow.getValue());
        
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
	}
	
	private void renderItemStack(final ItemStack stack, final int x, final int y) {
	    GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.enableAlpha();
	    fixGlintShit();
	    mc.getRenderItem().zLevel = -150.0F;

	    GlStateManager.disableDepth();
	    mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
	    if (showStackSize.getValue() && !(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemBow) && !(stack.getItem() instanceof ItemTool) && !(stack.getItem() instanceof ItemArmor)) {
	    	mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, y);
	    }
	    mc.getRenderItem().zLevel = 0.0F;

	    renderEnchantText(stack, x, y);

	    GlStateManager.popMatrix();
	}
	
	private void renderArmor(EntityPlayer e) {
		int pos = 0;
		for (ItemStack is : e.inventory.armorInventory) {
			if (is != null) {
				pos -= 8;
			}
		}
		if (e.getHeldItem() != null) {
			pos -= 8;
			ItemStack item = e.getHeldItem().copy();
			if (item.hasEffect() && (item.getItem() instanceof ItemTool || item.getItem() instanceof ItemArmor)) {
				item.stackSize = 1;
			}
			renderItemStack(item, pos, -20);
			pos += 16;
		}
		for (int i = 3; i >= 0; --i) {
			ItemStack stack = e.inventory.armorInventory[i];
			if (stack != null) {
				renderItemStack(stack, pos, -20);
				pos += 16;
			}
		}
	}
	
	private void renderEnchantText(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
		int newY = y - 24;
		if (showDurability.getValue() && stack.getItem() instanceof ItemArmor) {
			int remainingDurability = stack.getMaxDamage() - stack.getItemDamage();
			mc.fontRendererObj.drawWithShadow(String.valueOf(remainingDurability), (float) (x * 2), (float) y, 16777215);
		}
		
		if (showEnchants.getValue() && stack.getEnchantmentTagList() != null && stack.getEnchantmentTagList().tagCount() < 6) {
			if (stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemArmor) {
				NBTTagList nbttaglist = stack.getEnchantmentTagList();
				for(int i = 0; i < nbttaglist.tagCount(); ++i) {
					int id = nbttaglist.getCompoundTagAt(i).getShort("id");
					int lvl = nbttaglist.getCompoundTagAt(i).getShort("lvl");
					if (lvl > 0) {
						String abbreviated = getEnchantmentAbbreviated(id);
						mc.fontRendererObj.drawWithShadow(abbreviated + lvl, (float) (x * 2), (float) newY, -1);
						newY += 8;
					}
				}
			}
		}
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
	}
	
	private float getSize(EntityPlayer player) {
		return Math.max(mc.player.getDistanceToEntity(player) / 4.0F, distance.getValue().floatValue());
	}

	private String getEnchantmentAbbreviated(int id) {
		switch (id) {
		case 0:
			return "pt";   // Protection
		case 1:
			return "frp";   // Fire Protection
		case 2:
			return "ff";    // Feather Falling
		case 3:
			return "blp";   // Blast Protection
		case 4:
			return "prp";   // Projectile Protection
		case 5:
			return "thr";   // Thorns
		case 6:
			return "res";   // Respiration
		case 7:
			return "aa";    // Aqua Affinity
		case 16:
			return "sh";   // Sharpness
		case 17:
			return "smt";   // Smite
		case 18:
			return "ban";   // Bane of Arthropods
		case 19:
			return "kb";    // Knockback
		case 20:
			return "fa";    // Fire Aspect
		case 21:
			return "lot";  // Looting
		case 32:
			return "eff";   // Efficiency
		case 33:
			return "sil";   // Silk Touch
		case 34:
			return "ub";   // Unbreaking
		case 35:
			return "for";   // Fortune
		case 48:
			return "pow";   // Power
		case 49:
			return "pun";   // Punch
		case 50:
			return "flm";   // Flame
		case 51:
			return "inf";   // Infinity
		default:
			return null;
		}
	}
	
	private static void fixGlintShit() {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }
}