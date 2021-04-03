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
package com.github.jonathanxd.config.serialize;

import com.github.jonathanxd.config.Key;
import com.github.jonathanxd.config.SerializationException;
import com.github.jonathanxd.config.Storage;
import com.github.jonathanxd.iutils.type.TypeInfo;

/**
 * Serializer, transform objects into configuration values and configuration values into objects.
 *
 * @param <T> Object type.
 * @see Key
 */
public interface Serializer<T> {

    /**
     * Serialize object of type {@link T} to {@code key}.
     *
     * @param value       Object to serialize.
     * @param key         Key to store serialized object.
     * @param typeInfo    Type info provided to serializer.
     * @param storage     Current storage to push and fetch values safely.
     * @param serializers Serializers instance that is serializing values.
     */
    void serialize(T value, Key<T> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) throws SerializationException;

    /**
     * Deserialize {@code key} to object of type {@link T}.
     *
     * @param key         Key to deserialize.
     * @param typeInfo    Type info provided to serializer.
     * @param storage     Current storage to push and fetch values safely.
     * @param serializers Serializers instance that is de-serializing values.
     * @return De-serialized object of type {@link T}.
     */
    T deserialize(Key<T> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) throws SerializationException;
}
