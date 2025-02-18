package cc.unknown.cosmetics;

import cc.unknown.Sakura;
import cc.unknown.cosmetics.accessories.*;
import cc.unknown.cosmetics.api.CosmeticType;
import cc.unknown.cosmetics.aura.*;
import cc.unknown.cosmetics.hat.*;
import cc.unknown.cosmetics.pet.*;
import cc.unknown.cosmetics.wings.*;
import cc.unknown.cosmetics.cape.*;
import cc.unknown.module.impl.visual.Cosmetics;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class CosmeticController {

    private static Cosmetics getCosmetics() {
        return Sakura.instance.getModuleManager().get(Cosmetics.class);
    }

    private static boolean shouldRenderCosmetic(CosmeticType type) {
        Cosmetics cosmetics = getCosmetics();
        if (!cosmetics.isEnabled()) {
            return false;
        }

        switch (type) {
            case DIMMADOME: return cosmetics.hatType.is(type.getName());
            case TOP: return cosmetics.hatType.is(type.getName());
            case WHITER: return cosmetics.petType.is(type.getName());
            case DOG: return cosmetics.petType.is(type.getName());
            case BANDANA: return cosmetics.accesoriesType.is(type.getName());
            case GALAXY: return cosmetics.wingsType.is(type.getName());
            case CRYSTAL: return cosmetics.wingsType.is(type.getName());
            case WITCH: return cosmetics.hatType.is(type.getName());
            case ORBIT: return cosmetics.auraType.is(type.getName());
            case ENCHANTING: return cosmetics.auraType.is(type.getName());
            case BLAZE: return cosmetics.auraType.is(type.getName());
            case CREEPER: return cosmetics.auraType.is(type.getName());
            default: return false;
        }
    }

    public static boolean shouldRenderCosmeticForPlayer(AbstractClientPlayer player, CosmeticType type) {
        return shouldRenderCosmetic(type);
    }

    public static float[] getTophatColor(AbstractClientPlayer player) {
        return new float[]{1, 0, 0};
    }

    public static void addModels(RenderPlayer renderPlayer) {
        renderPlayer.addLayer(new DougDimmadome(renderPlayer));
        renderPlayer.addLayer(new Tophat(renderPlayer));
        renderPlayer.addLayer(new WhiterPet(renderPlayer));
        renderPlayer.addLayer(new DogPet(renderPlayer));
        renderPlayer.addLayer(new Bandana(renderPlayer));
        renderPlayer.addLayer(new GalaxyWings(renderPlayer));
        renderPlayer.addLayer(new WitchHat(renderPlayer));
        renderPlayer.addLayer(new OrbitAura(renderPlayer));
        renderPlayer.addLayer(new BlazeAura(renderPlayer));
        renderPlayer.addLayer(new CreeperAura(renderPlayer));
        renderPlayer.addLayer(new EnchantingAura(renderPlayer));
        renderPlayer.addLayer(new CrystalWings(renderPlayer));
        renderPlayer.addLayer(new Cape(renderPlayer));
    }
}
