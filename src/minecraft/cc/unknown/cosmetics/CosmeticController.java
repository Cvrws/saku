package cc.unknown.cosmetics;

import cc.unknown.Sakura;
import cc.unknown.cosmetics.impl.*;
import cc.unknown.module.impl.visual.Cosmetics;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class CosmeticController {

    private static Cosmetics getCosmetics() {
        return Sakura.instance.getModuleManager().get(Cosmetics.class);
    }

    private static boolean shouldRenderCosmetic(String modeName) {
        Cosmetics cosmetics = getCosmetics();
        if (!cosmetics.isEnabled()) {
            return false;
        }
        
        switch (modeName) {
            case "Doug Dimmadome":
                return cosmetics.doug.getValue();
            case "Top Hat":
                return cosmetics.topHat.getValue();
            case "Whiter Pet":
                return cosmetics.whiterPet.getValue();
            case "Dog Pet":
            	return cosmetics.dogPet.getValue();
            default:
                return false;
        }
    }

    public static boolean shouldRenderDougDimmadomeHat(AbstractClientPlayer player) {
        return shouldRenderCosmetic("Doug Dimmadome");
    }

    public static boolean shouldRenderTophat(AbstractClientPlayer player) {
        return shouldRenderCosmetic("Top Hat");
    }
    
    public static boolean shouldRenderWhiterPet(AbstractClientPlayer player) {
        return shouldRenderCosmetic("Whiter Pet");
    }
    
    public static boolean shouldRenderDogPet(AbstractClientPlayer player) {
    	return shouldRenderCosmetic("Dog Pet");
    }
    
    public static float[] getTophatColor(AbstractClientPlayer player) {
        return new float[] { 1, 0, 0 };
    }

	public static void addModels(RenderPlayer renderPlayer) {
		renderPlayer.addLayer(new DougDimmadome(renderPlayer));
		renderPlayer.addLayer(new Tophat(renderPlayer));
		renderPlayer.addLayer(new WhiterPet(renderPlayer));
		renderPlayer.addLayer(new DogPet(renderPlayer));
	}
}