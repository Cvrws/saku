package cc.unknown;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.lwjgl.opengl.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cc.unknown.bindable.BindableManager;
import cc.unknown.command.CommandManager;
import cc.unknown.event.Event;
import cc.unknown.event.bus.impl.EventBus;
import cc.unknown.handlers.*;
import cc.unknown.module.ModuleManager;
import cc.unknown.script.ScriptManager;
import cc.unknown.ui.clickgui.rice.RiceGui;
import cc.unknown.ui.menu.MainMenu;
import cc.unknown.ui.theme.ThemeManager;
import cc.unknown.util.file.FileManager;
import cc.unknown.util.file.config.ConfigManager;
import cc.unknown.util.file.enemy.EnemyManager;
import cc.unknown.util.file.friend.FriendManager;
import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;

@Getter
public enum Sakura {
    instance;

    public static final String NAME = "Sakura";
    public static final String VERSION_FULL = "5.6";

    private EventBus<Event> eventBus;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ThemeManager themeManager;

    private FileManager fileManager;
    
    private FriendManager friendManager;
    private EnemyManager enemyManager;

    private ConfigManager configManager;
    private BindableManager bindableManager;
    private ScriptManager scriptManager;

    private RiceGui clickGui = new RiceGui();
    public boolean firstLogin;
    
    private final ScheduledExecutorService ex = Executors.newScheduledThreadPool(4);
    private Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void init() {
    	Display.setTitle(NAME + " " + VERSION_FULL);
    	Runtime.getRuntime().addShutdownHook(new Thread(ex::shutdown));
    	
    	initAutoOptimization(Minecraft.getMinecraft());
    	initManagers();
    	
    	initHandler();
        
        ViaMCP.INSTANCE.initAsyncSlider();
        ViaMCP.INSTANCE.getAsyncVersionSlider().setVersion(ViaMCP.NATIVE_VERSION);
        
        Minecraft.getMinecraft().displayGuiScreen(new MainMenu());
    }

    public void terminate() {
    	if (getConfigManager().get("latest") != null) {
        	getConfigManager().get("latest").write();
        }
    	
        System.gc();
    }
    
    private void initHandler() {
    	eventBus.register(new ViaVersionHandler());
    	eventBus.register(new SpoofHandler());
    	eventBus.register(new AutoJoinHandler());
    	eventBus.register(new SinceTickHandler());
    	eventBus.register(new DragHandler());
    	eventBus.register(new NetworkingHandler());
    	eventBus.register(new ConnectionHandler());
    	eventBus.register(new RotationHandler());
    	eventBus.register(new FixHandler());
    }
    
    private void initManagers() {
        moduleManager = new ModuleManager();
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
        commandManager.init();
        friendManager.init();
        enemyManager.init();
        configManager.init();
        bindableManager.init();
        clickGui.initGui();
    }
    
    private void initAutoOptimization(Minecraft mc) {
    	mc.gameSettings.ofFastRender = Config.isShaders() ? false : true;
		mc.gameSettings.ofChunkUpdatesDynamic = true;
		mc.gameSettings.ofSmartAnimations = true;
        mc.gameSettings.ofShowGlErrors = false;
        mc.gameSettings.ofRenderRegions = true;
    	mc.gameSettings.ofSmoothFps = false;
        mc.gameSettings.ofFastMath = true;
        mc.gameSettings.useVbo = true;
        mc.gameSettings.guiScale = 2;
    }
}