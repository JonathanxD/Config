/*
 *      Config-Json - Json backend for Config <https://github.com/JonathanxD/Config/>
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
package com.github.jonathanxd.config.backend.json;

import com.github.jonathanxd.config.backend.AbstractIOBackend;
import com.github.jonathanxd.config.backend.ConfigIO;
import com.github.jonathanxd.iutils.exception.RethrowException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonBackend extends AbstractIOBackend {

    private final JSONParser parser;

    public JsonBackend(JSONParser parser, ConfigIO io) {
        super(io);
        this.parser = parser;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void save(Map<String, Object> map, Writer writer) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(map);
            jsonObject.writeJSONString(writer);
        } catch (IOException e) {
            throw RethrowException.rethrow(e);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> load(Reader reader) {
        try {
            return (Map<String, Object>) parser.parse(reader, new MapContainerFactory());
        } catch (IOException | ParseException e) {
            throw RethrowException.rethrow(e);
        }
    }

    private static final class MapContainerFactory implements ContainerFactory {

        private MapContainerFactory() {
        }

        @Override
        public Map createObjectContainer() {
            return new HashMap();
        }

        @Override
        public List creatArrayContainer() {
            return new ArrayList();
        }
    }
}
