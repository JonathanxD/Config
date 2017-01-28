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

import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * A config key, the {@link Key key} fetches and pushes the value to the {@link Storage storage} and
 * create child keys linked to another {@link Storage storage} that fetches and pushes values to
 * current {@link Storage value storage}.
 *
 * @param <T> Value type.
 * @see Storage
 */
public class Key<T> {

    private final Config config;
    private final Key<?> parent;
    private final String name;
    private final TypeInfo<T> typeInfo;
    private final Storage storage;

    Key(Config config, Key<?> parent, String name, TypeInfo<T> typeInfo, Storage storage) {
        this.config = config;
        this.parent = parent;
        this.name = name;
        this.typeInfo = typeInfo;
        this.storage = storage;
    }

    /**
     * Creates a new {@link Key key} of type {@link V} linked to a {@link Storage storage}, this
     * child key overwrite the current value.
     *
     * @param name Name of the key.
     * @param type Type of the value.
     * @param <V>  Value type.
     * @return A new {@link Key key} linked to a new {@link Storage storage} that fetches and pushes
     * values to {@link Storage storage} of {@code this} {@link Key key}.
     */
    public <V> Key<V> getKey(String name, Class<V> type) {
        return this.getKey(name, TypeInfo.aEnd(type));
    }

    /**
     * Creates a new {@link Key key} of type {@link V} linked to a {@link Storage storage}, this
     * child key overwrite the current value.
     *
     * @param name     Name of the key.
     * @param typeInfo Type information about the value type.
     * @param <V>      Value type.
     * @return A new {@link Key key} linked to a new {@link Storage storage} that fetches and pushes
     * values to {@link Storage storage} of {@code this} {@link Key key}.
     */
    @SuppressWarnings("unchecked")
    public <V> Key<V> getKey(String name, TypeInfo<V> typeInfo) {
        Map<String, Object> map;

        Object o = this.getStorage().fetchValue(this);

        if(o instanceof Map<?, ?>)
            map = (Map<String, Object>) o;
        else
            map = new HashMap<>();

        Storage storage = new Storage.MapStorage(this.config, map);

        this.getStorage().pushValue(this, map);

        return new Key<>(this.getConfig(), this, name, typeInfo, storage);
    }

    /**
     * Fetches value from {@link Storage value storage}.
     *
     * @return Value stored in {@link Storage value storage}.
     */
    @SuppressWarnings("unchecked")
    public T getValue() {
        return (T) this.getStorage().get(this);
    }

    /**
     * Pushes the {@code value} to {@link Storage value storage}.
     *
     * @param value Value to push to {@link Storage value storage}.
     */
    public void setValue(T value) {
        this.getStorage().store(this, value);
    }

    /**
     * Gets the {@link Config config}.
     *
     * @return {@link Config}.
     */
    public Config getConfig() {
        return this.config;
    }

    /**
     * Gets the {@link Key parent key}.
     *
     * @return {@link Key Parent key}.
     */
    public Key<?> getParent() {
        return this.parent;
    }

    /**
     * Gets the key name.
     *
     * @return Key name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the {@link TypeInfo value type info}.
     *
     * @return {@link TypeInfo Value type info}.
     */
    public TypeInfo<T> getTypeInfo() {
        return this.typeInfo;
    }

    /**
     * Gets the {@link Storage value storage}.
     *
     * @return {@link Storage Value storage}.
     */
    public Storage getStorage() {
        return this.storage;
    }
}
