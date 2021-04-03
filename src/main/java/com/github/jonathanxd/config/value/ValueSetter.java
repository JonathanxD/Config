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
package com.github.jonathanxd.config.value;

import com.github.jonathanxd.config.Config;
import com.github.jonathanxd.config.Path;
import com.github.jonathanxd.config.backend.ConfigBackend;
import com.github.jonathanxd.config.key.Key;
import com.github.jonathanxd.config.key.Node;
import com.github.jonathanxd.config.transformer.Transformer;
import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.Collections;
import java.util.List;

/**
 * Created by jonathan on 24/06/16.
 */

/**
 * Translate, Apply Modifiers, Serialize and Send value to {@link ConfigBackend}
 */
public class ValueSetter {

    private final Config config;
    private final ConfigBackend backend;

    public ValueSetter(Config config, ConfigBackend backend) {
        this.config = config;
        this.backend = backend;
    }

    public void setObjectValue(Path<?> path, Object value) {
        if (!backend.isSupported(TypeInfo.of(value.getClass()))) {
            throw new IllegalArgumentException("Object '" + value + "' not supported!");
        }

        backend.setValueToPath(path.getPath(), value);
    }

    public <T> void setValue(Key<T> key, T value) {
        setValue(key.createNode(), value, key.getTypeRepresentation(), key.getTransformers());
    }

    public <T> void setValue(Node node, T value, TypeInfo<T> representation) {
        serialize(node, value, representation, null);
    }

    public <T> void setValue(Node node, T value, TypeInfo<T> representation, List<Transformer<T>> transformers) {
        serialize(node, value, representation, transformers);
    }

    public <T> void setValue(Node node, T value, TypeInfo<T> representation, Transformer<T> transformers) {
        serialize(node, value, representation, Collections.singletonList(transformers));
    }

    @SuppressWarnings("unchecked")
    private <T> void serialize(Node node, T value, TypeInfo<T> representation, List<Transformer<T>> transformerList) {

        value = transform(value, transformerList);

        if (backend.isSupported(representation)) {
            backend.setValueToPath(node.getPath().getPath(), value, representation);
        } else {
            config.getSerializers().getRequiredSerializer(representation).serialize(value, node.createNewNode(config, node.getPath()), representation);
        }
    }


    private <T> T transform(T value, List<Transformer<T>> transformers) {
        if (transformers != null) {
            for (Transformer<T> tTransformer : transformers) {
                value = tTransformer.revertTransformation(value);
            }
        }

        return value;
    }
}
