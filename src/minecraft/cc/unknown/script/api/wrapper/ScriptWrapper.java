package cc.unknown.script.api.wrapper;

import cc.unknown.script.api.API;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class ScriptWrapper<T> extends API {
    protected T wrapped;
}
