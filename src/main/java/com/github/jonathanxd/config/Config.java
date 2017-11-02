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
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration, this class stores values in a {@link Map} of {@link String} and {@link Object},
 * the {@link #root Root key} is a key that pushes and fetches all values from the {@link #map Root
 * map} using the {@link Storage.ConfigStorage Configuration Storage}.
 *
 * Methods {@link #put(String, Object)} and {@link #get(String)} must store and get values from
 * {@link #map Root map}, and not from the {@link #root Root key}.
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
     * Serializers
     */
    private final Serializers serializers = new Serializers();

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
        this.backend = backend;
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
     * Put the {@code key} and {@code value} into the {@link #map root map} (not the root key).
     *
     * @param key   Key
     * @param value Value.
     */
    public void put(String key, Object value) {
        this.map.put(key, value);
    }

    /**
     * Gets the value linked to {@code key} from {@link #map root map} (not the root key).
     *
     * @param key Key.
     * @return Value linked to {@code key}.
     */
    public Object get(String key) {
        return this.map.get(key);
    }

    /**
     * Saves the configuration.
     */
    public void save() {
        this.backend.doAction(this.map, Backend.Action.SAVE);
    }

    /**
     * Loads the configuration.
     */
    public void load() {
        this.backend.doAction(this.map, Backend.Action.LOAD);
    }

    /**
     * {@link #root Root key} instance type, this class uses a different strategy to create a child
     * {@link Key}, the {@link Key} implementation creates a new {@link Storage.ObjectStorage} to
     * store the value, and this passes the {@link Storage.ConfigStorage Configuration Storage} to
     * child {@link Key}.
     */
    class RootKey extends Key<Map<String, Object>> {

        /**
         * Creates a root key.
         *
         * @param config Configuration,
         */
        RootKey(Config config) {
            super(config, null, null, TYPE, new Storage.ConfigStorage(Config.this));
        }

        @Override
        public <V> Key<V> getKey(String name, TypeInfo<V> typeInfo) {
            return new Key<>(Config.this, this, name, typeInfo, this.getStorage());
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
