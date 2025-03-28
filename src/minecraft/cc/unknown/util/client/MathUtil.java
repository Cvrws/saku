package cc.unknown.util.client;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.lwjgl.input.Keyboard;

import cc.unknown.util.Accessor;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

@UtilityClass
public class MathUtil implements Accessor {

    public Number nextRandom(Number origin, Number bound) {
        if (origin.equals(bound)) return origin;

        if (origin instanceof Integer && bound instanceof Integer) {
            return ThreadLocalRandom.current().nextInt((Integer) origin, (Integer) bound);
        } else if (origin instanceof Long && bound instanceof Long) {
            return ThreadLocalRandom.current().nextLong((Long) origin, (Long) bound);
        } else if (origin instanceof Float && bound instanceof Float) {
            return (float) ThreadLocalRandom.current().nextDouble((Float) origin, (Float) bound);
        } else if (origin instanceof Double && bound instanceof Double) {
            return ThreadLocalRandom.current().nextDouble((Double) origin, (Double) bound);
        } else {
            throw new IllegalArgumentException("Unsupported number types: " + origin.getClass() + " and " + bound.getClass());
        }
    }
    
    public Number nextSecure(Number origin, Number bound) {
        if (origin.equals(bound)) return origin;
        SecureRandom secureRandom = new SecureRandom();

        if (origin instanceof Integer && bound instanceof Integer) {
            return origin.intValue() + secureRandom.nextInt(bound.intValue() - origin.intValue());
        } else if (origin instanceof Double && bound instanceof Double) {
            return origin.doubleValue() + secureRandom.nextDouble() * (bound.doubleValue() - origin.doubleValue());
        } else {
            throw new IllegalArgumentException("Unsupported number types for secure random: " + origin.getClass() + " and " + bound.getClass());
        }
    }
	
	public Number lerp(final Number a, final Number b, final Number c) {
		if (a instanceof Integer && b instanceof Integer && c instanceof Integer) {
			return a.intValue() + c.intValue() * (b.intValue() - a.intValue());
		} else if (a instanceof Double && b instanceof Double && c instanceof Double) {
			return a.doubleValue() + c.doubleValue() * (b.doubleValue() - a.doubleValue());
		} else if (a instanceof Float && b instanceof Float && c instanceof Float) {
			return a.floatValue() + c.floatValue() * (b.floatValue() - a.floatValue());
		} else if (a instanceof Long && b instanceof Long && c instanceof Long) {
			return a.longValue() + c.longValue() * (b.longValue() - a.longValue());
		} else {
            throw new IllegalArgumentException("Unsupported number types for: " + a.getClass() + ", " + b.getClass() + " and " + c.getClass());
        }
	}

	public double roundWithSteps(final double value, final double steps) {
		double a = ((Math.round(value / steps)) * steps);
		a *= 1000;
		a = (int) a;
		a /= 1000;
		return a;
	}

	public double wrappedDifference(double number1, double number2) {
		return Math.min(Math.abs(number1 - number2), Math.min(Math.abs(number1 - 360) - Math.abs(number2 - 0),
				Math.abs(number2 - 360) - Math.abs(number1 - 0)));
	}
	
	public double clamp(double min, double max, double n) {
		return Math.max(min, Math.min(max, n));
	}

	public long getSafeRandom(long min, long max) {
		double randomPercent = nextRandom(0.7, 1.3).doubleValue();
		long delay = (long) (randomPercent * nextRandom(min, max + 1).longValue());
		return delay;
	}

	public double getRandomFactor(double chanceValue) {
		return Math.abs(Math.sin(System.nanoTime() * Double.doubleToLongBits(chanceValue))) * 100.0;
	}
    
	public boolean shouldPerformAction(double chanceValue, double randomFactor) {
		return chanceValue >= 100.0D || ThreadLocalRandom.current().nextDouble(100.0D + randomFactor) < chanceValue;
	}

	public boolean inBetween(double min, double max, double value) {
		return value >= min && value <= max;
	}
	
    public double round(double n, int d) {
        if (d == 0) {
            return (double) Math.round(n);
        } else {
            double p = Math.pow(10.0D, (double) d);
            return (double) Math.round(n * p) / p;
        }
    }
    
    public boolean isWholeNumber(double num) {
        return num == Math.floor(num);
    }
    
    public boolean isChance(NumberValue chance, BooleanValue speed, BooleanValue jumpBoost) {
		int chanceValue = chance.getValueToInt();
		double randomFactor = MathUtil.getRandomFactor(chanceValue);

		if (noAction(speed.getValue(), jumpBoost.getValue()) || checks()) return false;
		if (!shouldPerformAction(chanceValue, randomFactor)) return false;
		
		return true;
	}
    
    public boolean isChance(NumberValue chance) {
		int chanceValue = chance.getValueToInt();
		double randomFactor = MathUtil.getRandomFactor(chanceValue);

		if (checks()) return false;
		if (!shouldPerformAction(chanceValue, randomFactor)) return false;
    	return true;
    }
    
    public boolean shouldPerformAction(BooleanValue onlyClick, BooleanValue onlyTarget, BooleanValue disablePressS, EntityPlayer target) {
        return !(onlyClick.getValue() && !mc.player.isSwingInProgress)
                && !(onlyTarget.getValue() && target != null)
                && !(disablePressS.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
    }
    
    public boolean checks() {
        return Stream.<Supplier<Boolean>>of(mc.player::isInLava, mc.player::isBurning, mc.player::isInWater, () -> mc.player.isInWeb).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
    }
    
    private boolean noAction(boolean speed, boolean jumpBoost) {
        return mc.player.getActivePotionEffects().stream().anyMatch(effect ->
            (speed && effect.getPotionID() == Potion.moveSpeed.getId()) ||
            (jumpBoost && effect.getPotionID() == Potion.jump.getId()));
    }
}