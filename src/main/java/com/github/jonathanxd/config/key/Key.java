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

/**
 * Created by jonathan on 24/06/16.
 */

import com.github.jonathanxd.config.Config;
import com.github.jonathanxd.config.Path;
import com.github.jonathanxd.config.converter.Converter;
import com.github.jonathanxd.config.transformer.Transformer;
import com.github.jonathanxd.iutils.object.GenericRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Link to a Path
 */
public class Key<T> {

    private final GenericRepresentation<T> typeRepresentation;
    private final List<Transformer<T>> transformers = new ArrayList<>();
    private final Path<?> path;
    private final Config<?> config;

    public Key(GenericRepresentation<T> typeRepresentation, Path<?> path, Config<?> config) {
        this.typeRepresentation = typeRepresentation;
        this.path = path;
        this.config = config;
    }

    public Key(GenericRepresentation<T> typeRepresentation, Path<?> path, Config<?> config, List<Transformer<T>> transformers) {
        this(typeRepresentation, path, config);

        this.transformers.addAll(transformers);
    }

    public void setValue(T value) {
        config.getValueSetter().setValue(this, value);
    }

    /**
     * Set value if doesn't exists
     * @param value Value
     * @return {@code this}
     */
    public Key<T> setDefaultValue(T value) {
        if(!config.getBackend().pathExists(this.getPath().getStringPath())) {
            setValue(value);
        }

        return this;
    }

    public T getValue() {
        return config.getValueGetter().getValue(this);
    }

    public T getValue(T defaultValue) {
        setDefaultValue(defaultValue);

        return config.getValueGetter().getValue(this);
    }

    public Path<?> getPath() {
        return path;
    }

    public Config<?> getConfig() {
        return config;
    }

    public GenericRepresentation<T> getTypeRepresentation() {
        return typeRepresentation;
    }

    public List<Transformer<T>> getTransformers() {
        return transformers;
    }

    public Node createNode() {
        return SimpleNode.create(this);
    }

    public <U> ConvertKey<U, T> createConvertKey(Converter<T, U> converter) {
        return new ConvertKey<>(this, converter);
    }
}
