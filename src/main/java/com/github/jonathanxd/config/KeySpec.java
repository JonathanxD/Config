/*
 *      Config - Configuration library <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2018 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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

/**
 * Simple class to be used as {@link Key} specification. This class is commonly used to refer to a
 * key in contexts where the parent key is not available.
 *
 * Example of use:
 *
 * <pre>
 *     {@code
 *      public static final KeySpec<String> DESCRIPTION =
 *                          KeySpec.create("description", TypeInfo.of(String.class));
 *
 *      public Product deserialize(Key<?> key, ...) {
 *          String description = key.get(DESCRIPTION).getValue()
 *          ...
 *          return new Product(..., description, ...)
 *      }
 *
 *     }
 * </pre>
 */
public final class KeySpec<T> {
    /**
     * Key name.
     */
    private final String name;

    /**
     * Key type.
     */
    private final TypeInfo<T> type;

    private KeySpec(String name, TypeInfo<T> type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Creates a {@link KeySpec key specification}.
     *
     * @param name Name of the key.
     * @param type Type of the key value.
     * @param <T>  Type of the key value.
     * @return Key specification.
     */
    public static <T> KeySpec<T> create(String name, TypeInfo<T> type) {
        return new KeySpec<>(name, type);
    }

    /**
     * Creates a {@link KeySpec key specification}.
     *
     * @param name Name of the key.
     * @param type Type of the key value.
     * @param <T>  Type of the key value.
     * @return Key specification.
     */
    public static <T> KeySpec<T> create(String name, Class<T> type) {
        return KeySpec.create(name, TypeInfo.of(type));
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
     * Gets the key type.
     *
     * @return Key type.
     */
    public TypeInfo<T> getType() {
        return this.type;
    }

    /**
     * Gets the children key of {@code parent} key that this instance specifies.
     *
     * @param parent Parent key to get children key.
     * @return Children key of {@code parent} key that this instance specifies.
     */
    public Key<T> get(Key<?> parent) {
        return parent.getKey(this.getName(), this.getType());
    }

    /**
     * Gets the children key of {@code parent} key that this instance specifies.
     *
     * This method ignores the type info of this specification.
     *
     * @param parent   Parent key to get children key.
     * @param typeInfo Type info to use to get key.
     * @return Children key of {@code parent} key that this instance specifies.
     */
    public <U> Key<U> get(Key<?> parent, TypeInfo<U> typeInfo) {
        return parent.getKey(this.getName(), typeInfo);
    }
}
