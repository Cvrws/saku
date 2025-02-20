package cc.unknown.script.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.script.ScriptException;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.module.Module;
import cc.unknown.script.api.wrapper.impl.ScriptBlockPos;
import cc.unknown.script.api.wrapper.impl.ScriptCommand;
import cc.unknown.script.api.wrapper.impl.ScriptModule;
import cc.unknown.script.api.wrapper.impl.vector.ScriptVector2f;
import cc.unknown.script.api.wrapper.impl.vector.ScriptVector3d;
import cc.unknown.script.util.ScriptModuleInfo;
import cc.unknown.util.client.ChatUtil;
import cc.unknown.util.player.EnemyUtil;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.rotation.MoveFix;
import cc.unknown.util.player.rotation.RotationUtil;
import cc.unknown.util.structure.geometry.Vector2f;
import cc.unknown.util.structure.geometry.Vector3d;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraft.client.Minecraft;

public class ScriptAPI {
    private static final Map<Module, ScriptModule> SCRIPT_MODULE_MAP = new HashMap<>();

    private static final Map<Command, ScriptCommand> SCRIPT_COMMAND_MAP = new HashMap<>();

    private static ScriptModule getModule(final Module module) {
        SCRIPT_MODULE_MAP.putIfAbsent(module, new ScriptModule(module, true));
        return SCRIPT_MODULE_MAP.get(module);
    }

    private static ScriptCommand getCommand(final Command command) {
        SCRIPT_COMMAND_MAP.putIfAbsent(command, new ScriptCommand(command));
        return SCRIPT_COMMAND_MAP.get(command);
    }

    public ScriptModule registerModule(final String name, final String description) {
        final AtomicReference<ScriptModule> scriptModuleReference = new AtomicReference<>(null);

        final Module module = new Module(new ScriptModuleInfo(name, description)) {
            @Override
            public void onEnable() {
                final ScriptModule scriptModule = scriptModuleReference.get();
                if (scriptModule == null) return;

                scriptModule.call("onEnable");
            }

            @Override
            public void onDisable() {
                final ScriptModule scriptModule = scriptModuleReference.get();
                if (scriptModule == null) return;

                scriptModule.call("onDisable");
            }
        };

        scriptModuleReference.set(getModule(module));
        Sakura.instance.getModuleManager().add(module);

        if (Sakura.instance.getClickGui() != null) Sakura.instance.getClickGui().rebuildModuleCache();
        return scriptModuleReference.get();
    }

    public ScriptModule[] getModules() {
        final List<Module> modules = Sakura.instance.getModuleManager().getAll();
        final ScriptModule[] scriptModules = new ScriptModule[modules.size()];

        for (int i = 0; i < modules.size(); i++) {
            scriptModules[i] = new ScriptModule(modules.get(i));
        }

        return scriptModules;
    }

    public ScriptModule getModule(final String name) {
        return new ScriptModule(Sakura.instance.getModuleManager().get(name));
    }

    public void rotate(float yaw, float pitch, double speed) {
        RotationHandler.setRotations(new Vector2f(yaw, pitch), speed, MoveFix.OFF);
    }

    public float[] getRotations(int entity) {
        Vector2f rotations = RotationUtil.calculate(Minecraft.getInstance().world.getEntityByID(entity));

        return new float[]{rotations.x, rotations.y};
    }

    public float[] getRotations(ScriptVector3d vector3d) {
        Vector2f rotations = RotationUtil.calculate(new Vector3d(vector3d.getX(), vector3d.getY(), vector3d.getZ()));

        return new float[]{rotations.x, rotations.y};
    }

    public ScriptCommand registerCommand(final String name, final String description) {
        final AtomicReference<ScriptCommand> scriptCommandReference = new AtomicReference<>(null);

        final Command command = new Command(description, name) {
            @Override
            public void execute(final String[] args) {
                final ScriptCommand scriptCommand = scriptCommandReference.get();
                if (scriptCommand == null) return;

                scriptCommand.call("onExecute", (Object[]) args);
            }

			@Override
			public List<String> autocomplete(int arg, String[] args) {
				return null;
			}
        };

        scriptCommandReference.set(getCommand(command));
        Sakura.instance.getCommandManager().commandList.add(command);

        return scriptCommandReference.get();
    }

    public ScriptCommand[] getCommands() {
        final List<Command> commands = Sakura.instance.getCommandManager().commandList;
        final ScriptCommand[] scriptCommands = new ScriptCommand[commands.size()];

        for (int i = 0; i < commands.size(); ++i) {
            scriptCommands[i] = getCommand(commands.get(i));
        }

        return scriptCommands;
    }

    public ScriptCommand getCommand(final String name) {
        return getCommand(Sakura.instance.getCommandManager().get(name));
    }

    public void displayChat(final String message) {
    	ChatUtil.display(message);
    }

    public long getSystemMillis() {
        return System.currentTimeMillis();
    }

    public ScriptVector2f vector2F(float x, float y) {
        return new ScriptVector2f(x, y);
    }

    public ScriptVector3d vector3D(double x, double y, double z) {
        return new ScriptVector3d(x, y, z);
    }

    public int getFPS() {
        return Minecraft.getDebugFPS();
    }

    public boolean isFriend(String name){
        return FriendUtil.isFriend(name);
    }
    
    public boolean isEnemy(String name) {
    	return EnemyUtil.isEnemy(name);
    }

    public ScriptBlockPos newBlockPos(int x, int y, int z) {
    	ChatUtil.display("Please use world.newBlockPos(), instead of sakura.newBlockPos().");
        return null;
    }

    public void thread(JSObject function) throws ScriptException {
        if (!function.isFunction()) throw new ScriptException("Not a function!");
        new Thread(() -> function.call(null)).start();
    }
}
