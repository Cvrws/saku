package cc.unknown.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cc.unknown.Sakura;
import cc.unknown.util.file.FileType;
import cc.unknown.util.file.enemy.EnemyFile;
import cc.unknown.util.file.friend.FriendFile;
import cc.unknown.util.player.EnemyUtil;
import cc.unknown.util.player.FriendUtil;
import net.minecraft.entity.player.EntityPlayer;

public class FriendManager {
    public static final File FRIEND_DIRECTORY = new File(FileManager.DIRECTORY, "friends");
    
    public FriendManager() {
        if (!FRIEND_DIRECTORY.exists()) {
            FRIEND_DIRECTORY.mkdir();
        }
    }

    public FriendFile getFriendFile() {
        return new FriendFile(getFile(), FileType.FRIEND);
    }

    public boolean load() {
        return getFriendFile().read();
    }

    public boolean update() {
        return getFriendFile().write();
    }

    public void addFriend(String friend) {
        if (friend != null && !friend.isEmpty()) {
            FriendUtil.addFriend(friend);
            update();
        }
    }
    
    public void removeFriend(String friend) {
        if (friend != null && !friend.isEmpty()) {
            FriendUtil.removeFriend(friend);
            update();
        }
    }

    private File getFile() {
        return new File(FRIEND_DIRECTORY, "friends.json");
    }

    public List<String> getFriends() {
        EnemyFile friendFile = Sakura.instance.getEnemyManager().getEnemyFile();        
        if (friendFile.read()) {
            return FriendUtil.getFriends();
        }
        
        return new ArrayList<>();
    }
    
    public void removeFriends() {
        FriendUtil.removeFriends();
        update();
    }
    
    public boolean isFriend(String friend) {
        return FriendUtil.getFriends().contains(friend);
    }
    
    public boolean isFriend(EntityPlayer entityPlayer) {
        return !FriendUtil.getFriends().isEmpty() && FriendUtil.getFriends().contains(entityPlayer.getName().toLowerCase());
    }
}