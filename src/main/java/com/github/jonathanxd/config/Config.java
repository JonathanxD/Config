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
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration front-end. Values are stored in this {@link Storage ConfigStorage}, and later saved
 * and loaded from {@link Backend} through {@link #save()} and {@link #load()} in form of a {@link
 * Map}.
 *
 * Keys uses {@link Storage} to save and retrieve their values, also {@link Storage} is used by
 * serialization and deserialization of objects.
 */
public class Config extends Storage {

    /**
     * Root Map type.
     */
    private static final TypeInfo<Map<Object, Object>> TYPE = TypeInfo.builderOf(Map.class).of(Object.class, Object.class).buildGeneric();

    /**
     * Root map
     */
    private final Map<Object, Object> map = new LinkedHashMap<>();

    /**
     * Serializers
     */
    private final Serializers serializers;

    /**
     * Root key.
     */
    private final Key<Map<Object, Object>> root = new RootKey<>(this, TYPE);

    /**
     * Backend saver and loader.
     */
    private final Backend backend;

    /**
     * Creates a configuration backing save and load operations to a {@link Backend}.
     *
     * @param backend Backend to save and load configuration.
     */
    public Config(Backend backend) {
        this(backend, new Serializers());
    }

    /**
     * Creates a configuration backing save and load operations to a {@link Backend}.
     *
     * @param backend     Backend to save and load configuration.
     * @param serializers Serializers instance to use to manage serializers.
     */
    public Config(Backend backend, Serializers serializers) {
        this.backend = backend;
        this.serializers = serializers;
        this.getBackend().registerSerializers(this.getSerializers());
    }

    /**
     * Gets the config backend.
     *
     * @return Config backend.
     */
    public Backend getBackend() {
        return this.backend;
    }

    /**
     * Gets the root key.
     *
     * @return Root key.
     */
    public Key<?> getRootKey() {
        return this.getBackend().resolveRoot(this.root);
    }

    /**
     * Gets the {@link Serializers serializer manager} instance.
     *
     * @return {@link Serializers Serializer manager} instance.
     */
    public Serializers getSerializers() {
        return this.serializers;
    }

    /**
     * Saves the configuration.
     */
    public void save() {
        this.backend.save(Collections.unmodifiableMap(new LinkedHashMap<>(this.map)));
    }

    /**
     * Loads the configuration.
     */
    public void load() {
        this.map.clear();
        this.map.putAll(this.backend.load());
    }

    // Storage


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
    public boolean exists(Key<?> key) {
        return this.map.containsKey(key.getName());
    }

    @Override
    public Config getConfig() {
        return this;
    }

    @Override
    public String toString() {
        return "Config[entries=" + this.map.size() + "]";
    }

    /**
     * Root configuration key, the {@link Storage storage} of this key is the same as {@link Config
     * enclosing config}, children uses the same {@link Storage storage} as this key, but sets the
     * entry of the map instead of the entire map.
     */
    class RootKey<X> extends Key<X> {

        /**
         * Creates a root key.
         *
         * @param config Configuration,
         */
        RootKey(Config config, TypeInfo<X> type) {
            super(config, null, "root", type, Config.this, null);
        }

        /**
         * Creates a root key.
         *
         * @param config Configuration,
         */
        RootKey(Config config, TypeInfo<X> type, Storage storage) {
            super(config, null, "root", type, storage, null);
        }

        @Override
        public <V> Key<V> getKey(String name, TypeInfo<V> typeInfo) {
            return new Key<>(Config.this, this, name, typeInfo, this.getStorage(), null);
        }

        @Override
        public <V> Key<V> getAs(TypeInfo<V> type) {
            return new RootKey<>(super.getConfig(), type);
        }

        @Override
        public <V> Key<V> getAs(TypeInfo<V> type, Storage storage) {
            return new RootKey<>(super.getConfig(), type, storage);
        }

        /**
         * Calling this in the {@link RootKey} has the same effect as calling {@link #getKey(String, TypeInfo)}.
         *
         * @inheritDoc
         */
        @Override
        public <V> Key<V> getAs(String name, Class<V> type) {
            return this.getKey(name, type);
        }

        /**
         * Calling this in the {@link RootKey} has the same effect as calling {@link #getKey(String, TypeInfo)}.
         *
         * @inheritDoc
         */
        @Override
        public <V> Key<V> getAs(String name, TypeInfo<V> type) {
            return this.getKey(name, type);
        }

        /**
         * Calling this in the {@link RootKey} has the same effect as calling {@link #getKey(String, TypeInfo)} and
         * then {@link #getAs(String, TypeInfo, Storage)} against the generated key.
         *
         * @inheritDoc
         */
        @Override
        public <V> Key<V> getAs(String name, Class<V> type, Storage storage) {
            return this.getKey(name, type).getAs(name, type, storage);
        }

        /**
         * Calling this in the {@link RootKey} has the same effect as calling {@link #getKey(String, TypeInfo)} and
         * then {@link #getAs(String, TypeInfo, Storage)} against the generated key.
         *
         * @inheritDoc
         */
        @Override
        public <V> Key<V> getAs(String name, TypeInfo<V> type, Storage storage) {
            return this.getKey(name, type).getAs(name, type, storage);
        }

        @SuppressWarnings("unchecked")
        @Override
        public X getValue() {
            if (super.getTypeInfo().equals(TYPE)) {
                return (X) Config.this.map;
            } else {
                return (X) super.getStorage().get(this);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setValue(X value) {
            if (super.getTypeInfo().equals(TYPE)) {
                Map<Object, Object> map = (Map<Object, Object>) this.getValue();
                map.clear();
                map.putAll((Map<?, ?>) value);
            } else {
                super.getStorage().store(this, value);
            }
        }
    }
}
