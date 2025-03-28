package cc.unknown.util.file.config;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import com.google.gson.JsonObject;

import cc.unknown.Sakura;
import cc.unknown.module.Module;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.theme.Themes;
import cc.unknown.util.file.FileType;
import cc.unknown.util.structure.geometry.Vector2d;
import cc.unknown.value.Value;
import cc.unknown.value.impl.*;

public class ConfigFile extends cc.unknown.util.file.File {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");

    private boolean loadKeyCodes;


    public ConfigFile(final File file, final FileType fileType) {
        super(file, fileType);
    }

    @Override
    public boolean read() {
        if (!this.getFile().exists()) {
            return false;
        }

        try {
            // reads file to a json object
            final FileReader fileReader = new FileReader(getFile());
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            final JsonObject jsonObject = getGSON().fromJson(bufferedReader, JsonObject.class);

            // closes both readers
            bufferedReader.close();
            fileReader.close();

            // checks if there was data read
            if (jsonObject == null) {
                return false;
            }

            if (jsonObject.has("theme")) {
                Optional<Themes> theme = Arrays.stream(Themes.values())
                        .filter(x -> x.name().equalsIgnoreCase(jsonObject.get("theme").getAsString()))
                        .findFirst();

                theme.ifPresent(configTheme -> Sakura.instance.getThemeManager().setTheme(configTheme));
            }

            // loops through all modules to update their data
            for (final Module module : Sakura.instance.getModuleManager().getAll()) {
                // checks if config contains module
                if (!jsonObject.has(module.getName())) {
                    continue;
                }

                module.setEnabled(false);

                // gets the modules json object
                final JsonObject moduleJsonObject = jsonObject.getAsJsonObject(module.getName());
                int index = 0;
                for (final Value<?> value : module.getAllValues()) {
                    index++;

                    // checks if config contains value
                    if (!moduleJsonObject.has(value.getName() + "*" + index)) {
                        continue;
                    }

                    // gets the values json object
                    final JsonObject valueJsonObject = moduleJsonObject.getAsJsonObject(value.getName() + "*" + index);

                    try {
                        // applies the settings from the config if it has the setting
                        if (value instanceof ModeValue) {
                            final ModeValue enumValue = (ModeValue) value;

                            if (valueJsonObject.has("value")) {
                                enumValue.setDefault(valueJsonObject.get("value").getAsString());
                            }
                        } else if (value instanceof BooleanValue) {
                            final BooleanValue booleanValue = (BooleanValue) value;

                            if (valueJsonObject.has("value")) {
                                booleanValue.setValue(valueJsonObject.get("value").getAsBoolean());
                            }
                        } else if (value instanceof TextValue) {
                            final TextValue stringValue = (TextValue) value;

                            if (valueJsonObject.has("value")) {

                                String load = valueJsonObject.get("value").getAsString();
                                load = load.replace("<percentsign>", "%");

                                stringValue.setValue(load);
                            }
                        } else if (value instanceof DescValue) {
                        	final DescValue descValue = (DescValue) value;
                        	
                        	if (valueJsonObject.has("value")) {
                        		
                        		String load = valueJsonObject.get("value").getAsString();
                        		load = load.replace("<percentsign>", "%");
                        		
                        		descValue.setValue(load);
                        	}
                        } else if (value instanceof NumberValue) {
                            final NumberValue numberValue = (NumberValue) value;

                            if (valueJsonObject.has("value")) {
                                numberValue.setValue(valueJsonObject.get("value").getAsDouble());
                            }
                        } else if (value instanceof BoundsNumberValue) {
                            final BoundsNumberValue boundsNumberValue = (BoundsNumberValue) value;

                            if (valueJsonObject.has("first")) {
                                boundsNumberValue.setValue(valueJsonObject.get("first").getAsDouble());
                            }

                            if (valueJsonObject.has("second")) {
                                boundsNumberValue.setSecondValue(valueJsonObject.get("second").getAsDouble());
                            }
                        } else if (value instanceof DragValue) {
                            final DragValue positionValue = (DragValue) value;

                            double positionX = 0, positionY = 0, scaleX = 0, scaleY = 0;

                            if (valueJsonObject.has("positionX")) {
                                positionX = valueJsonObject.get("positionX").getAsDouble();
                            }

                            if (valueJsonObject.has("positionY")) {
                                positionY = valueJsonObject.get("positionY").getAsDouble();
                            }

                            if (valueJsonObject.has("scaleX")) {
                                scaleX = valueJsonObject.get("scaleX").getAsDouble();
                            }

                            if (valueJsonObject.has("scaleY")) {
                                scaleY = valueJsonObject.get("scaleY").getAsDouble();
                            }

                            positionValue.setTargetPosition(new Vector2d(positionX, positionY));
                            positionValue.setPosition(new Vector2d(positionX, positionY));
                            positionValue.setScale(new Vector2d(scaleX, scaleY));
                        } else if (value instanceof ListValue) {
                            final ListValue<?> enumValue = (ListValue<?>) value;

                            for (Object mode : enumValue.getModes()) {
                                if (mode.toString().equals(valueJsonObject.get("value").getAsString())) {
                                    enumValue.setValueAsObject(mode);
                                }
                            }
                        }
                    } catch (final Exception exception) {
                        exception.printStackTrace();
                    }
                }

                // checks if state of the module can be updated
                try {
                    if (moduleJsonObject.has("state")) {
                        final boolean state = moduleJsonObject.get("state").getAsBoolean();
                        module.setEnabled(state);
                    }
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }

                // checks if key codes should be updated
                if (!loadKeyCodes) {
                    continue;
                }

                // checks if key codes of the module can be updated
                if (moduleJsonObject.has("keyCode")) {
                    final int keyCode = moduleJsonObject.get("keyCode").getAsInt();
                    module.setKey(keyCode);
                }
            }
        } catch (final IOException ignored) {
            return false;
        }

        return true;
    }

    @Override
    public boolean write() {
        try {
            // creates the file
            this.getFile().createNewFile();
            // creates a new json object where all data is stored in
            final JsonObject jsonObject = new JsonObject();

            // Add some extra information to the config
            final JsonObject metadataJsonObject = new JsonObject();
            metadataJsonObject.addProperty("version", Sakura.VERSION);
            metadataJsonObject.addProperty("creationDate", DATE_FORMATTER.format(new Date()));
            jsonObject.add("Metadata", metadataJsonObject);

            // loops through all modules to save their data
            for (final Module module : Sakura.instance.getModuleManager().getAll()) {
                // creates an own module json object
                final JsonObject moduleJsonObject = new JsonObject();

                // adds data to the module json object
                if (!(module instanceof ClickGUI)) {
                    moduleJsonObject.addProperty("state", module.isEnabled());
                }

                moduleJsonObject.addProperty("keyCode", module.getKey());

                int index = 0;
                for (final Value<?> value : module.getAllValues()) {
                    index++;
                    final JsonObject valueJsonObject = new JsonObject();

                    if (value instanceof ModeValue) {
                        final ModeValue enumValue = (ModeValue) value;
                        valueJsonObject.addProperty("value", enumValue.getValue().getName());
                    } else if (value instanceof BooleanValue) {
                        final BooleanValue booleanValue = (BooleanValue) value;
                        valueJsonObject.addProperty("value", booleanValue.getValue());
                    } else if (value instanceof NumberValue) {
                        final NumberValue numberValue = (NumberValue) value;
                        valueJsonObject.addProperty("value", numberValue.getValue().doubleValue());
                    } else if (value instanceof TextValue) {
                        final TextValue stringValue = (TextValue) value;

                        String save = stringValue.getValue();
                        save = save.replace("%", "<percentsign>");

                        valueJsonObject.addProperty("value", save);
                    } else if (value instanceof BoundsNumberValue) {
                        final BoundsNumberValue boundsNumberValue = (BoundsNumberValue) value;
                        valueJsonObject.addProperty("first", boundsNumberValue.getValue().doubleValue());
                        valueJsonObject.addProperty("second", boundsNumberValue.getSecondValue().doubleValue());
                    } else if (value instanceof DragValue) {
                        final DragValue positionValue = (DragValue) value;

                        valueJsonObject.addProperty("positionX", positionValue.position.x);
                        valueJsonObject.addProperty("positionY", positionValue.position.y);

                        valueJsonObject.addProperty("scaleX", positionValue.scale.x);
                        valueJsonObject.addProperty("scaleY", positionValue.scale.y);
                    } else if (value instanceof ListValue) {
                        final ListValue<?> enumValue = (ListValue<?>) value;
                        valueJsonObject.addProperty("value", enumValue.getValue().toString());
                    }

                    moduleJsonObject.add(value.getName() + "*" + index, valueJsonObject);
                }

                // updates json object which contains all data
                jsonObject.add(module.getName(), moduleJsonObject);
            }
            jsonObject.addProperty("theme", Sakura.instance.getThemeManager().getTheme().name());

            // writes json object data to a file
            final FileWriter fileWriter = new FileWriter(getFile());
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            getGSON().toJson(jsonObject, bufferedWriter);

            // closes the writer
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (final IOException exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    public void allowKeyCodeLoading() {
        this.loadKeyCodes = true;
    }
}