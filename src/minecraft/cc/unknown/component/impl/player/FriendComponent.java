package cc.unknown.component.impl.player;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class FriendComponent {
    private static final ArrayList<String> friends = new ArrayList<>();

    public static void addFriend(String friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
        }
    }

    public static void removeFriend(String friend) {
        friends.remove(friend);
    }
    
    public static void addFriend(EntityPlayer entityPlayer) {
        if (!friends.contains(entityPlayer.getName().toLowerCase())) {
            friends.add(entityPlayer.getName().toLowerCase());
        }
    }
    
    public static boolean removeFriend(EntityPlayer entityPlayer) {
        return !friends.isEmpty() && friends.remove(entityPlayer.getName().toLowerCase());
    }

    public static boolean isFriend(String friend) {
        return friends.contains(friend);
    }
    
    public static boolean isFriend(EntityPlayer entityPlayer) {
        return !friends.isEmpty() && friends.contains(entityPlayer.getName().toLowerCase());
    }
    
    public static List<String> getFriends() {
        return new ArrayList<>(friends);
    }
    
    public static void removeFriends() {
    	friends.clear();
    }
}
