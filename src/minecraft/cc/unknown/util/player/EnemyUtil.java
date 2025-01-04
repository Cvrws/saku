package cc.unknown.util.player;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayer;

@UtilityClass
public class EnemyUtil {
    private final ArrayList<String> enemys = new ArrayList<>();

    public void addEnemy(String target) {
        if (!enemys.contains(target)) {
            enemys.add(target);
        }
    }

    public void removeEnemy(String target) {
        enemys.remove(target);
    }
    
    public boolean isEnemy(EntityPlayer entityPlayer) {
        return !enemys.isEmpty() && enemys.contains(entityPlayer.getName().toLowerCase());
    }
    
    public boolean isEnemy(String target) {
        return enemys.contains(target);
    }

    public List<String> getEnemy() {
        return new ArrayList<>(enemys);
    }
    
    public void removeEnemy() {
    	enemys.clear();
    }
}
