package cc.unknown.util.file.friend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import cc.unknown.util.file.FileType;
import cc.unknown.util.player.FriendUtil;

public class FriendFile extends cc.unknown.util.file.File {

    public FriendFile(final File file, final FileType fileType) {
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

            FriendUtil.getFriends().clear();

            JsonArray array = jsonObject.getAsJsonArray("friends");
            if (array != null) {
                for (int i = 0; i < array.size(); ++i) {
                    String friend = array.get(i).getAsString();
                    FriendUtil.addFriend(friend);
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

            for (String friend : FriendUtil.getFriends()) {
                array.add(new JsonPrimitive(friend));
            }

            jsonObject.add("friends", array);

            bufferedWriter.write(getGSON().toJson(jsonObject));

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}