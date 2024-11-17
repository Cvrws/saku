package cc.unknown.util.file.enemy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.EnemyComponent;
import cc.unknown.util.file.FileManager;
import cc.unknown.util.file.FileType;
import net.minecraft.entity.player.EntityPlayer;

public class EnemyManager {
    public static final File ENEMY_DIRECTORY = new File(FileManager.DIRECTORY, "enemys");
    private EnemyComponent enemyComponent;

    public void init() {
        if (!ENEMY_DIRECTORY.exists()) {
        	ENEMY_DIRECTORY.mkdir();
        }
        enemyComponent = new EnemyComponent();
    }

    public EnemyFile getEnemyFile() {
        return new EnemyFile(getFile(), FileType.ACCOUNT);
    }

    public boolean load() {
        return getEnemyFile().read();
    }

    public boolean update() {
        return getEnemyFile().write();
    }

    public void addEnemy(String target) {
        if (target != null && !target.isEmpty()) {
        	enemyComponent.addEnemy(target);
            update();
        }
    }
    
    public void removeEnemy(String target) {
        if (target != null && !target.isEmpty()) {
        	enemyComponent.removeEnemy(target);
            update();
        }
    }

    private File getFile() {
        return new File(ENEMY_DIRECTORY, "enemy.json");
    }

    public List<String> getEnemy() {
        EnemyFile enemyFile = Sakura.instance.getEnemyManager().getEnemyFile();        
        if (enemyFile.read()) {
            return enemyComponent.getEnemy();
        }
        
        return new ArrayList<>();
    }
    
    public boolean isEnemy(EntityPlayer entityPlayer) {
        return !enemyComponent.getEnemy().isEmpty() && enemyComponent.getEnemy().contains(entityPlayer.getName().toLowerCase());
    }
    
    public boolean isEnemy(String friend) {
        return enemyComponent.getEnemy().contains(friend);
    }
    
    public void removeEnemy() {
    	enemyComponent.removeEnemy();
        update();
    }
}