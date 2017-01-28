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

/**
 * Base {@link Key} class of all custom keys.
 *
 * @param <T> Type of the key value.
 */
public abstract class AbstractKey<T> extends Key<T> {

    /**
     * Creates a abstract key.
     *
     * @param config   Configuration.
     * @param parent   Parent key.
     * @param name     Name of this key.
     * @param typeInfo Type information of the {@link T value type}.
     * @param storage  Storage to push and fetch values.
     */
    public AbstractKey(Config config, Key<?> parent, String name, TypeInfo<T> typeInfo, Storage storage) {
        super(config, parent, name, typeInfo, storage);
    }

    /**
     * Basic implementation of {@link AbstractKey}
     * @param <T> Type of value.
     */
    public static class Impl<T> extends AbstractKey<T> {

        /**
         * Creates a key.
         *
         * @param config   Configuration.
         * @param parent   Parent key.
         * @param name     Name of this key.
         * @param typeInfo Type information of the {@link T value type}.
         * @param storage  Storage to push and fetch values.
         */
        public Impl(Config config, Key<?> parent, String name, TypeInfo<T> typeInfo, Storage storage) {
            super(config, parent, name, typeInfo, storage);
        }
    }
}
