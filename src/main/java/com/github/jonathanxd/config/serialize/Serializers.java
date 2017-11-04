/*
 *      Config - Configuration library <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2017 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
 *      Copyright (c) contributors
 *
 *
 *      Permission is hereby granted, free of charge, to any person obtaining a copy
 *      of this software and associated documentation files (the "Software"), to deal
 *      in the Software without restriction, including without limitation the rights
 *      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *      copies of the Software, and to permit persons to whom the Software is
 *      furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in
 *      all copies or substantial portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *      THE SOFTWARE.
 */
package com.github.jonathanxd.config.serialize;

import com.github.jonathanxd.config.CommonTypes;
import com.github.jonathanxd.config.Key;
import com.github.jonathanxd.config.Storage;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Serializer manager. We recommend to not call {@link Serializers} function directly from {@link
 * Serializer}. Use {@link Key} functions, such as {@link Key#setValue(Object)}, {@link
 * Key#getValue()}, {@link Key#getKey(String, Class)}. Read {@link Key} documentation for more
 * info.
 *
 * @see Key
 */
public final class Serializers {

    /**
     * Default serializers
     */
    public static final Serializers GLOBAL = new Serializers();

    static {
        Serializers.GLOBAL.registerAll(CommonTypes.ALL, new BasicSerializer<>());
        Serializers.GLOBAL.register(TypeInfo.of(List.class), new ListSerializer());
        Serializers.GLOBAL.register(TypeInfo.of(Map.class), new MapSerializer());
    }

    /**
     * Map to store serializers.
     */
    private final Map<TypeInfo<?>, Serializer> serializerMap = new HashMap<>();

    /**
     * Returns true if a serializer of provided {@link TypeInfo type information} is present, false
     * otherwise.
     *
     * @param typeInfo Type information.
     * @return True if a serializer of provided {@link TypeInfo type information} is present, false
     * otherwise.
     */
    public boolean hasSerializer(TypeInfo<?> typeInfo) {
        return this.findSerializer(typeInfo).isPresent()
                || (this != Serializers.GLOBAL && Serializers.GLOBAL.hasSerializer(typeInfo));
    }

    /**
     * Serialize the {@code value} to {@code key}.
     *
     * @param value Value to serialize.
     * @param key   Key to store serialized value.
     * @param <T>   Type of the value.
     */
    @SuppressWarnings("unchecked")
    public <T> void serialize(T value, Key<T> key) {
        this.findSerializer(key.getTypeInfo())
                .orElseThrow(() -> missingSerializer(key))
                .serialize(value, key, key.getTypeInfo(), key.getStorage(), this);
    }

    /**
     * Serialize the {@code value} to {@code key} and returns the plain value.
     *
     * @param value Value to serialize.
     * @param key   Key to store serialized value.
     * @param <T>   Type of the value.
     * @return Serialized value.
     */
    @SuppressWarnings("unchecked")
    public <T> T serializeAndGet(T value, Key<T> key) {
        this.serialize(value, key);
        return (T) key.getStorage().fetchValue(key);
    }

    /**
     * Serialize the {@code value} to {@code key}.
     *
     * @param value Value to serialize.
     * @param key   Key to store serialized value.
     */
    @SuppressWarnings("unchecked")
    public void serializeUnchecked(Object value, Key<?> key) {
        this.serializeUnchecked(value, key, key.getTypeInfo());
    }

    /**
     * Serialize the {@code value} to {@code key} and returns the plain value.
     *
     * @param value Value to serialize.
     * @param key   Key to store serialized value.
     * @return Serialized value.
     */
    @SuppressWarnings("unchecked")
    public Object serializeUncheckedAndGet(Object value, Key<?> key) {
        this.serializeUnchecked(value, key);
        return key.getStorage().fetchValue(key);
    }

    /**
     * Serialize the {@code value} to {@code key}.
     *
     * @param value Value to serialize.
     * @param key   Key to store serialized value.
     *              @param typeInfo Type of expected value.
     */
    @SuppressWarnings("unchecked")
    public void serializeUnchecked(Object value, Key<?> key, TypeInfo<?> typeInfo) {
        ((Serializer<Object>) this.findSerializer(typeInfo)
                .orElseThrow(() -> missingSerializer(key, typeInfo)))
                .serialize(value, (Key<Object>) key, typeInfo, key.getStorage(), this);
    }

    /**
     * Deserialize object from {@code key}.
     *
     * @param key Key to deserialize object.
     * @param <T> Type of value.
     * @return Object de-serialized from {@code key}.
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(Key<T> key) {
        return this.findSerializer(key.getTypeInfo())
                .orElseThrow(() -> missingSerializer(key))
                .deserialize(key, key.getTypeInfo(), key.getStorage(), this);
    }

    /**
     * Deserialize object from {@code key}.
     *
     * @param key      Key to deserialize object.
     * @param typeInfo Information of type to deserialize.
     * @return Object de-serialized from {@code key}.
     */
    @SuppressWarnings("unchecked")
    public Object deserializeUnchecked(Key<?> key, TypeInfo<?> typeInfo) {
        return ((Serializer<Object>) this.findSerializer(typeInfo)
                .orElseThrow(() -> missingSerializer(key, typeInfo)))
                .deserialize((Key<Object>) key, typeInfo, key.getStorage(), this);
    }

    /**
     * Register a serializer of values of type {@link T}.
     *
     * @param typeInfo   Type of value that {@code serializer} can serialize and deserialize.
     * @param serializer Serializer.
     * @param <T>        Type of the value.
     */
    public <T> void register(TypeInfo<T> typeInfo, Serializer<T> serializer) {
        this.getSerializerMap().put(typeInfo, serializer);
    }

    /**
     * Register a serializer of values of type {@code typeInfo}.
     *
     * @param typeInfo   Type of value that {@code serializer} can serialize and deserialize.
     * @param serializer Serializer.
     */
    public void registerUnchecked(TypeInfo typeInfo, Serializer serializer) {
        this.getSerializerMap().put(typeInfo, serializer);
    }

    /**
     * Register a serializer for all type info in {@code typeInfoIterable}.
     *
     * @param typeInfoIterable Iterable with all type info to register serializer to.
     * @param serializer       Serializer.
     */
    public void registerAll(Iterable<TypeInfo<?>> typeInfoIterable, Serializer serializer) {
        for (TypeInfo<?> info : typeInfoIterable) {
            this.registerUnchecked(info, serializer);
        }

    }

    /**
     * Find the serializer of type {@link T}.
     *
     * @param typeInfo Information about type {@link T}.
     * @param <T>      Type.
     * @return an Optional of the Serializer if found, or {@link Optional#empty()} otherwise.
     */
    @SuppressWarnings("unchecked")
    private <T> Optional<Serializer<T>> findSerializer(TypeInfo<T> typeInfo) {

        if (typeInfo == null)
            return Optional.empty();

        Class<? extends T> aClass = typeInfo.getTypeClass();

        if (aClass.getTypeParameters().length > 0) {
            TypeInfo<? extends T> info = TypeInfo.of(aClass);

            if (this.getSerializerMap().containsKey(info))
                return Optional.of((Serializer<T>) this.serializerMap.get(info));
        }

        if (this.getSerializerMap().containsKey(typeInfo))
            return Optional.of((Serializer<T>) this.serializerMap.get(typeInfo));

        for (Map.Entry<TypeInfo<?>, Serializer> entry : this.getSerializerMap().entrySet()) {
            if (entry.getKey().isAssignableFrom(typeInfo))
                return Optional.of((Serializer<T>) entry.getValue());
        }

        if (this != Serializers.GLOBAL)
            return Serializers.GLOBAL.findSerializer(typeInfo);

        return Optional.empty();
    }

    /**
     * Create exception of missing serializer of {@code key}.
     *
     * @param key Key which serializer is not present.
     * @return Serializer Exception.
     */
    private RuntimeException missingSerializer(Key<?> key) {
        throw new IllegalStateException("Missing serializer of key '" + key + "' of type '" + key.getTypeInfo() + "'!");
    }

    /**
     * Create exception of missing serializer of {@code key} of {@code type}.
     *
     * @param key  Key which serializer is not present.
     * @param type Type of missing serializer.
     * @return Serializer Exception.
     */
    private RuntimeException missingSerializer(Key<?> key, TypeInfo<?> type) {
        throw new IllegalStateException("Missing serializer of key '" + key + "' of type '" + type + "'!");
    }

    /**
     * Gets the serializer map.
     *
     * @return Serializer map.
     */
    private Map<TypeInfo<?>, Serializer> getSerializerMap() {
        return this.serializerMap;
    }

    static class BasicSerializer<T> implements Serializer<T> {

        @Override
        public void serialize(T value, Key<T> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            storage.pushValue(key, value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T deserialize(Key<T> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            return (T) storage.fetchValue(key);
        }
    }

    static class MapSerializer implements Serializer<Map> {

        @SuppressWarnings("unchecked")
        @Override
        public void serialize(Map value, Key<Map> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {

            Map<Object, Object> newMap = new HashMap<>();
            TypeInfo<?> keyType = typeInfo.getTypeParameter(0); // K
            TypeInfo<?> valueType = typeInfo.getTypeParameter(1); // V

            Map<String, Object> temp = new HashMap<>();
            Storage newStorage = Storage.createMapStorage(key, temp);

            for (Map.Entry<?, ?> o : ((Map<?, ?>) value).entrySet()) {
                Key<?> vKey = key.getAs(keyType, newStorage);
                Key<?> vValue = key.getAs(valueType, newStorage);

                Object serializedKey = serializers.serializeUncheckedAndGet(o.getKey(), vKey);
                temp.clear();

                Object serializedValue = serializers.serializeUncheckedAndGet(o.getValue(), vValue);
                temp.clear();

                newMap.put(serializedKey, serializedValue);
            }

            storage.pushValue(key, newMap);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map deserialize(Key<Map> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {

            Map<Object, Object> newMap = new HashMap<>();

            Object value = storage.fetchValue(key);

            if (value instanceof Map) {
                TypeInfo<?> keyType = typeInfo.getTypeParameter(0); // K
                TypeInfo<?> valueType = typeInfo.getTypeParameter(1); // V

                Storage newStorage = Storage.createMapStorage(key);

                for (Map.Entry<?, ?> o : ((Map<?, ?>) value).entrySet()) {
                    Key<?> vKey = key.getAs(keyType, newStorage);
                    Key<?> vValue = key.getAs(valueType, newStorage);

                    newStorage.pushValue(vKey, o.getKey());

                    Object deserializedKey = serializers.deserialize(vKey);

                    newStorage.pushValue(vKey, o.getValue()); // Emulated
                    Object desserializedValue = serializers.deserialize(vValue);

                    newMap.put(deserializedKey, desserializedValue);
                }

            }


            return newMap;
        }
    }

    /**
     * Serialize a list
     */
    static class ListSerializer implements Serializer<List> {

        @Override
        public void serialize(List value, Key<List> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            TypeInfo<?> elementType = typeInfo.getTypeParameter(0);

            Storage newStorage = Storage.createListStorage(key);

            for (Object o : value) {
                Key<?> newKey = key.getAs(elementType, newStorage);

                serializers.serializeUncheckedAndGet(o, newKey);
            }
        }

        @Override
        public List deserialize(Key<List> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {

            Object value = storage.fetchValue(key);

            List<Object> result = new ArrayList<>();

            if (value instanceof List) {
                TypeInfo<?> elementType = typeInfo.getTypeParameter(0);

                List list = (List) value;

                Storage listStorage = Storage.createListStorage(key);

                for (Object o : list) {
                    Key<?> newKey = key.getAs(elementType, listStorage);

                    listStorage.pushValue(newKey, o);

                    result.add(serializers.deserialize(newKey));
                }
            }

            return result;
        }
    }
}
