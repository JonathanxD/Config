/*
 *      Config - Configuration API. <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2017 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/ & https://github.com/TheRealBuggy/) <jonathan.scripter@programmer.net>
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

import com.github.jonathanxd.config.AbstractKey;
import com.github.jonathanxd.config.Key;
import com.github.jonathanxd.config.Storage;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Serializer manager
 */
public final class Serializers {

    /**
     * Default serializers
     */
    public static final Serializers GLOBAL = new Serializers();

    static {
        Serializers.GLOBAL.register(TypeInfo.of(List.class), new ListSerializer());
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
                .serialize(value, key, key.getStorage());
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
     * Serialize the {@code value} to {@code key}.
     *
     * @param value Value to serialize.
     * @param key   Key to store serialized value.
     */
    @SuppressWarnings("unchecked")
    public void serializeUnchecked(Object value, Key<?> key, TypeInfo<?> typeInfo) {
        ((Serializer<Object>) this.findSerializer(typeInfo)
                .orElseThrow(() -> missingSerializer(key, typeInfo)))
                .serialize(value, (Key<Object>) key, key.getStorage());
    }

    /**
     * Deserialize object from {@code key}.
     *
     * @param key Key to deserialize object.
     * @param <T> Type of value.
     * @return Object deserialized from {@code key}.
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(Key<T> key) {
        return this.findSerializer(key.getTypeInfo())
                .orElseThrow(() -> missingSerializer(key))
                .deserialize(key, key.getStorage());
    }

    /**
     * Deserialize object from {@code key}.
     *
     * @param key      Key to deserialize object.
     * @param typeInfo Information of type to deserialize.
     * @return Object deserialized from {@code key}.
     */
    @SuppressWarnings("unchecked")
    public <T> Object deserializeUnchecked(Key<?> key, TypeInfo<?> typeInfo) {
        return this.findSerializer((TypeInfo<T>) typeInfo)
                .orElseThrow(() -> missingSerializer(key, typeInfo))
                .deserialize((Key<T>) key, key.getStorage());
    }

    /**
     * Register a serializer of values of type {@link T}.
     *
     * @param typeInfo   Type information.
     * @param serializer Serializer.
     * @param <T>        Type of the value.
     */
    public <T> void register(TypeInfo<T> typeInfo, Serializer<T> serializer) {
        this.getSerializerMap().put(typeInfo, serializer);
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

    /**
     * Serialize a list
     */
    static class ListSerializer implements Serializer<List> {

        @Override
        public void serialize(List value, Key<List> key, Storage storage) {
            TypeInfo[] related = key.getTypeInfo().getRelated();

            TypeInfo<?> elementType;

            if (related.length > 0) {
                elementType = related[0];
            } else {
                elementType = TypeInfo.of(Object.class);
            }

            List<Map<String, Object>> list = new ArrayList<>();


            Map<String, Object> map = new HashMap<>();

            for (Object o : value) {
                Storage newStorage = new Storage.MapStorage(key.getConfig(), map);

                Key<?> newKey = new AbstractKey.Impl<>(key.getConfig(), key.getParent(), key.getName(), elementType, newStorage);
                ((Key<Object>) newKey).setValue(o);

            }

            list.add(map);

            storage.pushValue(key, list);

        }

        @Override
        public List deserialize(Key<List> key, Storage storage) {

            Object value = storage.fetchValue(key);

            List result = new ArrayList();

            if (value instanceof List) {
                TypeInfo[] related = key.getTypeInfo().getRelated();

                TypeInfo<?> elementType;

                if (related.length > 0) {
                    elementType = related[0];
                } else {
                    elementType = TypeInfo.of(Object.class);
                }

                List list = (List) value;

                Object at0;
                if (list.size() == 1 && (at0 = list.get(0)) instanceof Map<?, ?>) {
                    Map<String, Object> map = (Map<String, Object>) at0;


                    //

                    if (!map.isEmpty()) {
                        Storage newStorage = new Storage.MapStorage(key.getConfig(), map);

                        Key<?> newKey = new AbstractKey.Impl<>(key.getConfig(), key.getParent(), key.getName(), elementType, newStorage);

                        Object deserialize = key.getConfig().getSerializers().deserialize(newKey);
                        result.add(deserialize);
                    }

                }
            }

            return result;
        }
    }
}
