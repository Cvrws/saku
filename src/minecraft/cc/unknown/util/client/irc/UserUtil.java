package cc.unknown.util.client.irc;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtil {

    private String user = "";

    public String getUser() {
        if (user.isEmpty()) {
            return user;
        }
        return user.substring(0, 1).toUpperCase() + user.substring(1);
    }

    public void setUser(String name) {
        if (name == null || name.isEmpty()) {
            user = "";
        } else {
            user = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
    }
}