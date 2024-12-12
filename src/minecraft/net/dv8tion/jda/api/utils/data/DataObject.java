/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dv8tion.jda.api.utils.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.etf.ExTermDecoder;
import net.dv8tion.jda.api.utils.data.etf.ExTermEncoder;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class DataObject implements SerializableData {
    private static final Gson gson = new GsonBuilder().create();
    private static final Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

    protected final Map<String, Object> data;

    protected DataObject(@Nonnull Map<String, Object> data) {
        this.data = data;
    }

    @Nonnull
    public static DataObject empty() {
        return new DataObject(new HashMap<>());
    }

    @Nonnull
    public static DataObject fromJson(@Nonnull byte[] data) {
        try {
            Map<String, Object> map = gson.fromJson(new String(data), mapType);
            return new DataObject(map);
        } catch (JsonSyntaxException ex) {
            throw new ParsingException(ex);
        }
    }

    @Nonnull
    public static DataObject fromJson(@Nonnull InputStream stream) {
        try (Reader reader = new InputStreamReader(stream)) {
            Map<String, Object> map = gson.fromJson(reader, mapType);
            return new DataObject(map);
        } catch (IOException | JsonSyntaxException ex) {
            throw new ParsingException(ex);
        }
    }

    @Nonnull
    public static DataObject fromJson(@Nonnull Reader stream) {
        try {
            Map<String, Object> map = gson.fromJson(stream, mapType);
            return new DataObject(map);
        } catch (JsonSyntaxException ex) {
            throw new ParsingException(ex);
        }
    }

    @Nonnull
    public static DataObject fromETF(@Nonnull byte[] data) {
        Checks.notNull(data, "Data");
        try {
            Map<String, Object> map = ExTermDecoder.unpackMap(ByteBuffer.wrap(data));
            return new DataObject(map);
        } catch (Exception ex) {
            throw new ParsingException(ex);
        }
    }

    public boolean hasKey(@Nonnull String key) {
        return data.containsKey(key);
    }

    public boolean isNull(@Nonnull String key) {
        return data.get(key) == null;
    }

    public boolean isType(@Nonnull String key, @Nonnull DataType type) {
        return type.isType(data.get(key));
    }

    @Nonnull
    public DataObject getObject(@Nonnull String key) {
        return optObject(key).orElseThrow(() -> valueError(key, "DataObject"));
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public Optional<DataObject> optObject(@Nonnull String key) {
        Map<String, Object> child = null;
        try {
            child = (Map<String, Object>) get(Map.class, key);
        } catch (ClassCastException ex) {
        }
        return child == null ? Optional.empty() : Optional.of(new DataObject(child));
    }

    @Nonnull
    public DataArray getArray(@Nonnull String key) {
        return optArray(key).orElseThrow(() -> valueError(key, "DataArray"));
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public Optional<DataArray> optArray(@Nonnull String key) {
        List<Object> child = null;
        try {
            child = (List<Object>) get(List.class, key);
        } catch (ClassCastException ex) {
        }
        return child == null ? Optional.empty() : Optional.of(new DataArray(child));
    }

    @Nonnull
    public Optional<Object> opt(@Nonnull String key) {
        return Optional.ofNullable(data.get(key));
    }

    @Nonnull
    public Object get(@Nonnull String key) {
        Object value = data.get(key);
        if (value == null)
            throw valueError(key, "any");
        return value;
    }

    @Nonnull
    public String getString(@Nonnull String key) {
        String value = getString(key, null);
        if (value == null)
            throw valueError(key, "String");
        return value;
    }

    @Contract("_, !null -> !null")
    public String getString(@Nonnull String key, @Nullable String defaultValue) {
        String value = get(String.class, key, UnaryOperator.identity(), String::valueOf);
        return value == null ? defaultValue : value;
    }

    public boolean getBoolean(@Nonnull String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(@Nonnull String key, boolean defaultValue) {
        Boolean value = get(Boolean.class, key, Boolean::parseBoolean, null);
        return value == null ? defaultValue : value;
    }

    public long getLong(@Nonnull String key) {
        Long value = get(Long.class, key, MiscUtil::parseLong, Number::longValue);
        if (value == null)
            throw valueError(key, "long");
        return value;
    }

    public long getLong(@Nonnull String key, long defaultValue) {
        Long value = get(Long.class, key, Long::parseLong, Number::longValue);
        return value == null ? defaultValue : value;
    }

    public long getUnsignedLong(@Nonnull String key) {
        Long value = get(Long.class, key, Long::parseUnsignedLong, Number::longValue);
        if (value == null)
            throw valueError(key, "unsigned long");
        return value;
    }

    public long getUnsignedLong(@Nonnull String key, long defaultValue) {
        Long value = get(Long.class, key, Long::parseUnsignedLong, Number::longValue);
        return value == null ? defaultValue : value;
    }

    public int getInt(@Nonnull String key) {
        Integer value = get(Integer.class, key, Integer::parseInt, Number::intValue);
        if (value == null)
            throw valueError(key, "int");
        return value;
    }

    public int getInt(@Nonnull String key, int defaultValue) {
        Integer value = get(Integer.class, key, Integer::parseInt, Number::intValue);
        return value == null ? defaultValue : value;
    }

    public int getUnsignedInt(@Nonnull String key) {
        Integer value = get(Integer.class, key, Integer::parseUnsignedInt, Number::intValue);
        if (value == null)
            throw valueError(key, "unsigned int");
        return value;
    }

    public int getUnsignedInt(@Nonnull String key, int defaultValue) {
        Integer value = get(Integer.class, key, Integer::parseUnsignedInt, Number::intValue);
        return value == null ? defaultValue : value;
    }

    public double getDouble(@Nonnull String key) {
        Double value = get(Double.class, key, Double::parseDouble, Number::doubleValue);
        if(value == null)
            throw valueError(key, "double");
        return value;
    }

    public double getDouble(@Nonnull String key, double defaultValue) {
        Double value = get(Double.class, key, Double::parseDouble, Number::doubleValue);
        return value == null ? defaultValue : value;
    }

    @Nonnull
    public OffsetDateTime getOffsetDateTime(@Nonnull String key) {
        OffsetDateTime value = getOffsetDateTime(key, null);
        if(value == null)
            throw valueError(key, "OffsetDateTime");
        return value;
    }

    @Contract("_, !null -> !null")
    public OffsetDateTime getOffsetDateTime(@Nonnull String key, @Nullable OffsetDateTime defaultValue) {
        OffsetDateTime value;
        try {
            value = get(OffsetDateTime.class, key, OffsetDateTime::parse, null);
        } catch (DateTimeParseException e) {
            String reason = "Cannot parse value for %s into an OffsetDateTime object. Try double checking that %s is a valid ISO8601 timestamp";
            throw new ParsingException(String.format(reason, key, e.getParsedString()));
        }
        return value == null ? defaultValue : value;
    }

    @Nonnull
    public DataObject remove(@Nonnull String key) {
        data.remove(key);
        return this;
    }

    @Nonnull

    public DataObject putNull(@Nonnull String key)
    {
        data.put(key, null);
        return this;
    }

    /**
     * Upserts a new value for the provided key.
     *
     * @param  key
     *         The key to upsert
     * @param  value
     *         The new value
     *
     * @return A DataObject with the updated value
     */
    @Nonnull
    public DataObject put(@Nonnull String key, @Nullable Object value)
    {
        if (value instanceof SerializableData)
            data.put(key, ((SerializableData) value).toData().data);
        else if (value instanceof SerializableArray)
            data.put(key, ((SerializableArray) value).toDataArray().data);
        else
            data.put(key, value);
        return this;
    }


    @Nonnull
    public Collection<Object> values()
    {
        return data.values();
    }

    /**
     * {@link java.util.Set} of all keys in this DataObject.
     *
     * @return {@link Set} of keys
     */
    @Nonnull
    public Set<String> keys()
    {
        return data.keySet();
    }

    /**
     * Serialize this object as JSON.
     *
     * @return byte array containing the JSON representation of this object
     */
    @Nonnull
    public byte[] toJson() {
        try {
            String json = gson.toJson(data);
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new UncheckedIOException(new IOException(e));
        }
    }

    /**
     * Serializes this object as ETF MAP term.
     *
     * @return byte array containing the encoded ETF term
     *
     * @since  4.2.1
     */
    @Nonnull
    public byte[] toETF()
    {
        ByteBuffer buffer = ExTermEncoder.pack(data);
        return Arrays.copyOfRange(buffer.array(), buffer.arrayOffset(), buffer.arrayOffset() + buffer.limit());
    }

    @Override
    public String toString() {
        try {
            return gson.toJson(data);
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }



    /**
     * Converts this DataObject to a {@link java.util.Map}
     *
     * @return The resulting map
     */
    @Nonnull
    public Map<String, Object> toMap()
    {
        return data;
    }

    @Nonnull
    @Override
    public DataObject toData()
    {
        return this;
    }

    private ParsingException valueError(String key, String expectedType)
    {
        return new ParsingException("Unable to resolve value with key " + key + " to type " + expectedType + ": " + data.get(key));
    }

    @Nullable
    private <T> T get(@Nonnull Class<T> type, @Nonnull String key)
    {
        return get(type, key, null, null);
    }

    @Nullable
    private <T> T get(@Nonnull Class<T> type, @Nonnull String key, @Nullable Function<String, T> stringParse, @Nullable Function<Number, T> numberParse)
    {
        Object value = data.get(key);
        if (value == null)
            return null;
        if (type.isInstance(value))
            return type.cast(value);
        if (type == String.class)
            return type.cast(value.toString());
        // attempt type coercion
        if (value instanceof Number && numberParse != null)
            return numberParse.apply((Number) value);
        else if (value instanceof String && stringParse != null)
            return stringParse.apply((String) value);

        throw new ParsingException(Helpers.format("Cannot parse value for %s into type %s: %s instance of %s",
                key, type.getSimpleName(), value, value.getClass().getSimpleName()));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (!(obj instanceof DataObject))
            return false;
        return ((DataObject) obj).toMap().equals(this.toMap());
    }

    @Override
    public int hashCode()
    {
        return toMap().hashCode();
    }
}