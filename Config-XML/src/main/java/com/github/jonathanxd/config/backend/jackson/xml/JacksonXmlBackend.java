/*
 *      Config-Jackson-XML - Json backend for Config <https://github.com/JonathanxD/Config/>
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
package com.github.jonathanxd.config.backend.jackson.xml;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.jonathanxd.config.backend.AbstractIOBackend;
import com.github.jonathanxd.config.backend.ConfigIO;
import com.github.jonathanxd.iutils.exception.RethrowException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class JacksonXmlBackend extends AbstractIOBackend {

    private final XmlMapper mapper;


    public JacksonXmlBackend(XmlMapper mapper, ConfigIO io) {
        super(io);
        this.mapper = mapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void save(Map<Object, Object> map, Writer writer) {
        try {
            this.mapper.writeValue(writer, new Root(map));
        } catch (IOException e) {
            throw RethrowException.rethrow(e);
        }

    }

    @JsonRootName("root")
    static class Root {
        @JsonValue
        private final Map<Object, Object> root;

        Root(Map<Object, Object> root) {
            this.root = root;
        }

        public Map<Object, Object> getRoot() {
            return root;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Object, Object> load(Reader reader) {
        try {
            return this.mapper.readValue(reader, Map.class);
        } catch (IOException e) {
            throw RethrowException.rethrow(e);
        }
    }
}
