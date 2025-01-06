package cc.unknown.script;

import java.io.File;
import java.nio.charset.Charset;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;

import cc.unknown.Sakura;
import cc.unknown.script.util.ScriptHandler;
import lombok.Data;

@Data
public final class Script {

    private final String name, author, version, description;

    private String code;

    private final File sourceFile;

    private ScriptEngine engine;

    private ScriptHandler apiHandler;

    private boolean loaded;

    public Script(final String name, final String author, final String version, final String description,
                  final String code) {
        this(name, author, version, description, code, null);
    }

    public Script(final String name, final String author, final String version, final String description,
                  final File sourceFile) {
        this(name, author, version, description, null, sourceFile);
    }

    public Script(final String name, final String author, final String version, final String description,
                  final String code, final File sourceFile) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.description = description;
        this.code = code;
        this.sourceFile = sourceFile;
    }

    public void load() throws ScriptException {
        try {
            if (this.loaded) this.unload();

            if (this.sourceFile != null) code = FileUtils.readFileToString(sourceFile, (Charset) null);
            if (this.code == null) throw new ScriptException("Empty script");

            this.engine = Sakura.instance.getScriptManager().createEngine();
            this.apiHandler = new ScriptHandler();

            this.engine.put("script", apiHandler);

            this.engine.eval(code);

            this.call("onLoad");

            this.loaded = true;
        } catch (final ScriptException ex) {
            throw ex;
        } catch (final Exception ex) {
            throw new ScriptException(ex);
        }
    }

    public void unload() throws ScriptException {
        try {
            this.call("onUnload");
        } catch (final Exception ex) {
            throw new ScriptException(ex);
        } finally {
        	Sakura.instance.getClickGui().rebuildModuleCache();
            this.engine = null;
            this.apiHandler = null;

            this.loaded = false;
        }
    }

    public void reload() throws ScriptException {
        if (this.loaded) this.unload();
        this.load();
    }

    private void call(final String functionName, final Object... parameters) {
        this.apiHandler.call(functionName, parameters);
    }
}
