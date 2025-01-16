package cc.unknown.util.render.font.api;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

import cc.unknown.util.render.font.Font;
import cc.unknown.util.render.font.impl.sakura.FontRenderer;
import cc.unknown.util.render.font.impl.sakura.FontUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;

@Getter
public enum Fonts {
    MONSERAT("Montserrat-%s", "ttf"),
    ROBOTO("Roboto-%s", "ttf"),
    ICONS_1("Icon-1", "ttf"),
    ICONS_2("Icon-2", "ttf"),
    MINECRAFT("Minecraft", () -> Minecraft.getInstance().fontRendererObj);

    private final Supplier<Font> get;
    private Font font;
    @Setter
    private String name;
    private final String extention;
    private final HashMap<Integer, FontRenderer> sizes = new HashMap<>();

    Fonts(String name, String extension) {
        this.name = name;
        this.extention = extension;
        this.get = null;
    }

    Fonts(String name, Supplier<Font> get) {
        this.name = name;
        this.extention = "";
        this.get = get;
        this.font = get.get();
    }

    public Font get(int size) {
        return get(size, Weight.NONE);
    }

    public Font get() {
        return get(0, Weight.NONE);
    }

    public Font get(int size, Weight weight) {
        if (get != null && font == null) {
            font = get.get();
            return font;
        }

        int key = generateKey(size, weight);

        if (!sizes.containsKey(key)) {
            java.awt.Font font = null;
            String location = "unknown";

            for (String alias : weight.getAliases().split(",")) {
                location = String.format("sakura/font/%s.%s", String.format(name, alias), extention);
                font = FontUtil.getResource(location, size);
                if (font != null) break;
            }

            if (font != null) {
                sizes.put(key, new FontRenderer(font, true, true, false));
            }
        }

        return sizes.get(key);
    }

    private int generateKey(int size, Weight weight) {
        return 31 * size + weight.getNum();
    }
}
