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

import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.data.etf.ExTermEncoder;
import net.dv8tion.jda.internal.utils.Helpers;

public class DataArray implements Iterable<Object>, SerializableArray {
    private static final Gson gson = new Gson();
    private static final Type listType = new TypeToken<List<Object>>() {}.getType();

    protected final List<Object> data;

    protected DataArray(List<Object> data) {
        this.data = data;
    }

    @Nonnull
    public static DataArray empty() {
        return new DataArray(new ArrayList<>());
    }

    @Nonnull
    public static DataArray fromCollection(@Nonnull Collection<?> col) {
        return empty().addAll(col);
    }

    @Nonnull
    public static DataArray fromJson(@Nonnull Reader json) {
        try {
            List<Object> list = gson.fromJson(json, listType);
            return new DataArray(list);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new ParsingException(e);
        }
    }

    public boolean isNull(int index) {
        return index >= length() || data.get(index) == null;
    }

    public boolean isType(int index, @Nonnull DataType type) {
        return type.isType(data.get(index));
    }

    public int length() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Nonnull
    public DataObject getObject(int index) {
        Map<String, Object> child = null;
        try {
            child = (Map<String, Object>) get(Map.class, index);
        } catch (ClassCastException ex) {
        }
        if (child == null)
            throw valueError(index, "DataObject");
        return new DataObject(child);
    }

    @Nonnull
    public DataArray getArray(int index) {
        List<Object> child = null;
        try {
            child = (List<Object>) get(List.class, index);
        } catch (ClassCastException ex) {
        }
        if (child == null)
            throw valueError(index, "DataArray");
        return new DataArray(child);
    }

    @Nonnull
    public String getString(int index) {
        String value = get(String.class, index, UnaryOperator.identity(), String::valueOf);
        if (value == null)
            throw valueError(index, "String");
        return value;
    }

    @Contract("_, !null -> !null")
    public String getString(int index, @Nullable String defaultValue) {
        String value = get(String.class, index, UnaryOperator.identity(), String::valueOf);
        return value == null ? defaultValue : value;
    }

    public boolean getBoolean(int index) {
        return getBoolean(index, false);
    }

    public boolean getBoolean(int index, boolean defaultValue) {
        Boolean value = get(Boolean.class, index, Boolean::parseBoolean, null);
        return value == null ? defaultValue : value;
    }

    public int getInt(int index) {
        Integer value = get(Integer.class, index, Integer::parseInt, Number::intValue);
        if (value == null)
            throw valueError(index, "int");
        return value;
    }

    public int getInt(int index, int defaultValue) {
        Integer value = get(Integer.class, index, Integer::parseInt, Number::intValue);
        return value == null ? defaultValue : value;
    }

    public long getLong(int index) {
        Long value = get(Long.class, index, Long::parseLong, Number::longValue);
        if (value == null)
            throw valueError(index, "long");
        return value;
    }

    public long getLong(int index, long defaultValue) {
        Long value = get(Long.class, index, Long::parseLong, Number::longValue);
        return value == null ? defaultValue : value;
    }

    @Nonnull
    public OffsetDateTime getOffsetDateTime(int index) {
        OffsetDateTime value = getOffsetDateTime(index, null);
        if (value == null)
            throw valueError(index, "OffsetDateTime");
        return value;
    }

    @Contract("_, !null -> !null")
    public OffsetDateTime getOffsetDateTime(int index, @Nullable OffsetDateTime defaultValue) {
        OffsetDateTime value;
        try {
            value = get(OffsetDateTime.class, index, OffsetDateTime::parse, null);
        } catch (DateTimeParseException e) {
            String reason = "Cannot parse value for index %d into an OffsetDateTime object. Try double checking that %s is a valid ISO8601 timestamp";
            throw new ParsingException(String.format(reason, index, e.getParsedString()));
        }
        return value == null ? defaultValue : value;
    }

    public double getDouble(int index) {
        Double value = get(Double.class, index, Double::parseDouble, Number::doubleValue);
        if (value == null)
            throw valueError(index, "double");
        return value;
    }

    public double getDouble(int index, double defaultValue) {
        Double value = get(Double.class, index, Double::parseDouble, Number::doubleValue);
        return value == null ? defaultValue : value;
    }

    @Nonnull
    public DataArray add(@Nullable Object value) {
        if (value instanceof SerializableData)
            data.add(((SerializableData) value).toData().data);
        else if (value instanceof SerializableArray)
            data.add(((SerializableArray) value).toDataArray().data);
        else
            data.add(value);
        return this;
    }

    @Nonnull
    public DataArray addAll(@Nonnull Collection<?> values) {
        values.forEach(this::add);
        return this;
    }

    @Nonnull
    public DataArray addAll(@Nonnull DataArray array) {
        return addAll(array.data);
    }

    @Nonnull
    public DataArray insert(int index, @Nullable Object value) {
        if (value instanceof SerializableData)
            data.add(index, ((SerializableData) value).toData().data);
        else if (value instanceof SerializableArray)
            data.add(index, ((SerializableArray) value).toDataArray().data);
        else
            data.add(index, value);
        return this;
    }

    @Nonnull
    public DataArray remove(int index) {
        data.remove(index);
        return this;
    }

    @Nonnull
    public DataArray remove(@Nullable Object value) {
        data.remove(value);
        return this;
    }
    public int getUnsignedInt(int index)
    {
        Integer value = get(Integer.class, index, Integer::parseUnsignedInt, Number::intValue);
        if (value == null)
            throw valueError(index, "unsigned int");
        return value;
    }


    public int getUnsignedInt(int index, int defaultValue)
    {
        Integer value = get(Integer.class, index, Integer::parseUnsignedInt, Number::intValue);
        return value == null ? defaultValue : value;
    }
    public long getUnsignedLong(int index)
    {
        Long value = get(Long.class, index, Long::parseUnsignedLong, Number::longValue);
        if (value == null)
            throw valueError(index, "unsigned long");
        return value;
    }

    public long getUnsignedLong(int index, long defaultValue)
    {
        Long value = get(Long.class, index, Long::parseUnsignedLong, Number::longValue);
        return value == null ? defaultValue : value;
    }

    @Nonnull
    public byte[] toJson() {
        try {
            return gson.toJson(data).getBytes();
        } catch (JsonIOException e) {

        }
        return new byte[0];
    }

    @Nonnull
    public byte[] toETF() {
        ByteBuffer buffer = ExTermEncoder.pack(data);
        return Arrays.copyOfRange(buffer.array(), buffer.arrayOffset(), buffer.arrayOffset() + buffer.limit());
    }

    @Override
    public String toString() {
        return gson.toJson(data);
    }

    @Nonnull
    public List<Object> toList() {
        return data;
    }

    private ParsingException valueError(int index, String expectedType) {
        return new ParsingException("Unable to resolve value at " + index + " to type " + expectedType + ": " + data.get(index));
    }

    @Nullable
    private <T> T get(@Nonnull Class<T> type, int index) {
        return get(type, index, null, null);
    }

    @Nullable
    private <T> T get(@Nonnull Class<T> type, int index, @Nullable Function<String, T> stringMapper, @Nullable Function<Number, T> numberMapper) {
        if (index < 0)
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        Object value = index < data.size() ? data.get(index) : null;
        if (value == null)
            return null;
        if (type.isInstance(value))
            return type.cast(value);
        if (type == String.class)
            return type.cast(value.toString());
        // attempt type coercion
        if (stringMapper != null && value instanceof String)
            return stringMapper.apply((String) value);
        else if (numberMapper != null && value instanceof Number)
            return numberMapper.apply((Number) value);

        throw new ParsingException(Helpers.format("Cannot parse value for index %d into type %s: %s instance of %s",
                index, type.getSimpleName(), value, value.getClass().getSimpleName()));
    }

    @Nonnull
    @Override
    public Iterator<Object> iterator() {
        return data.iterator();
    }

    @Nonnull
    public <T> Stream<T> stream(BiFunction<? super DataArray, Integer, ? extends T> mapper) {
        return IntStream.range(0, length())
                .mapToObj(index -> mapper.apply(this, index));
    }

    @Nonnull
    @Override
    public DataArray toDataArray() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DataArray))
            return false;
        DataArray objects = (DataArray) o;
        return Objects.equals(data, objects.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}