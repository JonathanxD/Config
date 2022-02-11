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
package com.github.jonathanxd.config;

public class KeyUtil {

    /**
     * Gets the path to {@code key} as a array string of key names.
     *
     * @param key Key to get path.
     * @return Path to {@code key} as a array string of key names.
     */
    public static String[] getPath(Key<?> key) {
        int capacity = 1;
        Key<?> temp = key;

        while ((temp = temp.getParent()) != null)
            ++capacity;

        String[] path = new String[capacity];

        temp = key;

        path[--capacity] = temp.getName();

        while ((temp = temp.getParent()) != null) {
            path[--capacity] = temp.getName();
        }

        return path;
    }

    /**
     * Gets the path to {@code key} as a string of key names separated by blank space.
     *
     * @param key Key to get path.
     * @return Path to {@code key} as a string of key names separated by blank space.
     */
    public static String getPathAsString(Key<?> key) {
        String[] path = KeyUtil.getPath(key);

        if (path.length == 1)
            return path[0];

        StringBuilder sb = new StringBuilder();
        sb.append(path[0]);

        for (int i = 1; i < path.length; i++) {
            sb.append(" ").append(path[i]);
        }

        return sb.toString();
    }

}
