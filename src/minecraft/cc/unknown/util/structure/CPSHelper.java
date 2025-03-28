package cc.unknown.util.structure;

import java.util.concurrent.atomic.AtomicLongArray;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class CPSHelper {

    private static final int MAX_CPS = 500;
    private static final AtomicLongArray[] TIMESTAMP_BUFFERS = new AtomicLongArray[MouseButton.values().length];

    static {
        for (int i = 0; i < TIMESTAMP_BUFFERS.length; i++) {
            TIMESTAMP_BUFFERS[i] = new AtomicLongArray(MAX_CPS);
        }
    }

    public static void registerClick(MouseButton button) {
        int index = button.getIndex();
        int slot = (int) (System.currentTimeMillis() % MAX_CPS);
        TIMESTAMP_BUFFERS[index].set(slot, System.currentTimeMillis());
    }

    public static int getCPS(MouseButton button) {
        int index = button.getIndex();
        long currentTime = System.currentTimeMillis();
        int count = 0;

        for (int i = 0; i < MAX_CPS; i++) {
            long timestamp = TIMESTAMP_BUFFERS[index].get(i);
            if (timestamp > currentTime - 1000L) {
                count++;
            }
        }

        return count;
    }

    @Getter
    @RequiredArgsConstructor
    public enum MouseButton {
        LEFT(0), RIGHT(1), MIDDLE(2);

        private final int index;
    }
}