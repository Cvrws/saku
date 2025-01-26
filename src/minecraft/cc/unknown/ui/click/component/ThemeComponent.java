package cc.unknown.ui.click.component;

import java.awt.Color;

import cc.unknown.ui.theme.Themes;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.animation.Animation;
import cc.unknown.util.render.animation.Easing;
import cc.unknown.util.render.font.api.Fonts;
import cc.unknown.util.render.font.api.Weight;
import cc.unknown.util.structure.geometry.Vector3d;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ThemeComponent implements Accessor {
    private final Themes activeTheme;
    private Vector3d lastDraw = new Vector3d(0, 0, 0);

    private Animation xAnimation = new Animation(Easing.EASE_OUT_QUINT, 500);
    private Animation yAnimation = new Animation(Easing.EASE_OUT_QUINT, 500);
    private Animation opacityAnimation = new Animation(Easing.EASE_OUT_QUINT, 500);
    private Animation selectorAnimation = new Animation(Easing.EASE_OUT_QUINT, 500);

    public void draw(double yOffset, double width) {
        final int alpha = (int) opacityAnimation.getValue();

        final boolean active = this.activeTheme.equals(this.getTheme());
        final Color color = active ? new Color(15, 19, 26, (int) opacityAnimation.getValue()) :
                new Color(18, 21, 30, alpha);

        final double x = this.xAnimation.getValue();
        final double y = this.yAnimation.getValue() + yOffset;

        RenderUtil.roundedRectangle(x - 28, y, width, 50, 10, color);

        RenderUtil.drawRoundedGradientRectTest(x - 28, y, width, 30, 9, ColorUtil.withAlpha(activeTheme.getFirstColor(), alpha), ColorUtil.withAlpha(activeTheme.getSecondColor(), alpha), ColorUtil.withAlpha(activeTheme.getThirdColor(), alpha));

        Fonts.MAISON.get(16, Weight.NONE).drawCentered(activeTheme.getThemeName(), x - 28 + width / 2D, y + 37, active ? ColorUtil.withAlpha(this.getTheme().getFirstColor(), alpha).getRGB() : new Color(255, 255, 255, alpha).getRGB());

        selectorAnimation.animate(this.activeTheme.equals(getTheme()) ? 255 : 0);
        this.lastDraw = new Vector3d(x - 28, y, width);
    }
}
