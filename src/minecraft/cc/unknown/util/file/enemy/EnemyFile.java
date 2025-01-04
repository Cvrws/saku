package cc.unknown.util.file.enemy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import cc.unknown.util.file.FileType;
import cc.unknown.util.player.EnemyUtil;

public class EnemyFile extends cc.unknown.util.file.File {

    public EnemyFile(final File file, final FileType fileType) {
        super(file, fileType);
    }

    @Override
    public boolean read() {
        if (!this.getFile().exists()) {
            return false;
        }

        try (FileReader fileReader = new FileReader(this.getFile());
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            final JsonObject jsonObject = getGSON().fromJson(bufferedReader, JsonObject.class);

            if (jsonObject == null) {
                return false;
            }

            EnemyUtil.getEnemy().clear();

            JsonArray array = jsonObject.getAsJsonArray("enemys");
            if (array != null) {
                for (int i = 0; i < array.size(); ++i) {
                    String enemy = array.get(i).getAsString();
                    EnemyUtil.addEnemy(enemy);
                }
            }

        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean write() {
        try (FileWriter fileWriter = new FileWriter(this.getFile());
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            JsonObject jsonObject = new JsonObject();
            JsonArray array = new JsonArray();

            for (String enemy : EnemyUtil.getEnemy()) {
                array.add(new JsonPrimitive(enemy));
            }

            jsonObject.add("enemys", array);

            bufferedWriter.write(getGSON().toJson(jsonObject));

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}