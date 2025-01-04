package cc.unknown.font;

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
    MONSERAT("Montserrat-%s", getExt()),
    ROBOTO("Roboto-%s", getExt()),
    ICONS_1("Icon-1", "ttf"),
    ICONS_2("Icon-2", "ttf"),
    MINECRAFT("Minecraft", () -> Minecraft.getInstance().fontRendererObj);
	
    Supplier<Font> get;
    Font font;
    @Setter String name;
    final String extention;
    private final HashMap<Integer, FontRenderer> sizes = new HashMap<>();
	
    Fonts(String name, String extension) {
        this.name = name;
        this.extention = extension;
    }
    
    Fonts(String name, Supplier<Font> get) {
        this.name = name;
        this.extention = "";
        this.font = get.get();
        this.get = get;
    }

    @SneakyThrows
    public Font get(int size) {
        return get(size, Weight.NONE);
    }

    @SneakyThrows
    public Font get() {
        return get(0, Weight.NONE);
    }

    @SneakyThrows
    public Font get(int size, Weight weight) {
        if (get != null) {
            if (font == null) font = get.get();
            return font;
        }
        
        int key = Integer.parseInt(size + "" + weight.getNum());

        if (!sizes.containsKey(key)) {
            java.awt.Font font = null;
            String location = "unknown";

            for (String alias : weight.getAliases().split(",")) {
                location = "sakura/font/" + String.format(name, alias) + "." + extention;
                font = FontUtil.getResource(location, size);
                if (font != null) break;
            }
            if (font != null) {
                sizes.put(key, new FontRenderer(font, true, true, false));
            }
        }

        return sizes.get(key);
    }
    
    private static String getExt() {
        String vendor = System.getProperty("java.vendor");
        return vendor != null && vendor.contains("Oracle") ? "ttf" : "otf";
    }
}
