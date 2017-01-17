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

import com.github.jonathanxd.config.backend.ConfigBackend;
import com.github.jonathanxd.config.key.Key;
import com.github.jonathanxd.config.serializer.Serializers;
import com.github.jonathanxd.config.transformer.Transformer;
import com.github.jonathanxd.config.value.ValueGetter;
import com.github.jonathanxd.config.value.ValueSetter;
import com.github.jonathanxd.iutils.arrays.JwArray;
import com.github.jonathanxd.iutils.object.GenericRepresentation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jonathan on 24/06/16.
 */
public class Config<T> {

    private final Map<T, Object[]> pathTag = new HashMap<>();

    private final ConfigBackend backend;

    private final Serializers serializers = new Serializers();

    private final ValueSetter valueSetter;
    private final ValueGetter valueGetter;

    public Config(ConfigBackend backend) {
        this.backend = backend;
        this.valueSetter = new ValueSetter(this, backend);
        this.valueGetter = new ValueGetter(this, backend);
    }


    public <V> Key<V> createKey(GenericRepresentation<V> typeRepresentation, Path<T> path) {
        return new Key<>(typeRepresentation, path, this);
    }

    public <V> Key<V> createKey(GenericRepresentation<V> typeRepresentation, Path<T> path, Transformer<V> transformer) {
        return new Key<>(typeRepresentation, path, this, Collections.singletonList(transformer));
    }

    public <V> Key<V> createKey(GenericRepresentation<V> typeRepresentation, Path<T> path, List<Transformer<V>> transformers) {
        return new Key<>(typeRepresentation, path, this, transformers);
    }

    public <V> Key<V> createKey(Class<V> typeRepresentation, Path<T> path) {
        return new Key<>(GenericRepresentation.aEnd(typeRepresentation), path, this);
    }

    public <V> Key<V> createKey(Class<V> typeRepresentation, Path<T> path, Transformer<V> transformer) {
        return new Key<>(GenericRepresentation.aEnd(typeRepresentation), path, this, Collections.singletonList(transformer));
    }

    public <V> Key<V> createKey(Class<V> typeRepresentation, Path<T> path, List<Transformer<V>> transformers) {
        return new Key<>(GenericRepresentation.aEnd(typeRepresentation), path, this, transformers);
    }

    public void setTagPath(T tag, Object[] path) {
        pathTag.put(tag, path);
    }

    public void setTagPath(T tag, Path<T> subPath) {
        pathTag.put(tag, subPath.path);
    }

    public Path<T> getTagPath(T tag) {
        return getPath(pathTag.get(tag));
    }

    public Path<T> getPath(Object[] path) {
        return new Path<>(this, path);
    }

    public Path<T> getPath(Object path) {
        if(path instanceof Object[]) {
            return getPath((Object[]) path);
        } else {
            return new Path<>(this, new Object[]{path});
        }
    }

    /*
    public Path<T> getFullPath(Object[] path) {
        return getPath(parsePath(path));
    }

    public Path<T> getPath(Object... path) {
        return new Path<>(this, path);
    }*/

    public Object[] getPathForTag(T tag) {
        return pathTag.get(tag);
    }

    public ConfigBackend getBackend() {
        return backend;
    }

    public Serializers getSerializers() {
        return serializers;
    }

    public ValueGetter getValueGetter() {
        return valueGetter;
    }

    public ValueSetter getValueSetter() {
        return valueSetter;
    }

    //"a.b\\.c.d" => String[3]{"a", "b.c", "d"}
    //"a.b\\\\.c.d" => String[4]{"a", "b\", "c", "d"}

    private String[] parsePath(String path) {
        JwArray<String> array = new JwArray<>(String.class);

        StringBuilder sb = new StringBuilder();

        char[] chars = path.toCharArray();
        boolean escape = false;

        for (int i = 0; i < chars.length; i++) {
            char currentChar = chars[i];

            if (i == 0) {
                sb.append(currentChar);
            } else {
                if (currentChar == '\\' && !escape) {
                    escape = true;
                } else if (escape) {
                    escape = false;
                    sb.append(currentChar);
                } else if (currentChar == '.') {
                    array.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(currentChar);
                }
            }
        }


        if (sb.length() > 0) {
            array.add(sb.toString());
            sb.setLength(0);
        }

        return array.toGenericArray();
    }
}
