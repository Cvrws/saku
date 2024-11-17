package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.util.chat.ChatUtil;

public final class Friend extends Command {

	public Friend() {
		super("Make friends", "friend");
	}

	@Override
	public void execute(final String[] args) {
	    if (args.length == 2) {
	        String action = args[1].toLowerCase();
	        switch (action) {
	            case "list":
	                ChatUtil.display(getFriendList());
	                break;
	            case "clear":
	            	getInstance().getFriendManager().removeFriends();
	            	break;
	        }
	        return;
	    }

	    if (args.length == 3) {
	        String action = args[1].toLowerCase();
	        String target = args[2];

	        switch (action) {
	            case "add":
	                getInstance().getFriendManager().addFriend(target);
	                ChatUtil.display(String.format("Added %s to friends list", target));
	                break;

	            case "remove":
	                getInstance().getFriendManager().removeFriend(target);
	                ChatUtil.display(String.format("Removed %s from friends list", target));
	                break;
	        }
	    } else {
	        error("Usage: .friend <add/remove/list> <player>");
	    }
	}

	private String getFriendList() {
	    if (getInstance().getFriendManager().getFriends().isEmpty()) {
	        return "Your friend list is empty.";
	    }

	    StringBuilder message = new StringBuilder("Friend list:\n");
	    for (String friend : getInstance().getFriendManager().getFriends()) {
	        message.append("- ").append(friend).append("\n");
	    }
	    return message.toString();
	}
}