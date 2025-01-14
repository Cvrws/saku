package cc.unknown.util.account;

import static cc.unknown.util.client.StreamerUtil.gray;
import static cc.unknown.util.client.StreamerUtil.green;

import com.google.gson.JsonObject;

import cc.unknown.ui.menu.AltManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

@Getter
@Setter
public class Account {
    private AccountType type;
    private String name;
    private String uuid;
    private String accessToken;

    public Account(AccountType type, String name, String uuid, String accessToken) {
        this.type = type;
        this.name = name;
        this.uuid = uuid;
        this.accessToken = accessToken;
    }

    public boolean login() {
        Minecraft.getInstance().setSession(new Session(name, uuid, accessToken, "mojang"));
        return true;
    }

    public boolean isValid() {
        return name != null && uuid != null && accessToken != null && !name.isEmpty() && !uuid.isEmpty() && !accessToken.isEmpty();
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", type.getName());
        object.addProperty("name", name);
        object.addProperty("uuid", uuid);
        object.addProperty("accessToken", accessToken);
        return object;
    }

    public void parseJson(JsonObject object) {
        if (object.has("type")) {
            type = AccountType.getByName(object.get("type").getAsString());
        }

        if (object.has("name")) {
            name = object.get("name").getAsString();
        }

        if (object.has("uuid")) {
            uuid = object.get("uuid").getAsString();
        }

        if (object.has("accessToken")) {
            accessToken = object.get("accessToken").getAsString();
        }
    }
}
