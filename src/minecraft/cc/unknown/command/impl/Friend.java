package cc.unknown.command.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
	
    @Override
    public List<String> autocomplete(int arg, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        switch (args.length) {
            case 1:
                return Arrays.asList("add", "remove", "list", "clear").stream()
                        .filter(option -> option.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());

            case 2:
                if ("remove".equalsIgnoreCase(args[0]) || "add".equalsIgnoreCase(args[0])) {
                    return FriendUtil.getFriends().stream()
                            .filter(friend -> friend.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
                return Collections.emptyList();

            default:
                return Collections.emptyList();
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