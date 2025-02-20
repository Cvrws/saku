package cc.unknown.module.impl.visual.gifs;

import lombok.Getter;

@Getter
public enum GifType {
	MOMOI("Momoi", "momoi.gif"),
	TACO("Taco Cat", "taco.gif");

    private final String displayName;
    private final String imagePath;
    
    GifType(String displayName, String fileName) {
        this.displayName = displayName;
        this.imagePath = "sakura/gif/" + fileName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}