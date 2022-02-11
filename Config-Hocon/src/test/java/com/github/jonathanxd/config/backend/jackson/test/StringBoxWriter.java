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
package com.github.jonathanxd.config.backend.jackson.test;

import com.github.jonathanxd.iutils.box.IMutableBox;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

class StringBoxWriter extends Writer {

    private final IMutableBox<String> box;
    private final StringWriter sw = new StringWriter();

    StringBoxWriter(IMutableBox<String> box) {
        this.box = box;
    }

    @Override
    public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
        this.sw.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.sw.flush();
        this.box.set(this.sw.toString());
    }

    @Override
    public void close() throws IOException {
        this.flush();
        this.sw.close();
    }
}
