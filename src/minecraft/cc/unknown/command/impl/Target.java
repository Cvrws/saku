package cc.unknown.command.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cc.unknown.command.Command;
import cc.unknown.util.player.EnemyUtil;

public final class Target extends Command {

    public Target() {
        super("Make enemies", "target");
    }

	@Override
	public void execute(final String[] args) {
	    if (args.length == 2) {
	        String action = args[1].toLowerCase();
	        switch (action) {
	            case "list":
	            	success(getTargetList());
	                break;
	            case "clear":
	            	EnemyUtil.removeEnemy();
	            	break;
	        }
	        return;
	    }

	    if (args.length == 3) {
	        String action = args[1].toLowerCase();
	        String target = args[2];

	        switch (action) {
	            case "add":
	                EnemyUtil.addEnemy(target);
	                success(String.format("Added %s to target list", target));
	                break;

	            case "remove":
	                EnemyUtil.removeEnemy(target);
	                success(String.format("Removed %s from target list", target));
	                break;
	        }
	    } else {
	        error("Usage: .target <add/remove/list> <player>");
	    }
	}
	
    @Override
    public List<String> autocomplete(int arg, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("add", "remove", "list", "clear");
        } else if (args.length == 2 && (args[1].equalsIgnoreCase("remove"))) {
            return EnemyUtil.getEnemy();
        }
        return Collections.emptyList();
    }
    
	private String getTargetList() {
	    if (EnemyUtil.getEnemy().isEmpty()) {
	        return "Your target list is empty.";
	    }

	    StringBuilder message = new StringBuilder("Target list:\n");
	    for (String enemy : EnemyUtil.getEnemy()) {
	        message.append("- ").append(enemy).append("\n");
	    }
	    return message.toString();
	}
}