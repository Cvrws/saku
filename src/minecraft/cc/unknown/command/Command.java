package cc.unknown.command;

import static cc.unknown.util.streamer.StreamerUtil.*;
import cc.unknown.util.Accessor;
import cc.unknown.util.chat.ChatUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ChatFormatting;

@Getter
public abstract class Command implements Accessor {

    private final String description;
    private final String[] expressions;
    
    public Command(final String description, final String... expressions) {
        this.description = description;
        this.expressions = expressions;
    }

    public abstract void execute(String[] args);
	
	public void error(String error) {
		ChatUtil.display(yellow + "[" + red + "%" + yellow + "] " + reset + error); 
	}
	
	public void warning(String warn) {
		ChatUtil.display(yellow + "[" + red + "!" + yellow + "] " + reset + warn);
	}
	
	public void success(String success) {
		ChatUtil.display(yellow + "[" + green + "*" + yellow + "] " + reset + success);
	}
}