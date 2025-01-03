package cc.unknown.util.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {

	public static int nextInt(int origin, int bound) {
		return origin == bound ? origin : ThreadLocalRandom.current().nextInt(origin, bound);
	}

	public static long nextLong(long origin, long bound) {
		return origin == bound ? origin : ThreadLocalRandom.current().nextLong(origin, bound);
	}

	public static float nextFloat(double origin, double bound) {
		return origin == bound ? (float) origin
				: (float) ThreadLocalRandom.current().nextDouble((double) ((float) origin), (double) ((float) bound));
	}

	public static float nextFloat(float origin, float bound) {
		return origin == bound ? origin
				: (float) ThreadLocalRandom.current().nextDouble((double) origin, (double) bound);
	}

	public static double nextDouble(double origin, double bound) {
		return origin == bound ? origin : ThreadLocalRandom.current().nextDouble(origin, bound);
	}

	public double getRandom(double min, double max) {
		if (min == max) {
			return min;
		} else if (min > max) {
			final double d = min;
			min = max;
			max = d;
		}
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	public double round(final double value, final int places) {
		try {
			final BigDecimal bigDecimal = BigDecimal.valueOf(value);

			return bigDecimal.setScale(places, RoundingMode.HALF_UP).doubleValue();
		} catch (Exception exception) {
			return 0;
		}
	}

	public double roundWithSteps(final double value, final double steps) {
		double a = ((Math.round(value / steps)) * steps);
		a *= 1000;
		a = (int) a;
		a /= 1000;
		return a;
	}

	public double lerp(final double a, final double b, final double c) {
		return a + c * (b - a);
	}

	public float lerp(final float a, final float b, final float c) {
		return a + c * (b - a);
	}

	public double clamp(double min, double max, double n) {
		return Math.max(min, Math.min(max, n));
	}

	public double wrappedDifference(double number1, double number2) {
		return Math.min(Math.abs(number1 - number2), Math.min(Math.abs(number1 - 360) - Math.abs(number2 - 0),
				Math.abs(number2 - 360) - Math.abs(number1 - 0)));
	}

	public double nextSecureDouble(double origin, double bound) {
		if (origin == bound) {
			return origin;
		} else {
			SecureRandom secureRandom = new SecureRandom();
			double difference = bound - origin;
			return origin + secureRandom.nextDouble() * difference;
		}
	}
	
	public int nextSecureInt(int origin, int bound) {
		if (origin == bound) {
			return origin;
		} else {
			SecureRandom secureRandom = new SecureRandom();
			int difference = bound - origin;
			return origin + secureRandom.nextInt() * difference;
		}
	}
	
	public long getSafeRandom(long min, long max) {
		double randomPercent = ThreadLocalRandom.current().nextDouble(0.7, 1.3);
		long delay = (long) (randomPercent * ThreadLocalRandom.current().nextLong(min, max + 1));
		return delay;
	}
	
	public double getRandomFactor(double chanceValue) {
	    return Math.abs(Math.sin(System.nanoTime() * Double.doubleToLongBits(chanceValue))) * 100.0;
	}

	public boolean shouldPerformAction(double chanceValue, double randomFactor) {
	    return chanceValue >= 100.0D || ThreadLocalRandom.current().nextDouble(100.0D + randomFactor) < chanceValue;
	}
	
    public int randomClickDelay(int minCPS, int maxCPS) {
        return (int) (Math.random() * (1000 / minCPS - 1000 / maxCPS + 1) + 1000 / maxCPS);
    }
    
    public static float getAdvancedRandom(float min, float max) {
        SecureRandom random = new SecureRandom();

        long finalSeed = System.nanoTime();

        for (int i = 0; i < 3; ++i) {
            long seed = (long) (Math.random() * 1_000_000_000);

            seed ^= (seed << 13);
            seed ^= (seed >>> 17);
            seed ^= (seed << 15);

            finalSeed += seed;
        }

        random.setSeed(finalSeed);

        return random.nextFloat() * (max - min) + min;
    }
}