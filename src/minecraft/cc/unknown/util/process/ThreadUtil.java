package cc.unknown.util.process;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ThreadUtil {
    public void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}
