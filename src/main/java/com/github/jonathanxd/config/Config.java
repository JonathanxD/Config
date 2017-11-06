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
package com.github.jonathanxd.config;

import com.github.jonathanxd.config.backend.Backend;
import com.github.jonathanxd.config.serialize.Serializers;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration front-end. Values are stored in a {@link Storage.MapStorage MapStorage}, and later
 * saved and loaded from {@link Backend} through {@link #save()} and {@link #load()} in form of a
 * {@link Map}.
 *
 * Keys uses {@link Storage} to save and retrieve their values, also {@link Storage} is used by
 * serialization and deserialization of objects.
 */
public class Config {

    /**
     * Root Map type.
     */
    private static final TypeInfo<Map<String, Object>> TYPE = TypeInfo.builderOf(Map.class).of(String.class, Object.class).buildGeneric();

    /**
     * Root map
     */
    private final Map<String, Object> map = new HashMap<>();

    /**
     * Configuration storage
     */
    private final Storage storage = Storage.createMapStorage(this, this.map);

    /**
     * Serializers
     */
    private final Serializers serializers;

    /**
     * Root key.
     */
    private final Key<Map<String, Object>> root = new RootKey(this);

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
    public Key<Map<String, Object>> getRootKey() {
        return this.root;
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
        this.backend.save(Collections.unmodifiableMap(new HashMap<>(this.map)));
    }

    /**
     * Loads the configuration.
     */
    public void load() {
        this.map.clear();
        this.map.putAll(this.backend.load());
    }

    /**
     * Root configuration key, the {@link Storage storage} of this key is the same as {@link Config
     * enclosing config}, children uses the same {@link Storage storage} as this key, but sets the
     * entry of the map instead of the entire map.
     */
    class RootKey extends Key<Map<String, Object>> {

        /**
         * Creates a root key.
         *
         * @param config Configuration,
         */
        RootKey(Config config) {
            super(config, null, null, TYPE, Config.this.storage, null);
        }

        @Override
        public <V> Key<V> getKey(String name, TypeInfo<V> typeInfo) {
            return new Key<>(Config.this, this, name, typeInfo, this.getStorage(), null);
        }

        @Override
        public Map<String, Object> getValue() {
            return Config.this.map;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setValue(Map<String, Object> value) {
            Map<String, Object> map = this.getValue();
            map.clear();
            map.putAll(value);
        }
    }
}
