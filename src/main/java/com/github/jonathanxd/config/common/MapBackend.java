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
package com.github.jonathanxd.config.common;

import com.github.jonathanxd.config.backend.AbstractConfigBackend;
import com.github.jonathanxd.iutils.function.collector.BiCollectors;
import com.github.jonathanxd.iutils.function.stream.BiStreams;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MapBackend extends AbstractConfigBackend {

    public static final TypeInfo[] SUPPORTED_TYPES = {
            TypeInfo.of(byte.class),
            TypeInfo.of(boolean.class),
            TypeInfo.of(short.class),
            TypeInfo.of(int.class),
            TypeInfo.of(float.class),
            TypeInfo.of(double.class),
            TypeInfo.of(long.class),
            TypeInfo.of(char.class),
            TypeInfo.of(Byte.class),
            TypeInfo.of(Boolean.class),
            TypeInfo.of(Short.class),
            TypeInfo.of(Float.class),
            TypeInfo.of(Double.class),
            TypeInfo.of(Long.class),
            TypeInfo.of(Character.class),
            TypeInfo.of(String.class),
            TypeInfo.of(Number.class),
            TypeInfo.builderOf(List.class).of(String.class).build(),
            TypeInfo.builderOf(List.class).of(Number.class).build(),
            TypeInfo.builderOf(List.class).build()
    };

    protected final Map<Object, Object> map;

    public MapBackend() {
        map = new LinkedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public MapBackend(LinkedHashMap map) {
        this.map = map;
    }

    @Override
    public void setValueToPath(Object[] paths, Object value) {
        //String[] paths = Path.splitPath(path);

        if (paths.length == 0) {
            throw new IllegalArgumentException("Null or Empty path!");
        }

        if (value != null) {
            putPath(paths, this.map, value);
        } else {
            putPath(paths, this.map, new HashMap<>());
        }

    }

    @Override
    public <T> void setValueToPath(Object[] path, T value, TypeInfo<T> expectedType) {
        setValueToPath(path, value);
    }

    @SuppressWarnings("unchecked")
    private void putPath(Object[] paths, Map<Object, Object> map, Object value) {

        for (int x = 0; x < paths.length; ++x) {
            Object root = paths[x];

            if (x + 1 >= paths.length) {
                map.put(root, value);
            } else if (map.containsKey(root)) {
                Object atRoot = map.get(root);

                if (atRoot instanceof Map) {
                    map = (Map<Object, Object>) atRoot;
                } else {
                    map = new HashMap<>();
                    map.put("?", atRoot);
                }
            } else {
                map.put(root, map = new HashMap<>());
            }
        }
/*
        String root = paths[pos];

        if(map.containsKey(root)) {
            Object atRoot = map.get(root);

            if(atRoot instanceof Map) {
                ((Map)atRoot).put(pat)
            }
        } else {
            map.put()
        }*/
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean pathExists(Object[] paths) {

        Map<Object, Object> map = this.map;

        //String[] paths = Path.splitPath(path);

        for (int x = 0; x < paths.length; ++x) {

            Object s = paths[x];

            boolean isLast = x + 1 >= paths.length;

            if (!map.containsKey(s))
                return false;

            Object o = map.get(s);

            if (o instanceof Map) {
                map = (Map<Object, Object>) o;

                if (isLast)
                    return true;

            } else {
                return isLast;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getValueFromPath(Object[] paths) {

        Map<Object, Object> map = this.map;

        //String[] paths = Path.splitPath(path);

        for (int x = 0; x < paths.length; ++x) {

            Object s = paths[x];

            boolean isLast = x + 1 >= paths.length;

            if (!map.containsKey(s))
                return null;

            Object o = map.get(s);

            if (o instanceof Map) {
                map = (Map<Object, Object>) o;
                if (isLast)
                    return map;
            } else {
                if (isLast)
                    return o;
                return null;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValueFromPath(Object[] path, TypeInfo<T> expectedType) {
        return (T) getValueFromPath(path);
    }

    @Override
    public String getValueFromPathAsString(Object[] path) {
        return getValueFromPath(path).toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Object, Object> getValuesOnPath(Object[] path) {

        Map<Object, Object> map = getAllOnPath(path);

        if (map != null) {
            return filterKey(map, o1 -> !(o1 instanceof Map));
        }

        return null;
    }

    private Map<Object, Object> filterKey(Map<Object, Object> in, Predicate<Object> o) {
        return BiStreams.mapStream(in).filter((s, o1) -> o.test(o1)).collect(BiCollectors.toMap());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Object, Object> getSectionsOnPath(Object[] path) {
        Map<Object, Object> map = getAllOnPath(path);

        if (map != null) {
            return filterKey(map, o1 -> (o1 instanceof Map));
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Object, Object> getAllOnPath(Object[] paths) {
        Map<Object, Object> map = this.map;

        //String[] paths = Path.splitPath(path);

        for (int x = 0; x < paths.length; ++x) {

            Object s = paths[x];

            boolean isLast = x + 1 >= paths.length;

            if (!map.containsKey(s)) {
                map = null;
                break;
            }

            Object o = map.get(s);

            if (o instanceof Map) {
                map = (Map<Object, Object>) o;
                if (isLast)
                    break;
            } else {
                if (!isLast) {
                    map = null;
                    break;
                }
            }
        }

        return map;
    }

    @Override
    public void save() {

    }

    @Override
    public void reload() {

    }

    @Override
    public boolean isSupported(TypeInfo<?> genericRepresentation) {

        for (TypeInfo supportedType : SUPPORTED_TYPES) {
            if (supportedType.compareTypeAndRelatedTo(genericRepresentation) == 0)
                return true;
        }

        return false;
    }

    public static void printMap(MapBackend mapBackend, Consumer<String> consumer) {
        consumer.accept(mapBackend.map.toString());
    }
}
