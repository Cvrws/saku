package cc.unknown.util.player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayer;

@UtilityClass
public class FriendUtil {
    private final Set<String> friends = new HashSet<>();

    public void addFriend(String friend) {
        friends.add(friend.toLowerCase());
    }

    public void removeFriend(String friend) {
        friends.remove(friend.toLowerCase());
    }

    public void addFriend(EntityPlayer entityPlayer) {
        friends.add(entityPlayer.getName().toLowerCase());
    }

    public boolean removeFriend(EntityPlayer entityPlayer) {
        return friends.remove(entityPlayer.getName().toLowerCase());
    }

    public boolean isFriend(String friend) {
        return friends.contains(friend.toLowerCase());
    }

    public boolean isFriend(EntityPlayer entityPlayer) {
        return friends.contains(entityPlayer.getName().toLowerCase());
    }

    public Set<String> getFriends() {
        return Collections.unmodifiableSet(friends);
    }

    public void removeFriends() {
        friends.clear();
    }
}
