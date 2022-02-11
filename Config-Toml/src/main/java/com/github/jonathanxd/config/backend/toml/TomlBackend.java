/*
 *      Config-Toml - Json backend for Config <https://github.com/JonathanxD/Config/>
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
package com.github.jonathanxd.config.backend.toml;

import com.github.jonathanxd.config.backend.AbstractIOBackend;
import com.github.jonathanxd.config.backend.ConfigIO;
import com.github.jonathanxd.iutils.exception.RethrowException;
import org.tomlj.*;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.util.*;

public class TomlBackend extends AbstractIOBackend {

    public TomlBackend(ConfigIO io) {
        super(io);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void save(Map<Object, Object> map, Writer writer) {
        try {
            TomlRenderer.render(map, writer);
        } catch (IOException e) {
            throw RethrowException.rethrow(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Object, Object> load(Reader reader) {
        try {
            TomlParseResult parse = Toml.parse(reader);
            if (parse.hasErrors()) {
                IOException e = new IOException("Failed to load TOML.");
                for (TomlParseError error : parse.errors()) {
                    e.addSuppressed(new ParseException(error.toString(), error.position().column()));
                }
                throw e;
            }

            return deepToMap(parse);
        } catch (IOException e) {
            throw RethrowException.rethrow(e);
        }
    }

    public static Object deepToObj(Object o) {
        if (o instanceof TomlArray) {
            List<Object> list = new ArrayList<>();
            TomlArray tomlArray = (TomlArray) o;
            for(int x = 0; x < tomlArray.size(); ++x) {
                Object obj = tomlArray.get(x);
                list.add(deepToObj(obj));
            }
            return list;
        } else if (o instanceof TomlTable) {
            return deepToMap((TomlTable) o);
        } else {
            return o;
        }
    }

    public static Map<Object, Object> deepToMap(TomlTable table) {
        Map<Object, Object> map = new LinkedHashMap<>();

        for (String s : table.keySet()) {
            Object o = table.get(s);
            map.put(s, deepToObj(o));
        }

        return map;
    }

}
