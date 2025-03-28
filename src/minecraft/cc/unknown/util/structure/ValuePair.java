package cc.unknown.util.structure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValuePair {
    private final String name;
    private final String value;

    @Override
    public String toString() {
        return name + "=" + value;
    }
}