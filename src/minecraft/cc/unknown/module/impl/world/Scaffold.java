package cc.unknown.module.impl.world;

import java.awt.Color;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.lwjgl.input.Keyboard;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.KeyboardInputEvent;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.netty.PacketSendEvent;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.movement.Speed;
import cc.unknown.module.impl.world.scaffold.sprint.BypassSprint;
import cc.unknown.module.impl.world.scaffold.sprint.DisabledSprint;
import cc.unknown.module.impl.world.scaffold.sprint.LegitSprint;
import cc.unknown.module.impl.world.scaffold.sprint.NormalSprint;
import cc.unknown.module.impl.world.scaffold.tower.VanillaTower;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.geometry.Vector2f;
import cc.unknown.util.geometry.Vector3d;
import cc.unknown.util.player.EnumFacingOffset;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.RayCastUtil;
import cc.unknown.util.player.RotationUtil;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.StringValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

@ModuleInfo(aliases = { "Scaffold",
		"scaff", "auto bridge" }, description = "Construye mientras caminas", category = Category.WORLD)
public class Scaffold extends Module {

	public final ModeValue rotationMode = new ModeValue("Rotation Mode", this)
			.add(new SubMode("Normal"))
			.add(new SubMode("Snap"))
			.add(new SubMode("Telly"))
			.add(new SubMode("Legit"))
			.setDefault("Normal");

	public final ModeValue rayCast = new ModeValue("Ray Cast", this)
			.add(new SubMode("Off"))
			.add(new SubMode("Normal"))
			.add(new SubMode("Strict"))
			.setDefault("Strict");

	public final ModeValue sprint = new ModeValue("Sprint", this)
			.add(new NormalSprint("Normal", this))
			.add(new DisabledSprint("Disabled", this))
			.add(new LegitSprint("Legit", this))
			.add(new BypassSprint("Cancel", this))
			.setDefault("Normal");

	public final ModeValue tower = new ModeValue("Tower", this)
			.add(new SubMode("Polar"))
			.add(new SubMode("Disabled"))
			.add(new VanillaTower("Vanilla", this))
			.setDefault("Disabled");

	public final ModeValue sameYValue = new ModeValue("Same Y", this, () -> !rotationMode.is("Snap") && !rotationMode.is("Normal") && !rotationMode.is("Telly"))
			.add(new SubMode("Off"))
			.add(new SubMode("On"))
			.add(new SubMode("Auto Jump"))
			.setDefault("Off");
	
    private final StringValue macroKey = new StringValue("Macro Key:", this, "Y", () -> !rotationMode.is("Snap") && !rotationMode.is("Normal"));
	
	public final ModeValue yawOffset = new ModeValue("Yaw Offset", this)
			.add(new SubMode("0"))
			.add(new SubMode("45"))
			.add(new SubMode("-45"))
			.setDefault("0");

	private final DescValue general = new DescValue("Basic Settings:", this);
    private final BoundsNumberValue placeDelay = new BoundsNumberValue("Place Delay", this, 0, 0, 0, 5, 1);
    private final BooleanValue safeWalk = new BooleanValue("Safe Walk", this, false);
	private final BoundsNumberValue timer = new BoundsNumberValue("Timer", this, 1, 1, 0.1, 10, 0.05);
	private final NumberValue expand = new NumberValue("Expand", this, 0, 0, 5, 1);
	
	private final DescValue vipas = new DescValue("Advanced Settings:", this);
	private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation Speed", this, 5, 10, 0, 10, 1);
	private final NumberValue range = new NumberValue("Block Range", this, 5, 4, 20, 1);
    private final BooleanValue randomiseRotationSpeed = new BooleanValue("Randomise Rotation Speed", this, false);
    private final BooleanValue movementCorrection = new BooleanValue("Movement Correction", this, false);
	private final BooleanValue useBiggestStack = new BooleanValue("Use Biggest Stack", this, true);
	private final BooleanValue ignoreSpeed = new BooleanValue("Ignore Speed Effect", this, false);
	private final BooleanValue disableWithTeleport = new BooleanValue("Disabled on LagBack", this, true);

	private final DescValue sneak = new DescValue("Sneak Settings:", this);
	private final BooleanValue sneakOffGround = new BooleanValue("Sneak OffGround", this, false);
	private final BooleanValue sneakOnGround = new BooleanValue("Sneak OnGround", this, false);
	private final NumberValue startSneaking = new NumberValue("Start Sneaking", this, 1, 1, 5, 1);
	private final BoundsNumberValue sneakEvery = new BoundsNumberValue("Sneak every x blocks", this, 1, 1, 1, 10, 1);
	private final NumberValue sneakingSpeed = new NumberValue("Sneaking Speed", this, 0.2, 0.2, 1, 0.05);

	private Vec3 targetBlock;
	private EnumFacingOffset enumFacing;
	public Vec3i offset = new Vec3i(0, 0, 0);
	private BlockPos blockFace;
	private float targetYaw;
	private float targetPitch;
	private float forward;
	private float strafe;
	private float yawDrift;
	private float pitchDrift;
	private int ticksOnAir;
	private int sneakingTicks;
	private int placements;
	private int slow;
	private int pause;
	public int recursions, recursion;
	public double startY;
	private int lastSlot;
	private boolean canPlace;
	private boolean sameY;

	@Override
	public void onEnable() {
		targetYaw = mc.player.rotationYaw - 180 + Integer.parseInt(yawOffset.getValue().getName());
		targetPitch = 90;

		pitchDrift = (float) ((Math.random() - 0.5) * (Math.random() - 0.5) * 10);
		yawDrift = (float) ((Math.random() - 0.5) * (Math.random() - 0.5) * 10);
		lastSlot = -1;

		startY = Math.floor(mc.player.posY);
		targetBlock = null;
		this.sneakingTicks = 0;
		recursions = 0;
		placements = 0;
		
		if (randomiseRotationSpeed.getValue()) {
			rotationSpeed.setValue(50 + (85 - 50) * Math.random());
		}
	}

	@Override
	public void onDisable() {
		resetBinds();
	}

	@EventLink
	public final Listener<KeyboardInputEvent> onKeyboard = event -> {
		if (rotationMode.is("Telly")) return;
		
	    try {
	        String keyFieldName = "KEY_" + macroKey.getValue().toUpperCase();
	        
	        int macroKeyCode = Keyboard.class.getField(keyFieldName).getInt(null);

	        if (event.getKeyCode() == macroKeyCode) {
	            if (sameYValue.is("Off")) {
	            	startY = Math.floor(mc.player.posY);
	                sameYValue.setDefault("Auto Jump");
	            } else {
	                sameYValue.setDefault("Off");
	            }
	        }
	    } catch (NoSuchFieldException | IllegalAccessException e) {
	        e.printStackTrace();
	    }
	};
	
	@EventLink
	public final Listener<MoveInputEvent> onMove = this::calculateSneaking;
	
	@EventLink
	public final Listener<PreStrafeEvent> onStrafe = event -> {
		if (tower.is("Polar")) {
	        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
	            return;
	        }

	        if(mc.player.onGround) {
	            mc.player.jump();
	            mc.player.motionY = 0.39;
	        }
		}
		
		if (rotationMode.is("Telly")) {
			if (mc.player.onGround && MoveUtil.isMoving()) {
				mc.player.jump();
			}
		}

		if (!Objects.equals(yawOffset.getValue().getName(), "0") && !movementCorrection.getValue()) {
			MoveUtil.useDiagonalSpeed();
		}

		if (this.sameYValue.is("Auto Jump")) {
			if (mc.player.onGround && MoveUtil.isMoving() && mc.player.posY == startY) {
				mc.player.jump();
			}
		}
	};

	@EventLink
	public final Listener<PacketSendEvent> onPacketSend = event -> {
		Packet<?> packet = event.getPacket();
		if (packet instanceof C08PacketPlayerBlockPlacement) {
			C08PacketPlayerBlockPlacement wrapper = (C08PacketPlayerBlockPlacement) packet;

			if (!wrapper.getPosition().equalsVector(new Vector3d(-1, -1, -1))) {
				placements--;
			}
		}
	};
	
	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		this.offset = new Vec3i(0, 0, 0);

		if (targetBlock == null || enumFacing == null || blockFace == null) {
			return;
		}

		mc.player.hideSneakHeight.reset();

		// Timer
		if (timer.getValue().floatValue() != 1 && timer.getSecondValue().floatValue() != 1)
			mc.timer.timerSpeed = (float) MathUtil.getRandom(timer.getValue().floatValue(), timer.getSecondValue().floatValue());
	};

	@EventLink
	public final Listener<TeleportEvent> onTeleport = event -> {
		if (disableWithTeleport.getValue() && event.getPosY() < mc.player.posY - 2)
			this.toggle();
	};

	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		for (recursion = 0; recursion <= recursions; recursion++) {
			mc.player.safeWalk = this.safeWalk.getValue();

			resetBinds(false, false, true, true, false, false);

			if (expand.getValue().intValue() != 0) {
				double direction = MoveUtil.direction(mc.player.rotationYaw, mc.gameSettings.keyBindForward.isKeyDown() ? 1 : mc.gameSettings.keyBindBack.isKeyDown() ? -1 : 0, mc.gameSettings.keyBindRight.isKeyDown() ? -1 : mc.gameSettings.keyBindLeft.isKeyDown() ? 1 : 0);
				for (int range = 0; range <= expand.getValue().intValue(); range++) {
					if (PlayerUtil.blockAheadOfPlayer(range, this.offset.getY() - 0.5) instanceof BlockAir) {
						this.offset = this.offset.add(new Vec3i((int) (-Math.sin(direction) * (range + 1)), 0, (int) (Math.cos(direction) * (range + 1))));
						break;
					}
				}
			}

			sameY = ((!this.sameYValue.is("Off") || this.getModule(Speed.class).isEnabled()) && !mc.gameSettings.keyBindJump.isKeyDown()) && MoveUtil.isMoving();

	        if (lastSlot == -1) {
	        	lastSlot = getComponent(Slot.class).getItemIndex();
	        }

	        int slot = getComponent(Slot.class).getItemIndex();
	        
	        if (useBiggestStack.getValue()) {
	        	slot = getSlot();
	        } else if (getComponent(Slot.class).getItemStack() == null || !(getComponent(Slot.class).getItemStack().getItem() instanceof ItemBlock) || !InventoryUtil.canBePlaced((ItemBlock) getComponent(Slot.class).getItemStack().getItem())) {
	        	slot = getSlot();
	        }
	        
	        getComponent(Slot.class).setSlot(slot);

			if (doesNotContainBlock(1) && (!sameY || (doesNotContainBlock(2) && doesNotContainBlock(3) && doesNotContainBlock(4)))) {
				ticksOnAir++;
			} else {
				ticksOnAir = 0;
			}

			canPlace = ticksOnAir > MathUtil.getRandom(placeDelay.getValue().intValue(), placeDelay.getSecondValue().intValue());

			if (recursion == 0)
				this.calculateSneaking();

			targetBlock = PlayerUtil.getPlacePossibility(range.getValue().intValue(), offset.getX(), offset.getY(), offset.getZ(),
					sameY ? (int) Math.floor(startY) : null);

			if (targetBlock == null) {
				return;
			}
			
			enumFacing = PlayerUtil.getEnumFacing(targetBlock, offset.getY() < 0);

			if (enumFacing == null) {
				return;
			}

			final BlockPos position = new BlockPos(targetBlock.xCoord, targetBlock.yCoord, targetBlock.zCoord);

			blockFace = position.add(enumFacing.getOffset().xCoord, enumFacing.getOffset().yCoord, enumFacing.getOffset().zCoord);
			
			if (blockFace == null || enumFacing == null || enumFacing.getEnumFacing() == null) {
				return;
			}
			
			this.calculateRotations();

			if (targetBlock == null || enumFacing == null || blockFace == null) {
				return;
			}

			if (startY - 1 != Math.floor(targetBlock.yCoord) && sameY) {
				return;
			}

			if (getComponent(Slot.class).getItemStack() == null || !(getComponent(Slot.class).getItemStack().getItem() instanceof ItemBlock)) {
				return;
			}

			if (getComponent(Slot.class).getItem() instanceof ItemBlock) {
				if (canPlace && (RayCastUtil.overBlock(enumFacing.getEnumFacing(), blockFace, rayCast.is("Strict")) || rayCast.is("Off"))) {
					this.place();

					ticksOnAir = 0;

					assert getComponent(Slot.class).getItemStack() != null;

					if (getComponent(Slot.class).getItemStack() != null && getComponent(Slot.class).getItemStack().stackSize == 0) {
						mc.player.inventory.mainInventory[getComponent(Slot.class).getItemIndex()] = null;
					}

				} else if (Math.random() > 0.3 && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit != null && mc.objectMouseOver.sideHit == EnumFacing.UP && rayCast.is("Strict") && !(PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir)) {
					mc.rightClickMouse();
				}
			}

			// For Same Y
			if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.posY % 1 > 0.5) {
				startY = Math.floor(mc.player.posY);
			}

			if ((mc.player.posY < startY || mc.player.onGround) && !MoveUtil.isMoving()) {
				startY = Math.floor(mc.player.posY);
			}
		}
	};
	
	public void calculateSneaking(MoveInputEvent moveInputEvent) {
		forward = moveInputEvent.getForward();
		strafe = moveInputEvent.getStrafe();

		if (slow > 0) {
			moveInputEvent.setForward(0);
			moveInputEvent.setStrafe(0);
			slow--;
			return;
		}

		double speed = this.sneakingSpeed.getValue().doubleValue();

		if (speed <= 0.2) {
			return;
		}

		moveInputEvent.setSneakSlowDownMultiplier(speed);
	}

	public void calculateSneaking() {
		boolean offOrOn = (mc.player.onGround && sneakOnGround.getValue()) || (!mc.player.onGround && sneakOffGround.getValue());
		
		if (ticksOnAir == 0 && offOrOn)
			mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);

		this.sneakingTicks--;

		int ahead = startSneaking.getValue().intValue();
		int place = placeDelay.getRandomBetween().intValue();

		if (this.sneakingTicks >= 0 && offOrOn) {
			mc.gameSettings.keyBindSneak.pressed = true;
			return;
		}

		if (ticksOnAir > 0 || PlayerUtil.blockRelativeToPlayer(mc.player.motionX * ahead, MoveUtil.HEAD_HITTER_MOTION, mc.player.motionZ * ahead) instanceof BlockAir) {
			if (placements <= 0) {
				this.sneakingTicks = (int) (double) (ahead + place);
				placements = sneakEvery.getRandomBetween().intValue();
			}
		}
	}

	public void calculateRotations() {
		int yawOffset = Integer.parseInt(String.valueOf(this.yawOffset.getValue().getName()));

		/* Smoothing rotations */
		final double minRotationSpeed = this.rotationSpeed.getValue().doubleValue();
		final double maxRotationSpeed = this.rotationSpeed.getSecondValue().doubleValue();
		float rotationSpeed = (float) MathUtil.getRandom(minRotationSpeed, maxRotationSpeed);

		MovementFix movementFix = this.movementCorrection.getValue() ? MovementFix.SILENT : MovementFix.OFF;

		/* Calculating target rotations */
		switch (rotationMode.getValue().getName()) {
		case "Normal":
			mc.entityRenderer.getMouseOver(1);

			if (canPlace && !mc.gameSettings.keyBindPickBlock.isKeyDown()) {
				if (mc.objectMouseOver.sideHit != enumFacing.getEnumFacing()
						|| !mc.objectMouseOver.getBlockPos().equals(blockFace)) {
					getRotations(yawOffset);
				}
			}
			break;
			
		case "Snap":
		    boolean shouldGetMouseOver = false;
		    getRotations(yawOffset);
		    
		    if (mc.player.onGround) {
		        boolean isAirTick = ticksOnAir > 0 && !RayCastUtil.overBlock(
		            RotationComponent.rotations, enumFacing.getEnumFacing(), blockFace, rayCast.is("Normal"));

		        if (!isAirTick)
		        	targetYaw = (float) Math.toDegrees(MoveUtil.direction(mc.player.rotationYaw, forward, strafe)) - yawOffset;

		        if (PlayerUtil.isOverAir()) {
		            shouldGetMouseOver = true;
		        }

		        if (sameYValue.is("Off") && PlayerUtil.isOnEdge()) {
		            shouldGetMouseOver = true;
		        }
		    } else {
		        shouldGetMouseOver = mc.player.motionY > 0 || mc.player.hurtTime > 0;
		    }
		    
		    if (shouldGetMouseOver) {
		        mc.entityRenderer.getMouseOver(1);
		        getRotations(yawOffset);
		    }
		    break;

		case "Legit":
		    if (canPlace && !mc.gameSettings.keyBindPickBlock.isKeyDown()) {
		        if (mc.objectMouseOver.sideHit != enumFacing.getEnumFacing()
		                || !mc.objectMouseOver.getBlockPos().equals(blockFace)) {

				    float yaw = (mc.player.rotationYaw + 10000000) % 360;
				    float staticYaw = (yaw - 180) - (yaw % 90) + 45;
				    float staticPitch = 75;

				    boolean straight = (Math.min(Math.abs(yaw % 90), Math.abs(90 - yaw) % 90) < Math.min(Math.abs(yaw + 45) % 90, Math.abs(90 - (yaw + 45)) % 90));

				    if (straight && RayCastUtil.rayCast(new Vector2f(staticYaw + 90, staticPitch), 30).typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && RayCastUtil.rayCast(new Vector2f(staticYaw, staticPitch), 3).typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
				        staticYaw += 90;
				    }

				    movementFix = MovementFix.SILENT;

				    if (!straight) {
				        staticYaw += 90;
				    }
				    
				    mc.entityRenderer.getMouseOver(1);

				    targetYaw = staticYaw;
				    targetPitch = staticPitch;
		        }
		    }
		    break;

		case "Telly":
			if (recursion == 0) {
				int time = mc.player.offGroundTicks;

				if (time == 2 || time == 0)
					mc.rightClickMouse();

				if (time >= 3 && mc.player.offGroundTicks <= (sameYValue.is("Off") ? 7 : 10)) {
					if (!RayCastUtil.overBlock(RotationComponent.rotations, enumFacing.getEnumFacing(), blockFace,
							rayCast.is("Strict"))) {
						getRotations(0);
					}
				} else {
					getRotations(Integer.parseInt(String.valueOf(this.yawOffset.getValue().getName())));
					targetYaw = mc.player.rotationYaw;
				}

				if (mc.player.offGroundTicks <= 3) {
					canPlace = false;
				}
			}
			break;
		}

		if (rotationSpeed != 0 && blockFace != null && enumFacing != null) {
			RotationComponent.setRotations(new Vector2f(targetYaw, targetPitch), rotationSpeed, movementFix);
		}
	}

	public boolean doesNotContainBlock(int down) {
		return PlayerUtil.blockRelativeToPlayer(offset.getX(), -down + offset.getY(), offset.getZ())
				.isReplaceable(mc.world, new BlockPos(mc.player).down(down));
	}

	public Vec3 getHitVec() {
		/* Correct HitVec */
		Vec3 hitVec = new Vec3(blockFace.getX(), blockFace.getY(), blockFace.getZ());

		final MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationComponent.rotations, mc.playerController.getBlockReachDistance());

		switch (enumFacing.getEnumFacing()) {
		case DOWN:
			hitVec.yCoord = blockFace.getY();
			break;

		case UP:
			hitVec.yCoord = blockFace.getY();
			break;

		case NORTH:
			hitVec.zCoord = blockFace.getZ();
			break;

		case EAST:
			hitVec.xCoord = blockFace.getX();
			break;

		case SOUTH:
			hitVec.zCoord = blockFace.getZ();
			break;

		case WEST:
			hitVec.xCoord = blockFace.getX();
			break;
		}

		if (movingObjectPosition != null && movingObjectPosition.getBlockPos() != null
				&& movingObjectPosition.hitVec != null && movingObjectPosition.getBlockPos().equals(blockFace)
				&& movingObjectPosition.sideHit == enumFacing.getEnumFacing()) {
			hitVec = movingObjectPosition.hitVec;
		}

		return hitVec;
	}

	private void place() {
		if (pause > 3)
			return;

		Vec3 hitVec = this.getHitVec();

		if (rayCast.is("Strict")) {
			mc.rightClickMouse();
		} else if (mc.playerController.onPlayerRightClick(mc.player, mc.world, getComponent(Slot.class).getItemStack(), blockFace, enumFacing.getEnumFacing(), hitVec)) {
			mc.clickMouseEvent();
			//PacketUtil.send(new C0APacketAnimation());
		}
	}
	
	public void getRotations(final int yawOffset) {
		EntityPlayer player = mc.player;
		double difference = player.posY + player.getEyeHeight() - targetBlock.yCoord - 0.5 - (Math.random() - 0.5) * 0.1;

		MovingObjectPosition movingObjectPosition = null;		
		for (int offset = -180 + yawOffset; offset <= 180; offset += 5) {
			player.setPosition(player.posX, player.posY - difference, player.posZ);
			movingObjectPosition = RayCastUtil.rayCast(new Vector2f((float) (player.rotationYaw + (offset * 3)), 0), 20);
			player.setPosition(player.posX, player.posY + difference, player.posZ);

			if (movingObjectPosition == null || movingObjectPosition.hitVec == null)
				return;

			Vector2f rotations = RotationUtil.calculate(movingObjectPosition.hitVec);

			if (RayCastUtil.overBlock(rotations, blockFace, enumFacing.getEnumFacing())) {
				targetYaw = rotations.x;
				targetPitch = rotations.y;
				return;
			}
		}

		final Vector2f rotations = RotationUtil.calculate(
				new Vector3d(blockFace.getX(), blockFace.getY(), blockFace.getZ()), enumFacing.getEnumFacing());

		if (!RayCastUtil.overBlock(new Vector2f(targetYaw, targetPitch), blockFace, enumFacing.getEnumFacing())) {
			targetYaw = rotations.x;
			targetPitch = rotations.y;
		}
	}
	
    private int getSlot() {
        int slot = -1;
        int highestStack = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack itemStack = mc.player.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock &&  SlotUtil.blacklist.stream().noneMatch(block -> block.equals(((ItemBlock) itemStack.getItem()).getBlock())) && itemStack.stackSize > 0) {
                if (mc.player.inventory.mainInventory[i].stackSize > highestStack) {
                    highestStack = mc.player.inventory.mainInventory[i].stackSize;
                    slot = i;
                }
            }
        }
        return slot;
    }
    
	public void resetBinds() {
		resetBinds(true, true, true, true, true, true);
	}

	public void resetBinds(boolean sneak, boolean jump, boolean right, boolean left, boolean forward, boolean back) {
		BiConsumer<Boolean, Runnable> setKeyBind = (condition, action) -> {
			if (condition)
				action.run();
		};

		setKeyBind.accept(sneak, () -> mc.gameSettings.keyBindSneak.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())));
		setKeyBind.accept(jump, () -> mc.gameSettings.keyBindJump.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())));
		setKeyBind.accept(right, () -> mc.gameSettings.keyBindRight.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())));
		setKeyBind.accept(left, () -> mc.gameSettings.keyBindLeft.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())));
		setKeyBind.accept(forward, () -> mc.gameSettings.keyBindForward.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())));
		setKeyBind.accept(back, () -> mc.gameSettings.keyBindBack.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())));
	}
}