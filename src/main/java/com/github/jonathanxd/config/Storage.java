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
package com.github.jonathanxd.config;

import com.github.jonathanxd.config.backend.Backend;
import com.github.jonathanxd.config.serialize.Serializers;
import com.github.jonathanxd.iutils.container.MutableContainer;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.Map;

/**
 * Value storage class, this class serialize and pushes the value, fetches and deserialize the
 * value, different implements pushes and fetches from different sources.
 */
public abstract class Storage {

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
                throw new IllegalArgumentException("Invalid storage of key '" + key + "'. Type '" +
                        typeInfo + "' is not supported by backend '" + backend + "'");

            this.pushValue(key, value);
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
            return this.fetchValue(key);
        }
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
     * Pushes the value.
     *
     * This method doesn't serialize the value.
     *
     * @param key   Key.
     * @param value Value.
     */
    public abstract void pushValue(Key<?> key, Object value);

    /**
     * Fetches the value linked to {@code key}.
     *
     * This doesn't deserialize the value.
     *
     * @param key Key to fetch value.
     * @return Value linked to key.
     */
    public abstract Object fetchValue(Key<?> key);

    /**
     * Gets the configuration.
     *
     * @return Configuration.
     */
    public abstract Config getConfig();

    /**
     * Configuration storage, this method delegates the push and fetch operation to {@link
     * Config#put(String, Object)} and {@link Config#get(String)}.
     */
    public static class ConfigStorage extends Storage {

        /**
         * Configuration.
         */
        private final Config config;

        /**
         * Creates a configuration storage.
         *
         * @param config Configuration to delegate push and fetch operations.
         */
        public ConfigStorage(Config config) {
            this.config = config;
        }

        @Override
        public void pushValue(Key<?> key, Object value) {
            this.config.put(key.getName(), value);
        }

        @Override
        public Object fetchValue(Key<?> key) {
            return this.config.get(key.getName());
        }

        @Override
        public Config getConfig() {
            return this.config;
        }
    }

    /**
     * Storage that delegates the push and fetch operation to {@link Map#put(Object, Object)} and
     * {@link Map#get(Object)}.
     */
    public static class MapStorage extends Storage {
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
        public MapStorage(Config config, Map<String, Object> map) {
            this.config = config;
            this.map = map;
        }

        @Override
        public void pushValue(Key<?> key, Object value) {
            this.map.put(key.getName(), value);
        }

        @Override
        public Object fetchValue(Key<?> key) {
            return this.map.get(key.getName());
        }

        @Override
        public Config getConfig() {
            return this.config;
        }

        public Map<String, Object> getMap() {
            return this.map;
        }
    }

    /**
     * Storage that push and fetch values from a {@link MutableContainer container}.
     */
    public static class ObjectStorage extends Storage {
        /**
         * Configuration
         */
        private final Config config;

        /**
         * Value container.
         */
        private final MutableContainer<Object> container;

        /**
         * Creates a Object storage.
         *
         * @param config Configuration.
         * @param o      Object to store (aka default value).
         */
        public ObjectStorage(Config config, Object o) {
            this.config = config;
            this.container = new MutableContainer<>(o);
        }

        @Override
        public void pushValue(Key<?> key, Object value) {
            this.container.set(value);
        }

        @Override
        public Object fetchValue(Key<?> key) {
            return this.container.get();
        }

        @Override
        public Config getConfig() {
            return this.config;
        }
    }

    /**
     * Storage that push and fetch values from a {@link MutableContainer container}, this storage
     * also returns null if the value is a empty map.
     */
    public static class CommonStorage extends Storage {
        /**
         * Configuration
         */
        private final Config config;

        /**
         * Value container.
         */
        private final MutableContainer<Object> container;

        /**
         * Creates a Object storage.
         *
         * @param config Configuration.
         * @param o      Initial map.
         */
        public CommonStorage(Config config, Map<String, Object> o) {
            this.config = config;
            this.container = new MutableContainer<>(o);
        }

        @Override
        public void pushValue(Key<?> key, Object value) {
            this.container.set(value);
        }

        @Override
        public Object fetchValue(Key<?> key) {
            Object o = this.container.get();

            if (o instanceof Map<?, ?>)
                if (((Map) o).isEmpty())
                    return null;

            return o;
        }

        @Override
        public Config getConfig() {
            return this.config;
        }
    }
}
