package cc.unknown.util.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.impl.ghost.AutoClicker;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.util.Vector3d;

@UtilityClass
public class PlayerUtil implements Accessor {
	private final HashMap<Integer, Integer> GOOD_POTIONS = new HashMap<Integer, Integer>() {
		{
			put(6, 1); // Instant Health
			put(10, 2); // Regeneration
			put(11, 3); // Resistance
			put(21, 4); // Health Boost
			put(22, 5); // Absorption
			put(23, 6); // Saturation
			put(5, 7); // Strength
			put(1, 8); // Speed
			put(12, 9); // Fire Resistance
		}
	};

	public final List<Block> BLOCK_BLACKLIST = Arrays.asList(Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest,
			Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch, Blocks.crafting_table,
			Blocks.furnace, Blocks.waterlily, Blocks.dispenser, Blocks.stone_pressure_plate,
			Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.dropper, Blocks.tnt, Blocks.standing_banner,
			Blocks.wall_banner, Blocks.redstone_torch);

	public Block block(final double x, final double y, final double z) {
		return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
	}

	public Block block(final BlockPos blockPos) {
		return mc.world.getBlockState(blockPos).getBlock();
	}

	public Block block(final Vec3i pos) {
		return block(new BlockPos(pos));
	}

	public boolean isOnEdge() {
		double posX = mc.player.posX;
		double posZ = mc.player.posZ;

		double edgeThreshold = -2.0;

		boolean onEdgeX = (posX % 1 < edgeThreshold || posX % 1 > (1 - edgeThreshold));
		boolean onEdgeZ = (posZ % 1 < edgeThreshold || posZ % 1 > (1 - edgeThreshold));

		return onEdgeX || onEdgeZ;
	}

	public Block block(final cc.unknown.util.structure.geometry.Vector3d pos) {
		return block(pos.getX(), pos.getY(), pos.getZ());
	}

	public Block block(final Vector3d pos) {
		return block(new BlockPos(new Vec3i(pos.x, pos.y, pos.z)));
	}

	public boolean lookingAtBlock(final BlockPos blockFace, final float yaw, final float pitch,
			final EnumFacing enumFacing, final boolean strict) {
		final MovingObjectPosition movingObjectPosition = mc.player
				.rayTraceCustom(mc.playerController.getBlockReachDistance(), yaw, pitch);
		if (movingObjectPosition == null)
			return false;
		final Vec3 hitVec = movingObjectPosition.hitVec;
		if (hitVec == null)
			return false;
		if ((hitVec.xCoord - blockFace.getX()) > 1.0 || (hitVec.xCoord - blockFace.getX()) < 0.0)
			return false;
		if ((hitVec.yCoord - blockFace.getY()) > 1.0 || (hitVec.yCoord - blockFace.getY()) < 0.0)
			return false;
		return !((hitVec.zCoord - blockFace.getZ()) > 1.0) && !((hitVec.zCoord - blockFace.getZ()) < 0.0)
				&& (movingObjectPosition.sideHit == enumFacing || !strict);
	}

	public boolean keysDown() {
		return Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())
				|| Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())
				|| Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())
				|| Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
	}

	public double distance(final BlockPos pos1, final BlockPos pos2) {
		final double x = pos1.getX() - pos2.getX();
		final double y = pos1.getY() - pos2.getY();
		final double z = pos1.getZ() - pos2.getZ();
		return x * x + y * y + z * z;
	}

	public double getFov(final double posX, final double posZ) {
		return getFov(mc.player.rotationYaw, posX, posZ);
	}
	
	public double fovFromTarget(Entity entity, float yaw) {
	    return ((yaw - fovToTarget(entity)) % 360.0 + 540.0) % 360.0 - 180.0;
	}

	private float fovToTarget(Entity entity) {
		double diffX = entity.posX - mc.player.posX;
		double diffZ = entity.posZ - mc.player.posZ;
		return (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
	}
	
    public double pitchFromTarget(Entity en, float offset, float pitch) {
        return (double) (pitch - pitchToEntity(en, offset));
    }

    public float pitchToEntity(Entity ent, float offset) {
        double x = mc.player.getDistanceToEntity(ent);
        double y = mc.player.posY - (ent.posY + offset);
        double pitch = (((Math.atan2(x, y) * 180.0D) / Math.PI));
        return (float) (90 - pitch);
    }

	public boolean fov(float fov, Entity entity) {
		fov = (float) ((double) fov * 0.5D);
		double v = ((double) (mc.player.rotationYaw - fovToTarget(entity)) % 360.0D + 540.0D) % 360.0D - 180.0D;
		return v > 0.0D && v < (double) fov || (double) (-fov) < v && v < 0.0D;
	}

	public double getFov(final float yaw, final double posX, final double posZ) {
		double angle = (yaw - angle(posX, posZ)) % 360.0;
		return MathHelper.wrapAngleTo180_double(angle);
	}

	public float angle(final double n, final double n2) {
		return (float) (Math.atan2(n - mc.player.posX, n2 - mc.player.posZ) * 57.295780181884766 * -1.0);
	}

	public Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
		return block(mc.player.posX + offsetX, mc.player.posY + offsetY, mc.player.posZ + offsetZ);
	}

	public Block blockAheadOfPlayer(final double offsetXZ, final double offsetY) {
		return blockRelativeToPlayer(-Math.sin(MoveUtil.direction()) * offsetXZ, offsetY,
				Math.cos(MoveUtil.direction()) * offsetXZ);
	}

	public String name(final EntityPlayer player) {
		return player.getName();
	}

	public boolean isHotbarFull() {
		for (int i = 0; i <= 36; ++i) {
			ItemStack itemstack = mc.player.inventory.getStackInSlot(i);
			if (itemstack == null) {
				return false;
			}
		}

		return true;
	}

	public boolean isBlockUnder(final double height) {
		return isBlockUnder(height, true);
	}

	public boolean isBlockUnder(final double height, final boolean boundingBox) {
		if (boundingBox) {
			final AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, -height, 0);

			if (!mc.world.getCollidingBoundingBoxes(mc.player, bb).isEmpty()) {
				return true;
			}
		} else {
			for (int offset = 0; offset < height; offset++) {
				if (blockRelativeToPlayer(0, -offset, 0).isFullBlock()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isBlockOver(final double height, final boolean boundingBox) {
		final AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, height / 2f, 0).expand(0,
				height - mc.player.height, 0);

		if (!mc.world.getCollidingBoundingBoxes(mc.player, bb).isEmpty()) {
			return true;
		}

		return false;
	}

	public boolean isOverAir() {
		return mc.world.isAirBlock(new BlockPos(MathHelper.floor_double(mc.player.posX),
				MathHelper.floor_double(mc.player.posY - 1.0D), MathHelper.floor_double(mc.player.posZ)));
	}

	public boolean isBlockUnder() {
		return isBlockUnder(10);
	}

	public boolean goodPotion(final int id) {
		return GOOD_POTIONS.containsKey(id);
	}

	public int potionRanking(final int id) {
		return GOOD_POTIONS.getOrDefault(id, -1);
	}

	public boolean inLiquid() {
		return mc.player.isInWater() || mc.player.isInLava();
	}

	public boolean blockNear(final int range) {
		for (int x = -range; x <= range; ++x) {
			for (int y = -range; y <= range; ++y) {
				for (int z = -range; z <= range; ++z) {
					final Block block = blockRelativeToPlayer(x, y, z);

					if (!(block instanceof BlockAir)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean blockNear(final int range, final Material material) {
		for (int x = -range; x <= range; ++x) {
			for (int y = -range; y <= range; ++y) {
				for (int z = -range; z <= range; ++z) {
					final Block block = blockRelativeToPlayer(x, y, z);

					if (block.getMaterial().equals(material)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean insideBlock() {
		if (mc.player.ticksExisted < 5) {
			return false;
		}

		final EntityPlayerSP player = mc.player;
		final WorldClient world = mc.world;
		final AxisAlignedBB bb = player.getEntityBoundingBox();
		for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
			for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
				for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
					final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
					final AxisAlignedBB boundingBox;
					if (block != null && !(block instanceof BlockAir)
							&& (boundingBox = block.getCollisionBoundingBox(world, new BlockPos(x, y, z),
									world.getBlockState(new BlockPos(x, y, z)))) != null
							&& player.getEntityBoundingBox().intersectsWith(boundingBox)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void sendClick(final int button, final boolean state) {
		final int keyBind = button == 0 ? mc.gameSettings.keyBindAttack.getKeyCode()
				: mc.gameSettings.keyBindUseItem.getKeyCode();

		KeyBinding.setKeyBindState(keyBind, state);

		if (state) {
			KeyBinding.onTick(keyBind);
		}
	}

	public boolean onLiquid() {
		boolean onLiquid = false;
		final AxisAlignedBB playerBB = mc.player.getEntityBoundingBox();
		final WorldClient world = mc.world;
		final int y = (int) playerBB.offset(0.0, -0.01, 0.0).minY;
		for (int x = MathHelper.floor_double(playerBB.minX); x < MathHelper.floor_double(playerBB.maxX) + 1; ++x) {
			for (int z = MathHelper.floor_double(playerBB.minZ); z < MathHelper.floor_double(playerBB.maxZ) + 1; ++z) {
				final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (block != null && !(block instanceof BlockAir)) {
					if (!(block instanceof BlockLiquid)) {
						return false;
					}
					onLiquid = true;
				}
			}
		}
		return onLiquid;
	}
	
    public EnumFacingOffset getEnumFacing(final Vec3 position) {
        return getEnumFacing(position, false);
    }

	public EnumFacingOffset getEnumFacing(final Vec3 position, boolean downwards) {
		List<EnumFacingOffset> possibleFacings = new ArrayList<>();
		for (int z2 = -1; z2 <= 1; z2 += 2) {
			if (!(block(position.xCoord, position.yCoord, position.zCoord + z2).isReplaceable(mc.world,
					new BlockPos(position.xCoord, position.yCoord, position.zCoord + z2)))) {
				if (z2 < 0) {
					possibleFacings.add(new EnumFacingOffset(EnumFacing.SOUTH, new Vec3(0, 0, z2)));
				} else {
					possibleFacings.add(new EnumFacingOffset(EnumFacing.NORTH, new Vec3(0, 0, z2)));
				}
			}
		}

		for (int x2 = -1; x2 <= 1; x2 += 2) {
			if (!(block(position.xCoord + x2, position.yCoord, position.zCoord).isReplaceable(mc.world,
					new BlockPos(position.xCoord + x2, position.yCoord, position.zCoord)))) {
				if (x2 > 0) {
					possibleFacings.add(new EnumFacingOffset(EnumFacing.WEST, new Vec3(x2, 0, 0)));
				} else {
					possibleFacings.add(new EnumFacingOffset(EnumFacing.EAST, new Vec3(x2, 0, 0)));
				}
			}
		}

		possibleFacings.sort(Comparator.comparingDouble(enumFacing -> {
			double enumFacingRotations = Math
					.toDegrees(Math.atan2(enumFacing.getOffset().zCoord, enumFacing.getOffset().xCoord)) % 360;
			double rotations = RotationHandler.rotations.x % 360 + 90;

			return Math.abs(MathUtil.wrappedDifference(enumFacingRotations, rotations));
		}));

		if (!possibleFacings.isEmpty())
			return possibleFacings.get(0);

		for (int y2 = -1; y2 <= 1; y2 += 2) {
			if (!(block(position.xCoord, position.yCoord + y2, position.zCoord).isReplaceable(mc.world,
					new BlockPos(position.xCoord, position.yCoord + y2, position.zCoord)))) {
				if (y2 < 0) {
					return new EnumFacingOffset(EnumFacing.UP, new Vec3(0, y2, 0));
				} else if (downwards) {
					return new EnumFacingOffset(EnumFacing.DOWN, new Vec3(0, y2, 0));
				}
			}
		}

		return null;
	}
	
    public Vec3 getPlacePossibility(double offsetX, double offsetY, double offsetZ) {
        return getPlacePossibility(20, offsetX, offsetY, offsetZ, null);
    }
	
	public Vec3 getPlacePossibility(int rangeV, double offsetX, double offsetY, double offsetZ, Integer sameY) {
	    List<Vec3> possibilities = new ArrayList<>();
	    int range = rangeV + (int) (Math.abs(offsetX) + Math.abs(offsetZ));

	    for (int x = -range; x <= range; ++x) {
	        for (int y = -range; y <= range; ++y) {
	            for (int z = -range; z <= range; ++z) {
	                Block block = blockRelativeToPlayer(x, y, z);
	                BlockPos blockPos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);

	                if (!block.isReplaceable(mc.world, blockPos)) {
	                    possibilities.add(new Vec3(mc.player.posX + x + 1, mc.player.posY + y, mc.player.posZ + z));
	                    possibilities.add(new Vec3(mc.player.posX + x - 1, mc.player.posY + y, mc.player.posZ + z));
	                    possibilities.add(new Vec3(mc.player.posX + x, mc.player.posY + y + 1, mc.player.posZ + z));
	                    possibilities.add(new Vec3(mc.player.posX + x, mc.player.posY + y - 1, mc.player.posZ + z));
	                    possibilities.add(new Vec3(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z + 1));
	                    possibilities.add(new Vec3(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z - 1));
	                }
	            }
	        }
	    }

	    // Filter out blocks that are too far from the player
	    possibilities = possibilities.stream()
	        .filter(vec3 -> mc.player.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord) <= 5)
	        .filter(vec3 -> PlayerUtil.block(vec3.xCoord, vec3.yCoord, vec3.zCoord)
	                .isReplaceable(mc.world, new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord)))
	        .collect(Collectors.toList());

	    if (possibilities.isEmpty()) {
	        return null;
	    }

	    if (sameY != null) {
	        possibilities = possibilities.stream()
	            .filter(vec3 -> Math.floor(vec3.yCoord + 1) == sameY)
	            .collect(Collectors.toList());

	        if (possibilities.isEmpty()) {
	            return null;
	        }
	    }

	    // Sort possibilities by proximity to the target offset
	    possibilities.sort(Comparator.comparingDouble(vec3 -> {
	        double d0 = (mc.player.posX + offsetX) - vec3.xCoord;
	        double d1 = (mc.player.posY - 1 + offsetY) - vec3.yCoord;
	        double d2 = (mc.player.posZ + offsetZ) - vec3.zCoord;
	        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
	    }));

	    return possibilities.get(0);
	}

	public double calculatePerfectRangeToEntity(Entity entity) {
		double range = 1000;
		Vec3 eyes = mc.player.getPositionEyes(1);
		Vector2f rotations = RotationUtil.calculate(entity);
		final Vec3 rotationVector = mc.player.getVectorForRotation(rotations.getY(), rotations.getX());
		MovingObjectPosition movingObjectPosition = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1)
				.calculateIntercept(eyes, eyes.addVector(rotationVector.xCoord * range, rotationVector.yCoord * range,
						rotationVector.zCoord * range));

		return movingObjectPosition.hitVec.distanceTo(eyes);
	}
	
	public boolean isTeam(EntityPlayer player) {
		return isTeam(player, true, true);
	}

	public boolean isTeam(EntityPlayer player, boolean scoreboard, boolean checkColor) {
		String entityName = player.getDisplayName().getUnformattedText();
		String playerName = mc.player.getDisplayName().getUnformattedText();

		if (entityName.length() >= 3 && playerName.startsWith(entityName.substring(0, 3))) {
			return true;
		}

		if (mc.player.isOnSameTeam(player)) {
			return true;
		}
		
		if (PlayerUtil.unusedNames(player)) {
			return false;
		}

		if (scoreboard && mc.player.getTeam() != null && player.getTeam() != null && mc.player.getTeam().isSameTeam(player.getTeam())) {
			return true;
		}

		if (checkColor && playerName != null && player.getDisplayName() != null) {
			String targetName = player.getDisplayName().getFormattedText().replace("�r", "");
			String clientName = playerName.replace("�r", "");
			return targetName.startsWith("�" + clientName.charAt(1));
		}

		return false;
	}

	public Vec3 getEyePos(Entity entity, Vec3 position) {
		return position.add(new Vec3(0, entity.getEyeHeight(), 0));
	}

	public Vec3 getEyePos(Entity entity) {
		return getEyePos(entity, new Vec3(entity));
	}

	public Vec3 getEyePos() {
		return getEyePos(mc.player);
	}

	public boolean isClicking() {
		AutoClicker clicker = Sakura.instance.getModuleManager().get(AutoClicker.class);

		if (clicker.isEnabled()) {
			return Mouse.isButtonDown(0);
		} else
			return Mouse.isButtonDown(0) && clicker != null && !clicker.isEnabled();
	}
	
	public boolean lookingAtPlayer(EntityPlayer player1, EntityPlayer player2, double m) {
		double deltaX = player2.posX - player1.posX;
		double deltaY = player2.posY - player1.posY + player1.getEyeHeight();
		double deltaZ = player2.posZ - player1.posZ;
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
		return distance < m;
	}
    
    public Item getItem() {
        ItemStack stack = getItemStack();
        return stack == null ? null : stack.getItem();
    }
    
    public ItemStack getItemStack() {
        return (mc.player == null || mc.player.inventoryContainer == null ? null : mc.player.inventoryContainer.getSlot(mc.player.inventory.currentItem + 36).getStack());
    }
    
	public void drop(int slot) {
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 1, 4, mc.player);
	}

	public void shiftClick(int slot) {
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, 1, mc.player);
	}
    
    public boolean overVoid(double posX, double posY, double posZ) {
        for (int i = (int) posY; i > -1; i--) {
            if (!(mc.world.getBlockState(new BlockPos(posX, i, posZ)).getBlock() instanceof BlockAir)) {
                return false;
            }
        }
        return true;
    }
    
    public void fakeDamage() {
        mc.player.handleStatusUpdate((byte) 2);
        mc.ingameGUI.healthUpdateCounter = mc.ingameGUI.updateCounter + 20;
    }

    public boolean overVoid() {
        return overVoid(mc.player.posX, mc.player.posY, mc.player.posZ);
    }
    
    public boolean unusedNames(EntityPlayer player) {
    	String name = player.getName();
    	return name.contains("CLICK DERECHO") || name.contains("MEJORAS") || name.contains("[NPC]") || name.contains("[SHOP]") || name.contains("CLIQUE PARA ABRIR");
    }
    
    public boolean canTarget(Entity entity, boolean idk) {
        if(entity != null && entity != mc.player) {
            EntityLivingBase entityLivingBase = null;

            if(entity instanceof EntityLivingBase) {
                entityLivingBase = (EntityLivingBase)entity;
            }

            boolean isTeam = isTeam((EntityPlayer) entity, true, true);
            boolean isVisible = (!entity.isInvisible());

            return !(entity instanceof EntityArmorStand) && isVisible && (entity instanceof EntityPlayer && !isTeam && !idk || entity instanceof EntityAnimal || entity instanceof EntityMob || entity instanceof EntityLivingBase && entityLivingBase.isEntityAlive());
        } else {
            return false;
        }
    }

    public float getCompleteHealth(EntityLivingBase entity) {
        if (entity == null) return 0;
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

    public String getHealthStr(EntityLivingBase entity) {
        float completeHealth = getCompleteHealth(entity);
        return getColorForHealth(entity.getHealth() / entity.getMaxHealth(), completeHealth);
    }
    
    private String getColorForHealth(double n, double n2) {
        double health = MathUtil.round(n2, 1);
        return ((n < 0.3) ? "�c" : ((n < 0.5) ? "�6" : ((n < 0.7) ? "�e" : "�a"))) + (MathUtil.isWholeNumber(health) ? (int) health + "" : health);
    }
    
    public String getHitsToKill(final EntityPlayer entityPlayer, final ItemStack itemStack) {
        final int n = (int) Math.ceil(ap(entityPlayer, itemStack));
        return "�" + ((n <= 1) ? "c" : ((n <= 3) ? "6" : ((n <= 5) ? "e" : "a"))) + n;
    }

    private double ap(final EntityPlayer entityPlayer, final ItemStack itemStack) {
        double n = 1.0;
        if (itemStack != null && (itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemAxe)) {
            n += getDamage(itemStack);
        }
        double n2 = 0.0;
        double n3 = 0.0;
        for (int i = 0; i < 4; ++i) {
            final ItemStack armorItemInSlot = entityPlayer.inventory.armorItemInSlot(i);
            if (armorItemInSlot != null) {
                if (armorItemInSlot.getItem() instanceof ItemArmor) {
                    n2 += ((ItemArmor) armorItemInSlot.getItem()).damageReduceAmount * 0.04;
                    final int getEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, armorItemInSlot);
                    if (getEnchantmentLevel != 0) {
                        n3 += Math.floor(0.75 * (6 + getEnchantmentLevel * getEnchantmentLevel) / 3.0);
                    }
                }
            }
        }
        return MathUtil.round((double) getCompleteHealth(entityPlayer) / (n * (1.0 - (n2 + 0.04 * Math.min(Math.ceil(Math.min(n3, 25.0) * 0.75), 20.0) * (1.0 - n2)))), 1);
    }
    
    public double getDamage(final ItemStack itemStack) {
        double getAmount = 0;
        for (final Map.Entry<String, AttributeModifier> entry : itemStack.getAttributeModifiers().entries()) {
            if (entry.getKey().equals("generic.attackDamage")) {
                getAmount = entry.getValue().getAmount();
                break;
            }
        }
        return getAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25;
    }
}