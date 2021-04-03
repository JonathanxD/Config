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
import com.github.jonathanxd.iutils.type.TypeInfo;

/**
 * Created by jonathan on 25/06/16.
 */
public class SimpleNode implements Node {

    private final Config<?> config;
    private final Path<?> path;

    SimpleNode(Config<?> config, Path<?> path) {
        this.config = config;
        this.path = path;
    }

    public static SimpleNode create(Key<?> key) {
        return new SimpleNode(key.getConfig(), key.getPath());
    }

    @Override
    public Node getNode(Path<?> path) {
        return createNewNode(config, this.path.withGeneric(path));
    }

    @Override
    public <T> void setValue(T value, TypeInfo<T> representation) {
        config.getValueSetter().setValue(this, value, representation);
    }

    @Override
    public Object getValue() {
        return config.getValueGetter().getObjectValue(path);
    }

    @Override
    public void setValue(Object value) {
        config.getValueSetter().setObjectValue(path, value);
    }

    @Override
    public <T> T getValue(TypeInfo<T> representation) {
        return config.getValueGetter().getValue(this, representation);
    }

    @Override
    public Path<?> getPath() {
        return path;
    }

    @Override
    public Config<?> getConfig() {
        return config;
    }

    @Override
    public Node createNewNode(Config<?> config, Path<?> path) {
        return new SimpleNode(config, path);
    }
}
