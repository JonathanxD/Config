/*
 *      Config-Yaml-1.2 - Yaml backend for Config <https://github.com/JonathanxD/Config/>
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
package com.github.jonathanxd.config.backend.yaml1_2;

import com.github.jonathanxd.config.CommonTypes;
import com.github.jonathanxd.config.Key;
import com.github.jonathanxd.config.backend.AbstractIOBackend;
import com.github.jonathanxd.config.backend.ConfigIO;
import com.github.jonathanxd.iutils.exception.RethrowException;
import com.github.jonathanxd.iutils.type.TypeInfo;

import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.StreamDataWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;

/**
 * Yaml 1.2 backend that uses {@link ConfigIO} to read and write configuration. Save and load operations
 * may throw {@link IOException}.
 *
 * Note that Yaml 1.2 structure is not fully supported by {@code Config}. Intermediate values will be created to emulate
 * root access. Saved Yaml will contain these intermediate keys, thus keeping compatibility with {@code Config}, but not others
 * tools which could read this yaml config.
 *
 * For unsupported structures, such as lists without a declared key, {@code Config} creates a section named {@code .},
 * which could be accessed through {@code config.getRootKey().getKeySection(".")}.
 */
public class YamlBackend extends AbstractIOBackend {

    private final Load load;
    private final Dump dump;

    /**
     * Creates yaml backend.
     *
     * @param load     Yaml loader.
     * @param dump     Yaml dumper.
     * @param configIO IO to read and write yaml.
     */
    public YamlBackend(Load load,
                       Dump dump,
                       ConfigIO configIO) {
        super(configIO);
        this.load = load;
        this.dump = dump;
    }

    @Override
    public void save(Map<Object, Object> map, Writer writer) {
        Object o = map;

        if (map.size() == 1 && map.containsKey(".")) {
            o = map.get(".");
        }

        this.dump.dump(o, new StreamDataWriter() {
            @Override
            public void write(String str) {
                try {
                    writer.write(str);
                } catch (IOException e) {
                    throw RethrowException.rethrow(e);
                }
            }

            @Override
            public void write(String str, int off, int len) {
                try {
                    writer.write(str, off, len);
                } catch (IOException e) {
                    throw RethrowException.rethrow(e);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Object, Object> load(Reader reader) {
        Map<Object, Object> objMap = new HashMap<>();
        Iterable<Object> objs = this.load.loadAllFromReader(reader);

        Iterator<Object> iterator = objs.iterator();

        int x = 0;
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (x == 0 && !iterator.hasNext()) {
                if (next instanceof Map<?, ?>) {
                    return new LinkedHashMap<>((Map<?, ?>) next);
                } else {
                    objMap.put(".", next);
                    return objMap;
                }
            } else {
                objMap.put(x, next);
            }
            ++x;
        }

        return objMap;
    }

    @Override
    public boolean supports(TypeInfo<?> type) {
        return CommonTypes.isValidBasicType(type);
    }

    @Override
    public Key<?> resolveRoot(Key<?> defaultRootKey) {
        Object value = defaultRootKey.getValue();
        if (value instanceof Map<?, ?>) {
            if (((Map<?, ?>) value).get(".") instanceof List<?>)
                return defaultRootKey.getKey(".", CommonTypes.LIST_OF_OBJECT);
        }

        return super.resolveRoot(defaultRootKey);
    }
}
