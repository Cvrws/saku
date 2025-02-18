package cc.unknown.util.render.gui;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.Font;
import cc.unknown.util.render.gui.box.TextAlign;
import cc.unknown.util.structure.geometry.Vector2d;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrollUtil implements Accessor {

    public double target, scroll, max = 25;
    public StopWatch stopwatch = new StopWatch();
    public StopWatch stopWatch2 = new StopWatch();
    public boolean scrollingIsAllowed, active, animating;
    private boolean reverse;

    public void onRender() {
        onRender(true);
    }

    public void onRender(boolean update) {
        if (stopWatch2.finished(0)) {
            final float wheel = update ? Mouse.getDWheel() * (reverse ? -1 : 1) : 0;
            double stretch = 30;
            active = wheel != 0;
            target = Math.min(Math.max(target + wheel / 2, max - (wheel == 0 ? 0 : stretch)), (wheel == 0 ? 0 : stretch));

            stopWatch2.reset();
        }

        for (int i = 0; i < stopwatch.getElapsedTime(); ++i) {
            scroll = MathUtil.lerp(scroll, target, 0.01).doubleValue();
        }

        animating = Math.abs(scroll - target) > 0.5;

        stopwatch.reset();
    }

    public void reset() {
        this.scroll = 0;
        this.target = 0;
    }
}
