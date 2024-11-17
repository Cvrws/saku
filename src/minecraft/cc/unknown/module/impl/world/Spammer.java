package cc.unknown.module.impl.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.StringValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.scoreboard.Scoreboard;

@ModuleInfo(aliases = "Spammer", description = "Spam on chat", category = Category.WORLD)
public final class Spammer extends Module {
	
    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Nexus"))
            .add(new SubMode("SkyPit"))
            .add(new SubMode("Murder Mystery"))
            .add(new SubMode("TNT Tag"))
            .add(new SubMode("EggWars"))
            .add(new SubMode("CTW"))
            .add(new SubMode("TNT Run"))
            .add(new SubMode("Creativo"))
            .add(new SubMode("SkyBlock"))
            .add(new SubMode("UHC Run"))
            .add(new SubMode("Speed Builders"))
            .add(new SubMode("Team Skywars"))
            .add(new SubMode("SkyWars"))
            .add(new SubMode("EDLB"))
            .add(new SubMode("Juegos del Hambre"))
            .add(new SubMode("Escondite"))
            .add(new SubMode("BedWars"))
            .setDefault("Nexus");
    
    private final BooleanValue Randoms = new BooleanValue("Mention Randoms", this, true);
    private final BooleanValue DifferentMessages = new BooleanValue("3 Messages", this, true);
    private final BooleanValue LunarFix = new BooleanValue("Lunar Fix", this, true);
    private final StringValue msg1 = new StringValue("1-. Message: ", this, "MTF on top");
    private final StringValue msg2 = new StringValue("2-. Message: ", this, "Que esperan zperras");
    private final StringValue msg3 = new StringValue("3-. Message: ", this, "Unanse de una vez dc zornhub xyz");
    private boolean sent;
    
    @EventLink
    public final Listener<PreUpdateEvent> onPre = event -> {
    	if (mc.player == null || mc.world == null) {
            return;
        }
        
        if (mc.currentScreen != null) {
        	if (!(mc.currentScreen instanceof GuiChest)) {
                mc.displayGuiScreen(null);
        	}
        }

        final String[] targetString = {""};
        final String[] scoreboardTarget = {""};
        switch (mode.getValue().getName()) {
            case "Nexus":
                targetString[0] = "Destruye el Nexus";
                break;
            case "SkyPit":
                targetString[0] = "SkyPit";
                break;
            case "Murder Mystery":
                targetString[0] = "MurderMystery";
                break;
            case "TNT Tag":
                targetString[0] = "TNTTag";
                break;
            case "EggWars":
                targetString[0] = "EggWars";
                break;
            case "SkyWars":
            	targetString[0] = "SkyWars";
            	break;
            case "CTW": 
                targetString[0] = "Captura la Lana";
                break;
            case "TNT Run":
                targetString[0] = "TNTRun";
                break;
            case "Creativo":
                targetString[0] = "Creativo";
                break;
            case "SkyBlock":
                targetString[0] = "SkyBlock";
                break;
            case "UHC Run":
            	targetString[0] = "UHC Run";
            	scoreboardTarget[0] = "UHCRUN";
            	break;
            case "Speed Builders":
            	targetString[0] = "SpeedBuilders";
            	scoreboardTarget[0] = "Speed Builders";
            	break;
            case "Team Skywars":
            	targetString[0] = "Team Skywars";
            	scoreboardTarget[0] = "TeamSkywars";
            	break;
            case "EDLB":
            	targetString[0] = "Escapa de la Bestia";
            	scoreboardTarget[0] = "EDLB";
            	break;
            case "Juegos del Hambre":
            	targetString[0] = "Juegos del Hambre";
            	scoreboardTarget[0] = "HungerGames";
            	break;
            case "Escondite":
            	targetString[0] = "Escondite";
            	break;
            case "BedWars":
            	targetString[0] = "BedWars";
            	break;
            default:
                return;
        }

        Item clockItem = Item.getByNameOrId("minecraft:clock");
        if (clockItem == null) {
            return;
        }

        int clockIndex = 0;
        ItemStack stack = mc.player.inventory.getStackInSlot(clockIndex);
        if (stack != null && (stack.getItem() == clockItem || stack.getItem() instanceof ItemSword) || stack == null) {
            Scoreboard scoreboard = mc.world.getScoreboard();
            boolean containsString = scoreboard.getScoreObjectives().stream()
                    .map(objective -> objective.getDisplayName().replaceAll("§[0-9A-FK-ORa-fk-or]", ""))
                    .anyMatch(displayName -> displayName.contains(targetString[0]) || displayName.contains(scoreboardTarget[0]) && !displayName.contains("UniversoCraft"));
            
            if (!containsString) {
                mc.player.inventory.currentItem = clockIndex;
                if (mc.currentScreen == null) {
                    this.sent = false;
                    sendPacket(new C08PacketPlayerBlockPlacement(mc.player.inventory.getCurrentItem()));
                }
            } else {
                if (!sent) {
                    String messageToSend = msg1.getValue();
                    
                    if (this.DifferentMessages.getValue()) {
                        String[] messages = {msg1.getValue(), msg2.getValue(), msg3.getValue()};
                        Random random = new Random();
                        int index = random.nextInt(messages.length);
                        messageToSend = messages[index];
                    }

                    if (this.Randoms.getValue()) {
                        List<String> playerNames = getRandomPlayerNames(mc);
                        if (!playerNames.isEmpty()) {
                            messageToSend = String.join(" ", playerNames) + " " + messageToSend;
                        }
                    }
                    
                    if (this.LunarFix.getValue()) {
                        messageToSend += " " + getRandomString() + getRandomString();
                    }
                    
                    sendPacket(new C01PacketChatMessage("/hub"));
                    sendPacket(new C01PacketChatMessage(messageToSend));
                    
                    sent = true;
                }  
            }
        }

        if (mc.currentScreen instanceof GuiChest) {
            ContainerChest chest = (ContainerChest) ((GuiChest) mc.currentScreen).inventorySlots;
            for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                ItemStack stackInSlot = chest.getLowerChestInventory().getStackInSlot(i);
                if (stackInSlot != null && stackInSlot.hasDisplayName() && stackInSlot.getDisplayName().contains(targetString[0])
                        || mode.is("TNT Tag") && stackInSlot != null && stackInSlot.hasDisplayName() && stackInSlot.getDisplayName().contains("TNTTag 1")
                        || mode.is("EggWars") && stackInSlot != null && stackInSlot.hasDisplayName() && stackInSlot.getDisplayName().contains("EggWars 1")
                        || mode.is("Speed Builders") && stackInSlot != null && stackInSlot.hasDisplayName() && stackInSlot.getDisplayName().contains("SpeedBuilders 1")
                        || mode.is("EDLB") && stackInSlot != null && stackInSlot.hasDisplayName() && stackInSlot.getDisplayName().contains("Escapa de la Bestia 1")
                        || mode.is("BedWars") && stackInSlot != null && stackInSlot.hasDisplayName() && stackInSlot.getDisplayName().contains("BedWars 8")) {
                    sendPacket(new C0EPacketClickWindow(chest.windowId, i, 0, 1, stackInSlot, (short) 0));
                }
            }
        }
    };
    
    private void sendPacket(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    private List<String> getRandomPlayerNames(Minecraft mc) {
        List<String> playerNames = new ArrayList<>();
        List<String> allPlayerNames = new ArrayList<>();

        for (Object player : mc.world.playerEntities) {
            if (player instanceof EntityPlayer && !(((EntityPlayer) player).getEntityId() == mc.player.getEntityId())) {
                allPlayerNames.add(((EntityPlayer) player).getName());
            }
        }

        if (!allPlayerNames.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(allPlayerNames.size());
            playerNames.add(allPlayerNames.get(randomIndex));
        }

        return playerNames;
    }
    
    private String getRandomString() {
        Random random = new Random();
        if (random.nextBoolean()) {
            char randomLetter = (char) (random.nextInt(26) + 'a');
            return String.valueOf(randomLetter);
        } else {
            int randomNumber = random.nextInt(10);
            return String.valueOf(randomNumber);
        }
    }


}