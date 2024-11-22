package cc.unknown.component.impl.player;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cc.unknown.component.impl.Component;
import cc.unknown.component.impl.render.NotificationComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.util.player.PingerCallable;
import cc.unknown.util.time.StopWatch;

public final class PingComponent extends Component {
    private static long ping = 250;
    private static final Executor thread = Executors.newFixedThreadPool(1);
    private final static StopWatch lastPing = new StopWatch();
    private final static StopWatch lastGrab = new StopWatch();
    private static final long TIMEOUT_AUTO_DISABLE = 120000;
    private static final long DELAY = 10000;
    private static final long DEFAULT_PING = 250;

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (lastPing.finished(DELAY) && !lastGrab.finished(TIMEOUT_AUTO_DISABLE)) {
            ping();
            lastPing.reset();
        }
    };

    public static long getPing() {
        if (lastGrab.finished(TIMEOUT_AUTO_DISABLE)) {
            ping();
            lastGrab.reset();
            return DEFAULT_PING;
        } else {
            lastGrab.reset();
            return ping;
        }
    }

    private static void ping() {
        if (mc.isIntegratedServerRunning()) {
            ping = 0;
            return;
        }

        thread.execute(() -> {
            lastPing.reset();
            NotificationComponent.post("Ping", "Se está analizando tu ping..", 7000);
            PingerCallable pingerCallable = new PingerCallable(LastConnectionComponent.ip);

            ping = pingerCallable.call();
        });
    }
}