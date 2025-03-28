package cc.unknown.util.memoryfix;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import cc.unknown.Sakura;
import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResourcePackImageScaler {
    public BufferedImage reScaleImage(BufferedImage image) {
        if (image == null) return null;
        
        Sakura.instance.getLogger().debug("Scaling resource pack icon from " + image.getWidth() + " to " + "64");
        BufferedImage smallImage = new BufferedImage(64, 64, 2);
        Graphics graphics = smallImage.getGraphics();
        graphics.drawImage(image, 0, 0, 64, 64, null);
        graphics.dispose();
        return smallImage;
    }
}