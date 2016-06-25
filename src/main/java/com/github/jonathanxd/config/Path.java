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
package com.github.jonathanxd.config;

import com.github.jonathanxd.iutils.annotations.NotNull;
import com.github.jonathanxd.iutils.arrays.JwArray;
import com.github.jonathanxd.iutils.conditions.Conditions;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by jonathan on 24/06/16.
 */
public class Path<T> {

    private final Config<T> config;
    final String path;

    Path(Config<T> config) {
        this(config, null);
    }

    Path(Config<T> config, String path) {
        this.config = config;
        this.path = path;
    }

    @SuppressWarnings("unchecked")
    public Path<T> withGeneric(Path<?> path) {
        return with((Path<T>) path);
    }

    public Path<T> with(Path<T> path) {
        if(path.isMain())
            return this;

        return new Path<>(config, this.getStringPath() + "." + path.getStringPath());
    }

    public Path<T> path(T... ids) {
        StringJoiner paths = new StringJoiner(".");

        for (T id : ids) {
            String path = Conditions.checkNotNull(config.getPathForTag(id), "Cannot determine path of id '"+id+"'!");

            paths.add(path);
        }

        return new Path<>(config, paths.toString());
    }

    public String getStringPath() {
        return path;
    }

    @NotNull
    public String getPathName() {

        if(isMain())
            return "";

        @NotNull String[] strings = splitPath(getStringPath());

        return strings[strings.length-1];
    }

    @NotNull
    public static String[] splitPath(String path) {
        if (path == null)
            return new String[0];

        StringBuilder currentStr = new StringBuilder();
        JwArray<String> array = new JwArray<>();

        char[] toCharArray = path.toCharArray();

        boolean escape = false;

        for (int i = 0; i < toCharArray.length; i++) {
            char current = toCharArray[i];

            if (escape) {
                currentStr.append(current);
            } else if (current == '\\') {
                escape = true;
            } else if (current == '.') {
                array.add(currentStr.toString());
                currentStr.setLength(0);
                continue;
            }

            currentStr.append(current);

            if (i + 1 >= toCharArray.length) {
                array.add(currentStr.toString());
            }
        }

        return array.toGenericArray(String[].class);
    }

    public boolean isMain() {
        return path == null;
    }
}
