package cc.unknown.util.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopWatch {
	public long millis;

	public StopWatch() {
		reset();
	}

	public boolean finished(long delay) {
		return System.currentTimeMillis() - delay >= millis;
	}

	public boolean elapse(double delay, boolean reset) {
		if ((double) (System.currentTimeMillis() - this.millis) >= delay) {
			if (reset) {
				this.reset();
			}

			return true;
		} else {
			return false;
		}
	}
	
    public boolean reached(final long lastTime, final long currentTime) {
        return Math.max(0L, System.currentTimeMillis() - millis + lastTime) >= currentTime;
    }

	public boolean hasFinished() {
		return System.currentTimeMillis() >= millis;
	}

	public boolean hasReach(float millis) {
		return getElapsedTime() >= (millis);
	}

	public void reset() {
		this.millis = System.currentTimeMillis();
	}

	public long getElapsedTime() {
		return System.currentTimeMillis() - this.millis;
	}

	private long getSystemTime() {
		return (long) (System.nanoTime() / 1E6);
	}
}