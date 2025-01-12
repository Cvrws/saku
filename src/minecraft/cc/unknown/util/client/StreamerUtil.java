package cc.unknown.util.client;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.util.ChatFormatting;

@UtilityClass
@Getter
public class StreamerUtil {
	public ChatFormatting yellow = ChatFormatting.YELLOW;
	public ChatFormatting red = ChatFormatting.RED;
	public ChatFormatting reset = ChatFormatting.RESET;
	public ChatFormatting white = ChatFormatting.RESET;
	public ChatFormatting aqua = ChatFormatting.AQUA;
	public ChatFormatting gray = ChatFormatting.GRAY;
	public ChatFormatting green = ChatFormatting.GREEN;
	public ChatFormatting blue = ChatFormatting.BLUE;
	public ChatFormatting black = ChatFormatting.BLACK;
	public ChatFormatting gold = ChatFormatting.GOLD;
	
	public ChatFormatting darkAqua = ChatFormatting.DARK_AQUA;
	public ChatFormatting darkGray = ChatFormatting.DARK_GRAY;
	public ChatFormatting darkPurple = ChatFormatting.DARK_PURPLE;
	public ChatFormatting darkBlue = ChatFormatting.DARK_BLUE;
	public ChatFormatting darkGreen = ChatFormatting.DARK_GREEN;
	public ChatFormatting darkRed = ChatFormatting.DARK_RED;

	public ChatFormatting pink = ChatFormatting.LIGHT_PURPLE;
	
   	public String usu = " ?§r§{0,3}§8§8\\[§r§f§fUsu§r§8§8\\]| ?§8\\[§fUsu§8\\]";
   	public String jup = " ?§r§{0,3}§8§8\\[§r§b§bJup§r§8§8\\]| ?§8\\[§bJup§8\\]";

	public String getPrefix(String rank, ChatFormatting rankColor) {
		return darkGray + "[" + rankColor + rank + darkGray + "] " + rankColor;
	}
}
