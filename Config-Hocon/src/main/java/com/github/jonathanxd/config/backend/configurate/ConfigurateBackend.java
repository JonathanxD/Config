/*
 *      Config-Hocon - Hocon backend for Config <https://github.com/JonathanxD/Config/>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2022 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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
package com.github.jonathanxd.config.backend.configurate;

import com.github.jonathanxd.config.backend.Backend;
import com.github.jonathanxd.iutils.exception.RethrowException;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.util.Map;

public class ConfigurateBackend implements Backend {

    private final ConfigurationLoader<CommentedConfigurationNode> loader;

    public ConfigurateBackend(ConfigurationLoader<CommentedConfigurationNode> loader) {
        this.loader = loader;
    }

    @Override
    public void save(Map<Object, Object> map) {
        var root = loader.createNode();
        root.raw(map);

        try {
            loader.save(root);
        } catch (IOException e) {
            throw RethrowException.rethrow(e);
        }

    }

    @Override
    public Map<Object, Object> load() {
        try {
            var node = loader.load();
            if (node.isMap()) {
                return (Map<Object, Object>) node.raw();
            } else {
                throw new IllegalStateException("Root node is not a map");
            }
        } catch (IOException e) {
            throw RethrowException.rethrow(e);
        }
    }
}
