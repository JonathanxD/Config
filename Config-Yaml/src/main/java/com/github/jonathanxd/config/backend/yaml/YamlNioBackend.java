/*
 *      Config-Yaml - Yaml backend for Config <https://github.com/JonathanxD/Config/>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2017 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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
package com.github.jonathanxd.config.backend.yaml;

import com.github.jonathanxd.iutils.function.checked.supplier.ESupplier;

import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * Yaml nio 2 path backend. The path must exists, otherwise {@link java.io.IOException} may be throw
 * by save and load operations.
 */
public class YamlNioBackend extends YamlBackend {
    /**
     * Creates a yaml backend that saves and load yaml from a path.
     *
     * @param yaml    Yaml specification.
     * @param path    Path to save and load yaml.
     * @param charset Charset to be used to write and read yaml characters.
     */
    public YamlNioBackend(Yaml yaml, Path path, Charset charset) {
        super(yaml, YamlNioBackend.getWriter(path, charset), YamlNioBackend.getReader(path, charset));
    }

    private static Supplier<Writer> getWriter(Path path, Charset charset) {
        return (ESupplier<Writer>) () -> Files.newBufferedWriter(path, charset);
    }

    private static Supplier<Reader> getReader(Path path, Charset charset) {
        return (ESupplier<Reader>) () -> Files.newBufferedReader(path, charset);
    }
}
