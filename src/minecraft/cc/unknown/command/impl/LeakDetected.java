package cc.unknown.command.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cc.unknown.command.Command;
import cc.unknown.util.player.EnemyUtil;
import io.netty.util.ResourceLeakDetector;

public final class LeakDetected extends Command {

    public LeakDetected() {
        super("Detect memory leaks", "leakdetected");
    }

	@Override
	public void execute(final String[] args) {
	    if (args.length == 1) {
	           try {
	                ResourceLeakDetector.Level level = ResourceLeakDetector.Level.valueOf(args[0]);
	                ResourceLeakDetector.setLevel(level);
	                success("Set leak detector level to " + level);
	            } catch (IllegalArgumentException e) {
	            	success("Invalid level (" + Arrays.toString(ResourceLeakDetector.Level.values()) + ")");
	            }
	        return;
	    }
	}
	
    @Override
    public List<String> autocomplete(int arg, String[] args) {
        return Collections.emptyList();
    }
}