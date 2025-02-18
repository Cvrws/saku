package cc.unknown.command.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.command.Command;
import cc.unknown.handlers.AutoJoinHandler;
import cc.unknown.util.client.ChatUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ChatFormatting;

public class Join extends Command {
	final HashMap<String, Item> hashMap;

	public Join() {
		super("Ingresa automaticamente a una modalidad", "game", "j", "join");
		this.hashMap = new HashMap<>();
	}

	@Override
	public void execute(String[] args) {
	    if (args.length == 2 && args[0].equalsIgnoreCase("game") && args[1].equalsIgnoreCase("list")) {
	    	ChatUtil.display(getList());
	        return;
	    }

	    if (args.length < 2) {
	    	warning("?");
	        return;
	    }
	    
	    this.hashMap.put("sw", Items.bow);
	    this.hashMap.put("tsw", Items.arrow);
	    this.hashMap.put("bw", Items.bed);
	    this.hashMap.put("tnt", Items.gunpowder);
	    this.hashMap.put("pgames", Items.cake);
	    this.hashMap.put("arena", Items.diamond_sword);
	    
	    String gameName = args[1];
	    int lobbyNumber;

	    if (!this.hashMap.containsKey(gameName)) {
	    	warning("Invalid game. Use: .game list");
	        return;
	    }

	    if (!args[2].matches("\\d+")) {
	    	warning("Invalid number.");
	        return;
	    }

	    lobbyNumber = Integer.parseInt(args[2]);

	    if (lobbyNumber == 0) {
	    	warning("Invalid lobby.");
	        return;
	    }

	    AutoJoinHandler.init(hashMap.get(gameName), lobbyNumber);
	}
	
    @Override
    public List<String> autocomplete(int arg, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        switch (args.length) {
            case 1:
                return Arrays.asList("game", "list").stream()
                        .filter(option -> option.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());

            case 2:
                if ("game".equalsIgnoreCase(args[0])) {
                    return new ArrayList<>(hashMap.keySet()).stream()
                            .filter(game -> game.startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
                return Collections.emptyList();

            case 3:
                if ("game".equalsIgnoreCase(args[0]) && hashMap.containsKey(args[1].toLowerCase())) {
                    return Collections.singletonList("<lobbyNumber>");
                }
                return Collections.emptyList();

            default:
                return Collections.emptyList();
        }
    }

    private String getList() {
        return "\n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "sw" + ChatFormatting.GRAY + " (Skywars)        \n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "tsw" + ChatFormatting.GRAY + " (Team Skywars)  \n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "tnt" + ChatFormatting.GRAY + " (Tnt Tag)       \n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "bw" + ChatFormatting.GRAY + " (Bedwars)        \n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "pgames" + ChatFormatting.GRAY + " (Party Games)\n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "arena" + ChatFormatting.GRAY + " (Arenapvp)    \n";
    }
}
