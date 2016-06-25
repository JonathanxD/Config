/*
 *      Config - Configuration API. <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2016 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/ & https://github.com/TheRealBuggy/) <jonathan.scripter@programmer.net>
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


import com.github.jonathanxd.iutils.object.GenericRepresentation;

import java.util.Map;

/**
 * Created by jonathan on 01/01/16.
 */
public interface ConfigBackend {

    /**
     * Define new value
     *
     * @param path Path to value
     */
    void setValueToPath(String path, Object value);

    /**
     * Define new value
     *
     * @param path         Path
     * @param value        Value
     * @param expectedType Expected Type
     * @param <T>          Type
     */
    <T> void setValueToPath(String path, T value, GenericRepresentation<T> expectedType);

    /**
     * Check if a path exists
     *
     * @param path Path
     * @return True if path exists.
     */
    boolean pathExists(String path);

    /**
     * Get value in the PATH
     *
     * @param path Path to value
     * @return Value
     */
    Object getValueFromPath(String path);

    /**
     * Get value in the Path expecting a type
     *
     * @param path         Path
     * @param expectedType Type
     * @param <T>          Type
     * @return Converted Value
     */
    <T> T getValueFromPath(String path, GenericRepresentation<T> expectedType);

    /**
     * Get value IN Path as {@link String}
     *
     * @param path Path to value
     * @return The value as {@link String}
     */
    String getValueFromPathAsString(String path);

    /**
     * Get all values in determinate path
     *
     * @param path Path to values
     * @return Values
     */
    Map<String, Object> getValuesOnPath(String path);

    /**
     * Get all paths of sections in determinate path
     *
     * @param path Path to Sections
     * @return Sections path
     */
    Map<String, Object> getSectionsOnPath(String path);

    /**
     * Get all keys and sections in determinate path
     *
     * @param path Path of keys and sections
     * @return Keys and Sections paths and values
     */
    Map<String, Object> getAllOnPath(String path);


    /**
     * Save changes!
     */
    void save();

    /**
     * Return true if type is supported
     *
     * @return True if type is supported
     */
    boolean isSupported(GenericRepresentation<?> value);
}
