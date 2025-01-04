package cc.unknown.util.render.font.impl.sakura;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class FontUtil {
    public Font getResource(final String resource, final int size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(resource)).getInputStream()).deriveFont((float) size);
        } catch (final FontFormatException | IOException ignored) {
            return null;
        }
    }
}
