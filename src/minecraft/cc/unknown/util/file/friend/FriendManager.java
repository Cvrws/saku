package cc.unknown.util.file.friend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.FriendComponent;
import cc.unknown.util.file.FileManager;
import cc.unknown.util.file.FileType;
import net.minecraft.entity.player.EntityPlayer;

public class FriendManager {
    public static final File FRIEND_DIRECTORY = new File(FileManager.DIRECTORY, "friends");
    private FriendComponent friendComponent;

    public void init() {
        if (!FRIEND_DIRECTORY.exists()) {
            FRIEND_DIRECTORY.mkdir();
        }
        friendComponent = new FriendComponent();
    }

    public FriendFile getFriendFile() {
        return new FriendFile(getFile(), FileType.ACCOUNT);
    }

    public boolean load() {
        return getFriendFile().read();
    }

    public boolean update() {
        return getFriendFile().write();
    }

    public void addFriend(String friend) {
        if (friend != null && !friend.isEmpty()) {
            friendComponent.addFriend(friend);
            update();
        }
    }
    
    public void removeFriend(String friend) {
        if (friend != null && !friend.isEmpty()) {
            friendComponent.removeFriend(friend);
            update();
        }
    }

    private File getFile() {
        return new File(FRIEND_DIRECTORY, "friends.json");
    }

    public synchronized List<String> getFriends() {
        FriendFile friendFile = Sakura.instance.getFriendManager().getFriendFile();        
        if (friendFile.read()) {
            return friendComponent.getFriends();
        }
        
        return new ArrayList<>();
    }
    
    public void removeFriends() {
        friendComponent.removeFriends();
        update();
    }
    
    public boolean isFriend(String friend) {
        return friendComponent.getFriends().contains(friend);
    }
    
    public boolean isFriend(EntityPlayer entityPlayer) {
        return !friendComponent.getFriends().isEmpty() && friendComponent.getFriends().contains(entityPlayer.getName().toLowerCase());
    }
}