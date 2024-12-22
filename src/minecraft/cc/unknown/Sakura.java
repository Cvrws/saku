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
import cc.unknown.module.impl.movement.Sprint;
import cc.unknown.script.ScriptManager;
import cc.unknown.ui.clickgui.kerosene.KeroScreen;
import cc.unknown.ui.clickgui.rice.RiceScreen;
import cc.unknown.ui.menu.LoginMenu;
import cc.unknown.ui.theme.ThemeManager;
import cc.unknown.util.file.FileManager;
import cc.unknown.util.file.config.ConfigManager;
import cc.unknown.util.file.enemy.EnemyManager;
import cc.unknown.util.file.friend.FriendManager;
import cc.unknown.util.socket.EncryptUtil;
import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import net.minecraft.client.Minecraft;

@Getter
public enum Sakura {
    instance;

    public static final String NAME = "Sakura";
    public static final String VERSION = "[KDR Edition]";
    public static final String VERSION_FULL = "5.5";

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

    private RiceScreen clickGui;
    private KeroScreen betaGui;
    public boolean firstLogin;
    
    private Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void init() {
    	Display.setTitle(NAME + " " + VERSION_FULL + " " + VERSION);
    	
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

        clickGui = new RiceScreen();
        betaGui = new KeroScreen();
        
        clickGui.initGui();
        betaGui.initGui();
        
        ViaMCP.INSTANCE.initAsyncSlider();
        ViaMCP.INSTANCE.getAsyncVersionSlider().setVersion(ViaMCP.NATIVE_VERSION);

        configManager.init();
        bindableManager.init();
        
		if (instance != null && getModuleManager() != null) {
		    Sprint sprint = getModuleManager().get(Sprint.class);

		    if (sprint != null && !sprint.logged) {
		    	Minecraft.getMinecraft().displayGuiScreen(new LoginMenu());
		    }
		}
    }

    public void terminate() {
    	if (getConfigManager().get("latest") != null) {
        	getConfigManager().get("latest").write();
        }
    	
        System.gc();
    }
}