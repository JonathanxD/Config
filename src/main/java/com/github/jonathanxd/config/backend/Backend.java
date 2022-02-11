/*
 *      Config - Configuration library <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2022 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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
package com.github.jonathanxd.config.backend;

import com.github.jonathanxd.config.CommonTypes;
import com.github.jonathanxd.config.Key;
import com.github.jonathanxd.config.serialize.Serializers;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.List;
import java.util.Map;

/**
 * Configuration save and load backend.
 *
 * Backend should save and load configuration from an object or upstream, this should also specify
 * to frontend which value types it support or not. The backend should support all types specified
 * by {@link CommonTypes} documentation (if upstream do not, backend should do the extra work).
 */
public interface Backend {

    /**
     * Save configuration {@code map}.
     *
     * The provided map should be unmodifiable, and backend should copy all elements of the {@code
     * map} to internal object or upstream.
     *
     * @param map Map to save (may be unmodifiable).
     */
    void save(Map<Object, Object> map);

    /**
     * Loads configuration map (can be unmodifiable).
     *
     * The implementation should copy all elements of the returned {@link Map map} into your
     * internal {@link Map map} or {@link com.github.jonathanxd.config.Storage}.
     *
     * @return Configuration map.
     */
    Map<Object, Object> load();

    /**
     * Returns true if this backed supports object of {@code type} without serialization.
     *
     * By default, all values will be serialized if there is a serializer registered, if no one
     * serializer of {@code type} is registered, then the frontend will call this method to check if
     * it can be inserted inside the {@link com.github.jonathanxd.config.Storage}, if not, it will
     * thrown an exception.
     *
     * Default implementation always returns true for: {@code primitive types}, {@link String},
     * {@link Map} and {@link List} of any of valid types.
     *
     * @param type Type to check if this backend supports.
     * @return True if this backed supports object of {@code type} without serialization.
     */
    default boolean supports(TypeInfo<?> type) {
        return CommonTypes.isValidBasicType(type);
    }

    /**
     * Called by the frontend to register serializers required by backend.
     *
     * @param serializers Serializer manager.
     */
    default void registerSerializers(Serializers serializers) {
    }

    /**
     * Specifies a root key different from the {@code defaultRootKey} to be used.
     *
     * @param defaultRootKey Default root key.
     * @return New root key.
     */
    default Key<?> resolveRoot(Key<?> defaultRootKey) {
        return defaultRootKey;
    }
}
