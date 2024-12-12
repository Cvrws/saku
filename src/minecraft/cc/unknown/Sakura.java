package cc.unknown;

import org.lwjgl.opengl.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cc.unknown.bindable.BindableManager;
import cc.unknown.command.CommandManager;
import cc.unknown.component.ComponentManager;
import cc.unknown.event.Event;
import cc.unknown.event.bus.impl.EventBus;
import cc.unknown.module.api.manager.ModuleManager;
import cc.unknown.script.ScriptManager;
import cc.unknown.ui.ClickGui;
import cc.unknown.ui.theme.ThemeManager;
import cc.unknown.util.file.FileManager;
import cc.unknown.util.file.config.ConfigManager;
import cc.unknown.util.file.enemy.EnemyManager;
import cc.unknown.util.file.friend.FriendManager;
import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;

@Getter
public enum Sakura {
    instance;

    public static final String NAME = "Sakura";
    public static final String VERSION_FULL = "5.4";

    private EventBus<Event> eventBus;
    private ModuleManager moduleManager;
    private ComponentManager componentManager;
    private CommandManager commandManager;
    private ThemeManager themeManager;

    private FileManager fileManager;
    
    private FriendManager friendManager;
    private EnemyManager enemyManager;

    private ConfigManager configManager;
    private BindableManager bindableManager;
    private ScriptManager scriptManager;

    private ClickGui clickGui;
    
    private Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void init() {
        moduleManager = new ModuleManager();
        componentManager = new ComponentManager();
        commandManager = new CommandManager();
        fileManager = new FileManager();
        configManager = new ConfigManager();
        friendManager = new FriendManager();
        enemyManager = new EnemyManager();
        themeManager = new ThemeManager();
        eventBus = new EventBus<>();
        bindableManager = new BindableManager();
        scriptManager = new ScriptManager();

        fileManager.init();

        moduleManager.init();
        scriptManager.init();
        componentManager.init();
        commandManager.init();
        friendManager.init();
        enemyManager.init();

        clickGui = new ClickGui();
        clickGui.initGui();
        
        ViaMCP.INSTANCE.initAsyncSlider();
        ViaMCP.INSTANCE.getAsyncVersionSlider().setVersion(ViaMCP.NATIVE_VERSION);

        configManager.init();
        bindableManager.init();

        Display.setTitle(NAME + " " + VERSION_FULL);
    }

    public void terminate() {
    	if (getConfigManager().get("latest") != null) {
        	getConfigManager().get("latest").write();
        }
    	
        System.gc();
    }
}