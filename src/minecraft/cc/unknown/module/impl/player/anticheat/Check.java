package cc.unknown.module.impl.player.anticheat;

import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.impl.player.AntiCheat;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.ChatUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.render.ColorUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatFormatting;

public abstract class Check implements Accessor {
	public final StopWatch stopWatch = new StopWatch();
	
    public abstract String getName();

    public void onPlayer(EntityPlayer player) { }

    public void onMotion(EntityPlayer player, double x, double y, double z) { }
    
    public void onReceive(PacketReceiveEvent event, EntityPlayer player) { }

    public void flag(EntityPlayer player, String verbose) {
    	ChatFormatting white = ColorUtil.white;
    	ChatFormatting gray = ColorUtil.gray;
    	ChatFormatting pink = ColorUtil.pink;
    	ChatFormatting red = ColorUtil.red;
    	ChatFormatting blue = ColorUtil.blue;
    	
    	String prefix = pink + "[S] " + red + player.getName() + white + " detected for " + blue + getName() + white + " Reason: " + verbose;
    	
        ChatUtil.display(prefix);
        getModuleManager().get(AntiCheat.class).mark(player);
    }
}
