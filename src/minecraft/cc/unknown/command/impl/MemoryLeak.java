package cc.unknown.command.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cc.unknown.command.Command;
import cc.unknown.util.player.EnemyUtil;
import io.netty.util.ResourceLeakDetector;

public final class MemoryLeak extends Command {

    public MemoryLeak() {
        super("Detect memory leaks", "memoryleaks");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length != 2) {
            error("Usage: <command> <level>");
            return;
        }
        
        try {
            ResourceLeakDetector.Level level = ResourceLeakDetector.Level.valueOf(args[1].toUpperCase());
            ResourceLeakDetector.setLevel(level);
            success("Set leak detector level to " + level);
        } catch (IllegalArgumentException e) {
            error("Invalid level. Available levels: " + Arrays.toString(ResourceLeakDetector.Level.values()));
        }
    }
	
    @Override
    public List<String> autocomplete(int arg, String[] args) {
        return Collections.emptyList();
    }
}