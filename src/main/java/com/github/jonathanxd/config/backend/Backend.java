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
package com.github.jonathanxd.config.backend;

import com.github.jonathanxd.config.CommonTypes;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.List;
import java.util.Map;

/**
 * Backend, the backend is responsible to save and load the configuration.
 */
public interface Backend {

    /**
     * Do save or load action.
     *
     * @param map    Mutable map to apply the action.
     * @param action Action to apply to map.
     */
    void doAction(Map<String, Object> map, Action action);

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
     * Configuration action
     */
    enum Action {
        /**
         * Save the configuration map
         */
        SAVE,

        /**
         * Load the configuration map
         */
        LOAD
    }
}
