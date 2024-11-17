package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.file.enemy.EnemyFile;

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
	                ChatUtil.display(getTargetList());
	                break;
	            case "clear":
	            	getInstance().getEnemyManager().removeEnemy();
	            	break;
	        }
	        return;
	    }

	    if (args.length == 3) {
	        String action = args[1].toLowerCase();
	        String target = args[2];

	        switch (action) {
	            case "add":
	                getInstance().getEnemyManager().addEnemy(target);
	                ChatUtil.display(String.format("Added %s to target list", target));
	                break;

	            case "remove":
	                getInstance().getEnemyManager().removeEnemy(target);
	                ChatUtil.display(String.format("Removed %s from target list", target));
	                break;
	        }
	    } else {
	        error("Usage: .target <add/remove/list> <player>");
	    }
	}
    
	private String getTargetList() {
	    if (getInstance().getEnemyManager().getEnemy().isEmpty()) {
	        return "Your target list is empty.";
	    }

	    StringBuilder message = new StringBuilder("Target list:\n");
	    for (String enemy : getInstance().getEnemyManager().getEnemy()) {
	        message.append("- ").append(enemy).append("\n");
	    }
	    return message.toString();
	}
}