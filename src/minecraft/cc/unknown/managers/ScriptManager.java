package cc.unknown.managers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.io.FileUtils;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.script.Script;
import cc.unknown.script.api.GameSettingsAPI;
import cc.unknown.script.api.MinecraftAPI;
import cc.unknown.script.api.NetworkAPI;
import cc.unknown.script.api.PacketAPI;
import cc.unknown.script.api.PlayerAPI;
import cc.unknown.script.api.RenderAPI;
import cc.unknown.script.api.ScriptAPI;
import cc.unknown.script.api.WorldAPI;
import cc.unknown.script.util.ScriptClassFilter;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.ChatUtil;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import lombok.Getter;
import lombok.SneakyThrows;

public final class ScriptManager implements Accessor {

    public static final File SCRIPT_DIRECTORY = new File(FileManager.DIRECTORY, "scripts");

    public static final FilenameFilter SCRIPT_FILE_FILTER = (file, name) -> name.toLowerCase(Locale.ENGLISH).endsWith(".js");

    private static final ClassFilter SCRIPT_CLASS_FILTER = new ScriptClassFilter();

    private NashornScriptEngineFactory engineFactory;

	private Bindings globalBindings;

    private final List<Script> scripts = new ArrayList<>();
    
    public void init() {
        this.engineFactory = new NashornScriptEngineFactory();

        this.loadBindings();

        this.loadScripts();

        Sakura.instance.getEventBus().register(this);
    }

    @EventLink
    public final Listener<WorldChangeEvent> onWorldChange = event -> loadBindings();

    public Script getScript(final String name) {
        return this.scripts.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    @SneakyThrows
    public void loadScripts() {
        this.loadScriptFiles();

        this.scripts.removeIf(script -> {
            try {
                script.load();
                return false;
            } catch (final ScriptException ex) {
                ex.printStackTrace();

                ChatUtil.display("Syntax error!");

                ChatUtil.display(ex.getMessage());
                return true;
            }
        });
    }

    public void loadBindings() {
        this.globalBindings = new SimpleBindings() {{
            this.put("mc", new MinecraftAPI());
            this.put("saku", new ScriptAPI());
            this.put("player", new PlayerAPI());
            this.put("world", new WorldAPI());
            this.put("network", new NetworkAPI());
            this.put("render", new RenderAPI());
            this.put("packet", new PacketAPI());
            this.put("input", new GameSettingsAPI());
        }};
    }

    public void reloadScripts() {
        this.unloadScripts();

        try {
            this.loadScriptFiles();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        this.scripts.removeIf(script -> {
            try {
                script.reload();
                return false;
            } catch (final ScriptException ex) {
                ex.printStackTrace();
                ChatUtil.display("Syntax error!");

                ChatUtil.display(ex.getMessage());
                return true;
            }
        });
    }

    public void unloadScripts() {
        this.scripts.forEach(script -> {
            try {
                script.unload();
            } catch (final ScriptException ex) {
                ex.printStackTrace();

                ChatUtil.display("Script \"" + script.getName() + "\" unloaded incorrectly");
            }
        });

        this.scripts.clear();
    }

    private void loadScriptFiles() throws IOException {
        if (!SCRIPT_DIRECTORY.exists()) SCRIPT_DIRECTORY.mkdirs();

        for (final File file : Objects.requireNonNull(SCRIPT_DIRECTORY.listFiles(SCRIPT_FILE_FILTER))) {
            if (!file.canRead()) continue;

            final String name = this.getName(file);
            final boolean exists = this.scripts.stream().anyMatch(script -> script.getName().equals(name));

            if (!exists) {
                final Script script = this.parseScript(FileUtils.readFileToString(file, (Charset) null), file);
                this.scripts.add(script);
            }
        }
    }

    public ScriptEngine createEngine() {
        final ScriptEngine engine = this.engineFactory.getScriptEngine();
        assert engine != null;
        engine.setBindings(this.globalBindings, ScriptContext.GLOBAL_SCOPE);
        return engine;
    }

    public Script parseScript(final String code, final File file) {
        return new Script(this.getName(file), code, file);
    }

    private String getName(final File file) {
        return file.getName().replace(".js", "");
    }
    
    public static File getScriptDirectory() {
		return SCRIPT_DIRECTORY;
	}

	public static FilenameFilter getScriptFileFilter() {
		return SCRIPT_FILE_FILTER;
	}

	public static ClassFilter getScriptClassFilter() {
		return SCRIPT_CLASS_FILTER;
	}

	public NashornScriptEngineFactory getEngineFactory() {
		return engineFactory;
	}

	public Bindings getGlobalBindings() {
		return globalBindings;
	}

	public List<Script> getScripts() {
		return scripts;
	}

	public Listener<WorldChangeEvent> getOnWorldChange() {
		return onWorldChange;
	}
}
