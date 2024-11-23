package cc.unknown.module.impl.other;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.KeyboardInputEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.StringValue;

@ModuleInfo(aliases = "Auto Text", description = "Envia cualquier mensaje al presionar una tecla", category = Category.OTHER)
public final class AutoText extends Module {
	
    private final List<MessagePair> messagePair = Arrays.asList(
            new MessagePair(
            		new StringValue("Message 1:", this, "hola"), 
            		new DescValue(" ", this), 
            		new StringValue("Key", this, "BACKSLASH")),
            
            new MessagePair(
            		new DescValue(" ", this),
            		new StringValue("Message 2:", this, ":3"), 
            		new DescValue(" ", this), 
            		new StringValue("Key:", this, "SUBTRACT"))
    	);
    
    @EventLink
    public final Listener<KeyboardInputEvent> onKeyboard = event -> {
        for (MessagePair pair : messagePair) {
            try {
                String keyFieldName = "KEY_" + pair.key.getValue().toUpperCase();
                int keyCode = Keyboard.class.getField(keyFieldName).getInt(null);

                if (event.getKeyCode() == keyCode) {
                	PlayerUtil.send(pair.message.getValue());
                    break;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    };
	
    class MessagePair {
        private final DescValue separate1;
    	private final StringValue message;
        private final DescValue separate2;
        private final StringValue key;

        public MessagePair(DescValue separate1, StringValue message, DescValue separate2, StringValue key) {
            this.separate1 = separate1;
        	this.message = message;
        	this.separate2 = separate2;
            this.key = key;
        }
        
        public MessagePair(StringValue message, DescValue separate2, StringValue key) {
        	this.separate1 = null;
        	this.message = message;
            this.separate2 = separate2;
            this.key = key;
        }
    }
}
