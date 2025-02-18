package cc.unknown.module.impl.visual.stickers;

import lombok.Getter;

@Getter
public enum StickerType {
    SHIROKO("Shiroko", "shiroko.png", 85, 160),
    JUPO("Jupo", "jupo.png", 85, 130),
    MIKA("Mika", "mika.png", 95, 160),
    HOSHINO("Ai Hoshino", "ai.png", 95, 160),
    ASTOLFO("Astolfo", "astolfo.png", 95, 160),
    ASTOLFO2("Astolfo 2", "astolfo2.png", 130, 160),
    ELF("Elf", "elf.png", 95, 160),
    KIWI("Kiwi", "kiwi.png", 95, 160),
    KUMI("Kumi", "kumi.png", 95, 160),
    KURUMI("Kurumi", "kurumi.png", 100, 180),
    MAGIC("Magic", "magic.png", 95, 160),
    MAI("Mai", "mai.png", 95, 160),
    MEGUMIN("Megumin Cat", "megumin.png", 95, 160),
    UTENA("Utena", "utena.png", 95, 160),
    UZAKI("Uzaki Chan", "uzaki.png", 95, 160),
    HALFLIN("Halflin", "manolo.png", 95, 160),
    KOMI("Komi San", "komi.png", 95, 160),
    HIDERI("Hideri", "hideri.png", 95, 160),
    FUJIWARA("Fujiwara", "fujiwara.png", 95, 160),
    GWEN("Gwen Bunny", "bunny.png", 95, 160),
    AKARI("Akari", "akari.png", 95, 160),
    TYPH("Typh", "typh.png", 95, 160),
    MILIM("Milim", "milim.png", 95, 160),
    OMNI("Omni Man", "omni.png", 170, 160),
    AMONGUS("Among Us", "amongus.png", 85, 90);

    private final String displayName;
    private final String imagePath;
    private final int width;
    private final int height;

    StickerType(String displayName, String fileName, int width, int height) {
        this.displayName = displayName;
        this.imagePath = "sakura/stickers/" + fileName;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return displayName;
    }
}