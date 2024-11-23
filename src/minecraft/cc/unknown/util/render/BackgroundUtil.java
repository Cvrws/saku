package cc.unknown.util.render;

import java.awt.Color;

import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class BackgroundUtil implements Accessor {
    public void renderBackground(GuiScreen gui) {
        ScaledResolution sr = mc.scaledResolution;
        RenderUtil.image(new ResourceLocation("sakura/images/background.png"), 0, 0, gui.width, gui.height);
        RenderUtil.drawRoundedRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, new Color(0, 0, 0, 170).getRGB());
    }
}