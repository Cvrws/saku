package cc.unknown.module.api;

import cc.unknown.font.Fonts;
import cc.unknown.ui.screen.Screen;
import cc.unknown.ui.screen.impl.*;
import cc.unknown.util.render.font.Font;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    COMBAT("Combat", Fonts.ICONS_1.get(17), "a", 0x2, new CategoryScreen()),
    MOVEMENT("Movement", Fonts.ICONS_1.get(17), "b", 0x3, new CategoryScreen()),
    PLAYER("Player", Fonts.ICONS_1.get(17), "c", 0x4, new CategoryScreen()),
    LATENCY("Latency", Fonts.ICONS_2.get(17), "o", 0x5, new CategoryScreen()),
    GHOST("Ghost", Fonts.ICONS_1.get(17), "f", 0x6, new CategoryScreen()),
    OTHER("Other", Fonts.ICONS_2.get(17), "c", 0x7, new CategoryScreen()),
    VISUALS("Visuals", Fonts.ICONS_1.get(17), "g", 0x7, new CategoryScreen()),
    EXPLOIT("Exploit", Fonts.ICONS_2.get(17), "f", 0x7, new CategoryScreen()),
    WORLD("World", Fonts.ICONS_2.get(17), "h", 0x7, new CategoryScreen()),
    THEME("Themes", Fonts.ICONS_2.get(17), "m", 0x7, new ThemeScreen()),
    CONFIG("Configs", Fonts.ICONS_2.get(17), "m", 0xA, new ConfigScreen()),
    SCRIPT("Scripts", Fonts.ICONS_2.get(17), "m", 0xA, new ScriptScreen());
	
	private final String name;
    private final Font fontRenderer;
	private final String icon;
    private final int color;
    public final Screen clickGUIScreen;
}