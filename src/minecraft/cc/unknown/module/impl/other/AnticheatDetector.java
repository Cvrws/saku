package cc.unknown.module.impl.other;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketReceiveEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

@ModuleInfo(aliases = "Anticheat Detector", description = "Detecta que anticheat tiene cada servidor [BETA]", category = Category.OTHER)
public final class AnticheatDetector extends Module {

    private static final int PATTERN_LENGTH = 15;

    private final Queue<Integer> transactionHistory = new LinkedList<>();

    private final HashMap<String, PatternChecker> anticheatPatterns = new HashMap<>();
    
    @Override
    public void onEnable() {
    	transactionHistory.clear();
    }
    
    @Override
    public void onDisable() {
    	transactionHistory.clear();
    }
    
    public AnticheatDetector() {
    	anticheatPatterns.put("Grim", new PatternChecker((history) -> {
    	    if (history.size() < 2) return false;

    	    for (int i = 0; i < history.size() - 1; i++) {
    	        if (history.get(i) - history.get(i + 1) != 1) return false;
    	    }

    	    return true;
    	}));
    	
    	anticheatPatterns.put("Intave", new PatternChecker((history) -> {
    	    for (int num : history) {
    	        if (num < 0 || num > 3) return false;
    	    }
    	    return true;
    	}));

    	anticheatPatterns.put("Polar", new PatternChecker((history) -> {
    	    if (history.size() < 2) return false;

    	    int first = history.get(0);
    	    if (first >= 0) return false;

    	    int expected = -0;

    	    for (int i = 1; i < history.size(); i++) {
    	        int current = history.get(i);
    	        
    	        if (current != expected) {
    	            return false;
    	        }

    	        expected--;
    	    }

    	    return true;
    	}));

    	anticheatPatterns.put("Vulcan", new PatternChecker((history) -> {
    	    if (history.isEmpty() || history.get(0) != -23767) return false;
    	    for (int i = 0; i < history.size() - 1; i++) {
    	        if (history.get(i) - history.get(i + 1) != -1) return false;
    	    }
    	    return true;
    	}));
    	
    	anticheatPatterns.put("Frequency", new PatternChecker((history) -> {
    	    int first = history.get(0);
    	    for (int num : history) {
    	        if (num != first) return false;
    	    }
    	    return true;
    	}));

    	anticheatPatterns.put("Karhu", new PatternChecker((history) -> {
    	    if (history.size() < 2) return false;

    	    if (history.get(0) != -3000) return false;

    	    for (int i = 0; i < history.size() - 1; i++) {
    	        if (history.get(i) - history.get(i + 1) != 1) return false;
    	    }

    	    return true;
    	}));
    	
    	anticheatPatterns.put("Matrix", new PatternChecker((history) -> {
    	    if (history.size() < 2) return false;

    	    int first = history.get(0);
    	    if (first < -19999 || first > -19900) return false;

    	    for (int i = 1; i < history.size(); i++) {
    	        int current = history.get(i);
    	        int previous = history.get(i - 1);

    	        if (current != previous - 1) {
    	            return false;
    	        }
    	    }

    	    return true;
    	}));
    }

    @EventLink
    public final Listener<PacketReceiveEvent> onPacket = event -> {
        final Packet<?> packet = event.getPacket();
        if (packet instanceof S32PacketConfirmTransaction) {
            final S32PacketConfirmTransaction wrapper = (S32PacketConfirmTransaction) packet;
            int actionNumber = wrapper.actionNumber;

            if (transactionHistory.size() >= PATTERN_LENGTH) {
                transactionHistory.poll();
            }
            transactionHistory.add(actionNumber);

            for (String anticheat : anticheatPatterns.keySet()) {
                boolean matches = anticheatPatterns.get(anticheat).matches(new LinkedList<>(transactionHistory));
                if (matches) {
                    PlayerUtil.displayInClient(String.format("Detected %s", anticheat));
                    toggle();
                    break;
                }
            }
        }
    };
    
    private interface PatternMatcher {
        boolean matches(LinkedList<Integer> history);
    }

    private static class PatternChecker {
        private final PatternMatcher matcher;

        public PatternChecker(PatternMatcher matcher) {
            this.matcher = matcher;
        }

        public boolean matches(LinkedList<Integer> history) {
            return matcher.matches(history);
        }
    }
}
