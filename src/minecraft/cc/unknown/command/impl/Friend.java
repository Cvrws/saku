package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.util.player.FriendUtil;

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
	            	success(getFriendList());
	                break;
	            case "clear":
	            	FriendUtil.removeFriends();
	            	break;
	        }
	        return;
	    }

	    if (args.length == 3) {
	        String action = args[1].toLowerCase();
	        String target = args[2];

	        switch (action) {
	            case "add":
	                FriendUtil.addFriend(target);
	                success(String.format("Added %s to friends list", target));
	                break;

	            case "remove":
	                FriendUtil.removeFriend(target);
	                success(String.format("Removed %s from friends list", target));
	                break;
	        }
	    } else {
	        error("Usage: .friend <add/remove/list> <player>");
	    }
	}

	private String getFriendList() {
	    if (FriendUtil.getFriends().isEmpty()) {
	        return "Your friend list is empty.";
	    }

	    StringBuilder message = new StringBuilder("Friend list:\n");
	    for (String friend : FriendUtil.getFriends()) {
	        message.append("- ").append(friend).append("\n");
	    }
	    return message.toString();
	}
}