package cc.unknown.module.impl.combat;

import java.util.Comparator;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.RightClickEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.HitSlowDownEvent;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.MouseOverEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.netty.PacketUtil;
import cc.unknown.util.player.EnemyUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.RayCastUtil;
import cc.unknown.util.player.TargetUtil;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.structure.EvictingList;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.ListValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import de.florianmichael.viamcp.fixes.AttackOrder;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Tuple;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = { "Kill Aura", "aura" }, description = "Ataca automáticamente a tus enemigos.", category = Category.COMBAT)
public final class KillAura extends Module {
	private final ModeValue attackMode = new ModeValue("Attack Mode", this)
			.add(new SubMode("Single"))
			.add(new SubMode("Switch"))
			.add(new SubMode("Multiple"))
			.setDefault("Single");

	private final BoundsNumberValue switchDelay = new BoundsNumberValue("Switch Delay", this, 0, 0, 0, 10, 1, () -> !attackMode.is("Switch"));

	public final ModeValue autoBlock = new ModeValue("Auto Block", this)
			.add(new SubMode("Fake"))
			.add(new SubMode("Experimental"))
			.add(new SubMode("Post"))
			.add(new SubMode("Legit"))
			.setDefault("Experimental");
	
	private final BooleanValue rightClickOnly = new BooleanValue("Right Click Only", this, false, () -> autoBlock.is("Fake"));
	private final BooleanValue preventServerSideBlocking = new BooleanValue("Prevent Serverside Blocking", this, false, () -> !autoBlock.is("Fake"));

	private final ModeValue sorting = new ModeValue("Sorting", this)
			.add(new SubMode("Distance"))
			.add(new SubMode("Health"))
			.add(new SubMode("Hurt Time"))
			.add(new SubMode("Armor"))
			.add(new SubMode("Fov"))
			.setDefault("Distance");

    private final ModeValue clickMode = new ModeValue("Click Delay", this)
            .add(new SubMode("None"))
            .add(new SubMode("1.9+"))
            .add(new SubMode("1.9+ With 1.8 Animations"))
            .setDefault("None");
	
    public final NumberValue preRange = new NumberValue("Pre Range", this, 4.5, 3, 6, 0.1);
	public final NumberValue range = new NumberValue("Range", this, 3, 3, 6, 0.1);
	private final BoundsNumberValue cps = new BoundsNumberValue("CPS", this, 10, 15, 1, 20, 1);
	private final NumberValue randomization = new NumberValue("Randomization", this, 1.5, 0, 2, 0.1);

	private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation speed", this, 5, 10, 0, 10, 1);
	private final ListValue<MoveFix> movementCorrection = new ListValue<>("Move Fix", this);

	private final BooleanValue keepSprint = new BooleanValue("Keep sprint", this, false);
	private final BooleanValue defCheck = new BooleanValue("Deffensive Check", this, false, () -> !keepSprint.getValue());
	private final NumberValue defMotion = new NumberValue("Deffensive Motion", this, 0.6, 0, 1, 0.05, () -> !keepSprint.getValue() || !this.defCheck.getValue());
	private final BooleanValue offeCheck = new BooleanValue("Offensive Check", this, false, () -> !keepSprint.getValue());
	private final NumberValue offeMotion = new NumberValue("Offensive Motion", this, 0.6, 0, 1, 0.05, () -> !keepSprint.getValue() || !this.offeCheck.getValue());

	private final BooleanValue rayCast = new BooleanValue("Ray cast", this, false);
	private final BooleanValue throughWalls = new BooleanValue("Through Walls", this, false, () -> !rayCast.getValue());

	private final DescValue advanced = new DescValue("Advanced Settings:", this);
	private final BooleanValue attackWhilstScaffolding = new BooleanValue("Attack whilst Scaffolding", this, false);
	private final BooleanValue noSwing = new BooleanValue("No swing", this, false);
	private final BooleanValue autoDisable = new BooleanValue("Auto disable", this, false);
	public final BooleanValue smoothRotation = new BooleanValue("Smooth Rotation", this, false);
	public final BooleanValue teams = new BooleanValue("Ignore Teammates", this, false);
	public final BooleanValue scoreboardCheckTeam = new BooleanValue("Scoreboard Check Team", this, false, () -> !teams.getValue());
	public final BooleanValue checkArmorColor = new BooleanValue("Check Armor Color", this, false, () -> !teams.getValue());

	private final DescValue showTargets = new DescValue("Targets Settings:", this);
	public final BooleanValue player = new BooleanValue("Players", this, true);
	public final BooleanValue friends = new BooleanValue("Friends", this, false);
	public final BooleanValue invisibles = new BooleanValue("Invisibles", this, true);
	public final BooleanValue animals = new BooleanValue("Animals", this, false);
	public final BooleanValue mobs = new BooleanValue("Mobs", this, false);

	private final StopWatch attackStopWatch = new StopWatch();
	private final StopWatch clickStopWatch = new StopWatch();
	private final StopWatch switchTimer = new StopWatch();

	public boolean blocking;

	private boolean allowAttack;
	private long nextSwing;

	private List<EntityLivingBase> targets;
	public EntityLivingBase target;

	private int attack;
	private int blockTicks;
	private int hitTicks;
	private int switchTicks;
	private boolean resetting;

	private final EvictingList<EntityLivingBase> pastTargets = new EvictingList<>(9);

	public KillAura() {
		for (MoveFix movementFix : MoveFix.values()) {
			movementCorrection.add(movementFix);
		}

		movementCorrection.setDefault(MoveFix.OFF);
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (target == null || mc.player.isDead || target == mc.player) {
			return;
		}
		
		hitTicks++;
	};

	@Override
	public void onEnable() {
		attack = 0;
		blockTicks = 0;
		nextSwing = 0;
	}

	@Override
	public void onDisable() {
		target = null;
		this.unblock(true);
	}

	@EventLink
	public final Listener<WorldChangeEvent> onWorldChange = event -> {
		if (this.autoDisable.getValue()) {
			this.toggle();
		}
	};

	public void getTargets() {
		double range = this.preRange.getValue().doubleValue();

		targets = TargetUtil.getTargets(range);

		if (attackMode.is("Switch")) {
			targets.removeAll(pastTargets);

			switchTicks++;
			if (switchTicks >= switchDelay.getRandomBetween().intValue()) {
				pastTargets.add(target);
				switchTicks = 0;
			}
		}

		if (targets.isEmpty()) {
			pastTargets.clear();
			targets = TargetUtil.getTargets(range/* + expandRange*/);
		}

		switch (sorting.getValue().getName()) {
		case "Health":
			targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
			sortByTargets();
			break;
		case "Distance":
			targets.sort(Comparator.comparingDouble(mc.player::getDistanceToEntity));
			sortByTargets();
			break;
		case "Hurt Time":
            targets.sort(Comparator.comparingInt(entity -> entity.hurtTime));
			sortByTargets();
			break;
        case "Armor":
            targets.sort(Comparator.comparingInt(EntityLivingBase::getTotalArmorValue));
            sortByTargets();
            break;
        case "FOV":
            targets.sort(Comparator.comparingDouble(RotationUtil::distanceFromYaw));
            sortByTargets();
            break;
		}
	}

	private void sortByTargets() {
		targets.sort((o1, o2) -> {
			boolean isTarget1 = EnemyUtil.isEnemy(o1.getName());
			boolean isTarget2 = EnemyUtil.isEnemy(o2.getName());
			if (isTarget1 && !isTarget2) {
				return -1;
			} else if (!isTarget1 && isTarget2) {
				return 1;
			}
			return 0;
		});
	}

	@EventLink(value = Priority.HIGH)
	public final Listener<PreUpdateEvent> onHighPreUpdate = event -> {
		if (!smoothRotation.getValue() && RotationHandler.isSmoothed()) {
			return;
		}

		mc.entityRenderer.getMouseOver(1);

		this.allowAttack = true;

		if (mc.player.getHealth() <= 0.0 && this.autoDisable.getValue()) {
			this.toggle();
		}

		if (getModule(Scaffold.class).isEnabled() && attackWhilstScaffolding.getValue()) {
			return;
		}
		
		this.attack = Math.max(Math.min(this.attack, this.attack - 2), 0);

		if (mc.currentScreen instanceof GuiContainer) {
			this.unblock(true);
			allowAttack = false;
			target = null;
			return;
		}

		this.getTargets();

		if (targets.isEmpty()) {
			target = null;
			return;
		}

		target = targets.get(0);
		
		this.doAttack(targets);

		if (target == null || mc.player.isDead || target == mc.player) {
			return;
		}
		
		if (this.canBlock()) {
			this.preBlock();
		}

		final float rotationSpeed = this.rotationSpeed.getRandomBetween().floatValue();
        Vector2f targetRotations = RotationUtil.calculate(target, true, range.getValue().doubleValue());

        if (rotationSpeed != 0) RotationHandler.setRotations(targetRotations, rotationSpeed,
                movementCorrection.getValue() == MoveFix.OFF ? MoveFix.OFF : movementCorrection.getValue(),
                rotations -> {
                    MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(rotations, range.getValue().floatValue(), -0.1f);

                    return movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY;
                });
	};

	@EventLink
	public final Listener<MouseOverEvent> onMouseOver = event -> event.setRange(event.getRange() + range.getValue().doubleValue() - 3);

    public Tuple<Boolean, Double> getDelay() {
        double delay = -1;
        boolean flag = false;

        switch (clickMode.getValue().getName()) {
            case "1.9+ With 1.8 Animations":
            case "1.9+": {
                if (clickMode.is("1.9+ With 1.8 Animations") && Math.random() > 0.2) {
                    RenderUtil.renderAttack(target);
                }

                double speed = 4;

                if (PlayerUtil.getItemStack() != null) {
                    final Item item = PlayerUtil.getItemStack().getItem();

                    if (item instanceof ItemSword) {
                        speed = 1.6;
                    } else if (item instanceof ItemSpade) {
                        speed = 1;
                    } else if (item instanceof ItemPickaxe) {
                        speed = 1.2;
                    } else if (item instanceof ItemAxe) {
                        switch (((ItemAxe) item).getToolMaterial()) {
                            case WOOD:
                            case STONE:
                                speed = 0.8;
                                break;

                            case IRON:
                                speed = 0.9;
                                break;

                            default:
                                speed = 1;
                                break;
                        }
                    } else if (item instanceof ItemHoe) {
                        switch (((ItemTool) item).getToolMaterial()) {
                            case WOOD:
                            case GOLD:
                                speed = 1;
                                break;

                            case STONE:
                                speed = 2;
                                break;

                            case IRON:
                                speed = 3;
                                break;
						default:
							break;
                        }
                    }
                }

                delay = 1 / speed * 20 - 1;
                break;
            }
        }

        return new Tuple<>(flag, delay);
    }

    private void doAttack(final List<EntityLivingBase> targets) {
        Tuple<Boolean, Double> tuple = getDelay();
        final double delay = tuple.getSecond();
        final boolean flag = tuple.getFirst();

        if (attackStopWatch.finished(this.nextSwing) && target != null && (clickStopWatch.finished((long) (delay * 50)) || flag)) {
            final long clicks = (long) (this.cps.getRandomBetween().longValue() * 1.5);
            this.nextSwing = 1000 / clicks;

            if (Math.sin(nextSwing) + 1 > Math.random() || attackStopWatch.finished(this.nextSwing + 500) || Math.random() > 0.5) {
				if (this.allowAttack) {
					final double range = this.range.getValue().doubleValue();
					final Vec3 rotationVector = mc.player.getVectorForRotation(RotationHandler.rotations.getY(), RotationHandler.rotations.getX());
					MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationHandler.rotations, range);

					if (throughWalls.getValue()) {
						Vec3 eyes = mc.player.getPositionEyes(1);
						movingObjectPosition = target.getEntityBoundingBox().expand(0.1, 0.1, 0.1)
								.calculateIntercept(eyes, eyes.addVector(rotationVector.xCoord * range,
										rotationVector.yCoord * range, rotationVector.zCoord * range));

						if (movingObjectPosition != null) {
							movingObjectPosition.typeOfHit = MovingObjectPosition.MovingObjectType.ENTITY;
							movingObjectPosition.entityHit = target;
						}
					}

					switch (this.attackMode.getValue().getName()) {
					case "Switch":
					case "Single": {
						if ((mc.player.getDistanceToEntity(target) <= range && !rayCast.getValue())
								|| (rayCast.getValue() && movingObjectPosition != null
										&& movingObjectPosition.entityHit == target)) {
							this.attack(target);
						} else if (movingObjectPosition != null
								&& movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
							if ((movingObjectPosition.entityHit instanceof EntityFireball))
								this.attack((EntityLivingBase) movingObjectPosition.entityHit);
						}
					}
						break;

					case "Multiple": {
						if ((rayCast.getValue() && movingObjectPosition != null
										&& movingObjectPosition.entityHit == target)) {
							targets.removeIf(target -> mc.player.getDistanceToEntity(target) > range);

							if (!targets.isEmpty()) {
								targets.forEach(this::attack);
							}
						}
						break;
					}
					}

					this.attackStopWatch.reset();
				}
			}
		}
	}

	@EventLink
	public final Listener<RightClickEvent> onRightClick = event -> {
		if (target == null || PlayerUtil.getItemStack() == null || !(PlayerUtil.getItemStack().getItem() instanceof ItemSword))
			return;
		
		if (autoBlock.is("Fake")) {
			if (!preventServerSideBlocking.getValue() || PlayerUtil.getItemStack() == null || !(PlayerUtil.getItemStack().getItem() instanceof ItemSword)) {
				return;
			}

			event.setCancelled();
		} else {
			event.setCancelled();
		}
	};

	@EventLink
	public final Listener<HitSlowDownEvent> onHitSlowDown = event -> {
		if (keepSprint.getValue()) {
			if (mc.player.hurtTime > 0) {
				event.setSlowDown(this.defMotion.getValue().doubleValue());
				event.setSprint(this.defCheck.getValue());
			} else {
				event.setSlowDown(this.offeMotion.getValue().doubleValue());
				event.setSprint(this.offeCheck.getValue());
			}
		}
	};

	private void preBlock() {
		switch (autoBlock.getValue().getName()) {
		case "Post":
			boolean furry = false;
			
			if (PlayerUtil.isHoldingWeapon()) {
				block(false);
				
			}
			
			if (target == null) {
				furry = true;
			}
			
			furry = true;
			
			if (furry) {
				unblock(true);
			}
			break;
		case "Experimental":
			switch (hitTicks) {
			case 1:
				block(false);
				unblock(true);
				break;
			case 2:
				block(false);
				unblock(true);
				break;
			}
			break;
		case "Legit":
			if (hitTicks == 1) {
				block(false);
			}
			break;
		}
	}

	private void attack(final EntityLivingBase target) {
		final AttackEvent event = new AttackEvent(target);
		Sakura.instance.getEventBus().handle(event);
		AttackOrder.sendFixedAttack(mc.player, target, noSwing.getValue());
		clickStopWatch.reset();
		hitTicks = 0;
	}

	public boolean canBlock() {
		return (!rightClickOnly.getValue() || mc.gameSettings.keyBindUseItem.isKeyDown())
				&& PlayerUtil.getItemStack() != null
				&& PlayerUtil.getItemStack().getItem() instanceof ItemSword;
	}

    public void unblock(boolean keyBind) {
        if (blocking) {
        	
        	if (keyBind) {
        		mc.gameSettings.keyBindUseItem.pressed = false;
        	} else {
        		PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
        				BlockPos.ORIGIN, EnumFacing.DOWN));
        	}

        	blocking = false;
        }
    }

    public void block(boolean interact) {
        if (!blocking) {
            MovingObjectPosition mouse = RayCastUtil.rayCast(RotationHandler.lastRotations, 3);

            if (interact && mouse.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                if (!mc.playerController.isPlayerRightClickingOnEntity(mc.player, mouse.entityHit, mouse)) {
                    mc.playerController.interactWithEntitySendPacket(mc.player, mouse.entityHit);
                }
            }

            mc.playerController.sendUseItem(mc.player, mc.world, PlayerUtil.getItemStack());
            
            blocking = true;
        }
    }
}