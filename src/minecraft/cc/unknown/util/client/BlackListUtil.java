package cc.unknown.util.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BlackListUtil {

    private final String TEMP_PATH = System.getProperty("java.io.tmpdir") + File.separator + "secure.txt";
    private Set<String> blacklistedKeys = new HashSet<>();

    private void load() {
        File tempFile = new File(TEMP_PATH);
        if (tempFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    blacklistedKeys.add(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(String key) {
    	load();

        if (!blacklistedKeys.contains(key)) {
            blacklistedKeys.add(key);
            save();
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_PATH))) {
            for (String key : blacklistedKeys) {
                writer.write(key);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getBlacklistedKeys() {
        return blacklistedKeys;
    }

    public boolean isBlacklisted(String key) {
    	load();
        return blacklistedKeys.contains(key);
    }
}

