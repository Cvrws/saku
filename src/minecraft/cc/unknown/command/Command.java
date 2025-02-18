package cc.unknown.command;

import static cc.unknown.util.render.ColorUtil.green;
import static cc.unknown.util.render.ColorUtil.red;
import static cc.unknown.util.render.ColorUtil.reset;
import static cc.unknown.util.render.ColorUtil.yellow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.ChatUtil;
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
    
    public abstract List<String> autocomplete(int arg, String[] args);

    public boolean match(String name) {
        for (String alias : expressions) {
            if (alias.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    @NotNull
    public List<String> getNameAndAliases() {
        List<String> l = new ArrayList<>();
        l.addAll(Arrays.asList(expressions));

        return l;
    }
	
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