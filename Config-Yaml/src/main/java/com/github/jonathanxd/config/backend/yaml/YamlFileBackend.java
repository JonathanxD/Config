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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.function.Supplier;

/**
 * Yaml file backend. The file must exists, otherwise {@link java.io.FileNotFoundException} may be
 * throw by save and load operations.
 */
public class YamlFileBackend extends YamlBackend {

    /**
     * Creates a yaml backend that saves and load yaml from a file.
     *
     * @param yaml    Yaml specification.
     * @param file    File to save and load yaml.
     * @param charset Charset to be used to write and read yaml characters.
     */
    public YamlFileBackend(Yaml yaml, File file, Charset charset) {
        super(yaml, YamlFileBackend.getWriter(file, charset), YamlFileBackend.getReader(file, charset));
    }

    private static Supplier<Writer> getWriter(File file, Charset charset) {
        return (ESupplier<Writer>) () -> new OutputStreamWriter(new FileOutputStream(file), charset);
    }

    private static Supplier<Reader> getReader(File file, Charset charset) {
        return (ESupplier<Reader>) () -> new InputStreamReader(new FileInputStream(file), charset);
    }
}
