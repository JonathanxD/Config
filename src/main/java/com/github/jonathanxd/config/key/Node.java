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
package com.github.jonathanxd.config.key;

import com.github.jonathanxd.config.Config;
import com.github.jonathanxd.config.Path;
import com.github.jonathanxd.iutils.function.stream.MapStream;
import com.github.jonathanxd.iutils.object.GenericRepresentation;

import java.util.Map;

/**
 * Created by jonathan on 24/06/16.
 */
public interface Node {

    Node getNode(Path<?> path);

    default Node getNode(String... paths) {
        return getNode(getConfig().getPath(paths));
    }

    default <T> void setValue(T value, Class<T> representation) {
        setValue(value, GenericRepresentation.aEnd(representation));
    }

    <T> void setValue(T value, GenericRepresentation<T> representation);

    Object getValue();

    void setValue(Object value);

    default <T> T getValue(Class<T> tClass) {
        return getValue(GenericRepresentation.aEnd(tClass));
    }

    <T> T getValue(GenericRepresentation<T> representation);

    default Node[] getChildrenNodes() {
        Map<String, Object> sectionsOnPath = getConfig().getBackend().getSectionsOnPath(getPath().getStringPath());

        return MapStream.of(sectionsOnPath)
                .streamMap((sec, obj) -> sec)
                .map(path -> createNewNode(getConfig(), this.getPath().withGeneric(getConfig().getFullPath(path))))
                .toArray(Node[]::new);
    }

    Path<?> getPath();

    Config<?> getConfig();

    Node createNewNode(Config<?> config, Path<?> path);
}
