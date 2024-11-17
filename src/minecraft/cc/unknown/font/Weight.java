package cc.unknown.font;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Weight {
    NONE(0, ""),
    LIGHT(1, "Light"),
    BOLD(4, "Bold");

    final private int num;
    final private String aliases;
}
