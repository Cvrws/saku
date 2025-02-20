package cc.unknown;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.lwjgl.opengl.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jna.Platform;

import cc.unknown.event.Event;
import cc.unknown.event.bus.impl.EventBus;
import cc.unknown.handlers.*;
import cc.unknown.managers.*;
import cc.unknown.ui.click.RiceGui;
import cc.unknown.ui.menu.saku.SakuMenu;
import cc.unknown.util.client.CustomLogger;
import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;

@Getter
public class Sakura {

    public static final String NAME = "Sakura";
    public static final String VERSION = "5.9";

    public static final Sakura instance = new Sakura();
    
    private final CustomLogger logger = new CustomLogger(Sakura.class);
    private final EventBus<Event> eventBus = new EventBus<>();
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ThemeManager themeManager;
    private FileManager fileManager;
    private BindableManager bindableManager;
    private ConfigManager configManager;
    private ScriptManager scriptManager;
    
    private RiceGui clickGui;
    public boolean firstStart;
    
    private final DiscordHandler discordHandler = new DiscordHandler();
    public static final List<Object> registered = new ArrayList<>();
    private final ScheduledExecutorService ex = Executors.newScheduledThreadPool(4);
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private static final String EXPECTED_CONTENT = "[Sakura] Init Sound | DONT DELETE THIS";
    
    private final Minecraft mc = Minecraft.getInstance();

    public void init() {
        Display.setTitle(NAME + " " + VERSION);
        Runtime.getRuntime().addShutdownHook(new Thread(ex::shutdown));

        initAutoOptimization();
        initManagers();
        initHandler();
        initVia();

        if (Platform.isWindows()) checkFirstStart();
        else firstStart = false;
        
        mc.displayGuiScreen(new SakuMenu());

        logger.info("Initialized successfully.");
    }

    public void terminate() {    	
    	if (configManager.get("latest") != null) {
        	configManager.get("latest").write();
        }
    	
    	discordHandler.stop();
    	logger.info("Discord RPC Terminated.");
        System.gc();
        logger.info("Client Terminated.");
    }
    
    private void initHandler() {
    	logger.info("Handlers initialized.");
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
    			new TransactionHandler());
    	logger.info("Handlers registered.");
    }
    
    private void initVia() {
        ViaMCP.INSTANCE.initAsyncSlider();
        ViaMCP.INSTANCE.getAsyncVersionSlider().setVersion(ViaMCP.NATIVE_VERSION);
    	logger.info("ViaMCP initialized.");
    }
    
    private void initManagers() {
        moduleManager = new ModuleManager();
        fileManager = new FileManager();
        commandManager = new CommandManager();
        configManager = new ConfigManager();
        themeManager = new ThemeManager();
        scriptManager = new ScriptManager();
        bindableManager = new BindableManager();
        
        scriptManager.init();
        commandManager.init();
        bindableManager.init();
        moduleManager.init();
        
        clickGui = new RiceGui();
        clickGui.initGui();
        
        logger.info("Managers initialized.");
    }
    
    private void initAutoOptimization() {
    	if (Platform.isWindows()) {
	    	mc.gameSettings.ofFastRender = Config.isShaders() ? false : true;
			mc.gameSettings.ofChunkUpdatesDynamic = true;
			mc.gameSettings.ofSmartAnimations = true;
	        mc.gameSettings.ofShowGlErrors = false;
	        mc.gameSettings.ofRenderRegions = true;
	        mc.gameSettings.ofSmoothFps = false;
	        mc.gameSettings.ofFastMath = true;
	        mc.gameSettings.useVbo = true;
    	}
        mc.gameSettings.guiScale = 2;
    }

	private void register(Object... handlers) {
	    for (Object handler : handlers) {
	        try {
	            registered.add(handler);
	            eventBus.register(handler);
	            logger.info(handler.getClass().getSimpleName() + " registered.");
	        } catch (Exception e) {
	        	logger.error("Failed to register handler: " + handler.getClass().getSimpleName(), e);
	        }
	    }
	}
	
	private void initTempFile() {
	    final File dir = new File(mc.mcDataDir, "Sakura" + File.separator + "sound");
	    final File firstInitFile = new File(dir, "sound.txt");
		
        if (firstInitFile.exists()) {
            try {
                String content = new String(Files.readAllBytes(firstInitFile.toPath())).trim();
                if (!EXPECTED_CONTENT.equals(content)) {
                    if (firstInitFile.delete()) {
                        logger.info("Delete sound.txt, invalid content.");
                        firstStart = true;
                    } else {
                        logger.error("Failed.");
                        return;
                    }
                } else {
                    return;
                }
            } catch (IOException e) {
                logger.error("Failed to read", e);
                return;
            }
        } else {
            firstStart = true;
        }

        if (!dir.exists() && !dir.mkdirs()) {
            logger.error("Failed dir.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(firstInitFile))) {
            writer.write(EXPECTED_CONTENT);
            logger.info("Created sound.txt.");
        } catch (IOException e) {
            logger.error("Failed created", e);
        }
	}

	private void checkFirstStart() {
	    final File dir = new File(mc.mcDataDir, "Sakura" + File.separator + "sound");
	    final File firstInitFile = new File(dir, "sound.txt");
		
        if (firstInitFile.exists()) {
            try {
                String content = new String(Files.readAllBytes(firstInitFile.toPath())).trim();
                if (EXPECTED_CONTENT.equals(content)) {
                    firstStart = false;
                    logger.info("Start detected, canceling sound...");
                } else {
                    firstStart = true;
                    logger.info("Contenido incorrecto, activando sonido...");
                    initTempFile();
                }
            } catch (IOException e) {
                logger.error("Error leyendo el archivo sound.txt", e);
            }
        } else {
            firstStart = true;
            logger.info("No sound.txt found, initializing sound...");
            initTempFile();
        }
	}
}