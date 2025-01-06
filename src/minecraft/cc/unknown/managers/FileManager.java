package cc.unknown.managers;

import java.io.File;

import cc.unknown.Sakura;
import cc.unknown.util.Accessor;

public class FileManager {

    public static final File DIRECTORY = new File(Accessor.mc.mcDataDir, Sakura.NAME);

    public FileManager() {
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdir();
        }
    }
}