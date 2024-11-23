package cc.unknown.util.client.user;

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

    public void setUser(String usser) {
        if (usser == null || usser.isEmpty()) {
            user = "";
        } else {
            user = usser.substring(0, 1).toUpperCase() + usser.substring(1);
        }
    }
}