package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.LegitScaffold;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.EnumFacingOffset;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.RayCastUtil;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.util.structure.geometry.Vector3d;
import cc.unknown.value.impl.BoundsNumberValue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

@ModuleInfo(aliases = "Clutch", description = "Clutchea automaticamente [Presionando shift]", category = Category.PLAYER)
public class Clutch extends Module {

	private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation Speed", this, 5, 10, 0, 10, 1);
	private final BoundsNumberValue placeDelay = new BoundsNumberValue("Place Delay", this, 0, 0, 0, 1, 1);

	private Vec3 targetBlock;
	private EnumFacingOffset enumFacing;
	private BlockPos blockFace;
	private float targetYaw, targetPitch;
	private int ticksOnAir;
	private int toggle;
	private int lastSlot;

	@Override
	public void onEnable() {
		targetYaw = mc.player.rotationYaw - 180;
		targetPitch = 90;
		lastSlot = -1;
		targetBlock = null;
	}

	@Override
	public void onDisable() {
		mc.player.inventory.currentItem = lastSlot;
	}

	public void calculateRotations() {
		if (ticksOnAir > 0
				&& !RayCastUtil.overBlock(RotationHandler.rotations, enumFacing.getEnumFacing(), blockFace, true)) {
			getRotations(0);
		}

		/* Smoothing rotations */
		final double minRotationSpeed = this.rotationSpeed.getValue().doubleValue();
		final double maxRotationSpeed = this.rotationSpeed.getSecondValue().doubleValue();
		float rotationSpeed = (float) MathUtil.getRandom(minRotationSpeed, maxRotationSpeed);

		if (rotationSpeed != 0) {
			RotationHandler.setRotations(new Vector2f(targetYaw, targetPitch), rotationSpeed, MoveFix.SILENT);
		}
	}

	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		if (lastSlot == -1) {
			lastSlot = mc.player.inventory.currentItem;
		}

		if (getModule(LegitScaffold.class).isEnabled() || getModule(Scaffold.class).isEnabled()
				|| (!mc.gameSettings.keyBindSneak.isKeyDown()))
			return;

		final int slot = SlotUtil.findBlock();

		if (slot == -1) {
			return;
		}

		mc.player.inventory.currentItem = slot;

		final Vec3i offset = new Vec3i(0, 0, 0);

		if (PlayerUtil.blockRelativeToPlayer(offset.getX(), -1 + offset.getY(), offset.getZ())
				.isReplaceable(mc.theWorld, new BlockPos(mc.player).down())) {
			ticksOnAir++;
		} else {
			ticksOnAir = 0;
		}

		targetBlock = PlayerUtil.getPlacePossibility(offset.getX(), offset.getY(), offset.getZ());

		if (targetBlock == null) {
			return;
		}

		enumFacing = PlayerUtil.getEnumFacing(targetBlock);

		if (enumFacing == null) {
			return;
		}

		final BlockPos position = new BlockPos(targetBlock.xCoord, targetBlock.yCoord, targetBlock.zCoord);

		blockFace = position.add(enumFacing.getOffset().xCoord, enumFacing.getOffset().yCoord,
				enumFacing.getOffset().zCoord);

		if (blockFace == null || enumFacing == null) {
			return;
		}

		this.calculateRotations();

		if (targetBlock == null || enumFacing == null || blockFace == null) {
			return;
		}

		if (ticksOnAir > MathUtil.getRandom(placeDelay.getValue().intValue(), placeDelay.getSecondValue().intValue())
				&& (RayCastUtil.overBlock(enumFacing.getEnumFacing(), blockFace, true))) {

			Vec3 hitVec = RayCastUtil.rayCast(RotationHandler.rotations,
					mc.playerController.getBlockReachDistance()).hitVec;

			if (mc.playerController.onPlayerRightClick(mc.player, mc.theWorld, PlayerUtil.getItemStack(), blockFace,
					enumFacing.getEnumFacing(), hitVec)) {
				PacketUtil.send(new C0APacketAnimation());
			}

			mc.rightClickDelayTimer = 0;
			ticksOnAir = 0;

			assert PlayerUtil.getItemStack() != null;
			if (PlayerUtil.getItemStack() != null && PlayerUtil.getItemStack().stackSize == 0) {
				mc.player.inventory.mainInventory[mc.player.inventory.currentItem] = null;
			}
		} else if (Math.random() > 0.92 && mc.rightClickDelayTimer <= 0) {
			PacketUtil.send(new C08PacketPlayerBlockPlacement(PlayerUtil.getItemStack()));
			mc.rightClickDelayTimer = 0;
		}

	};

	public void getRotations(final int yawOffset) {
		EntityPlayer entityPlayer = mc.player;
		double difference = entityPlayer.posY + entityPlayer.getEyeHeight() - targetBlock.yCoord - 0.1
				- Math.random() * 0.8;

		MovingObjectPosition movingObjectPosition;

		for (int offset = -180 + yawOffset; offset <= 180; offset += 45) {
			entityPlayer.setPosition(entityPlayer.posX, entityPlayer.posY - difference, entityPlayer.posZ);
			movingObjectPosition = RayCastUtil.rayCast(new Vector2f(entityPlayer.rotationYaw + offset, 0), 4.5);
			entityPlayer.setPosition(entityPlayer.posX, entityPlayer.posY + difference, entityPlayer.posZ);

			if (movingObjectPosition != null && new BlockPos(blockFace).equals(movingObjectPosition.getBlockPos())
					&& enumFacing.getEnumFacing() == movingObjectPosition.sideHit) {
				Vector2f rotations = RotationUtil.calculate(movingObjectPosition.hitVec);

				targetYaw = rotations.x;
				targetPitch = rotations.y;
				return;
			}
		}

		final Vector2f rotations = RotationUtil.calculate(
				new Vector3d(blockFace.getX(), blockFace.getY(), blockFace.getZ()), enumFacing.getEnumFacing());

		targetYaw = rotations.x;
		targetPitch = rotations.y;
	}

}
