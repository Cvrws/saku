package cc.unknown.managers;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Sakura;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.util.client.ReflectionUtil;
import cc.unknown.util.structure.AdaptiveMap;

public final class ModuleManager {

    private AdaptiveMap<Class<Module>, Module> moduleMap = new AdaptiveMap<>();
    private boolean initialized = false;
    
    public void init() {
        if (initialized) {
            throw new IllegalStateException("ModuleManager is already initialized.");
        }

        moduleMap = new AdaptiveMap<>();
        String packageName = "cc.unknown.module.impl";

        List<Class<? extends Module>> moduleClasses = ReflectionUtil.resolvePackage(packageName, Module.class);

        for (Class<? extends Module> moduleClass : moduleClasses) {
            if (moduleClass == null) {
                continue;
            }

            try {
                if (Modifier.isAbstract(moduleClass.getModifiers())) {
                    continue;
                }

                if (!Module.class.isAssignableFrom(moduleClass)) {
                    continue;
                }

                Module moduleInstance = moduleClass.getDeclaredConstructor().newInstance();
                put(moduleClass, moduleInstance);
            } catch (NoSuchMethodException e) {
                Sakura.instance.LOGGER.error("No default constructor found for module: " + moduleClass.getName());
            } catch (Exception e) {
                Sakura.instance.LOGGER.error("Failed to initialize module: " + moduleClass.getName() + ". Reason: " + e.getMessage());
            }
        }

        initialized = true;

        this.getAll().stream()
            .filter(module -> module != null && module.getModuleInfo() != null && module.getModuleInfo().autoEnabled())
            .forEach(module -> module.setEnabled(true));

        Sakura.instance.getEventBus().register(this);
    }

    public ArrayList<Module> getAll() {
        return this.moduleMap.values();
    }

    public <T extends Module> T get(final Class<T> clazz) {
        if (!initialized) {
            throw new IllegalStateException("ModuleManager has not been initialized. Call init() before accessing modules.");
        }

        T module = (T) this.moduleMap.get(clazz);
        if (module == null) {
            Sakura.instance.LOGGER.error("Module not found for class: " + clazz.getName());
        }
        return module;
    }
    
    public List<Module> getModulesByCategory(Category category) {
        return getAll().stream().filter(m -> m.getModuleInfo().category() == category).collect(Collectors.toList());
    }
    
    public <T extends Module> T get(final String name) {
        return (T) this.getAll().stream()
            .filter(module -> module != null && module.getAliases() != null &&
                Arrays.stream(module.getAliases())
                      .anyMatch(alias -> alias.replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))))
            .findAny()
            .orElse(null);
    }

    public void put(Class clazz, Module module) {
        this.moduleMap.put(clazz, module);
    }
    
    public void put(Class<? extends Module>[] clazzArray, Module[] moduleArray) {
        if (clazzArray.length != moduleArray.length) {
            throw new IllegalArgumentException("Class and Module arrays must have the same length");
        }
        
        for (int i = 0; i < clazzArray.length; i++) {
            this.moduleMap.put((Class<Module>) clazzArray[i], moduleArray[i]);
        }
    }

    public void remove(Module key) {
        this.moduleMap.removeValue(key);
    }

    public boolean add(final Module module) {
        this.moduleMap.put(module);
        return true;
    }
}