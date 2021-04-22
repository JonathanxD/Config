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

import com.github.jonathanxd.iutils.string.ToStringHelper;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

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
 * to get which key was used to create emulated key, use {@link #getOriginalKey()}. Emulated keys
 * only equals to keys that have same {@link #getOriginalKey() original key}.
 *
 * {@link Key} fetches and pushes the value to the {@link Storage storage} and create child keys
 * linked to another {@link Storage storage} that fetches and pushes values to current {@link
 * Storage value storage}.
 *
 * Keys carry your own {@link T type}, the type does not really reflect the type of stored values,
 * the type reflects expected value type. This carried {@link T type} is used to serialize and
 * deserialize value. The deserialization/serialization is called internally, during value fetching
 * and pushing. Query and set methods can throw exceptions if there is no serializer for key type
 * and the key type is not supported by {@link com.github.jonathanxd.config.backend.Backend}.
 *
 * Keys are identified by {@link #getConfig() config}, {@link #getParent() parent key} and {@link
 * #getName() key name}.
 *
 * Some configuration APIs allows you to create {@code Section} instances where keys reside, in
 * {@code Config}, Keys reside in {@link Storage} and sections are simple keys with {@link Void}
 * {@link Key#typeInfo type}.
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
     * Private since {@code 2.2}.
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
    private static <T> Key<T> createKey(Config config, Key<?> parent, String name, TypeInfo<T> type, Storage storage, Key<?> original) {
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
     * Since {@code 2.2}, you should use {@link Key#getAs(TypeInfo)} and {@link Key#getAs(TypeInfo,
     * Storage)} instead.
     *
     * @param base    Base key.
     * @param type    Type of the new key.
     * @param storage Storage where key and key value resides.
     * @param <T>     Type of the new key.
     * @return Emulated key based on {@code base}.
     */
    private static <T> Key<T> createKey(Key<?> base, TypeInfo<T> type, Storage storage) {
        return Key.createKey(base.getConfig(), base.getParent(), base.getName(), type, storage, base);
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
     * This variant is used for keys that a special name is required, such as Lists.
     *
     * @param base    Base key.
     * @param name    New name.
     * @param type    Type of the new key.
     * @param storage Storage where key and key value resides.
     * @param <T>     Type of the new key.
     * @return Emulated key based on {@code base}.
     */
    private static <T> Key<T> createKey(Key<?> base, String name, TypeInfo<T> type, Storage storage) {
        return Key.createKey(base.getConfig(), base.getParent(), name, type, storage, base);
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
     * Gets this key as a get of another type.
     *
     * @param name New Name.
     * @param type New type.
     * @param <V>  New type.
     * @return A new emulated key that emulates this key with different type and name.
     */
    public <V> Key<V> getAs(String name, TypeInfo<V> type) {
        return Key.createKey(this, name, type, this.getStorage());
    }

    /**
     * Gets this key as a get of another type that stores values in alternative {@code storage}.
     *
     * @param type    New type.
     * @param storage New storage.
     * @param <V>     New type.
     * @return A new emulated key that emulates this key with different type and name, and that
     * stores values in alternative {@code storage}.
     */
    public <V> Key<V> getAs(String name, TypeInfo<V> type, Storage storage) {
        return Key.createKey(this, name, type, storage);
    }

    /**
     * Gets this key as a get of another type.
     *
     * The new emulated key type does not guarantee that the value cast will succeed, it depends
     * on the original type of the key and the key implementation.
     *
     * @param name New Name.
     * @param type New type.
     * @param <V>  New type.
     * @return A new emulated key that emulates this key with different type and name.
     */
    public <V> Key<V> getAs(String name, Class<V> type) {
        return this.getAs(name, TypeInfo.of(type));
    }

    /**
     * Gets this key as a get of another type that stores values in alternative {@code storage}.
     *
     * The new emulated key type does not guarantee that the value cast will succeed, it depends
     * on the original type of the key and the key implementation.
     *
     * @param type    New type.
     * @param storage New storage.
     * @param <V>     New type.
     * @return A new emulated key that emulates this key with different type and name, and that
     * stores values in alternative {@code storage}.
     */
    public <V> Key<V> getAs(String name, Class<V> type, Storage storage) {
        return this.getAs(name, TypeInfo.of(type), storage);
    }

    /**
     * Gets this key as a get of another type.
     *
     * The new emulated key type does not guarantee that the value cast will succeed, it depends
     * on the original type of the key and the key implementation.
     *
     * @param type New type.
     * @param <V>  New type.
     * @return A new emulated key that emulates this key with different type.
     */
    public <V> Key<V> getAs(TypeInfo<V> type) {
        return Key.createKey(this, type, this.getStorage());
    }

    /**
     * Gets this key as a get of another type that stores values in alternative {@code storage}.
     *
     * The new emulated key type does not guarantee that the value cast will succeed, it depends
     * on the original type of the key and the key implementation.
     *
     * @param type    New type.
     * @param storage New storage.
     * @param <V>     New type.
     * @return A new emulated key that emulates this key with different type that stores values in
     * alternative {@code storage}.
     */
    public <V> Key<V> getAs(TypeInfo<V> type, Storage storage) {
        return Key.createKey(this, type, storage);
    }

    /**
     * Gets this key as a get of another type.
     *
     * The new emulated key type does not guarantee that the value cast will succeed, it depends
     * on the original type of the key and the key implementation.
     *
     * @param type New type.
     * @param <V>  New type.
     * @return A new emulated key that emulates this key with different type.
     */
    public <V> Key<V> getAs(Class<V> type) {
        return this.getAs(TypeInfo.of(type));
    }

    /**
     * Gets this key as a get of another type that stores values in alternative {@code storage}.
     *
     * The new emulated key type does not guarantee that the value cast will succeed, it depends
     * on the original type of the key and the key implementation.
     *
     * @param type    New type.
     * @param storage New storage.
     * @param <V>     New type.
     * @return A new emulated key that emulates this key with different type that stores values in
     * alternative {@code storage}.
     */
    public <V> Key<V> getAs(Class<V> type, Storage storage) {
        return this.getAs(TypeInfo.of(type), storage);
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
     * Gets the children key that {@code keySpec} specifies. This method ignores the {@link
     * TypeInfo} of {@code keySpec}.
     *
     * @param keySpec  Key specification.
     * @param typeInfo Type info to use to get children key.
     * @param <V>      Type of key value.
     * @return Children key that {@code keySpec} specifies.
     */
    public <V> Key<V> get(KeySpec<?> keySpec, TypeInfo<V> typeInfo) {
        return keySpec.get(this, typeInfo);
    }

    /**
     * Creates a new key that is intended only to be used to create other keys from it (like {@code
     * Section} of other configuration APIs).
     *
     * @param name Name of the key section.
     * @return A new key that is intended only to be used to create other keys from it.
     */
    public Key<Void> getKeySection(String name) {
        return this.getKey(name, Void.TYPE);
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
        Storage storage = Storage.createInnerStorage(this);

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
     * Returns the value associated to this key in {@link Storage storage}, or returns {@code value}
     * if there is no value stored.
     *
     * @return Value stored in {@link Storage storage} or {@code value} if there is not value
     * stored.
     * @throws UnsupportedValueTypeException If there is no {@link com.github.jonathanxd.config.serialize.Serializer}
     *                                       registered for type and value is not supported by
     *                                       {@link com.github.jonathanxd.config.backend.Backend}.
     */
    @SuppressWarnings("unchecked")
    public T getValueOr(T value) throws UnsupportedValueTypeException {
        if (!this.exists())
            return value;
        return this.getValue();
    }

    /**
     * Returns the value associated to this key in {@link Storage storage}, or returns value
     * supplied by {@code valueSupplier} if there is no value stored.
     *
     * @return Value stored in {@link Storage storage} or value supplied by {@code valueSupplier} if
     * there is not value stored.
     * @throws UnsupportedValueTypeException If there is no {@link com.github.jonathanxd.config.serialize.Serializer}
     *                                       registered for type and value is not supported by
     *                                       {@link com.github.jonathanxd.config.backend.Backend}.
     */
    @SuppressWarnings("unchecked")
    public T getValueOrSupplied(Supplier<T> valueSupplier) throws UnsupportedValueTypeException {
        if (!this.exists())
            return valueSupplier.get();

        return this.getValue();
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
    public int hashCode() {
        return Objects.hash(this.getConfig(), this.getOriginalKey(), this.getParent(), this.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Key<?>))
            return super.equals(obj);

        Key<?> other = (Key<?>) obj;

        return Objects.equals(this.getConfig(), other.getConfig())
                && Objects.equals(this.getOriginalKey(), other.getOriginalKey())
                && Objects.equals(this.getParent(), other.getParent())
                && Objects.equals(this.getName(), other.getName());
    }

    @Override
    public String toString() {

        return ToStringHelper.defaultHelper(Key.getClassName(this.getClass()))
                .add("name", this.getName())
                .add("type", this.getTypeInfo())
                .add("storage", this.getStorage())
                .add("config", this.getConfig())
                .addOptional("parent", Optional.ofNullable(this.getParent()))
                .toString();
    }
}
