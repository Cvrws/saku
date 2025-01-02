package cc.unknown.command;

import static cc.unknown.util.client.StreamerUtil.green;
import static cc.unknown.util.client.StreamerUtil.red;
import static cc.unknown.util.client.StreamerUtil.reset;
import static cc.unknown.util.client.StreamerUtil.yellow;

import cc.unknown.util.Accessor;
import cc.unknown.util.player.PlayerUtil;
import lombok.Getter;

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
		PlayerUtil.displayInClient(yellow + "[" + red + "%" + yellow + "] " + reset + error); 
	}
	
	public void warning(String warn) {
		PlayerUtil.displayInClient(yellow + "[" + red + "!" + yellow + "] " + reset + warn);
	}
	
	public void success(String success) {
		PlayerUtil.displayInClient(yellow + "[" + green + "*" + yellow + "] " + reset + success);
	}
}