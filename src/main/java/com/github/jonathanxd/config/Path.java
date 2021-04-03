/*
 *      Config - Configuration API. <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2021 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/ & https://github.com/TheRealBuggy/) <jonathan.scripter@programmer.net>
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


import com.github.jonathanxd.iutils.array.ArrayUtils;
import com.github.jonathanxd.iutils.condition.Conditions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by jonathan on 24/06/16.
 */
public class Path<T> {

    private final Config<T> config;
    final Object[] path;

    Path(Config<T> config) {
        this(config, null);
    }

    Path(Config<T> config, Object[] path) {
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



        return new Path<>(config, ArrayUtils.addAllToArray(this.path, path.path));//new Path<>(config, this.getStringPath() + "." + path.getStringPath());
    }

    public Path<T> path(T... ids) {
        List<Object> array = new ArrayList<>();
        //StringJoiner paths = new StringJoiner(".");

        for (T id : ids) {
            Object[] path = Conditions.checkNotNull(config.getPathForTag(id), "Cannot determine path of id '"+id+"'!");

            array.add(path);
        }

        return new Path<>(config, array.toArray(new Object[0]));
    }

    /*public String getStringPath() {
        return path;
    }*/

    public Object[] getPath() {
        return path;
    }

    /**
     * Return Path or an empty string if is a Main Path
     * @return Path or an empty string if is a Main Path
     */
    @NotNull
    public Object getPathName() {

        if(isMain())
            return "";

        Object[] path = getPath();

        return path[path.length-1];
    }

    @NotNull
    public static String[] splitPath(String path) {
        if (path == null)
            return new String[0];

        StringBuilder currentStr = new StringBuilder();
        List<String> array = new ArrayList<>();

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

        return array.toArray(new String[0]);
    }

    public boolean isMain() {
        return path == null;
    }
}
