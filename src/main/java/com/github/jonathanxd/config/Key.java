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

import com.github.jonathanxd.iutils.string.ToStringHelper;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration key, the key is an accessor of configuration values, the {@link T type} provided to
 * key is the type that will be used to serialize and de-serialize values. To get a parent key use
 * the {@link #getKey(String, TypeInfo)} or {@link #getKey(String, Class)} method, factories method
 * are mostly used in serialization to simulate the same key with different type and that stores
 * values in alternative {@link Storage}. Those keys that are created by factory methods and not by
 * {@link #getKey(String, TypeInfo) getKey} methods are called {@code Emulated keys}.
 *
 * Emulated key is a key that represents something that cannot be expressed in configuration,
 * example, a key to an entry of an element of list, or a key to an entry of key or value of a
 * {@link Map map}. To determine whether a key is emulated or not, use {@link Key#isEmulated()}. And
 * to get which key was used to create emulated key, use {@link #getOriginalKey()}.
 *
 * {@link Key} fetches and pushes the value to the {@link Storage storage} and create child keys
 * linked to another {@link Storage storage} that fetches and pushes values to current {@link
 * Storage value storage}.
 *
 * Keys carry your own {@link T type}, the type does not really reflect the type of stored values,
 * the type reflects expected value type. This carried {@link T type} is used to serialize and
 * deserialize value. The deserialization/serialization is called internally, during value fetching
 * and pushing. Query and set methods can throw exceptions if there is no serialize for key type and
 * the key type is not supported by {@link com.github.jonathanxd.config.backend.Backend}.
 *
 * Implementations must not implement {@link #hashCode()} and {@link #equals(Object)}, implementing
 * them may lead to unpredictable behavior.
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
    private final Key<?> original;

    Key(Config config, Key<?> parent, String name, TypeInfo<T> typeInfo, Storage storage, Key<?> original) {
        this.config = config;
        this.parent = parent;
        this.name = name;
        this.typeInfo = typeInfo;
        this.storage = storage;
        this.original = Key.getOriginal(original); // Ensures the original field never references an emulated key.
    }

    /**
     * Creates a new configuration key for {@code config} with {@code name} that stores/fetches
     * values in/from {@code storage}.
     *
     * @param config   Configuration.
     * @param parent   Parent key.
     * @param name     Name of the key entry in {@code storage}.
     * @param type     Type of key value.
     * @param storage  Storage where key and key value resides.
     * @param original Original key that this emulated key was based, or {@code null} if this is not
     *                 an emulated key.
     * @param <T>      Type of the key.
     * @return Configuration key that stores/fetches values in/from {@code storage}.
     */
    public static <T> Key<T> createKey(Config config, Key<?> parent, String name, TypeInfo<T> type, Storage storage, Key<?> original) {
        return new AbstractKey.Impl<>(config, parent, name, type, storage, original);
    }

    /**
     * Creates a new emulated key based on {@code base}.
     *
     * This key stores/fetches values from {@code storage}.
     *
     * This is commonly used to call serializers with new key without affecting current storage,
     * allowing key-value pair to be stored in alternative {@code storage} before it is added into
     * {@link Key#getConfig() base key config} storage.
     *
     * @param base    Base key.
     * @param type    Type of the new key.
     * @param storage Storage where key and key value resides.
     * @param <T>     Type of the new key.
     * @return Emulated key based on {@code base}.
     */
    public static <T> Key<T> createKey(Key<?> base, TypeInfo<T> type, Storage storage) {
        return Key.createKey(base.getConfig(), base.getParent(), base.getName(), type, storage, base);
    }

    /**
     * Gets the original key.
     *
     * @param key Key to find original key.
     * @return Original key.
     */
    private static Key<?> getOriginal(Key<?> key) {
        if (key == null)
            return null;

        Key<?> original = key;

        while (original.isEmulated())
            original = key.getOriginalKey();

        return original;
    }

    /**
     * Used to get class simple name including its enclosing class name.
     *
     * @param aClass Class.
     * @return Simple class name including enclosing class name.
     */
    private static String getClassName(Class<?> aClass) {

        StringBuilder sb = new StringBuilder();

        Class<?> enclosing = aClass;

        while ((enclosing = enclosing.getEnclosingClass()) != null) {
            sb.append(enclosing.getSimpleName());
            sb.append('.');
        }

        sb.append(aClass.getSimpleName());

        return sb.toString();
    }

    /**
     * Gets the children key that {@code keySpec} specifies.
     *
     * @param keySpec Key specification.
     * @param <V>     Type of key value.
     * @return Children key that {@code keySpec} specifies.
     */
    public <V> Key<V> get(KeySpec<V> keySpec) {
        return keySpec.get(this);
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
        return this.getKey(name, TypeInfo.of(type));
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

        Object o;

        if (!this.getStorage().exists(this)
                || !((o = this.getStorage().fetchValue(this)) instanceof Map<?, ?>))
            map = new HashMap<>();
        else
            map = (Map<String, Object>) o;

        Storage storage = Storage.createMapStorage(this.config, map);

        this.getStorage().pushValue(this, map);

        return new Key<>(this.getConfig(), this, name, typeInfo, storage, null);
    }

    /**
     * Fetches value from {@link Storage value storage}.
     *
     * @return Value stored in {@link Storage value storage}.
     * @throws UnsupportedValueTypeException If there is no {@link com.github.jonathanxd.config.serialize.Serializer}
     *                                       registered for type and value is not supported by
     *                                       {@link com.github.jonathanxd.config.backend.Backend}.
     */
    @SuppressWarnings("unchecked")
    public T getValue() throws UnsupportedValueTypeException {
        return (T) this.getStorage().get(this);
    }

    /**
     * Pushes the {@code value} to {@link Storage value storage}.
     *
     * @param value Value to push to {@link Storage value storage}.
     * @throws UnsupportedValueTypeException If there is no {@link com.github.jonathanxd.config.serialize.Serializer}
     *                                       registered for type and value is not supported by
     *                                       {@link com.github.jonathanxd.config.backend.Backend}.
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

    /**
     * Returns the original key that this key was based to emulate, or returns {@code null} if this
     * is not an emulated key.
     *
     * @return Original key that this key was based to emulate, or returns {@code null} if this is
     * not an emulated key.
     */
    public Key<?> getOriginalKey() {
        return this.original;
    }

    /**
     * Returns the original key that this key was based to emulate, or returns {@code this} if this
     * is not an emulated key.
     *
     * @return Original key that this key was based to emulate, or returns {@code this} if this is
     * not an emulated key.
     */
    public Key<?> getOriginalKeyOrThis() {
        Key<?> originalKey = this.getOriginalKey();

        if (originalKey == null)
            return this;

        return originalKey;
    }

    /**
     * Returns {@code true} if this key {@link Storage#exists(Key) exists} in {@link
     * Key#getStorage() storage}.
     *
     * @return {@code true} if this key {@link Storage#exists(Key) exists} in {@link
     * Key#getStorage() storage}.
     */
    public boolean exists() {
        return this.getStorage().exists(this);
    }

    /**
     * Returns true if this key is emulated.
     *
     * See the {@link Key documentation}
     *
     * @return {@code true} if this key is emulated.
     */
    public boolean isEmulated() {
        return this.getOriginalKey() != null;
    }

    @Override
    public String toString() {

        return ToStringHelper.defaultHelper(Key.getClassName(this.getClass()))
                .add("name", this.getName())
                .add("type", this.getTypeInfo())
                .add("storage", this.getStorage())
                .add("config", this.getConfig())
                .addOptional("parent", Optional.ofNullable(this.getParent()).map(Object::toString))
                .toString();
    }
}
