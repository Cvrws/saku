package cc.unknown.util.client.toggle;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import cc.unknown.module.Module;
import cc.unknown.util.Accessor;
import net.minecraft.client.settings.KeyBinding;

public enum KeybindUtil {
	instance;

	private final Map<String, Integer> keyMap = new HashMap<>();

	public void bind(Module mod, int bind) {
		mod.setKey(bind);
	}

	public void unbind(Module mod) {
		mod.setKey(0);
	}

	public int toInt(String keyCode) {
		return keyMap.getOrDefault(keyCode.toLowerCase(), 0);
	}

	KeybindUtil() {
	    // Letras
	    keyMap.put("a", 30);
	    keyMap.put("b", 48);
	    keyMap.put("c", 46);
	    keyMap.put("d", 32);
	    keyMap.put("e", 18);
	    keyMap.put("f", 33);
	    keyMap.put("g", 34);
	    keyMap.put("h", 35);
	    keyMap.put("i", 23);
	    keyMap.put("j", 36);
	    keyMap.put("k", 37);
	    keyMap.put("l", 38);
	    keyMap.put("m", 50);
	    keyMap.put("n", 49);
	    keyMap.put("o", 24);
	    keyMap.put("p", 25);
	    keyMap.put("q", 16);
	    keyMap.put("r", 19);
	    keyMap.put("s", 31);
	    keyMap.put("t", 20);
	    keyMap.put("u", 22);
	    keyMap.put("v", 47);
	    keyMap.put("w", 17);
	    keyMap.put("x", 45);
	    keyMap.put("y", 21);
	    keyMap.put("z", 44);

	    // Números
	    keyMap.put("0", 11);
	    keyMap.put("1", 2);
	    keyMap.put("2", 3);
	    keyMap.put("3", 4);
	    keyMap.put("4", 5);
	    keyMap.put("5", 6);
	    keyMap.put("6", 7);
	    keyMap.put("7", 8);
	    keyMap.put("8", 9);
	    keyMap.put("9", 10);

	    // Teclado numérico
	    keyMap.put("numpad0", 82);
	    keyMap.put("numpad1", 79);
	    keyMap.put("numpad2", 80);
	    keyMap.put("numpad3", 81);
	    keyMap.put("numpad4", 75);
	    keyMap.put("numpad5", 76);
	    keyMap.put("numpad6", 77);
	    keyMap.put("numpad7", 71);
	    keyMap.put("numpad8", 72);
	    keyMap.put("numpad9", 73);
	    keyMap.put("numpad_divide", 181);
	    keyMap.put("numpad_multiply", 55);
	    keyMap.put("numpad_subtract", 74);
	    keyMap.put("numpad_add", 78);
	    keyMap.put("numpad_decimal", 83);

	    // Teclas de función
	    keyMap.put("f1", 59);
	    keyMap.put("f2", 60);
	    keyMap.put("f3", 61);
	    keyMap.put("f4", 62);
	    keyMap.put("f5", 63);
	    keyMap.put("f6", 64);
	    keyMap.put("f7", 65);
	    keyMap.put("f8", 66);
	    keyMap.put("f9", 67);
	    keyMap.put("f10", 68);
	    keyMap.put("f11", 87);
	    keyMap.put("f12", 88);

	    // Modificadores y especiales
	    keyMap.put("lshift", 42);
	    keyMap.put("rshift", 54);
	    keyMap.put("lcontrol", 29);
	    keyMap.put("rcontrol", 157);
	    keyMap.put("lalt", 56);
	    keyMap.put("ralt", 184);
	    keyMap.put("tab", 15);
	    keyMap.put("capslock", 58);
	    keyMap.put("enter", 28);
	    keyMap.put("backspace", 14);
	    keyMap.put("space", 57);
	    keyMap.put("esc", 1);

	    // Navegación
	    keyMap.put("up", 200);
	    keyMap.put("down", 208);
	    keyMap.put("left", 203);
	    keyMap.put("right", 205);
	    keyMap.put("home", 199);
	    keyMap.put("end", 207);
	    keyMap.put("pageup", 201);
	    keyMap.put("pagedown", 209);
	    keyMap.put("insert", 210);
	    keyMap.put("delete", 211);

	    // Botones del mouse
	    keyMap.put("mouse_left", 0);
	    keyMap.put("mouse_right", 1);
	    keyMap.put("mouse_middle", 2);
	    keyMap.put("mouse_button4", 3);
	    keyMap.put("mouse_button5", 4);
	    keyMap.put("mouse_button6", 5);
	    keyMap.put("mouse_button7", 6);
	    keyMap.put("mouse_button8", 7);
	}
}