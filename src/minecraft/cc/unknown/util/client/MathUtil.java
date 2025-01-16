package cc.unknown.util.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {

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
    
    public Number nextSecureRandom(Number origin, Number bound) {
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

	public double lerp(final double a, final double b, final double c) {
		return a + c * (b - a);
	}

	public float lerp(final float a, final float b, final float c) {
		return a + c * (b - a);
	}

	public double getRandom(double min, double max) {
		if (min == max) {
			return min;
		} else if (min > max) {
			final double d = min;
			min = max;
			max = d;
		}
		return nextRandom(min, max).doubleValue();
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
}