package cc.unknown.module.impl.visual;

import static cc.unknown.util.streamer.StreamerUtil.*;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.FriendComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.RenderTextEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.StringValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.util.ChatFormatting;

@ModuleInfo(aliases = "Streamer", description = "Hides your name", category = Category.VISUALS)
public final class Streamer extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Universocraft"))
			.add(new SubMode("Normal"))
			.setDefault("Universocraft");
	
	private final ModeValue spoofRank = new ModeValue("Rank", this, () -> !mode.is("Universocraft"))
			.add(new SubMode("Usu"))
			.add(new SubMode("Jup"))
			.add(new SubMode("Nep"))
			.add(new SubMode("Mer"))
			.add(new SubMode("Sat"))
			.add(new SubMode("Str"))
			.add(new SubMode("Ayu"))
			.add(new SubMode("Bui"))
			.add(new SubMode("Mod"))
			.add(new SubMode("Adm"))
			.setDefault("Jup");
	
	private final BooleanValue fixOwn = new BooleanValue("Fix Own Rank", this, true, () -> !mode.is("Universocraft"));

    public final StringValue replacement = new StringValue("Spoof Name: ", this, "You");
    private final BooleanValue checkFriends = new BooleanValue("Check Friends", this, false);
    private final StringValue protectFriends = new StringValue("New Name: ", this, "Friend", () -> !checkFriends.getValue());
    
    private Map<String, ChatFormatting> ranks = new HashMap<>();
    
    public Streamer() {
        ranks.put("Usu", reset);
        ranks.put("Jup", aqua);
        ranks.put("Nep", blue);
        ranks.put("Mer", darkGreen);
        ranks.put("Sat", darkPurple);
        ranks.put("Str", lightPurple);
        ranks.put("Ayu", yellow);
        ranks.put("Bui", green);
        ranks.put("Mod", darkAqua);
        ranks.put("Adm", red);
    }

    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<RenderTextEvent> onRenderText = event -> {
        assert mc.player != null;
        String text = event.getString();
        String playerName = mc.player.getName();
        String mode = spoofRank.getValue().getName();
        String newName = replacement.getValue();
        ChatFormatting color = ranks.get(mode);
        
        if (text.startsWith("/") || text.startsWith(Sakura.instance.getCommandManager().getPrefix())) {
            return;
        }

        if (text.contains(playerName)) {
        	if (fixOwn.getValue()) {
        		
        	} else {
        		text = text.replaceAll(usu, "");
        	}
            
            if (color != null) {
            	newName = getPrefix(mode, color) + newName;
            }

            text = text.replace(playerName, newName);
            event.setString(text);
        }

        if (checkFriends.getValue()) {
            for (String friend : Sakura.instance.getFriendManager().getFriends()) {
                text = text.replaceAll(jup, "");
                
                if (color != null) {
                	newName = getPrefix(mode, color) + friend; 
                } else {
                	newName = friend;
                }

                text = text.replace(friend, newName);
                
                event.setString(text);
            }
        }
    };
}