package cc.unknown.script.util;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.util.client.ChatUtil;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.ECMAException;

public class ScriptHandler {

    private final Map<String, JSObject> functionRegistry = new HashMap<>();

    public void handle(final String functionName, final JSObject function) {
        this.functionRegistry.put(functionName, function);
    }

    public void unhandle(final String functionName) {
        this.functionRegistry.remove(functionName);
    }

    public void call(final String functionName, final Object... parameters) {
        final JSObject function = this.functionRegistry.get(functionName);
        if (function == null) return;

        try {
            function.call(this, parameters);
        } catch (final ECMAException ex) {
            ChatUtil.display(ex.toString());
        } catch (final Exception ex) {
            ex.printStackTrace();
            ChatUtil.display("A script threw an exception, stacktrace printed.");
        }
    }
}
