package cc.unknown.util.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserInfo {
    private final UserType type;

    public enum UserType {
        BETA, CUSTOMER;
    }

    public String getType() {
        return type.toString().charAt(0) + type.toString().substring(1).toLowerCase();
    }
}