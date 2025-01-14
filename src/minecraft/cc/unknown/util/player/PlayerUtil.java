package cc.unknown.util.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.impl.ghost.AutoClicker;
import cc.unknown.module.impl.move.Sprint;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
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
			put(14, 10); // Invisibility
			put(3, 11); // Haste
			put(13, 12); // Water Breathing
		}
	};

	public final List<Block> BLOCK_BLACKLIST = Arrays.asList(Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest,
			Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch, Blocks.crafting_table,
			Blocks.furnace, Blocks.waterlily, Blocks.dispenser, Blocks.stone_pressure_plate,
			Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.dropper, Blocks.tnt, Blocks.standing_banner,
			Blocks.wall_banner, Blocks.redstone_torch);

	/**
	 * Gets the block at a position
	 *
	 * @return block
	 */
	public Block block(final double x, final double y, final double z) {
		return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
	}

	/**
	 * Gets the block at a position
	 *
	 * @return block
	 */
	public Block block(final BlockPos blockPos) {
		return mc.theWorld.getBlockState(blockPos).getBlock();
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
		return block(new BlockPos(new Vec3i(pos.field_181059_a, pos.field_181060_b, pos.field_181061_c)));
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

	public float[] getDirectionToBlock(final double x, final double y, final double z, final EnumFacing enumfacing) {
		final EntityEgg var4 = new EntityEgg(mc.theWorld);
		var4.posX = x + 0.5D;
		var4.posY = y + 0.5D;
		var4.posZ = z + 0.5D;
		var4.posX += (double) enumfacing.getDirectionVec().getX() * 0.5D;
		var4.posY += (double) enumfacing.getDirectionVec().getY() * 0.5D;
		var4.posZ += (double) enumfacing.getDirectionVec().getZ() * 0.5D;
		return getRotations(var4.posX, var4.posY, var4.posZ);
	}

	public float[] getRotations(final double posX, final double posY, final double posZ) {
		final EntityPlayerSP player = mc.player;
		final double x = posX - player.posX;
		final double y = posY - (player.posY + (double) player.getEyeHeight());
		final double z = posZ - player.posZ;
		final double dist = MathHelper.sqrt_double(x * x + z * z);
		final float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		final float pitch = (float) (-(Math.atan2(y, dist) * 180.0D / Math.PI));
		return new float[] { yaw, pitch };
	}

	/**
	 * Gets the distance between 2 positions
	 *
	 * @return distance
	 */
	public double distance(final BlockPos pos1, final BlockPos pos2) {
		final double x = pos1.getX() - pos2.getX();
		final double y = pos1.getY() - pos2.getY();
		final double z = pos1.getZ() - pos2.getZ();
		return x * x + y * y + z * z;
	}

	public double getFov(final double posX, final double posZ) {
		return getFov(mc.player.rotationYaw, posX, posZ);
	}
	
	public double fovFromTarget(Entity tg) {
	    return ((mc.player.rotationYaw - fovToTarget(tg)) % 360.0 + 540.0) % 360.0 - 180.0;
	}

	private float fovToTarget(Entity tg) {
		double diffX = tg.posX - mc.player.posX;
		double diffZ = tg.posZ - mc.player.posZ;
		return (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
	}
	
    public double pitchFromTarget(Entity en, float f) {
        return (double) (mc.player.rotationPitch - pitchToEntity(en, f));
    }

    public float pitchToEntity(Entity ent, float f) {
        double x = mc.player.getDistanceToEntity(ent);
        double y = mc.player.posY - (ent.posY + f);
        double pitch = (((Math.atan2(x, y) * 180.0D) / Math.PI));
        return (float) (90 - pitch);
    }

	public boolean fov(Entity entity, float fov) {
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

	/**
	 * Gets the block relative to the player from the offset
	 *
	 * @return block relative to the player
	 */
	public Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
		return block(mc.player.posX + offsetX, mc.player.posY + offsetY, mc.player.posZ + offsetZ);
	}

	public Block blockAheadOfPlayer(final double offsetXZ, final double offsetY) {
		return blockRelativeToPlayer(-Math.sin(MoveUtil.direction()) * offsetXZ, offsetY,
				Math.cos(MoveUtil.direction()) * offsetXZ);
	}

	/**
	 * Gets another players' username without any formatting
	 *
	 * @return players username
	 */
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

	/**
	 * Checks if there is a block under the player
	 *
	 * @return block under
	 */
	public boolean isBlockUnder(final double height) {
		return isBlockUnder(height, true);
	}

	public boolean isBlockUnder(final double height, final boolean boundingBox) {
		if (boundingBox) {
			final AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, -height, 0);

			if (!mc.theWorld.getCollidingBoundingBoxes(mc.player, bb).isEmpty()) {
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

		if (!mc.theWorld.getCollidingBoundingBoxes(mc.player, bb).isEmpty()) {
			return true;
		}

		return false;
	}

	public boolean isOverAir() {
		return mc.theWorld.isAirBlock(new BlockPos(MathHelper.floor_double(mc.player.posX),
				MathHelper.floor_double(mc.player.posY - 1.0D), MathHelper.floor_double(mc.player.posZ)));
	}

	public boolean isBlockUnder() {
		return isBlockUnder(10);
	}

	public double distanceToBlockUnder() {
		double distance = 0;

		for (int i = 0; i < 256; i++) {
			if (blockRelativeToPlayer(0, -i, 0).isFullBlock()) {
				distance = i;
				break;
			}
		}

		return distance;
	}

	/**
	 * Checks if a potion is good
	 *
	 * @return good potion
	 */
	public boolean goodPotion(final int id) {
		return GOOD_POTIONS.containsKey(id);
	}

	/**
	 * Gets a potions ranking
	 *
	 * @return potion ranking
	 */
	public int potionRanking(final int id) {
		return GOOD_POTIONS.getOrDefault(id, -1);
	}

	/**
	 * Checks if the player is in a liquid
	 *
	 * @return in liquid
	 */
	public boolean inLiquid() {
		return mc.player.isInWater() || mc.player.isInLava();
	}

	/**
	 * Fake damages the player
	 */
	public void fakeDamage() {
		mc.player.handleHealthUpdate((byte) 2);
		mc.ingameGUI.healthUpdateCounter = mc.ingameGUI.updateCounter + 20;
	}

	/**
	 * Checks if the player is near a block
	 *
	 * @return block near
	 */
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

	/**
	 * Checks if the player is inside a block
	 *
	 * @return inside block
	 */
	public boolean insideBlock() {
		if (mc.player.ticksExisted < 5) {
			return false;
		}

		final EntityPlayerSP player = mc.player;
		final WorldClient world = mc.theWorld;
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

	/**
	 * Sends a click to Minecraft legitimately
	 */
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
		final WorldClient world = mc.theWorld;
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
			if (!(block(position.xCoord, position.yCoord, position.zCoord + z2).isReplaceable(mc.theWorld,
					new BlockPos(position.xCoord, position.yCoord, position.zCoord + z2)))) {
				if (z2 < 0) {
					possibleFacings.add(new EnumFacingOffset(EnumFacing.SOUTH, new Vec3(0, 0, z2)));
				} else {
					possibleFacings.add(new EnumFacingOffset(EnumFacing.NORTH, new Vec3(0, 0, z2)));
				}
			}
		}

		for (int x2 = -1; x2 <= 1; x2 += 2) {
			if (!(block(position.xCoord + x2, position.yCoord, position.zCoord).isReplaceable(mc.theWorld,
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
			if (!(block(position.xCoord, position.yCoord + y2, position.zCoord).isReplaceable(mc.theWorld,
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

	                if (!block.isReplaceable(mc.theWorld, blockPos)) {
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
	                .isReplaceable(mc.theWorld, new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord)))
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
	
	public boolean jumpDown() {
		return Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
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

	public boolean isHoldingWeapon() {
		if (mc.player.getCurrentEquippedItem() == null) {
			return false;
		} else {
			Item item = mc.player.getCurrentEquippedItem().getItem();
			return item instanceof ItemSword;
		}
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

		if (mc.player.isOnSameTeam((EntityLivingBase) player)) {
			return true;
		}

		if (scoreboard && mc.player.getTeam() != null && player.getTeam() != null
				&& mc.player.getTeam().isSameTeam(player.getTeam())) {
			return true;
		}

		if (checkColor && playerName != null && player.getDisplayName() != null) {
			String targetName = player.getDisplayName().getFormattedText().replace("§r", "");
			String clientName = playerName.replace("§r", "");
			return targetName.startsWith("§" + clientName.charAt(1));
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
	
    public void displayInClient(final Object message, final Object... objects) {
        if (mc.player != null) {
            final String format = String.format(message.toString(), objects);
            mc.player.addChatMessage(new ChatComponentText(format));
        }
    }

    public void sendInChat(final Object message) {
        if (mc.player != null) {
            PacketUtil.send(new C01PacketChatMessage(message.toString()));
        }
    }
    
    public double[] getPredictedPos(float forward, float strafe, double motionX, double motionY, double motionZ, double posX, double posY, double posZ, boolean isJumping) {
       strafe *= 0.98F;
       forward *= 0.98F;
       float f4 = 0.91F;
       boolean isSprinting = mc.player.isSprinting();
       if (isJumping && mc.player.onGround && mc.player.jumpTicks == 0) {
          motionY = mc.player.getJumpUpwardsMotion();
          if (mc.player.isPotionActive(Potion.jump)) {
             motionY += (float)(mc.player.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
          }

          if (isSprinting) {
             float f = mc.player.rotationYaw * (float) (Math.PI / 180.0);
             motionX -= MathHelper.sin(f) * 0.2F;
             motionZ += MathHelper.cos(f) * 0.2F;
          }
       }

       if (mc.player.onGround) {
          f4 = mc.player.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY) - 1, MathHelper.floor_double(posZ))).getBlock().slipperiness * 0.91F;
       }

       float f3 = 0.16277136F / (f4 * f4 * f4);
       float friction;
       if (mc.player.onGround) {
          friction = mc.player.getAIMoveSpeed() * f3;
          if (mc.player == mc.player && Sakura.instance.getModuleManager().get(Sprint.class).isEnabled() && !Sakura.instance.getModuleManager().get(Sprint.class).legit.getValue()) {
             friction = 0.12999998F;
          }
       } else {
          friction = mc.player.jumpMovementFactor;
       }

       float f = strafe * strafe + forward * forward;
       if (f >= 1.0E-4F) {
          f = MathHelper.sqrt_float(f);
          if (f < 1.0F) {
             f = 1.0F;
          }

          f = friction / f;
          strafe *= f;
          forward *= f;
          float f1 = MathHelper.sin(mc.player.rotationYaw * (float) Math.PI / 180.0F);
          float f2 = MathHelper.cos(mc.player.rotationYaw * (float) Math.PI / 180.0F);
          motionX += strafe * f2 - forward * f1;
          motionZ += forward * f2 + strafe * f1;
       }

       posX += motionX;
       posY += motionY;
       posZ += motionZ;
       f4 = 0.91F;
       if (mc.player.onGround) {
          f4 = mc.player.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(mc.player.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(posZ))).getBlock().slipperiness * 0.91F;
       }

       if (mc.player.worldObj.isRemote && (!mc.player.worldObj.isBlockLoaded(new BlockPos((int)posX, 0, (int)posZ)) || !mc.player.worldObj.getChunkFromBlockCoords(new BlockPos((int)posX, 0, (int)posZ)).isLoaded())) {
          if (posY > 0.0) {
             motionY = -0.1;
          } else {
             motionY = 0.0;
          }
       } else {
          motionY -= 0.08;
       }

       motionY *= 0.98F;
       motionX *= f4;
       motionZ *= f4;
       return new double[]{posX, posY, posZ, motionX, motionY, motionZ};
    }
    
    public Item getItem() {
        ItemStack stack = getItemStack();
        return stack == null ? null : stack.getItem();
    }
    
    public ItemStack getItemStack() {
        return (mc.player == null || mc.player.inventoryContainer == null ? null : mc.player.inventoryContainer.getSlot(mc.player.inventory.currentItem + 36).getStack());
    }
    
    public int findTool(final BlockPos blockPos) {
        float bestSpeed = 1;
        int bestSlot = -1;

        final IBlockState blockState = mc.theWorld.getBlockState(blockPos);

        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

            if (itemStack == null) {
                continue;
            }

            final float speed = itemStack.getStrVsBlock(blockState.getBlock());

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        return bestSlot;
    }
    
	public ItemStack getBestSword() {
		int size = mc.player.inventoryContainer.getInventory().size();
		ItemStack lastSword = null;
		for (int i = 0; i < size; i++) {
			ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
			if (stack != null && stack.getItem() instanceof ItemSword)
				if (lastSword == null) {
					lastSword = stack;
				} else if (isBetterSword(stack, lastSword)) {
					lastSword = stack;
				}
		}
		return lastSword;
	}

	public ItemStack getBestAxe() {
		int size = mc.player.inventoryContainer.getInventory().size();
		ItemStack lastAxe = null;
		for (int i = 0; i < size; i++) {
			ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
			if (stack != null && stack.getItem() instanceof ItemAxe)
				if (lastAxe == null) {
					lastAxe = stack;
				} else if (isBetterTool(stack, lastAxe, Blocks.planks)) {
					lastAxe = stack;
				}
		}
		return lastAxe;
	}

	public ItemStack getBestPickaxe() {
		int size = mc.player.inventoryContainer.getInventory().size();
		ItemStack lastPickaxe = null;
		for (int i = 0; i < size; i++) {
			ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
			if (stack != null && stack.getItem() instanceof ItemPickaxe)
				if (lastPickaxe == null) {
					lastPickaxe = stack;
				} else if (isBetterTool(stack, lastPickaxe, Blocks.stone)) {
					lastPickaxe = stack;
				}
		}
		return lastPickaxe;
	}

	public boolean isBetterTool(ItemStack better, ItemStack than, Block versus) {
		return (getToolDigEfficiency(better, versus) > getToolDigEfficiency(than, versus));
	}

	public boolean isBetterSword(ItemStack better, ItemStack than) {
		return (getSwordDamage((ItemSword) better.getItem(), better) > getSwordDamage((ItemSword) than.getItem(),
				than));
	}

	public float getSwordDamage(ItemSword sword, ItemStack stack) {
		float base = sword.getMaxDamage();
		return base + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
	}

	public float getToolDigEfficiency(ItemStack stack, Block block) {
		float f = stack.getStrVsBlock(block);
		if (f > 1.0F) {
			int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
			if (i > 0)
				f += (i * i + 1);
		}
		return f;
	}
	
    public boolean overVoid(double posX, double posY, double posZ) {
        for (int i = (int) posY; i > -1; i--) {
            if (!(mc.theWorld.getBlockState(new BlockPos(posX, i, posZ)).getBlock() instanceof BlockAir)) {
                return false;
            }
        }
        return true;
    }

    public boolean overVoid() {
        return overVoid(mc.player.posX, mc.player.posY, mc.player.posZ);
    }
}