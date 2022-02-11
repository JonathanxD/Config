/*
 *      Config - Configuration library <https://github.com/JonathanxD/Config>
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
package com.github.jonathanxd.config.backend;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Backend that delegates save and load to {@link Consumer} and {@link Supplier}.
 */
public class FunctionBackend implements Backend {
    private final Consumer<Map<Object, Object>> saver;
    private final Supplier<Map<Object, Object>> loader;

    public FunctionBackend(Consumer<Map<Object, Object>> saver, Supplier<Map<Object, Object>> loader) {
        this.saver = saver;
        this.loader = loader;
    }

    public Consumer<Map<Object, Object>> getSaver() {
        return this.saver;
    }

    public Supplier<Map<Object, Object>> getLoader() {
        return this.loader;
    }

    @Override
    public void save(Map<Object, Object> map) {
        this.getSaver().accept(map);
    }

    @Override
    public Map<Object, Object> load() {
        return this.getLoader().get();
    }
}
