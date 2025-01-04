package cc.unknown.util.player;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayer;

@UtilityClass
public class FriendUtil {
    private final ArrayList<String> friends = new ArrayList<>();

    public void addFriend(String friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
        }
    }

    public void removeFriend(String friend) {
        friends.remove(friend);
    }
    
    public void addFriend(EntityPlayer entityPlayer) {
        if (!friends.contains(entityPlayer.getName().toLowerCase())) {
            friends.add(entityPlayer.getName().toLowerCase());
        }
    }
    
    public boolean removeFriend(EntityPlayer entityPlayer) {
        return !friends.isEmpty() && friends.remove(entityPlayer.getName().toLowerCase());
    }

    public boolean isFriend(String friend) {
        return friends.contains(friend);
    }
    
    public boolean isFriend(EntityPlayer entityPlayer) {
        return !friends.isEmpty() && friends.contains(entityPlayer.getName().toLowerCase());
    }
    
    public List<String> getFriends() {
        return new ArrayList<>(friends);
    }
    
    public void removeFriends() {
    	friends.clear();
    }
}
