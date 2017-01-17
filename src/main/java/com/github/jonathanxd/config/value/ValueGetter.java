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
package com.github.jonathanxd.config.value;

/**
 * Created by jonathan on 24/06/16.
 */

import com.github.jonathanxd.config.Config;
import com.github.jonathanxd.config.Path;
import com.github.jonathanxd.config.backend.ConfigBackend;
import com.github.jonathanxd.config.key.Key;
import com.github.jonathanxd.config.key.Node;
import com.github.jonathanxd.config.transformer.Transformer;
import com.github.jonathanxd.iutils.object.GenericRepresentation;

import java.util.Collections;
import java.util.List;

/**
 * Fetch value from {@link ConfigBackend} and Translate, Apply, Deserialize
 */
public class ValueGetter {
    private final Config config;
    private final ConfigBackend backend;

    public ValueGetter(Config config, ConfigBackend backend) {
        this.config = config;
        this.backend = backend;
    }


    public Object getObjectValue(Path<?> path) {
        return backend.getValueFromPath(path.getPath());
    }

    public <T> T getValue(Node node, GenericRepresentation<T> representation) {
        return deserialize(node, representation, null);
    }

    public <T> T getValue(Node node, GenericRepresentation<T> representation, List<Transformer<T>> transformers) {
        return deserialize(node, representation, transformers);
    }

    public <T> T getValue(Node node, GenericRepresentation<T> representation, Transformer<T> transformer) {
        return deserialize(node, representation, Collections.singletonList(transformer));
    }

    public <T> T getValue(Key<T> key) {
        return getValue(key.createNode(), key.getTypeRepresentation(), key.getTransformers());
    }

    @SuppressWarnings("unchecked")
    private <T> T deserialize(Node node, GenericRepresentation<T> representation, List<Transformer<T>> transformerList) {
        if (backend.isSupported(representation)) {
            return tranform(backend.getValueFromPath(node.getPath().getPath(), representation), transformerList);
        } else {
            return tranform(config.getSerializers().getRequiredSerializer(representation).deserialize(node.createNewNode(config, node.getPath()), representation), transformerList);
        }
    }

    private <T> T tranform(T value, List<Transformer<T>> transformers) {

        if (transformers == null)
            return value;

        for (Transformer<T> transformer : transformers) {
            value = transformer.transform(value);
        }

        return value;
    }
}
