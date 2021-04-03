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
package com.github.jonathanxd.config.key;

import com.github.jonathanxd.config.Config;
import com.github.jonathanxd.config.Path;
import com.github.jonathanxd.iutils.function.stream.BiStreams;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.Map;

/**
 * Created by jonathan on 24/06/16.
 */
public interface Node extends BaseValuable {

    Node getNode(Path<?> path);

    default Node getNode(Object... paths) {
        return getNode(getConfig().getPath(paths));
    }

    default <T> void setValue(T value, Class<T> representation) {
        setValue(value, TypeInfo.of(representation));
    }

    <T> void setValue(T value, TypeInfo<T> representation);

    Object getValue();

    void setValue(Object value);

    default <T> T getValue(Class<T> tClass) {
        return getValue(TypeInfo.of(tClass));
    }

    default boolean exists() {
        return this.getConfig().getBackend().pathExists(this.getPath().getPath());
    }

    @Override
    default Object getGenericValue() {
        return this.getValue();
    }

    @Override
    default void setGenericValue(Object o) {
        this.setValue(o);
    }

    <T> T getValue(TypeInfo<T> representation);

    default Node[] getChildrenNodes() {
        Map<Object, Object> sectionsOnPath = getConfig().getBackend().getSectionsOnPath(getPath().getPath());

        return BiStreams.mapStream(sectionsOnPath)
                .streamMap((sec, obj) -> sec)
                .map(path -> createNewNode(getConfig(), this.getPath().withGeneric(getConfig().getPath(path))))
                .toArray(Node[]::new);
    }

    Path<?> getPath();

    Config<?> getConfig();

    Node createNewNode(Config<?> config, Path<?> path);
}
