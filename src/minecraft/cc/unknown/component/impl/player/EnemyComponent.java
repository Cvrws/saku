package cc.unknown.component.impl.player;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class EnemyComponent {
    private static final ArrayList<String> enemys = new ArrayList<>();

    public static void addEnemy(String target) {
        if (!enemys.contains(target)) {
            enemys.add(target);
        }
    }

    public static void removeEnemy(String target) {
        enemys.remove(target);
    }
    
    public static boolean isEnemy(EntityPlayer entityPlayer) {
        return !enemys.isEmpty() && enemys.contains(entityPlayer.getName().toLowerCase());
    }
    
    public static boolean isEnemy(String target) {
        return enemys.contains(target);
    }

    public static List<String> getEnemy() {
        return new ArrayList<>(enemys);
    }
    
    public static void removeEnemy() {
    	enemys.clear();
    }
}
