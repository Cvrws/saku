package cc.unknown.util.render.gif;

import java.awt.image.BufferedImage;

import lombok.Getter;

@Getter
public class ImageFrame {

    private final BufferedImage image;
    private final int index, delay, width, height;

    public ImageFrame(BufferedImage image, int index, int delay) {
        this.image = image;
        this.index = index;
        this.delay = delay;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }
}