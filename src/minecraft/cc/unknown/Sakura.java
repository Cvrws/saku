package cc.unknown;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cc.unknown.event.Event;
import cc.unknown.event.bus.impl.EventBus;
import cc.unknown.handlers.*;
import cc.unknown.managers.*;
import cc.unknown.ui.click.RiceGui;
import cc.unknown.ui.menu.MainMenu;
import cc.unknown.util.client.CustomLogger;
import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;

@Getter
public class Sakura {

    public static final String NAME = "Sakura";
    public static final String VERSION = "5.7";

    public static final Sakura instance = new Sakura();
    public static final CustomLogger LOGGER = new CustomLogger(Sakura.class);
    
    private EventBus<Event> eventBus;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ThemeManager themeManager;

    private FileManager fileManager;

    private BindableManager bindableManager;
    private ConfigManager configManager;
    private ScriptManager scriptManager;
    
    private RiceGui clickGui;
    public boolean firstLogin;
    
    private DiscordHandler discordHandler = new DiscordHandler();
    public static final List<Object> registered = new ArrayList<Object>();
    private final ScheduledExecutorService ex = Executors.newScheduledThreadPool(4);
    private Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected Minecraft mc = Minecraft.getInstance();

    public void init() {
    	Display.setTitle(NAME + " " + VERSION);
    	Runtime.getRuntime().addShutdownHook(new Thread(ex::shutdown));
    	
    	initAutoOptimization();
    	initManagers();
    	initHandler();
    	initVia();
    	setupDiscordRPC();
        
    	mc.displayGuiScreen(new MainMenu());
    	LOGGER.info("Initialized successfully.");
    }

    public void terminate() {    	
    	if (getConfigManager().get("latest") != null) {
        	getConfigManager().get("latest").write();
        }
    	
        if (discordHandler != null) {
        	discordHandler.stop();
            LOGGER.info("Discord Rich Presence stopped.");
        }
    	
        System.gc();
        LOGGER.info("Client Terminated.");
    }
    
    private void initHandler() {
    	LOGGER.info("Handlers initialized.");

    	register(
    			new ViaHandler(),
    			new SpoofHandler(),
    			new AutoJoinHandler(),
    			new SinceTickHandler(),
    			new DragHandler(),
    			new NetworkingHandler(),
    			new ConnectionHandler(),
    			new RotationHandler(),
    			new FixHandler(),
    			new TransactionHandler(),
    			new UserHandler());
    	
    	LOGGER.info("Handlers registered.");
    }
    
    private void initVia() {
        ViaMCP.INSTANCE.initAsyncSlider();
        ViaMCP.INSTANCE.getAsyncVersionSlider().setVersion(ViaMCP.NATIVE_VERSION);
    	LOGGER.info("ViaMCP initialized.");
    }
    
    private void initManagers() {
        moduleManager = new ModuleManager();
        fileManager = new FileManager();
        commandManager = new CommandManager();
        configManager = new ConfigManager();
        themeManager = new ThemeManager();
        eventBus = new EventBus<>();
        scriptManager = new ScriptManager();
        bindableManager = new BindableManager();
        
        scriptManager.init();
        commandManager.init();
        bindableManager.init();
        moduleManager.init();
        
        clickGui = new RiceGui();
        clickGui.initGui();
        
        LOGGER.info("Managers initialized.");
    }
    
    private void setupDiscordRPC() {
        try {
        	discordHandler.init();
            LOGGER.info("Discord Rich Presence initialized.");
        } catch (Throwable throwable) {
            LOGGER.error("Failed to set up Discord RPC.", throwable);
        }
    }
    
    private void initAutoOptimization() {
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
    

	private void register(Object... handlers) {
	    for (Object handler : handlers) {
	        try {
	            registered.add(handler);
	            eventBus.register(handler);
	            LOGGER.info(handler.getClass().getSimpleName() + " registered.");
	        } catch (Exception e) {
	            LOGGER.error("Failed to register handler: " + handler.getClass().getSimpleName(), e);
	        }
	    }
	}

}