package cc.unknown.util.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientInfo {
    private final String name;
    private final String version;
    private final VersionType type;

    public enum VersionType {
        PRIVATE, DEVELOPER;
    }

    public String getType() {
        return type.toString().charAt(0) + type.toString().substring(1).toLowerCase();
    }
}