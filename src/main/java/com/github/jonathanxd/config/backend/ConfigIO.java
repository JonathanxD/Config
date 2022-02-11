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

import com.github.jonathanxd.iutils.box.IMutableBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * A base interface that provides {@link Reader} and {@link Writer} to read and write
 * configuration.
 */
public interface ConfigIO {

    /**
     * Creates a {@link ConfigIO} that provides {@code file} reader and writer.
     *
     * The file must exists at the time that save and read operations are executed.
     *
     * @param file    File to read and write configuration.
     * @param charset Charset to encode characters.
     * @return {@link ConfigIO} that provides {@code file} reader and writer.
     */
    static ConfigIO file(File file, Charset charset) {
        return new FileIO(file, charset);
    }

    /**
     * Creates a {@link ConfigIO} that provides {@code nio path} reader and writer.
     *
     * @param path        Nio path.
     * @param charset     Charset to encode characters.
     * @param openOptions Options to be used to open the file to write.
     * @return {@link ConfigIO} that provides {@code nio path} reader and writer.
     */
    static ConfigIO path(Path path, Charset charset, OpenOption... openOptions) {
        return new NioIO(path, charset, openOptions);
    }

    /**
     * Creates a {@link ConfigIO} that provides {@code nio path} reader and writer.
     *
     * @param path        Nio path.
     * @param charset     Charset to encode characters.
     * @return {@link ConfigIO} that provides {@code nio path} reader and writer.
     */
    static ConfigIO path(Path path, Charset charset) {
        return ConfigIO.path(path, charset, StandardOpenOption.READ, StandardOpenOption.WRITE);
    }

    /**
     * Creates a {@link ConfigIO} that reads and write to a string.
     *
     * @param reader Reader of string.
     * @param writer Writer of string.
     * @return {@link ConfigIO} that reads and write to a string.
     */
    static ConfigIO string(StringReader reader, StringWriter writer) {
        return new StringIO(reader, writer);
    }

    /**
     * Creates a {@link ConfigIO} that reads and write to {@code box value}.
     *
     * Throw {@link IOException} if attempt to read string before it is write to.
     *
     * @param box Box to store string
     * @return {@link ConfigIO} that reads and write to a string.
     */
    static ConfigIO stringBox(IMutableBox<String> box) {
        return new StringBoxIO(box);
    }

    /**
     * Opens {@link Reader} to be used to read configuration.
     *
     * @return {@link Reader} instance to be used to read configuration.
     * @throws IOException Thrown by implementation in some cases, such as file not existing.
     */
    Reader openReader() throws IOException;

    /**
     * Opens {@link Writer} to be used to write configuration.
     *
     * @return {@link Writer} instance to be used to write configuration.
     * @throws IOException Thrown by implementation in some cases, such as file not existing.
     */
    Writer openWriter() throws IOException;

    class FileIO implements ConfigIO {

        private final File file;
        private final Charset charset;

        FileIO(File file, Charset charset) {
            this.file = file;
            this.charset = charset;
        }

        @Override
        public Reader openReader() throws IOException {
            return new InputStreamReader(new FileInputStream(this.file), charset);
        }

        @Override
        public Writer openWriter() throws IOException {
            return new OutputStreamWriter(new FileOutputStream(this.file), this.charset);
        }
    }

    class NioIO implements ConfigIO {

        private final Path path;
        private final Charset charset;
        private final OpenOption[] openOptions;

        NioIO(Path path, Charset charset, OpenOption[] openOptions) {
            this.path = path;
            this.charset = charset;
            this.openOptions = openOptions;
        }

        @Override
        public Reader openReader() throws IOException {
            return Files.newBufferedReader(this.path, this.charset);
        }

        @Override
        public Writer openWriter() throws IOException {
            return Files.newBufferedWriter(this.path, this.charset, this.openOptions);
        }
    }

    class StringIO implements ConfigIO {

        private final StringReader stringReader;
        private final StringWriter stringWriter;

        public StringIO(StringReader stringReader, StringWriter stringWriter) {
            this.stringReader = stringReader;
            this.stringWriter = stringWriter;
        }

        @Override
        public Reader openReader() {
            return this.stringReader;
        }

        @Override
        public Writer openWriter() {
            return this.stringWriter;
        }
    }

    final class StringBoxIO implements ConfigIO {

        private final IMutableBox<String> box;

        public StringBoxIO(IMutableBox<String> box) {
            this.box = box;
        }

        @Override
        public Reader openReader() throws IOException {
            if (!this.box.isPresent())
                throw new IOException("No string stored");

            return new StringReader(this.box.get());
        }

        @Override
        public Writer openWriter() {
            return new InternalWriter();
        }

        class InternalWriter extends StringWriter {
            @Override
            public void flush() {
                super.flush();
                StringBoxIO.this.box.set(this.toString());
            }

            @Override
            public void close() throws IOException {
                super.close();
                StringBoxIO.this.box.set(this.toString());
            }
        }
    }
}
