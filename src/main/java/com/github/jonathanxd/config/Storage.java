/*
 *      Config - Configuration library <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2021 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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
package com.github.jonathanxd.config;

import com.github.jonathanxd.config.backend.Backend;
import com.github.jonathanxd.config.serialize.Serializers;
import com.github.jonathanxd.iutils.function.stream.BiStreams;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Value storage class, this class serialize and pushes the value, fetches and deserialize the
 * value, different implements pushes and fetches from different sources.
 *
 * Storage does not implement the key logic, so storing a key means that it will directly be stored
 * in the Storage without caring about the path of the key.
 */
public abstract class Storage {

    /**
     * Creates a map storage that stores values in {@code map}.
     *
     * This method calls {@link #createMapStorage(Config, Map)} with {@link Key#getConfig() config}
     * of {@code key}.
     *
     * Used in serialization process.
     *
     * @param key Key to get {@link Config configuration}.
     * @param map Map to store values.
     * @return Map Storage that stores values in {@code map}.
     */
    public static Storage createMapStorage(Key<?> key, Map<String, Object> map) {
        return Storage.createMapStorage(key.getConfig(), map);
    }

    /**
     * Creates a map storage that stores values in a {@link LinkedHashMap}.
     *
     * This method calls {@link #createMapStorage(Config, Map)} with {@link Key#getConfig() config}
     * of {@code key}.
     *
     * Used in serialization process.
     *
     * @param key Key to get {@link Config configuration}.
     * @return Map Storage that stores values in {@code map}.
     */
    public static Storage createMapStorage(Key<?> key) {
        return Storage.createMapStorage(key.getConfig(), new LinkedHashMap<>());
    }

    /**
     * Creates a map storage that stores values in {@code map}.
     *
     * Used in serialization process.
     *
     * @param config Configuration
     * @param map    Map to store values.
     * @return Map Storage that stores values in {@code map}.
     */
    public static Storage createMapStorage(Config config, Map<String, Object> map) {
        return new MapStorage(config, map);
    }

    /**
     * Creates an inner storage, an inner storage stores the value in a new section of the {@code
     * outerStorage} using {@code key}, but without linking directly to the object that this storage
     * stores to.
     *
     * An inner storage is like a storage with a path to where the value should be stored, example,
     * suppose that you want to store value {@code B} in path {@code a.c}, and every path entry is a
     * map entry, so {@code a} is a map entry and {@code c} is a map entry inside map entry {@code
     * a}, the inner storage is a link to the entry {@code c}, and outer storage is the link to
     * entry {@code a}, but neither the inner nor outer, references to the storage, they references
     * to the {@code path} {@code a.c}. The path in this case, is the {@code key}.
     *
     * @param key          Key to be used to store the section in {@code outerStorage}.
     * @param outerStorage Outer storage.
     * @return An inner storage that stores value in a new section.
     */
    public static Storage createInnerStorage(Key<?> key, Storage outerStorage) {
        return new InnerStorage(key, outerStorage);
    }

    /**
     * In this case, {@code outerStorage} is the {@code key} {@link Key#getStorage() storage}.
     *
     * @param key Key to be used to store the section in {@code outerStorage}.
     * @return An inner storage that stores value in a new section.
     * @see #createInnerStorage(Key, Storage)
     */
    public static Storage createInnerStorage(Key<?> key) {
        return new InnerStorage(key, key.getStorage());
    }

    /**
     * Creates a map storage that stores values in a {@link Map} stored in {@link Key#getStorage()
     * storage} of {@code key}.
     *
     * @param key Key to be used to store map with values.
     * @return Map storage that stores values in a {@link Map} stored in {@link Key#getStorage()
     * storage} of {@code key}.
     */
    public static Storage createAutoMapStorage(Key<?> key) {
        return new AutoPushMapStorage(key);
    }

    /**
     * Creates a list storage that stores values in a map and pushes them as a list. Every element
     * key should be unique.
     *
     * Used in serialization process.
     *
     * @param key Root key.
     * @return List storage that store values in {@code list}.
     */
    public static ListStorage createListStorage(Key<?> key) {
        return new ListStorage(key);
    }

    /**
     * Stores the value linked to {@code key}.
     *
     * Serialization is applied if a serializer of the provided {@code typeInfo} is registered in
     * the {@link #getConfig() config} or in {@link Serializers#GLOBAL Global serializers}.
     *
     * @param key      Key.
     * @param typeInfo Type information to find serializer.
     * @param value    Value to store.
     */
    @SuppressWarnings("unchecked")
    public final void store(Key<?> key, TypeInfo<?> typeInfo, Object value) {
        Serializers serializers = this.getConfig().getSerializers();

        boolean b = serializers.hasSerializer(typeInfo);

        if (b) {
            serializers.serializeUnchecked(value, key, typeInfo);
        } else {
            Backend backend = key.getConfig().getBackend();

            if (!backend.supports(typeInfo))
                throw new UnsupportedValueTypeException("Cannot store key '" + key + "': Type '" +
                        typeInfo + "' is not supported by backend '" + backend + "'. Register a serializer for this data type.");

            // The check is really needed?
            this.pushValue(key, this.checkType(value, typeInfo));
            //this.pushValue(key, value);
        }
    }

    /**
     * Fetches the value linked to {@code key}.
     *
     * Deserialization is applied if a serializer of the provided {@code typeInfo} is registered in
     * the {@link #getConfig() config} or in {@link Serializers#GLOBAL Global serializers}.
     *
     * @param key      Key.
     * @param typeInfo Type info.
     * @return Value linked to {@code key}.
     */
    public final Object get(Key<?> key, TypeInfo<?> typeInfo) {
        Serializers serializers = this.getConfig().getSerializers();

        boolean b = serializers.hasSerializer(typeInfo);

        if (b) {
            return serializers.deserializeUnchecked(key, typeInfo);
        } else {
            Backend backend = key.getConfig().getBackend();

            if (!backend.supports(typeInfo))
                throw new UnsupportedValueTypeException("Cannot fetch key '" + key + "': Type '" +
                        typeInfo + "' is not supported by backend '" + backend + "'. Register a serializer for this data type.");

            return this.checkType(this.fetchValue(key), typeInfo);
        }
    }

    /**
     * Fetches the value linked to {@code key}.
     *
     * Deserialization is applied if a serializer of the provided {@code typeInfo} is registered in
     * the {@link #getConfig() config} or in {@link Serializers#GLOBAL Global serializers}.
     *
     * @param key      Key.
     * @param typeInfo Type of value.
     * @param <T>      Type of expected value.
     * @return Value linked to {@code key}.
     */
    @SuppressWarnings("unchecked")
    public final <T> T getAs(Key<?> key, TypeInfo<T> typeInfo) {
        return (T) this.get(key, typeInfo);
    }

    /**
     * Fetches the value linked to {@code key}.
     *
     * Deserialization is applied if a serializer of the provided {@code typeInfo} is registered in
     * the {@link #getConfig() config} or in {@link Serializers#GLOBAL Global serializers}.
     *
     * @param key  Key.
     * @param type Type of value.
     * @param <T>  Type of expected value.
     * @return Value linked to {@code key}.
     */
    @SuppressWarnings("unchecked")
    public final <T> T getAs(Key<?> key, Class<T> type) {
        return this.getAs(key, TypeInfo.of(type));
    }

    /**
     * Stores the value linked to {@code key}.
     *
     * Serialization is applied if a serializer of {@link Key#getTypeInfo() key type} is registered
     * in the {@link #getConfig() config} or in {@link Serializers#GLOBAL Global serializers}.
     *
     * @param key   Key.
     * @param value Value to store.
     */
    @SuppressWarnings("unchecked")
    public final void store(Key<?> key, Object value) {
        this.store(key, key.getTypeInfo(), value);
    }

    /**
     * Fetches the value linked to {@code key}.
     *
     * Deserialization is applied if a serializer of {@link Key#getTypeInfo() key type} is
     * registered in the {@link #getConfig() config} or in {@link Serializers#GLOBAL Global
     * serializers}.
     *
     * @param key Key.
     * @return Value linked to {@code key}.
     */
    public final Object get(Key<?> key) {
        return this.get(key, key.getTypeInfo());
    }

    /**
     * Pushes the value directly to storage (no serialization is made).
     *
     * @param key   Key.
     * @param value Value.
     */
    public abstract void pushValue(Key<?> key, Object value);

    /**
     * Fetches the value linked to {@code key} directly from storage (no deserialization is made).
     *
     * @param key Key to fetch value.
     * @return Value linked to key.
     */
    public abstract Object fetchValue(Key<?> key);

    /**
     * Returns {@code true} if {@code key} and a value associated to {@code key} exists in this
     * storage.
     *
     * @param key Key to check.
     * @return {@code true} if {@code key} and a value associated to {@code key} exists in this
     * storage.
     */
    public abstract boolean exists(Key<?> key);

    /**
     * Gets the configuration.
     *
     * @return Configuration.
     */
    public abstract Config getConfig();

    private <T> T checkType(T value, TypeInfo<?> typeInfo) {
        if (!typeInfo.getTypeClass().isInstance(value))
            throw new IllegalStateException("Value of type '" + typeInfo + "' was expected, but value '" + value + "' was found");

        return value;
    }

    /**
     * Storage that delegates the push and fetch operation to {@link Map#put(Object, Object)} and
     * {@link Map#get(Object)}.
     */
    static class MapStorage extends Storage {
        /**
         * Configuration.
         */
        private final Config config;

        /**
         * Map to delegate push and fetch operations.
         */
        private final Map<String, Object> map;

        /**
         * Creates a storage that delegates push and fetch operations to {@code map}.
         *
         * @param config Configuration.
         * @param map    Map to delegate push and fetch operations.
         */
        MapStorage(Config config, Map<String, Object> map) {
            this.config = config;
            this.map = map;
        }

        @Override
        public void pushValue(Key<?> key, Object value) {
            this.map.put(key.getName(), value);
        }

        @Override
        public Object fetchValue(Key<?> key) {
            if (!this.exists(key))
                throw new KeyNotFoundException(key);

            return this.map.get(key.getName());
        }

        @Override
        public Config getConfig() {
            return this.config;
        }

        public Map<String, Object> getMap() {
            return this.map;
        }

        @Override
        public boolean exists(Key<?> key) {
            return this.getMap().containsKey(key.getName());
        }

        @Override
        public String toString() {
            return "MapStorage[config='" + this.config + "', map=" + this.getMap() + "]";
        }
    }

    /**
     * Storage that references to a section in an outer storage.
     *
     * @see Storage#createInnerStorage(Key, Storage)
     */
    static class InnerStorage extends Storage {

        /**
         * Path key
         */
        private final Key<?> key;

        /**
         * Outer storage.
         */
        private final Storage outer;

        /**
         * Creates a storage that references to a section in {@code outer} storage.
         *
         * @param key   Key to be used as path.
         * @param outer Outer storage.
         * @see Storage#createInnerStorage(Key, Storage)
         */
        InnerStorage(Key<?> key, Storage outer) {
            this.key = key;
            this.outer = outer;
            this.createIfNeeded();
        }

        private void createIfNeeded() {
            if (!this.outer.exists(this.key)) {
                LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();
                this.outer.pushValue(this.key, newMap);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void pushValue(Key<?> key, Object value) {
            this.getMap().put(key.getName(), value);
        }

        @Override
        public Object fetchValue(Key<?> key) {
            if (!this.exists(key))
                throw new KeyNotFoundException(key);

            return this.getMap().get(key.getName());
        }

        @Override
        public Config getConfig() {
            return this.key.getConfig();
        }

        @SuppressWarnings("unchecked")
        public Map<String, Object> getMap() {
            this.createIfNeeded();
            return (Map<String, Object>) this.outer.fetchValue(this.key);
        }

        @Override
        public boolean exists(Key<?> key) {
            return this.getMap().containsKey(key.getName());
        }

        @Override
        public String toString() {
            return "InnerStorage[key='" + KeyUtil.getPathAsString(this.key) + "', outer='" + this.outer + "', map=" +
                    (this.outer.exists(this.key) ? this.getMap() : "absent") +
                    "]";
        }
    }

    /**
     * Storage that stores/fetches values in/from a {@link Map} stored as values of {@link #key}.
     */
    static class AutoPushMapStorage extends Storage {
        /**
         * Configuration.
         */
        private final Config config;

        /**
         * Key.
         */
        private final Key<?> key;

        /**
         * Creates a storage that stores/fetches values in/from a {@link Map} stored as values of
         * {@link #key}.
         *
         * @param key Root key.
         */
        AutoPushMapStorage(Key<?> key) {
            this.key = key;
            this.config = this.key.getConfig();
            this.key.getStorage().pushValue(key, new LinkedHashMap<>());
        }

        @SuppressWarnings("unchecked")
        private Map<String, Object> getMap() {
            Storage storage = this.key.getStorage();

            if (!storage.exists(this.key))
                storage.pushValue(this.key, new LinkedHashMap<>());

            return (Map<String, Object>) storage.fetchValue(this.key);
        }

        @Override
        public void pushValue(Key<?> key, Object value) {
            this.getMap().put(key.getName(), value);
        }

        @Override
        public boolean exists(Key<?> key) {
            return this.getMap().containsKey(key.getName());
        }

        @Override
        public Object fetchValue(Key<?> key) {
            if (!this.exists(key))
                throw new KeyNotFoundException(key);

            return this.getMap().get(key.getName());
        }

        @Override
        public Config getConfig() {
            return this.config;
        }

        @Override
        public String toString() {
            return "AutoPushStorage[key='" + KeyUtil.getPathAsString(this.key) + "']";
        }
    }

    /**
     * Storage that stores values in a key-value pair in a map, and stores it in key storage as a
     * list of values only. The keys used to fetch and store values should be unique for each list
     * entry.
     *
     * Values is automatically pushed to {@link #key key} {@link Key#getStorage() storage} every
     * time an element is pushed to this storage.
     *
     * Fetch operation returns a value from local {@link #map cache map} instead of directly from
     * the {@code key} {@link Key#getStorage() storage}, this happens because the association of
     * {@link Key key}-list element is only present in {@link ListStorage}.
     */
    public static final class ListStorage extends Storage {
        /**
         * Configuration
         */
        private final Config config;

        /**
         * Key.
         */
        private final Key<?> key;

        /**
         * Key-value pair.
         */
        private final Map<String, Object> map = new LinkedHashMap<>();
        private final Supplier<List<Object>> valuesListSupplier =
                () -> BiStreams.mapStream(this.map).collectValue(Collectors.toList());

        /**
         * Creates a Object storage.
         *
         * @param key Root key.
         */
        public ListStorage(Key<?> key) {
            this.key = key;
            this.config = this.getKey().getConfig();
            this.createIfNeeded();
        }

        private void createIfNeeded() {
            if (!this.key.getStorage().exists(this.key))
                this.key.getStorage().pushValue(this.key, new ArrayList<>());
        }

        @SuppressWarnings("unchecked")
        private List<Object> getList() {
            this.createIfNeeded();
            Storage storage = this.key.getStorage();
            return (List<Object>) storage.fetchValue(this.key);
        }

        @Override
        public void pushValue(Key<?> key, Object value) {
            this.map.put(key.getName(), value);

            this.store();
        }

        @Override
        public Object fetchValue(Key<?> key) {
            if (!this.exists(key))
                throw new KeyNotFoundException(key);

            return this.map.get(key.getName());
        }

        @Override
        public boolean exists(Key<?> key) {
            return this.map.containsKey(key.getName());
        }

        private Key<?> getKey() {
            return this.key;
        }

        private void store() {
            this.key.getStorage().pushValue(this.key, this.valuesListSupplier.get());
        }

        @Override
        public Config getConfig() {
            return this.config;
        }

        @Override
        public String toString() {
            return "ListStorage[key='" + KeyUtil.getPathAsString(this.key) + "']";
        }
    }
}
