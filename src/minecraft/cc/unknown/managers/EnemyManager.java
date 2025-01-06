package cc.unknown.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.util.file.FileType;
import cc.unknown.util.file.enemy.EnemyFile;
import cc.unknown.util.player.EnemyUtil;
import net.minecraft.entity.player.EntityPlayer;

public class EnemyManager {
    public static final File ENEMY_DIRECTORY = new File(FileManager.DIRECTORY, "enemys");

    public EnemyManager() {
        if (!ENEMY_DIRECTORY.exists()) {
        	ENEMY_DIRECTORY.mkdir();
        }
    }

    public EnemyFile getEnemyFile() {
        return new EnemyFile(getFile(), FileType.ENEMY);
    }

    public boolean load() {
        return getEnemyFile().read();
    }

    public boolean update() {
        return getEnemyFile().write();
    }

    public void addEnemy(String target) {
        if (target != null && !target.isEmpty()) {
        	EnemyUtil.addEnemy(target);
            update();
        }
    }
    
    public void removeEnemy(String target) {
        if (target != null && !target.isEmpty()) {
        	EnemyUtil.removeEnemy(target);
            update();
        }
    }

    private File getFile() {
        return new File(ENEMY_DIRECTORY, "enemy.json");
    }

    public List<String> getEnemy() {
        EnemyFile enemyFile = Sakura.instance.getEnemyManager().getEnemyFile();        
        if (enemyFile.read()) {
            return EnemyUtil.getEnemy();
        }
        
        return new ArrayList<>();
    }
    
    public boolean isEnemy(EntityPlayer entityPlayer) {
        return !EnemyUtil.getEnemy().isEmpty() && EnemyUtil.getEnemy().contains(entityPlayer.getName().toLowerCase());
    }
    
    public boolean isEnemy(String friend) {
        return EnemyUtil.getEnemy().contains(friend);
    }
    
    public void removeEnemy() {
    	EnemyUtil.removeEnemy();
        update();
    }
}