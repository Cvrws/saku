package cc.unknown.util.render.font.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Weight {
    NONE(0, ""),
    LIGHT(1, "Light"),
    BLACK(2, "Black"),
    BOLD(3, "Bold");

    final private int num;
    final private String aliases;
}
