package cc.unknown;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cc.unknown.bindable.BindableManager;
import cc.unknown.command.CommandManager;
import cc.unknown.event.Event;
import cc.unknown.event.bus.impl.EventBus;
import cc.unknown.handlers.AutoJoinHandler;
import cc.unknown.handlers.ConnectionHandler;
import cc.unknown.handlers.DragHandler;
import cc.unknown.handlers.FixHandler;
import cc.unknown.handlers.NetworkingHandler;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.handlers.SinceTickHandler;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.handlers.ViaVersionHandler;
import cc.unknown.module.ModuleManager;
import cc.unknown.script.ScriptManager;
import cc.unknown.ui.click.RiceGui;
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
public class Sakura {

    public static final String NAME = "Sakura";
    public static final String VERSION = "5.6";

    public static final Sakura instance = new Sakura();
    public static final Logger LOGGER = LogManager.getLogger(Sakura.class);
    
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
    private TrayIcon trayIcon;
    public boolean firstLogin;
    
    private final ScheduledExecutorService ex = Executors.newScheduledThreadPool(4);
    private Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void init() {
    	Display.setTitle(NAME + " " + VERSION);
    	Runtime.getRuntime().addShutdownHook(new Thread(ex::shutdown));
    	Minecraft mc = Minecraft.getMinecraft();
    	
    	initAutoOptimization(mc);
    	initManagers();
    	initHandler();
    	initVia();
    	setupSystemTray();

        
    	mc.displayGuiScreen(new MainMenu());
    	LOGGER.info("{} {} initialized successfully.", NAME, VERSION);
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
    	LOGGER.info("Event handlers registered.");
    }
    
    private void initVia() {
        ViaMCP.INSTANCE.initAsyncSlider();
        ViaMCP.INSTANCE.getAsyncVersionSlider().setVersion(ViaMCP.NATIVE_VERSION);
    	LOGGER.info("ViaMCP initialized.");
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
    

    private void setupSystemTray() {
        if (isWindows() && SystemTray.isSupported()) {
            try {
                Image trayImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/assets/minecraft/sakura/images/sakura.png")));
                trayIcon = new TrayIcon(trayImage, NAME);
                trayIcon.setImageAutoSize(true);
                trayIcon.setToolTip(NAME);

                SystemTray.getSystemTray().add(trayIcon);
                trayIcon.displayMessage(NAME, "Client started successfully.", TrayIcon.MessageType.INFO);

                LOGGER.info("System tray icon added.");
            } catch (IOException | AWTException | NullPointerException e) {
                LOGGER.error("Failed to create or add TrayIcon.", e);
            }
        } else {
            LOGGER.warn("System tray not supported or not running on Windows.");
        }
    }
    
    private boolean isWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("windows");
    }
}