package cc.unknown.util.player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayer;

@UtilityClass
public class EnemyUtil {
    private final Set<String> enemies = new HashSet<>();

    public void addEnemy(String target) {
        enemies.add(target.toLowerCase());
    }

    public void removeEnemy(String target) {
        enemies.remove(target.toLowerCase());
    }

    public boolean isEnemy(EntityPlayer entityPlayer) {
        return enemies.contains(entityPlayer.getName().toLowerCase());
    }

    public boolean isEnemy(String target) {
        return enemies.contains(target.toLowerCase());
    }

    public Set<String> getEnemies() {
        return Collections.unmodifiableSet(enemies);
    }

    public void clearEnemies() {
        enemies.clear();
    }
}
